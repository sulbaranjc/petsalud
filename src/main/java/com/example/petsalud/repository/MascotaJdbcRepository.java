package com.example.petsalud.repository;

import com.example.petsalud.model.Mascota;
import com.example.petsalud.model.Page;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MascotaJdbcRepository implements MascotaRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public MascotaJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Mascota> ROW_MAPPER = (rs, rowNum) -> {
        Mascota m = new Mascota();
        m.setId(rs.getInt("id"));
        m.setNombre(rs.getString("nombre"));
        m.setSexo(rs.getString("sexo"));
        m.setColor(rs.getString("color"));
        m.setIdEspecie(rs.getInt("id_especie"));
        m.setIdPropietario(rs.getInt("id_propietario"));
        m.setFotoUrl(rs.getString("foto_url"));
        m.setActivo(rs.getBoolean("activo"));
        m.setNombreEspecie(rs.getString("nombre_especie"));
        m.setNombreRaza(rs.getString("nombre_raza"));
        m.setNombrePropietario(rs.getString("nombre_propietario"));

        Date fechaNac = rs.getDate("fecha_nacimiento");
        if (fechaNac != null) m.setFechaNacimiento(fechaNac.toLocalDate());

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) m.setCreatedAt(createdAt.toLocalDateTime());

        int idRaza = rs.getInt("id_raza");
        if (!rs.wasNull()) m.setIdRaza(idRaza);

        return m;
    };

    private static final String BASE_SELECT = """
            SELECT m.id,
                   m.nombre,
                   m.fecha_nacimiento,
                   m.sexo,
                   m.color,
                   m.id_especie,
                   m.id_raza,
                   m.id_propietario,
                   m.foto_url,
                   m.activo,
                   m.created_at,
                   e.nombre                              AS nombre_especie,
                   r.nombre                              AS nombre_raza,
                   CONCAT(p.apellido, ', ', p.nombre)    AS nombre_propietario
              FROM mascota m
              JOIN especie     e ON e.id = m.id_especie
              JOIN propietario p ON p.id = m.id_propietario
         LEFT JOIN raza        r ON r.id = m.id_raza
            """;

    /**
     * Whitelist de columnas ordenables: clave URL → expresiones SQL reales.
     * "especie" ordena por especie y raza como criterio secundario.
     * "propietario" usa apellido + nombre para un orden natural completo.
     * fecha_nacimiento ordena cronológicamente (NULL queda al final con ASC).
     */
    private static final Map<String, String[]> SORT_COLUMNS = Map.of(
            "nombre",       new String[]{"m.nombre"},
            "especie",      new String[]{"e.nombre", "r.nombre"},
            "sexo",         new String[]{"m.sexo"},
            "propietario",  new String[]{"p.apellido", "p.nombre"},
            "nacimiento",   new String[]{"m.fecha_nacimiento"}
    );
    private static final String DEFAULT_SORT = "nombre";

    private String buildOrderBy(String sortBy, String sortDir) {
        String[] cols = SORT_COLUMNS.getOrDefault(sortBy, SORT_COLUMNS.get(DEFAULT_SORT));
        String dir = "desc".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
        return " ORDER BY " + Arrays.stream(cols)
                .map(c -> c + " " + dir)
                .collect(Collectors.joining(", "));
    }

    // ── Paginación ────────────────────────────────────────────────────────────

    @Override
    public Page<Mascota> search(String q, Integer idEspecie, String sexo, Boolean activo,
                                int page, int pageSize, String sortBy, String sortDir) {
        StringBuilder where  = new StringBuilder(" WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();
        aplicarFiltros(where, params, q, idEspecie, sexo, activo);

        // 1. Contar el total de registros que cumplen los filtros
        String countSql = """
                SELECT COUNT(*)
                  FROM mascota m
                  JOIN especie     e ON e.id = m.id_especie
                  JOIN propietario p ON p.id = m.id_propietario
                 LEFT JOIN raza    r ON r.id = m.id_raza
                """ + where;
        Long total = jdbc.queryForObject(countSql, params, Long.class);

        // 2. Traer solo la página solicitada con ORDER BY dinámico + LIMIT/OFFSET
        int offset = (page - 1) * pageSize;
        params.addValue("pageSize", pageSize)
              .addValue("offset",   offset);
        String dataSql = BASE_SELECT + where
                + buildOrderBy(sortBy, sortDir)
                + " LIMIT :pageSize OFFSET :offset";
        List<Mascota> content = jdbc.query(dataSql, params, ROW_MAPPER);

        return new Page<>(content, page, pageSize, total != null ? total : 0L);
    }

    /**
     * Agrega cláusulas WHERE dinámicas al StringBuilder y registra los
     * parámetros correspondientes. Se reutiliza para la query de COUNT
     * y para la query de datos, garantizando que ambas usen los mismos filtros.
     */
    private void aplicarFiltros(StringBuilder where, MapSqlParameterSource params,
                                 String q, Integer idEspecie, String sexo, Boolean activo) {
        if (q != null && !q.isBlank()) {
            where.append("""
                    \s AND (m.nombre    LIKE :q
                            OR p.nombre    LIKE :q
                            OR p.apellido  LIKE :q
                            OR e.nombre    LIKE :q
                            OR r.nombre    LIKE :q)
                    """);
            params.addValue("q", "%" + q.trim() + "%");
        }
        if (idEspecie != null) {
            where.append(" AND m.id_especie = :idEspecie");
            params.addValue("idEspecie", idEspecie);
        }
        if (sexo != null && !sexo.isBlank()) {
            where.append(" AND m.sexo = :sexo");
            params.addValue("sexo", sexo);
        }
        if (activo != null) {
            where.append(" AND m.activo = :activo");
            params.addValue("activo", activo);
        }
    }

    // ── Operaciones individuales ──────────────────────────────────────────────

    @Override
    public Optional<Mascota> findById(Integer id) {
        String sql = BASE_SELECT + " WHERE m.id = :id";
        return jdbc.query(sql, Map.of("id", id), ROW_MAPPER).stream().findFirst();
    }

    @Override
    public void save(Mascota mascota) {
        if (mascota.getId() == null) {
            insert(mascota);
        } else {
            update(mascota);
        }
    }

    private void insert(Mascota m) {
        String sql = """
                INSERT INTO mascota
                    (nombre, fecha_nacimiento, sexo, color,
                     id_especie, id_raza, id_propietario, foto_url, activo)
                VALUES
                    (:nombre, :fechaNacimiento, :sexo, :color,
                     :idEspecie, :idRaza, :idPropietario, :fotoUrl, :activo)
                """;
        jdbc.update(sql, toParams(m));
    }

    private void update(Mascota m) {
        String sql = """
                UPDATE mascota
                   SET nombre           = :nombre,
                       fecha_nacimiento = :fechaNacimiento,
                       sexo             = :sexo,
                       color            = :color,
                       id_especie       = :idEspecie,
                       id_raza          = :idRaza,
                       id_propietario   = :idPropietario,
                       foto_url         = :fotoUrl,
                       activo           = :activo
                 WHERE id = :id
                """;
        jdbc.update(sql, toParams(m));
    }

    private MapSqlParameterSource toParams(Mascota m) {
        return new MapSqlParameterSource()
                .addValue("id",              m.getId())
                .addValue("nombre",          m.getNombre())
                .addValue("fechaNacimiento", m.getFechaNacimiento())
                .addValue("sexo",            m.getSexo())
                .addValue("color",           m.getColor())
                .addValue("idEspecie",       m.getIdEspecie())
                .addValue("idRaza",          m.getIdRaza())
                .addValue("idPropietario",   m.getIdPropietario())
                .addValue("fotoUrl",         m.getFotoUrl())
                .addValue("activo",          m.isActivo());
    }
}
