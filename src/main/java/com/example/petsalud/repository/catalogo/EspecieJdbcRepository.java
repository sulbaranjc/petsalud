package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.catalogo.Especie;
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
public class EspecieJdbcRepository implements EspecieRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public EspecieJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Especie> ROW_MAPPER = (rs, rowNum) -> {
        Especie e = new Especie();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre"));
        e.setDescripcion(rs.getString("descripcion"));
        e.setActivo(rs.getBoolean("activo"));
        return e;
    };

    private static final String BASE_SELECT =
            "SELECT id, nombre, descripcion, activo FROM especie";

    private static final Map<String, String[]> SORT_COLUMNS = Map.of(
            "nombre", new String[]{"nombre"}
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
    public List<Especie> findAllByOrderByNombreAsc() {
        return jdbc.query(BASE_SELECT + " ORDER BY nombre ASC", ROW_MAPPER);
    }

    @Override
    public Page<Especie> search(String q, Boolean activo, int page, int pageSize,
                                 String sortBy, String sortDir) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (q != null && !q.isBlank()) {
            where.append(" AND (nombre LIKE :q OR descripcion LIKE :q)");
            params.addValue("q", "%" + q.trim() + "%");
        }
        if (activo != null) {
            where.append(" AND activo = :activo");
            params.addValue("activo", activo);
        }

        Long total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM especie" + where, params, Long.class);

        int offset = (page - 1) * pageSize;
        params.addValue("pageSize", pageSize).addValue("offset", offset);
        List<Especie> content = jdbc.query(
                BASE_SELECT + where + buildOrderBy(sortBy, sortDir)
                        + " LIMIT :pageSize OFFSET :offset",
                params, ROW_MAPPER);

        return new Page<>(content, page, pageSize, total != null ? total : 0L);
    }

    @Override
    public Optional<Especie> findById(Integer id) {
        String sql = "SELECT id, nombre, descripcion, activo FROM especie WHERE id = :id";
        return jdbc.query(sql, Map.of("id", id), ROW_MAPPER)
                   .stream().findFirst();
    }

    @Override
    public void save(Especie especie) {
        if (especie.getId() == null) {
            insert(especie);
        } else {
            update(especie);
        }
    }

    private void insert(Especie especie) {
        String sql = """
                INSERT INTO especie (nombre, descripcion, activo)
                VALUES (:nombre, :descripcion, :activo)
                """;
        jdbc.update(sql, toParams(especie));
    }

    private void update(Especie especie) {
        String sql = """
                UPDATE especie
                   SET nombre      = :nombre,
                       descripcion = :descripcion,
                       activo      = :activo
                 WHERE id = :id
                """;
        jdbc.update(sql, toParams(especie));
    }

    private MapSqlParameterSource toParams(Especie especie) {
        return new MapSqlParameterSource()
                .addValue("id",          especie.getId())
                .addValue("nombre",      especie.getNombre())
                .addValue("descripcion", especie.getDescripcion())
                .addValue("activo",      especie.isActivo());
    }
}
