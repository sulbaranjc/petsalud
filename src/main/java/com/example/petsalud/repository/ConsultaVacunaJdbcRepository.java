package com.example.petsalud.repository;

import com.example.petsalud.model.ConsultaVacuna;
import com.example.petsalud.model.Page;
import com.example.petsalud.model.VacunacionRow;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ConsultaVacunaJdbcRepository implements ConsultaVacunaRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public ConsultaVacunaJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<ConsultaVacuna> ROW_MAPPER = (rs, rowNum) -> {
        ConsultaVacuna cv = new ConsultaVacuna();
        cv.setId(rs.getInt("id"));
        cv.setIdConsulta(rs.getInt("id_consulta"));
        cv.setIdVacuna(rs.getInt("id_vacuna"));
        cv.setLote(rs.getString("lote"));
        cv.setObservaciones(rs.getString("observaciones"));
        cv.setNombreVacuna(rs.getString("nombre_vacuna"));

        Date pd = rs.getDate("proxima_dosis");
        if (pd != null) cv.setProximaDosis(pd.toLocalDate());

        return cv;
    };

    @Override
    public void insert(ConsultaVacuna cv) {
        String sql = """
                INSERT INTO consulta_vacuna
                    (id_consulta, id_vacuna, proxima_dosis, lote, observaciones)
                VALUES
                    (:idConsulta, :idVacuna, :proximaDosis, :lote, :observaciones)
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("idConsulta",   cv.getIdConsulta())
                .addValue("idVacuna",     cv.getIdVacuna())
                .addValue("proximaDosis", cv.getProximaDosis() != null ? Date.valueOf(cv.getProximaDosis()) : null)
                .addValue("lote",         cv.getLote())
                .addValue("observaciones", cv.getObservaciones()));
    }

    // ── Reporte de vacunaciones ───────────────────────────────────────────────

    private static final String REPORT_FROM = """
               FROM consulta_vacuna cv
               JOIN vacuna      vac ON vac.id  = cv.id_vacuna
               JOIN consulta    con ON con.id  = cv.id_consulta
               JOIN cita        ci  ON ci.id   = con.id_cita
               JOIN mascota     m   ON m.id    = ci.id_mascota
               JOIN especie     e   ON e.id    = m.id_especie
               JOIN propietario p   ON p.id    = m.id_propietario
               JOIN veterinario v   ON v.id    = ci.id_veterinario
            """;

    private static final String REPORT_SELECT = """
            SELECT cv.id,
                   con.id                              AS id_consulta,
                   m.nombre                            AS nombre_mascota,
                   e.nombre                            AS nombre_especie,
                   CONCAT(p.apellido, ', ', p.nombre)  AS nombre_propietario,
                   vac.nombre                          AS nombre_vacuna,
                   CONCAT(v.apellido, ', ', v.nombre)  AS nombre_veterinario,
                   con.fecha_hora                      AS fecha_consulta,
                   cv.proxima_dosis,
                   cv.lote,
                   cv.observaciones
            """ + REPORT_FROM;

    private static final Map<String, String[]> SORT_COLS = Map.of(
            "fecha_consulta", new String[]{"con.fecha_hora"},
            "mascota",        new String[]{"m.nombre"},
            "proxima_dosis",  new String[]{"cv.proxima_dosis"}
    );

    private String buildOrderBy(String sortBy, String sortDir) {
        String[] cols = SORT_COLS.getOrDefault(sortBy, SORT_COLS.get("fecha_consulta"));
        String dir = "asc".equalsIgnoreCase(sortDir) ? "ASC" : "DESC";
        return " ORDER BY " + Arrays.stream(cols)
                .map(c -> c + " " + dir)
                .collect(Collectors.joining(", "));
    }

    private static final RowMapper<VacunacionRow> REPORT_MAPPER = (rs, rowNum) -> {
        VacunacionRow row = new VacunacionRow();
        row.setId(rs.getInt("id"));
        row.setIdConsulta(rs.getInt("id_consulta"));
        row.setNombreMascota(rs.getString("nombre_mascota"));
        row.setNombreEspecie(rs.getString("nombre_especie"));
        row.setNombrePropietario(rs.getString("nombre_propietario"));
        row.setNombreVacuna(rs.getString("nombre_vacuna"));
        row.setNombreVeterinario(rs.getString("nombre_veterinario"));
        Timestamp ts = rs.getTimestamp("fecha_consulta");
        if (ts != null) row.setFechaConsulta(ts.toLocalDateTime());
        Date pd = rs.getDate("proxima_dosis");
        if (pd != null) row.setProximaDosis(pd.toLocalDate());
        row.setLote(rs.getString("lote"));
        row.setObservaciones(rs.getString("observaciones"));
        return row;
    };

    @Override
    public Page<VacunacionRow> searchReport(String q, Integer idVacuna, String proximaDosis,
                                            int page, int pageSize, String sortBy, String sortDir) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (q != null && !q.isBlank()) {
            where.append("""
                     AND (m.nombre    LIKE :q
                          OR p.nombre    LIKE :q
                          OR p.apellido  LIKE :q
                          OR vac.nombre  LIKE :q)
                    """);
            params.addValue("q", "%" + q.trim() + "%");
        }
        if (idVacuna != null) {
            where.append(" AND cv.id_vacuna = :idVacuna");
            params.addValue("idVacuna", idVacuna);
        }
        if ("vencida".equals(proximaDosis)) {
            where.append(" AND cv.proxima_dosis < CURDATE()");
        } else if ("proximos30".equals(proximaDosis)) {
            where.append(" AND cv.proxima_dosis BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)");
        }

        Long total = jdbc.queryForObject("SELECT COUNT(*)" + REPORT_FROM + where, params, Long.class);

        int offset = (page - 1) * pageSize;
        params.addValue("pageSize", pageSize).addValue("offset", offset);
        List<VacunacionRow> content = jdbc.query(
                REPORT_SELECT + where + buildOrderBy(sortBy, sortDir)
                        + " LIMIT :pageSize OFFSET :offset",
                params, REPORT_MAPPER);

        return new Page<>(content, page, pageSize, total != null ? total : 0L);
    }

    @Override
    public List<ConsultaVacuna> findByIdConsulta(Integer idConsulta) {
        String sql = """
                SELECT cv.id, cv.id_consulta, cv.id_vacuna,
                       cv.proxima_dosis, cv.lote, cv.observaciones,
                       vac.nombre AS nombre_vacuna
                  FROM consulta_vacuna cv
                  JOIN vacuna vac ON vac.id = cv.id_vacuna
                 WHERE cv.id_consulta = :id
                 ORDER BY cv.id ASC
                """;
        return jdbc.query(sql, Map.of("id", idConsulta), ROW_MAPPER);
    }
}
