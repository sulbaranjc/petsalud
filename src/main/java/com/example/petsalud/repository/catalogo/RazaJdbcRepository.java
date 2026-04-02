package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.catalogo.Raza;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.petsalud.model.Page;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RazaJdbcRepository implements RazaRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public RazaJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Raza> ROW_MAPPER = (rs, rowNum) -> {
        Raza r = new Raza();
        r.setId(rs.getInt("id"));
        r.setNombre(rs.getString("nombre"));
        r.setIdEspecie(rs.getInt("id_especie"));
        r.setFotoUrl(rs.getString("foto_url"));
        r.setActivo(rs.getBoolean("activo"));
        r.setNombreEspecie(rs.getString("nombre_especie"));
        return r;
    };

    private static final String BASE_SELECT = """
            SELECT r.id,
                   r.nombre,
                   r.id_especie,
                   r.foto_url,
                   r.activo,
                   e.nombre AS nombre_especie
              FROM raza r
              JOIN especie e ON e.id = r.id_especie
            """;

    private static final Map<String, String[]> SORT_COLUMNS = Map.of(
            "nombre",  new String[]{"r.nombre"},
            "especie", new String[]{"e.nombre", "r.nombre"}
    );
    private static final String DEFAULT_SORT = "nombre";

    private String buildOrderBy(String sortBy, String sortDir) {
        String[] cols = SORT_COLUMNS.getOrDefault(sortBy, SORT_COLUMNS.get(DEFAULT_SORT));
        String dir = "desc".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
        return " ORDER BY " + Arrays.stream(cols)
                .map(c -> c + " " + dir)
                .collect(Collectors.joining(", "));
    }

    @Override
    public List<Raza> findAllByOrderByNombreAsc() {
        return jdbc.query(BASE_SELECT + " ORDER BY r.nombre ASC", ROW_MAPPER);
    }

    @Override
    public Page<Raza> search(String q, Integer idEspecie, Boolean activo,
                              int page, int pageSize, String sortBy, String sortDir) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (q != null && !q.isBlank()) {
            where.append(" AND r.nombre LIKE :q");
            params.addValue("q", "%" + q.trim() + "%");
        }
        if (idEspecie != null) {
            where.append(" AND r.id_especie = :idEspecie");
            params.addValue("idEspecie", idEspecie);
        }
        if (activo != null) {
            where.append(" AND r.activo = :activo");
            params.addValue("activo", activo);
        }

        String countSql = "SELECT COUNT(*) FROM raza r JOIN especie e ON e.id = r.id_especie" + where;
        Long total = jdbc.queryForObject(countSql, params, Long.class);

        int offset = (page - 1) * pageSize;
        params.addValue("pageSize", pageSize).addValue("offset", offset);
        List<Raza> content = jdbc.query(
                BASE_SELECT + where + buildOrderBy(sortBy, sortDir)
                        + " LIMIT :pageSize OFFSET :offset",
                params, ROW_MAPPER);

        return new Page<>(content, page, pageSize, total != null ? total : 0L);
    }

    @Override
    public Optional<Raza> findById(Integer id) {
        return jdbc.query(BASE_SELECT + " WHERE r.id = :id", Map.of("id", id), ROW_MAPPER)
                   .stream().findFirst();
    }

    @Override
    public void save(Raza raza) {
        if (raza.getId() == null) {
            insert(raza);
        } else {
            update(raza);
        }
    }

    private void insert(Raza raza) {
        String sql = """
                INSERT INTO raza (nombre, id_especie, foto_url, activo)
                VALUES (:nombre, :idEspecie, :fotoUrl, :activo)
                """;
        jdbc.update(sql, toParams(raza));
    }

    private void update(Raza raza) {
        String sql = """
                UPDATE raza
                   SET nombre     = :nombre,
                       id_especie = :idEspecie,
                       foto_url   = :fotoUrl,
                       activo     = :activo
                 WHERE id = :id
                """;
        jdbc.update(sql, toParams(raza));
    }

    private MapSqlParameterSource toParams(Raza raza) {
        return new MapSqlParameterSource()
                .addValue("id",        raza.getId())
                .addValue("nombre",    raza.getNombre())
                .addValue("idEspecie", raza.getIdEspecie())
                .addValue("fotoUrl",   raza.getFotoUrl())
                .addValue("activo",    raza.isActivo());
    }
}
