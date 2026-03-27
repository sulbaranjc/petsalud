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
        // total_mascotas solo existe en queries que incluyan la subquery
        try { p.setTotalMascotas(rs.getInt("total_mascotas")); } catch (Exception ignored) {}
        return p;
    };

    private static final String BASE_SELECT = """
            SELECT p.id,
                   p.nombre,
                   p.apellido,
                   p.documento,
                   p.telefono,
                   p.email,
                   p.direccion,
                   p.activo,
                   (SELECT COUNT(*) FROM mascota m WHERE m.id_propietario = p.id) AS total_mascotas
              FROM propietario p
            """;

    @Override
    public List<Propietario> findAllByOrderByApellidoNombreAsc() {
        return jdbc.query(BASE_SELECT + " ORDER BY p.apellido ASC, p.nombre ASC", ROW_MAPPER);
    }

    @Override
    public List<Propietario> search(String q, Boolean activo) {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (q != null && !q.isBlank()) {
            sql.append("""
                    \s AND (p.nombre    LIKE :q
                            OR p.apellido  LIKE :q
                            OR p.documento LIKE :q
                            OR p.email     LIKE :q)
                    """);
            params.addValue("q", "%" + q.trim() + "%");
        }
        if (activo != null) {
            sql.append(" AND p.activo = :activo");
            params.addValue("activo", activo);
        }

        sql.append(" ORDER BY p.apellido ASC, p.nombre ASC");
        return jdbc.query(sql.toString(), params, ROW_MAPPER);
    }

    @Override
    public Optional<Propietario> findById(Integer id) {
        return jdbc.query(BASE_SELECT + " WHERE p.id = :id",
                          Map.of("id", id), ROW_MAPPER).stream().findFirst();
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
