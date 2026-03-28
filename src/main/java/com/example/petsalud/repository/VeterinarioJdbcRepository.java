package com.example.petsalud.repository;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.Veterinario;
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
public class VeterinarioJdbcRepository implements VeterinarioRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public VeterinarioJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Veterinario> ROW_MAPPER = (rs, rowNum) -> {
        Veterinario v = new Veterinario();
        v.setId(rs.getInt("id"));
        v.setNombre(rs.getString("nombre"));
        v.setApellido(rs.getString("apellido"));
        v.setMatricula(rs.getString("matricula"));
        v.setTelefono(rs.getString("telefono"));
        v.setEmail(rs.getString("email"));
        v.setActivo(rs.getBoolean("activo"));
        v.setFotoUrl(rs.getString("foto_url"));
        v.setNombreEspecialidad(rs.getString("nombre_especialidad")); // NULL si sin especialidad

        // id_especialidad es nullable
        int idEsp = rs.getInt("id_especialidad");
        if (!rs.wasNull()) v.setIdEspecialidad(idEsp);

        return v;
    };

    /**
     * LEFT JOIN con especialidad porque id_especialidad es nullable:
     * un veterinario puede ser de medicina general sin especialidad asignada.
     */
    private static final String BASE_SELECT = """
            SELECT v.id,
                   v.nombre,
                   v.apellido,
                   v.matricula,
                   v.telefono,
                   v.email,
                   v.id_especialidad,
                   v.activo,
                   v.foto_url,
                   esp.nombre AS nombre_especialidad
              FROM veterinario v
         LEFT JOIN especialidad esp ON esp.id = v.id_especialidad
            """;

    /**
     * Whitelist de columnas ordenables.
     * "nombre" usa apellido + nombre para un orden natural del apellido.
     * "especialidad" ordena por nombre de la especialidad (NULL va primero en ASC).
     */
    private static final Map<String, String[]> SORT_COLUMNS = Map.of(
            "nombre",       new String[]{"v.apellido", "v.nombre"},
            "matricula",    new String[]{"v.matricula"},
            "especialidad", new String[]{"esp.nombre"},
            "telefono",     new String[]{"v.telefono"},
            "email",        new String[]{"v.email"}
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
    public List<Veterinario> findAllActivos() {
        String sql = BASE_SELECT + " WHERE v.activo = 1 ORDER BY v.apellido ASC, v.nombre ASC";
        return jdbc.query(sql, ROW_MAPPER);
    }

    @Override
    public Page<Veterinario> search(String q, Integer idEspecialidad, Boolean activo,
                                     int page, int pageSize, String sortBy, String sortDir) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();
        aplicarFiltros(where, params, q, idEspecialidad, activo);

        String countSql = """
                SELECT COUNT(*)
                  FROM veterinario v
             LEFT JOIN especialidad esp ON esp.id = v.id_especialidad
                """ + where;
        Long total = jdbc.queryForObject(countSql, params, Long.class);

        int offset = (page - 1) * pageSize;
        params.addValue("pageSize", pageSize).addValue("offset", offset);
        String dataSql = BASE_SELECT + where
                + buildOrderBy(sortBy, sortDir)
                + " LIMIT :pageSize OFFSET :offset";
        List<Veterinario> content = jdbc.query(dataSql, params, ROW_MAPPER);

        return new Page<>(content, page, pageSize, total != null ? total : 0L);
    }

    private void aplicarFiltros(StringBuilder where, MapSqlParameterSource params,
                                  String q, Integer idEspecialidad, Boolean activo) {
        if (q != null && !q.isBlank()) {
            where.append(" AND (v.nombre LIKE :q OR v.apellido LIKE :q"
                       + " OR v.matricula LIKE :q OR v.email LIKE :q)");
            params.addValue("q", "%" + q.trim() + "%");
        }
        if (idEspecialidad != null) {
            where.append(" AND v.id_especialidad = :idEspecialidad");
            params.addValue("idEspecialidad", idEspecialidad);
        }
        if (activo != null) {
            where.append(" AND v.activo = :activo");
            params.addValue("activo", activo);
        }
    }

    @Override
    public Optional<Veterinario> findById(Integer id) {
        return jdbc.query(BASE_SELECT + " WHERE v.id = :id",
                Map.of("id", id), ROW_MAPPER).stream().findFirst();
    }

    @Override
    public void save(Veterinario veterinario) {
        if (veterinario.getId() == null) {
            insert(veterinario);
        } else {
            update(veterinario);
        }
    }

    private void insert(Veterinario v) {
        String sql = """
                INSERT INTO veterinario (nombre, apellido, matricula, telefono, email, id_especialidad, foto_url, activo)
                VALUES (:nombre, :apellido, :matricula, :telefono, :email, :idEspecialidad, :fotoUrl, :activo)
                """;
        jdbc.update(sql, toParams(v));
    }

    private void update(Veterinario v) {
        String sql = """
                UPDATE veterinario
                   SET nombre          = :nombre,
                       apellido        = :apellido,
                       matricula       = :matricula,
                       telefono        = :telefono,
                       email           = :email,
                       id_especialidad = :idEspecialidad,
                       foto_url        = :fotoUrl,
                       activo          = :activo
                 WHERE id = :id
                """;
        jdbc.update(sql, toParams(v));
    }

    private MapSqlParameterSource toParams(Veterinario v) {
        return new MapSqlParameterSource()
                .addValue("id",              v.getId())
                .addValue("nombre",          v.getNombre())
                .addValue("apellido",        v.getApellido())
                .addValue("matricula",       v.getMatricula())
                .addValue("telefono",        v.getTelefono())
                .addValue("email",           v.getEmail())
                .addValue("idEspecialidad",  v.getIdEspecialidad()) // puede ser null
                .addValue("fotoUrl",         v.getFotoUrl())
                .addValue("activo",          v.isActivo());
    }
}
