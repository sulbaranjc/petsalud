package com.example.petsalud.repository;

import com.example.petsalud.model.Propietario;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PropietarioJdbcRepository implements PropietarioRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public PropietarioJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Propietario> ROW_MAPPER = (rs, rowNum) -> {
        Propietario p = new Propietario();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setApellido(rs.getString("apellido"));
        p.setDocumento(rs.getString("documento"));
        p.setTelefono(rs.getString("telefono"));
        p.setEmail(rs.getString("email"));
        p.setDireccion(rs.getString("direccion"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    };

    @Override
    public List<Propietario> findAllByOrderByApellidoNombreAsc() {
        String sql = """
                SELECT id, nombre, apellido, documento, telefono, email, direccion, activo
                  FROM propietario
                 ORDER BY apellido ASC, nombre ASC
                """;
        return jdbc.query(sql, ROW_MAPPER);
    }

    @Override
    public Optional<Propietario> findById(Integer id) {
        String sql = """
                SELECT id, nombre, apellido, documento, telefono, email, direccion, activo
                  FROM propietario
                 WHERE id = :id
                """;
        return jdbc.query(sql, Map.of("id", id), ROW_MAPPER).stream().findFirst();
    }

    @Override
    public void save(Propietario propietario) {
        if (propietario.getId() == null) {
            insert(propietario);
        } else {
            update(propietario);
        }
    }

    private void insert(Propietario p) {
        String sql = """
                INSERT INTO propietario (nombre, apellido, documento, telefono, email, direccion, activo)
                VALUES (:nombre, :apellido, :documento, :telefono, :email, :direccion, :activo)
                """;
        jdbc.update(sql, toParams(p));
    }

    private void update(Propietario p) {
        String sql = """
                UPDATE propietario
                   SET nombre    = :nombre,
                       apellido  = :apellido,
                       documento = :documento,
                       telefono  = :telefono,
                       email     = :email,
                       direccion = :direccion,
                       activo    = :activo
                 WHERE id = :id
                """;
        jdbc.update(sql, toParams(p));
    }

    private MapSqlParameterSource toParams(Propietario p) {
        return new MapSqlParameterSource()
                .addValue("id",        p.getId())
                .addValue("nombre",    p.getNombre())
                .addValue("apellido",  p.getApellido())
                .addValue("documento", p.getDocumento())
                .addValue("telefono",  p.getTelefono())
                .addValue("email",     p.getEmail())
                .addValue("direccion", p.getDireccion())
                .addValue("activo",    p.isActivo());
    }
}
