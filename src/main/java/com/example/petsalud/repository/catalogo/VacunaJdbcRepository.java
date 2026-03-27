package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Vacuna;
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
public class VacunaJdbcRepository implements VacunaRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public VacunaJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Vacuna> ROW_MAPPER = (rs, rowNum) -> {
        Vacuna v = new Vacuna();
        v.setId(rs.getInt("id"));
        v.setNombre(rs.getString("nombre"));
        v.setLaboratorio(rs.getString("laboratorio"));
        v.setDescripcion(rs.getString("descripcion"));
        v.setActivo(rs.getBoolean("activo"));
        return v;
    };

    private static final String BASE_SELECT = """
            SELECT id, nombre, laboratorio, descripcion, activo
              FROM vacuna
            """;

    private static final Map<String, String[]> SORT_COLUMNS = Map.of(
            "nombre",      new String[]{"nombre"},
            "laboratorio", new String[]{"laboratorio", "nombre"}
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
    public List<Vacuna> findAllActivas() {
        return jdbc.query(BASE_SELECT + " WHERE activo = 1 ORDER BY nombre ASC", ROW_MAPPER);
    }

    @Override
    public Page<Vacuna> search(String q, Boolean activo,
                                int page, int pageSize,
                                String sortBy, String sortDir) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (q != null && !q.isBlank()) {
            where.append("""
                     AND (nombre       LIKE :q
                          OR laboratorio LIKE :q
                          OR descripcion LIKE :q)
                    """);
            params.addValue("q", "%" + q.trim() + "%");
        }
        if (activo != null) {
            where.append(" AND activo = :activo");
            params.addValue("activo", activo);
        }

        Long total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM vacuna" + where, params, Long.class);

        int offset = (page - 1) * pageSize;
        params.addValue("pageSize", pageSize).addValue("offset", offset);
        List<Vacuna> content = jdbc.query(
                BASE_SELECT + where + buildOrderBy(sortBy, sortDir)
                        + " LIMIT :pageSize OFFSET :offset",
                params, ROW_MAPPER);

        return new Page<>(content, page, pageSize, total != null ? total : 0L);
    }

    @Override
    public Optional<Vacuna> findById(Integer id) {
        return jdbc.query(BASE_SELECT + " WHERE id = :id", Map.of("id", id), ROW_MAPPER)
                   .stream().findFirst();
    }

    @Override
    public void save(Vacuna vacuna) {
        if (vacuna.getId() == null) {
            insert(vacuna);
        } else {
            update(vacuna);
        }
    }

    private void insert(Vacuna v) {
        String sql = """
                INSERT INTO vacuna (nombre, laboratorio, descripcion, activo)
                VALUES (:nombre, :laboratorio, :descripcion, :activo)
                """;
        jdbc.update(sql, toParams(v));
    }

    private void update(Vacuna v) {
        String sql = """
                UPDATE vacuna
                   SET nombre      = :nombre,
                       laboratorio = :laboratorio,
                       descripcion = :descripcion,
                       activo      = :activo
                 WHERE id = :id
                """;
        jdbc.update(sql, toParams(v));
    }

    private MapSqlParameterSource toParams(Vacuna v) {
        return new MapSqlParameterSource()
                .addValue("id",          v.getId())
                .addValue("nombre",      v.getNombre())
                .addValue("laboratorio", v.getLaboratorio())
                .addValue("descripcion", v.getDescripcion())
                .addValue("activo",      v.isActivo());
    }
}
