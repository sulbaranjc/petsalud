package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.catalogo.EstadoCita;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class EstadoCitaJdbcRepository implements EstadoCitaRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public EstadoCitaJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<EstadoCita> ROW_MAPPER = (rs, rowNum) -> {
        EstadoCita e = new EstadoCita();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre"));
        return e;
    };

    @Override
    public List<EstadoCita> findAll() {
        return jdbc.query("SELECT id, nombre FROM estado_cita ORDER BY id ASC", ROW_MAPPER);
    }

    @Override
    public Optional<EstadoCita> findByNombre(String nombre) {
        List<EstadoCita> result = jdbc.query(
                "SELECT id, nombre FROM estado_cita WHERE nombre = :nombre",
                Map.of("nombre", nombre), ROW_MAPPER);
        return result.stream().findFirst();
    }
}
