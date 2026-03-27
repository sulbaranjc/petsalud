package com.example.petsalud.repository;

import com.example.petsalud.model.ConsultaVacuna;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Map;

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
