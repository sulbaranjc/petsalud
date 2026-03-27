package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Especialidad;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class EspecialidadJdbcRepository implements EspecialidadRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public EspecialidadJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Especialidad> ROW_MAPPER = (rs, rowNum) -> {
        Especialidad e = new Especialidad();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre"));
        e.setDescripcion(rs.getString("descripcion"));
        e.setActivo(rs.getBoolean("activo"));
        try { e.setTotalVeterinarios(rs.getInt("total_veterinarios")); } catch (Exception ignored) {}
        return e;
    };

    private static final String BASE_SELECT = """
            SELECT esp.id,
                   esp.nombre,
                   esp.descripcion,
                   esp.activo,
                   (SELECT COUNT(*) FROM veterinario v WHERE v.id_especialidad = esp.id) AS total_veterinarios
              FROM especialidad esp
            """;

    /**
     * Whitelist de columnas ordenables — nunca se concatenan valores externos al SQL.
     * "veterinarios" ordena por el alias de la subquery, válido en MySQL.
     */
    private static final Map<String, String[]> SORT_COLUMNS = Map.of(
            "nombre",       new String[]{"esp.nombre"},
            "veterinarios", new String[]{"total_veterinarios"}
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
    public List<Especialidad> findAllActivas() {
        String sql = BASE_SELECT + " WHERE esp.activo = 1 ORDER BY esp.nombre ASC";
        return jdbc.query(sql, ROW_MAPPER);
    }

    @Override
    public Page<Especialidad> search(String q, Boolean activo, int page, int pageSize,
                                      String sortBy, String sortDir) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();
        aplicarFiltros(where, params, q, activo);

        String countSql = "SELECT COUNT(*) FROM especialidad esp" + where;
        Long total = jdbc.queryForObject(countSql, params, Long.class);

        int offset = (page - 1) * pageSize;
        params.addValue("pageSize", pageSize).addValue("offset", offset);
        String dataSql = BASE_SELECT + where
                + buildOrderBy(sortBy, sortDir)
                + " LIMIT :pageSize OFFSET :offset";
        List<Especialidad> content = jdbc.query(dataSql, params, ROW_MAPPER);

        return new Page<>(content, page, pageSize, total != null ? total : 0L);
    }

    private void aplicarFiltros(StringBuilder where, MapSqlParameterSource params,
                                 String q, Boolean activo) {
        if (q != null && !q.isBlank()) {
            where.append(" AND (esp.nombre LIKE :q OR esp.descripcion LIKE :q)");
            params.addValue("q", "%" + q.trim() + "%");
        }
        if (activo != null) {
            where.append(" AND esp.activo = :activo");
            params.addValue("activo", activo);
        }
    }

    @Override
    public Optional<Especialidad> findById(Integer id) {
        return jdbc.query(BASE_SELECT + " WHERE esp.id = :id",
                Map.of("id", id), ROW_MAPPER).stream().findFirst();
    }

    @Override
    public void save(Especialidad especialidad) {
        if (especialidad.getId() == null) {
            insert(especialidad);
        } else {
            update(especialidad);
        }
    }

    private void insert(Especialidad e) {
        String sql = """
                INSERT INTO especialidad (nombre, descripcion, activo)
                VALUES (:nombre, :descripcion, :activo)
                """;
        jdbc.update(sql, toParams(e));
    }

    private void update(Especialidad e) {
        String sql = """
                UPDATE especialidad
                   SET nombre      = :nombre,
                       descripcion = :descripcion,
                       activo      = :activo
                 WHERE id = :id
                """;
        jdbc.update(sql, toParams(e));
    }

    private MapSqlParameterSource toParams(Especialidad e) {
        return new MapSqlParameterSource()
                .addValue("id",          e.getId())
                .addValue("nombre",      e.getNombre())
                .addValue("descripcion", e.getDescripcion())
                .addValue("activo",      e.isActivo());
    }
}
