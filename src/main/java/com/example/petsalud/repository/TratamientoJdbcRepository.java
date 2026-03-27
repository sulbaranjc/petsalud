package com.example.petsalud.repository;

import com.example.petsalud.model.Tratamiento;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Repository
public class TratamientoJdbcRepository implements TratamientoRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public TratamientoJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Tratamiento> ROW_MAPPER = (rs, rowNum) -> {
        Tratamiento t = new Tratamiento();
        t.setId(rs.getInt("id"));
        t.setIdConsulta(rs.getInt("id_consulta"));
        t.setDescripcion(rs.getString("descripcion"));
        t.setObservaciones(rs.getString("observaciones"));

        Date fi = rs.getDate("fecha_inicio");
        if (fi != null) t.setFechaInicio(fi.toLocalDate());

        Date ff = rs.getDate("fecha_fin");
        if (ff != null) t.setFechaFin(ff.toLocalDate());

        return t;
    };

    @Override
    public void insert(Tratamiento t) {
        String sql = """
                INSERT INTO tratamiento (id_consulta, descripcion, fecha_inicio, fecha_fin, observaciones)
                VALUES (:idConsulta, :descripcion, :fechaInicio, :fechaFin, :observaciones)
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("idConsulta",   t.getIdConsulta())
                .addValue("descripcion",  t.getDescripcion())
                .addValue("fechaInicio",  t.getFechaInicio() != null ? Date.valueOf(t.getFechaInicio()) : null)
                .addValue("fechaFin",     t.getFechaFin()    != null ? Date.valueOf(t.getFechaFin())    : null)
                .addValue("observaciones", t.getObservaciones()));
    }

    @Override
    public List<Tratamiento> findByIdConsulta(Integer idConsulta) {
        return jdbc.query(
                "SELECT * FROM tratamiento WHERE id_consulta = :id ORDER BY id ASC",
                Map.of("id", idConsulta), ROW_MAPPER);
    }
}
