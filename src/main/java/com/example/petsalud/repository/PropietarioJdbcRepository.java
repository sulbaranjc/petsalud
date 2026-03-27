package com.example.petsalud.repository;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.Propietario;
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
public class PropietarioJdbcRepository implements PropietarioRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public PropietarioJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Propietario> ROW_MAPPER = (rs, rowNum) -> {
        Propietario p = new Propietario();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setApellido(rs.getString("apellido"));
        p.setDocumento(rs.getString("documento"));
        p.setTelefono(rs.getString("telefono"));
        p.setEmail(rs.getString("email"));
        p.setDireccion(rs.getString("direccion"));
        p.setActivo(rs.getBoolean("activo"));
        // total_mascotas solo existe en queries que incluyan la subquery
        try { p.setTotalMascotas(rs.getInt("total_mascotas")); } catch (Exception ignored) {}
        return p;
    };

    /**
     * Mapa de columnas ordenables: clave usada en la URL → expresiones SQL reales.
     * Al ser un mapa cerrado (whitelist), no existe riesgo de inyección SQL:
     * cualquier valor no reconocido cae en la clave por defecto.
     * Las columnas compuestas (apellido+nombre) se aplican en bloque con la
     * misma dirección para mantener coherencia.
     */
    private static final Map<String, String[]> SORT_COLUMNS = Map.of(
            "nombre",    new String[]{"p.apellido", "p.nombre"},
            "documento", new String[]{"p.documento"},
            "telefono",  new String[]{"p.telefono"},
            "email",     new String[]{"p.email"},
            "mascotas",  new String[]{"total_mascotas"}
    );
    private static final String DEFAULT_SORT = "nombre";

    private String buildOrderBy(String sortBy, String sortDir) {
        String[] cols = SORT_COLUMNS.getOrDefault(sortBy, SORT_COLUMNS.get(DEFAULT_SORT));
        String dir = "desc".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
        return " ORDER BY " + Arrays.stream(cols)
                .map(c -> c + " " + dir)
                .collect(Collectors.joining(", "));
    }

    private static final String BASE_SELECT = """
            SELECT p.id,
                   p.nombre,
                   p.apellido,
                   p.documento,
                   p.telefono,
                   p.email,
                   p.direccion,
                   p.activo,
                   (SELECT COUNT(*) FROM mascota m WHERE m.id_propietario = p.id) AS total_mascotas
              FROM propietario p
            """;

    @Override
    public List<Propietario> findAllByOrderByApellidoNombreAsc() {
        return jdbc.query(BASE_SELECT + " ORDER BY p.apellido ASC, p.nombre ASC", ROW_MAPPER);
    }

    @Override
    public Page<Propietario> search(String q, Boolean activo, int page, int pageSize,
                                    String sortBy, String sortDir) {
        StringBuilder where  = new StringBuilder(" WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();
        aplicarFiltros(where, params, q, activo);

        // 1. Total de registros que cumplen los filtros
        String countSql = "SELECT COUNT(*) FROM propietario p" + where;
        Long total = jdbc.queryForObject(countSql, params, Long.class);

        // 2. Página de datos con ORDER BY dinámico + LIMIT/OFFSET
        int offset = (page - 1) * pageSize;
        params.addValue("pageSize", pageSize)
              .addValue("offset",   offset);
        String dataSql = BASE_SELECT + where
                + buildOrderBy(sortBy, sortDir)
                + " LIMIT :pageSize OFFSET :offset";
        List<Propietario> content = jdbc.query(dataSql, params, ROW_MAPPER);

        return new Page<>(content, page, pageSize, total != null ? total : 0L);
    }

    /**
     * Construye las cláusulas WHERE dinámicas y registra los parámetros.
     * Se reutiliza en la query de COUNT y en la query de datos para garantizar
     * que ambas apliquen exactamente los mismos filtros.
     */
    private void aplicarFiltros(StringBuilder where, MapSqlParameterSource params,
                                 String q, Boolean activo) {
        if (q != null && !q.isBlank()) {
            where.append(" AND (p.nombre LIKE :q OR p.apellido LIKE :q"
                       + " OR p.documento LIKE :q OR p.email LIKE :q)");
            params.addValue("q", "%" + q.trim() + "%");
        }
        if (activo != null) {
            where.append(" AND p.activo = :activo");
            params.addValue("activo", activo);
        }
    }

    @Override
    public Optional<Propietario> findById(Integer id) {
        return jdbc.query(BASE_SELECT + " WHERE p.id = :id",
                          Map.of("id", id), ROW_MAPPER).stream().findFirst();
    }

    @Override
    public void save(Propietario propietario) {
        if (propietario.getId() == null) {
            insert(propietario);
        } else {
            update(propietario);
        }
    }

    private void insert(Propietario p) {
        String sql = """
                INSERT INTO propietario (nombre, apellido, documento, telefono, email, direccion, activo)
                VALUES (:nombre, :apellido, :documento, :telefono, :email, :direccion, :activo)
                """;
        jdbc.update(sql, toParams(p));
    }

    private void update(Propietario p) {
        String sql = """
                UPDATE propietario
                   SET nombre    = :nombre,
                       apellido  = :apellido,
                       documento = :documento,
                       telefono  = :telefono,
                       email     = :email,
                       direccion = :direccion,
                       activo    = :activo
                 WHERE id = :id
                """;
        jdbc.update(sql, toParams(p));
    }

    private MapSqlParameterSource toParams(Propietario p) {
        return new MapSqlParameterSource()
                .addValue("id",        p.getId())
                .addValue("nombre",    p.getNombre())
                .addValue("apellido",  p.getApellido())
                .addValue("documento", p.getDocumento())
                .addValue("telefono",  p.getTelefono())
                .addValue("email",     p.getEmail())
                .addValue("direccion", p.getDireccion())
                .addValue("activo",    p.isActivo());
    }
}
