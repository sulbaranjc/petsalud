package com.example.petsalud.repository;

import com.example.petsalud.model.Cita;
import com.example.petsalud.model.Page;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CitaJdbcRepository implements CitaRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public CitaJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Cita> ROW_MAPPER = (rs, rowNum) -> {
        Cita c = new Cita();
        c.setId(rs.getInt("id"));
        c.setIdMascota(rs.getInt("id_mascota"));
        c.setIdVeterinario(rs.getInt("id_veterinario"));
        c.setIdEstadoCita(rs.getInt("id_estado_cita"));
        c.setMotivo(rs.getString("motivo"));
        c.setObservaciones(rs.getString("observaciones"));
        c.setNombreMascota(rs.getString("nombre_mascota"));
        c.setFotoUrlMascota(rs.getString("foto_url_mascota"));
        c.setNombreEspecie(rs.getString("nombre_especie"));
        c.setNombreRaza(rs.getString("nombre_raza"));
        c.setNombrePropietario(rs.getString("nombre_propietario"));
        c.setNombreVeterinario(rs.getString("nombre_veterinario"));
        c.setNombreEstadoCita(rs.getString("nombre_estado_cita"));

        Timestamp fechaHora = rs.getTimestamp("fecha_hora");
        if (fechaHora != null) c.setFechaHora(fechaHora.toLocalDateTime());

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) c.setCreatedAt(createdAt.toLocalDateTime());

        int idConsulta = rs.getInt("id_consulta");
        if (!rs.wasNull()) c.setIdConsulta(idConsulta);

        return c;
    };

    private static final String BASE_FROM = """
              FROM cita c
              JOIN mascota     m   ON m.id  = c.id_mascota
              JOIN especie     e   ON e.id  = m.id_especie
         LEFT JOIN raza        r   ON r.id  = m.id_raza
              JOIN propietario p   ON p.id  = m.id_propietario
              JOIN veterinario v   ON v.id  = c.id_veterinario
              JOIN estado_cita ec  ON ec.id = c.id_estado_cita
         LEFT JOIN consulta    con ON con.id_cita = c.id
            """;

    private static final String BASE_SELECT = """
            SELECT c.id,
                   c.id_mascota,
                   c.id_veterinario,
                   c.id_estado_cita,
                   c.fecha_hora,
                   c.motivo,
                   c.observaciones,
                   c.created_at,
                   con.id                                AS id_consulta,
                   m.nombre                              AS nombre_mascota,
                   m.foto_url                            AS foto_url_mascota,
                   e.nombre                              AS nombre_especie,
                   r.nombre                              AS nombre_raza,
                   CONCAT(p.apellido, ', ', p.nombre)    AS nombre_propietario,
                   CONCAT(v.apellido, ', ', v.nombre)    AS nombre_veterinario,
                   ec.nombre                             AS nombre_estado_cita
            """ + BASE_FROM;

    private static final Map<String, String[]> SORT_COLUMNS = Map.of(
            "fecha_hora",  new String[]{"c.fecha_hora"},
            "mascota",     new String[]{"m.nombre"},
            "veterinario", new String[]{"v.apellido", "v.nombre"}
    );
    private static final String DEFAULT_SORT = "fecha_hora";

    private String buildOrderBy(String sortBy, String sortDir) {
        String[] cols = SORT_COLUMNS.getOrDefault(sortBy, SORT_COLUMNS.get(DEFAULT_SORT));
        String dir = "desc".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
        return " ORDER BY " + Arrays.stream(cols)
                .map(col -> col + " " + dir)
                .collect(Collectors.joining(", "));
    }

    @Override
    public Page<Cita> search(String q, Integer idEstadoCita, Integer idVeterinario, LocalDate fecha,
                              int page, int pageSize, String sortBy, String sortDir) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();
        aplicarFiltros(where, params, q, idEstadoCita, idVeterinario, fecha);

        Long total = jdbc.queryForObject(
                "SELECT COUNT(*)" + BASE_FROM + where, params, Long.class);

        int offset = (page - 1) * pageSize;
        params.addValue("pageSize", pageSize).addValue("offset", offset);
        List<Cita> content = jdbc.query(
                BASE_SELECT + where + buildOrderBy(sortBy, sortDir)
                        + " LIMIT :pageSize OFFSET :offset",
                params, ROW_MAPPER);

        return new Page<>(content, page, pageSize, total != null ? total : 0L);
    }

    private void aplicarFiltros(StringBuilder where, MapSqlParameterSource params,
                                  String q, Integer idEstadoCita, Integer idVeterinario,
                                  LocalDate fecha) {
        if (q != null && !q.isBlank()) {
            where.append("""
                     AND (m.nombre   LIKE :q
                          OR p.nombre    LIKE :q
                          OR p.apellido  LIKE :q
                          OR v.nombre    LIKE :q
                          OR v.apellido  LIKE :q
                          OR c.motivo    LIKE :q)
                    """);
            params.addValue("q", "%" + q.trim() + "%");
        }
        if (idEstadoCita != null) {
            where.append(" AND c.id_estado_cita = :idEstadoCita");
            params.addValue("idEstadoCita", idEstadoCita);
        }
        if (idVeterinario != null) {
            where.append(" AND c.id_veterinario = :idVeterinario");
            params.addValue("idVeterinario", idVeterinario);
        }
        if (fecha != null) {
            where.append(" AND DATE(c.fecha_hora) = :fecha");
            params.addValue("fecha", java.sql.Date.valueOf(fecha));
        }
    }

    @Override
    public Optional<Cita> findById(Integer id) {
        return jdbc.query(BASE_SELECT + " WHERE c.id = :id",
                Map.of("id", id), ROW_MAPPER).stream().findFirst();
    }

    @Override
    public void save(Cita cita) {
        if (cita.getId() == null) {
            insert(cita);
        } else {
            update(cita);
        }
    }

    private void insert(Cita c) {
        String sql = """
                INSERT INTO cita (id_mascota, id_veterinario, id_estado_cita, fecha_hora, motivo, observaciones)
                VALUES (:idMascota, :idVeterinario, :idEstadoCita, :fechaHora, :motivo, :observaciones)
                """;
        jdbc.update(sql, toParams(c));
    }

    private void update(Cita c) {
        String sql = """
                UPDATE cita
                   SET id_mascota     = :idMascota,
                       id_veterinario = :idVeterinario,
                       id_estado_cita = :idEstadoCita,
                       fecha_hora     = :fechaHora,
                       motivo         = :motivo,
                       observaciones  = :observaciones
                 WHERE id = :id
                """;
        jdbc.update(sql, toParams(c));
    }

    private MapSqlParameterSource toParams(Cita c) {
        return new MapSqlParameterSource()
                .addValue("id",            c.getId())
                .addValue("idMascota",     c.getIdMascota())
                .addValue("idVeterinario", c.getIdVeterinario())
                .addValue("idEstadoCita",  c.getIdEstadoCita())
                .addValue("fechaHora",     c.getFechaHora() != null
                                           ? Timestamp.valueOf(c.getFechaHora()) : null)
                .addValue("motivo",        c.getMotivo())
                .addValue("observaciones", c.getObservaciones());
    }
}
