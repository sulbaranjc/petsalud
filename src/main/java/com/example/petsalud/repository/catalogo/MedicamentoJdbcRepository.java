package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Medicamento;
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
public class MedicamentoJdbcRepository implements MedicamentoRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public MedicamentoJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Medicamento> ROW_MAPPER = (rs, rowNum) -> {
        Medicamento m = new Medicamento();
        m.setId(rs.getInt("id"));
        m.setNombre(rs.getString("nombre"));
        m.setPresentacion(rs.getString("presentacion"));
        m.setDescripcion(rs.getString("descripcion"));
        m.setActivo(rs.getBoolean("activo"));
        return m;
    };

    private static final String BASE_SELECT = """
            SELECT id, nombre, presentacion, descripcion, activo
              FROM medicamento
            """;

    private static final Map<String, String[]> SORT_COLUMNS = Map.of(
            "nombre",       new String[]{"nombre"},
            "presentacion", new String[]{"presentacion", "nombre"}
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
    public List<Medicamento> findAllActivos() {
        return jdbc.query(BASE_SELECT + " WHERE activo = 1 ORDER BY nombre ASC", ROW_MAPPER);
    }

    @Override
    public Page<Medicamento> search(String q, Boolean activo,
                                     int page, int pageSize,
                                     String sortBy, String sortDir) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (q != null && !q.isBlank()) {
            where.append("""
                     AND (nombre       LIKE :q
                          OR presentacion LIKE :q
                          OR descripcion  LIKE :q)
                    """);
            params.addValue("q", "%" + q.trim() + "%");
        }
        if (activo != null) {
            where.append(" AND activo = :activo");
            params.addValue("activo", activo);
        }

        Long total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM medicamento" + where, params, Long.class);

        int offset = (page - 1) * pageSize;
        params.addValue("pageSize", pageSize).addValue("offset", offset);
        List<Medicamento> content = jdbc.query(
                BASE_SELECT + where + buildOrderBy(sortBy, sortDir)
                        + " LIMIT :pageSize OFFSET :offset",
                params, ROW_MAPPER);

        return new Page<>(content, page, pageSize, total != null ? total : 0L);
    }

    @Override
    public Optional<Medicamento> findById(Integer id) {
        return jdbc.query(BASE_SELECT + " WHERE id = :id", Map.of("id", id), ROW_MAPPER)
                   .stream().findFirst();
    }

    @Override
    public void save(Medicamento medicamento) {
        if (medicamento.getId() == null) {
            insert(medicamento);
        } else {
            update(medicamento);
        }
    }

    private void insert(Medicamento m) {
        String sql = """
                INSERT INTO medicamento (nombre, presentacion, descripcion, activo)
                VALUES (:nombre, :presentacion, :descripcion, :activo)
                """;
        jdbc.update(sql, toParams(m));
    }

    private void update(Medicamento m) {
        String sql = """
                UPDATE medicamento
                   SET nombre       = :nombre,
                       presentacion = :presentacion,
                       descripcion  = :descripcion,
                       activo       = :activo
                 WHERE id = :id
                """;
        jdbc.update(sql, toParams(m));
    }

    private MapSqlParameterSource toParams(Medicamento m) {
        return new MapSqlParameterSource()
                .addValue("id",           m.getId())
                .addValue("nombre",       m.getNombre())
                .addValue("presentacion", m.getPresentacion())
                .addValue("descripcion",  m.getDescripcion())
                .addValue("activo",       m.isActivo());
    }
}
