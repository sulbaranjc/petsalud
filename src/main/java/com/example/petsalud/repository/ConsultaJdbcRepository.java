package com.example.petsalud.repository;

import com.example.petsalud.model.Consulta;
import com.example.petsalud.model.Page;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ConsultaJdbcRepository implements ConsultaRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public ConsultaJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Consulta> ROW_MAPPER = (rs, rowNum) -> {
        Consulta c = new Consulta();
        c.setId(rs.getInt("id"));
        c.setIdCita(rs.getInt("id_cita"));
        c.setAnamnesis(rs.getString("anamnesis"));
        c.setExamenFisico(rs.getString("examen_fisico"));
        c.setDiagnostico(rs.getString("diagnostico"));
        c.setObservaciones(rs.getString("observaciones"));
        c.setNombreMascota(rs.getString("nombre_mascota"));
        c.setFotoUrlMascota(rs.getString("foto_url_mascota"));
        c.setNombreEspecie(rs.getString("nombre_especie"));
        c.setNombrePropietario(rs.getString("nombre_propietario"));
        c.setNombreVeterinario(rs.getString("nombre_veterinario"));

        var peso = rs.getBigDecimal("peso_kg");
        if (peso != null) c.setPesoKg(peso);

        var temp = rs.getBigDecimal("temperatura_c");
        if (temp != null) c.setTemperaturaC(temp);

        int fc = rs.getInt("frecuencia_cardiaca");
        if (!rs.wasNull()) c.setFrecuenciaCardiaca(fc);

        int fr = rs.getInt("frecuencia_resp");
        if (!rs.wasNull()) c.setFrecuenciaResp(fr);

        Timestamp fh = rs.getTimestamp("fecha_hora");
        if (fh != null) c.setFechaHora(fh.toLocalDateTime());

        Timestamp fhCita = rs.getTimestamp("fecha_hora_cita");
        if (fhCita != null) c.setFechaHoraCita(fhCita.toLocalDateTime());

        return c;
    };

    private static final String BASE_FROM = """
              FROM consulta con
              JOIN cita        ci  ON ci.id  = con.id_cita
              JOIN mascota     m   ON m.id   = ci.id_mascota
              JOIN especie     e   ON e.id   = m.id_especie
              JOIN propietario p   ON p.id   = m.id_propietario
              JOIN veterinario v   ON v.id   = ci.id_veterinario
            """;

    private static final String BASE_SELECT = """
            SELECT con.id,
                   con.id_cita,
                   con.fecha_hora,
                   con.peso_kg,
                   con.temperatura_c,
                   con.frecuencia_cardiaca,
                   con.frecuencia_resp,
                   con.anamnesis,
                   con.examen_fisico,
                   con.diagnostico,
                   con.observaciones,
                   ci.fecha_hora                          AS fecha_hora_cita,
                   m.nombre                               AS nombre_mascota,
                   m.foto_url                             AS foto_url_mascota,
                   e.nombre                               AS nombre_especie,
                   CONCAT(p.apellido, ', ', p.nombre)     AS nombre_propietario,
                   CONCAT(v.apellido, ', ', v.nombre)     AS nombre_veterinario
            """ + BASE_FROM;

    private static final Map<String, String[]> SORT_COLUMNS = Map.of(
            "fecha",       new String[]{"con.fecha_hora"},
            "mascota",     new String[]{"m.nombre"},
            "veterinario", new String[]{"v.apellido", "v.nombre"}
    );

    private String buildOrderBy(String sortBy, String sortDir) {
        String[] cols = SORT_COLUMNS.getOrDefault(sortBy, SORT_COLUMNS.get("fecha"));
        String dir = "asc".equalsIgnoreCase(sortDir) ? "ASC" : "DESC";
        return " ORDER BY " + Arrays.stream(cols)
                .map(c -> c + " " + dir)
                .collect(Collectors.joining(", "));
    }

    @Override
    public Page<Consulta> search(String q, Integer idVeterinario,
                                  int page, int pageSize,
                                  String sortBy, String sortDir) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (q != null && !q.isBlank()) {
            where.append("""
                     AND (m.nombre    LIKE :q
                          OR p.nombre     LIKE :q
                          OR p.apellido   LIKE :q
                          OR v.nombre     LIKE :q
                          OR v.apellido   LIKE :q
                          OR con.diagnostico LIKE :q)
                    """);
            params.addValue("q", "%" + q.trim() + "%");
        }
        if (idVeterinario != null) {
            where.append(" AND ci.id_veterinario = :idVeterinario");
            params.addValue("idVeterinario", idVeterinario);
        }

        Long total = jdbc.queryForObject("SELECT COUNT(*)" + BASE_FROM + where, params, Long.class);

        int offset = (page - 1) * pageSize;
        params.addValue("pageSize", pageSize).addValue("offset", offset);
        List<Consulta> content = jdbc.query(
                BASE_SELECT + where + buildOrderBy(sortBy, sortDir)
                        + " LIMIT :pageSize OFFSET :offset",
                params, ROW_MAPPER);

        return new Page<>(content, page, pageSize, total != null ? total : 0L);
    }

    @Override
    public Optional<Consulta> findById(Integer id) {
        return jdbc.query(BASE_SELECT + " WHERE con.id = :id",
                Map.of("id", id), ROW_MAPPER).stream().findFirst();
    }

    @Override
    public Optional<Consulta> findByIdCita(Integer idCita) {
        return jdbc.query(BASE_SELECT + " WHERE con.id_cita = :idCita",
                Map.of("idCita", idCita), ROW_MAPPER).stream().findFirst();
    }

    @Override
    public Integer insert(Consulta c) {
        String sql = """
                INSERT INTO consulta
                    (id_cita, fecha_hora, peso_kg, temperatura_c,
                     frecuencia_cardiaca, frecuencia_resp,
                     anamnesis, examen_fisico, diagnostico, observaciones)
                VALUES
                    (:idCita, NOW(), :pesoKg, :temperaturaC,
                     :frecuenciaCardiaca, :frecuenciaResp,
                     :anamnesis, :examenFisico, :diagnostico, :observaciones)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCita",             c.getIdCita())
                .addValue("pesoKg",             c.getPesoKg())
                .addValue("temperaturaC",       c.getTemperaturaC())
                .addValue("frecuenciaCardiaca", c.getFrecuenciaCardiaca())
                .addValue("frecuenciaResp",     c.getFrecuenciaResp())
                .addValue("anamnesis",          c.getAnamnesis())
                .addValue("examenFisico",       c.getExamenFisico())
                .addValue("diagnostico",        c.getDiagnostico())
                .addValue("observaciones",      c.getObservaciones());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public void actualizarEstadoCita(Integer idCita, Integer idEstadoCita) {
        jdbc.update("UPDATE cita SET id_estado_cita = :estado WHERE id = :id",
                Map.of("estado", idEstadoCita, "id", idCita));
    }
}
