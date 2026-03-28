package com.example.petsalud.repository;

import com.example.petsalud.model.DashboardCitaRow;
import com.example.petsalud.model.DashboardConsultaRow;
import com.example.petsalud.model.DashboardStats;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public class DashboardJdbcRepository implements DashboardRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public DashboardJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public DashboardStats stats() {
        String sql = """
                SELECT
                  (SELECT COUNT(*) FROM cita       WHERE DATE(fecha_hora) = CURDATE()) AS citas_hoy,
                  (SELECT COUNT(*) FROM veterinario WHERE activo = 1)                  AS veterinarios_activos,
                  (SELECT COUNT(*) FROM mascota     WHERE activo = 1)                  AS mascotas_registradas,
                  (SELECT COUNT(*) FROM propietario)                                   AS propietarios
                """;
        return jdbc.queryForObject(sql, Map.of(), (rs, rowNum) -> {
            DashboardStats s = new DashboardStats();
            s.setCitasHoy(rs.getLong("citas_hoy"));
            s.setVeterinariosActivos(rs.getLong("veterinarios_activos"));
            s.setMascotasRegistradas(rs.getLong("mascotas_registradas"));
            s.setPropietarios(rs.getLong("propietarios"));
            return s;
        });
    }

    @Override
    public List<DashboardCitaRow> citasHoy(int limit) {
        String sql = """
                   SELECT c.id,
                          con.id                              AS id_consulta,
                          m.nombre                            AS nombre_mascota,
                          m.foto_url                          AS foto_url_mascota,
                          e.nombre                            AS nombre_especie,
                          CONCAT(p.apellido, ', ', p.nombre)  AS nombre_propietario,
                          c.fecha_hora,
                          ec.nombre                           AS nombre_estado_cita
                     FROM cita c
                     JOIN mascota     m  ON m.id  = c.id_mascota
                     JOIN especie     e  ON e.id  = m.id_especie
                     JOIN propietario p  ON p.id  = m.id_propietario
                     JOIN estado_cita ec ON ec.id = c.id_estado_cita
                LEFT JOIN consulta   con ON con.id_cita = c.id
                    WHERE DATE(c.fecha_hora) = CURDATE()
                 ORDER BY c.fecha_hora ASC
                    LIMIT :limit
                """;
        return jdbc.query(sql, Map.of("limit", limit), (rs, rowNum) -> {
            DashboardCitaRow row = new DashboardCitaRow();
            row.setId(rs.getInt("id"));
            int idConsulta = rs.getInt("id_consulta");
            if (!rs.wasNull()) row.setIdConsulta(idConsulta);
            row.setNombreMascota(rs.getString("nombre_mascota"));
            row.setFotoUrlMascota(rs.getString("foto_url_mascota"));
            row.setNombreEspecie(rs.getString("nombre_especie"));
            row.setNombrePropietario(rs.getString("nombre_propietario"));
            Timestamp ts = rs.getTimestamp("fecha_hora");
            if (ts != null) row.setFechaHora(ts.toLocalDateTime());
            row.setNombreEstadoCita(rs.getString("nombre_estado_cita"));
            return row;
        });
    }

    @Override
    public List<DashboardConsultaRow> consultasRecientes(int limit) {
        String sql = """
                   SELECT con.id,
                          m.nombre                            AS nombre_mascota,
                          m.foto_url                          AS foto_url_mascota,
                          e.nombre                            AS nombre_especie,
                          CONCAT(p.apellido, ', ', p.nombre)  AS nombre_propietario,
                          CONCAT(v.apellido, ', ', v.nombre)  AS nombre_veterinario,
                          con.fecha_hora,
                          con.diagnostico
                     FROM consulta    con
                     JOIN cita        c  ON c.id  = con.id_cita
                     JOIN mascota     m  ON m.id  = c.id_mascota
                     JOIN especie     e  ON e.id  = m.id_especie
                     JOIN propietario p  ON p.id  = m.id_propietario
                     JOIN veterinario v  ON v.id  = c.id_veterinario
                 ORDER BY con.fecha_hora DESC
                    LIMIT :limit
                """;
        return jdbc.query(sql, Map.of("limit", limit), (rs, rowNum) -> {
            DashboardConsultaRow row = new DashboardConsultaRow();
            row.setId(rs.getInt("id"));
            row.setNombreMascota(rs.getString("nombre_mascota"));
            row.setFotoUrlMascota(rs.getString("foto_url_mascota"));
            row.setNombreEspecie(rs.getString("nombre_especie"));
            row.setNombrePropietario(rs.getString("nombre_propietario"));
            row.setNombreVeterinario(rs.getString("nombre_veterinario"));
            Timestamp ts = rs.getTimestamp("fecha_hora");
            if (ts != null) row.setFechaHora(ts.toLocalDateTime());
            row.setDiagnostico(rs.getString("diagnostico"));
            return row;
        });
    }
}
