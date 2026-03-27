package com.example.petsalud.repository;

import com.example.petsalud.model.ConsultaMedicamento;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ConsultaMedicamentoJdbcRepository implements ConsultaMedicamentoRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public ConsultaMedicamentoJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<ConsultaMedicamento> ROW_MAPPER = (rs, rowNum) -> {
        ConsultaMedicamento cm = new ConsultaMedicamento();
        cm.setId(rs.getInt("id"));
        cm.setIdConsulta(rs.getInt("id_consulta"));
        cm.setIdMedicamento(rs.getInt("id_medicamento"));
        cm.setDosis(rs.getString("dosis"));
        cm.setFrecuencia(rs.getString("frecuencia"));
        cm.setObservaciones(rs.getString("observaciones"));
        cm.setNombreMedicamento(rs.getString("nombre_medicamento"));
        return cm;
    };

    @Override
    public void insert(ConsultaMedicamento cm) {
        String sql = """
                INSERT INTO consulta_medicamento
                    (id_consulta, id_medicamento, dosis, frecuencia, observaciones)
                VALUES
                    (:idConsulta, :idMedicamento, :dosis, :frecuencia, :observaciones)
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("idConsulta",    cm.getIdConsulta())
                .addValue("idMedicamento", cm.getIdMedicamento())
                .addValue("dosis",         cm.getDosis())
                .addValue("frecuencia",    cm.getFrecuencia())
                .addValue("observaciones", cm.getObservaciones()));
    }

    @Override
    public List<ConsultaMedicamento> findByIdConsulta(Integer idConsulta) {
        String sql = """
                SELECT cm.id, cm.id_consulta, cm.id_medicamento,
                       cm.dosis, cm.frecuencia, cm.observaciones,
                       med.nombre AS nombre_medicamento
                  FROM consulta_medicamento cm
                  JOIN medicamento med ON med.id = cm.id_medicamento
                 WHERE cm.id_consulta = :id
                 ORDER BY cm.id ASC
                """;
        return jdbc.query(sql, Map.of("id", idConsulta), ROW_MAPPER);
    }
}
