package com.example.petsalud.repository;

import com.example.petsalud.model.Mascota;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class MascotaJdbcRepository implements MascotaRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public MascotaJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Mascota> ROW_MAPPER = (rs, rowNum) -> {
        Mascota m = new Mascota();
        m.setId(rs.getInt("id"));
        m.setNombre(rs.getString("nombre"));
        m.setSexo(rs.getString("sexo"));
        m.setColor(rs.getString("color"));
        m.setIdEspecie(rs.getInt("id_especie"));
        m.setIdPropietario(rs.getInt("id_propietario"));
        m.setFotoUrl(rs.getString("foto_url"));
        m.setActivo(rs.getBoolean("activo"));
        m.setNombreEspecie(rs.getString("nombre_especie"));
        m.setNombreRaza(rs.getString("nombre_raza"));
        m.setNombrePropietario(rs.getString("nombre_propietario"));

        Date fechaNac = rs.getDate("fecha_nacimiento");
        if (fechaNac != null) m.setFechaNacimiento(fechaNac.toLocalDate());

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) m.setCreatedAt(createdAt.toLocalDateTime());

        int idRaza = rs.getInt("id_raza");
        if (!rs.wasNull()) m.setIdRaza(idRaza);

        return m;
    };

    private static final String BASE_SELECT = """
            SELECT m.id,
                   m.nombre,
                   m.fecha_nacimiento,
                   m.sexo,
                   m.color,
                   m.id_especie,
                   m.id_raza,
                   m.id_propietario,
                   m.foto_url,
                   m.activo,
                   m.created_at,
                   e.nombre                              AS nombre_especie,
                   r.nombre                              AS nombre_raza,
                   CONCAT(p.apellido, ', ', p.nombre)    AS nombre_propietario
              FROM mascota m
              JOIN especie     e ON e.id = m.id_especie
              JOIN propietario p ON p.id = m.id_propietario
         LEFT JOIN raza        r ON r.id = m.id_raza
            """;

    @Override
    public List<Mascota> search(String q, Integer idEspecie, String sexo, Boolean activo) {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (q != null && !q.isBlank()) {
            sql.append("""
                    \s AND (m.nombre LIKE :q
                            OR p.nombre  LIKE :q
                            OR p.apellido LIKE :q)
                    """);
            params.addValue("q", "%" + q.trim() + "%");
        }
        if (idEspecie != null) {
            sql.append(" AND m.id_especie = :idEspecie");
            params.addValue("idEspecie", idEspecie);
        }
        if (sexo != null && !sexo.isBlank()) {
            sql.append(" AND m.sexo = :sexo");
            params.addValue("sexo", sexo);
        }
        if (activo != null) {
            sql.append(" AND m.activo = :activo");
            params.addValue("activo", activo);
        }

        sql.append(" ORDER BY m.nombre ASC");
        return jdbc.query(sql.toString(), params, ROW_MAPPER);
    }

    @Override
    public Optional<Mascota> findById(Integer id) {
        String sql = BASE_SELECT + " WHERE m.id = :id";
        return jdbc.query(sql, Map.of("id", id), ROW_MAPPER).stream().findFirst();
    }

    @Override
    public void save(Mascota mascota) {
        if (mascota.getId() == null) {
            insert(mascota);
        } else {
            update(mascota);
        }
    }

    private void insert(Mascota m) {
        String sql = """
                INSERT INTO mascota
                    (nombre, fecha_nacimiento, sexo, color,
                     id_especie, id_raza, id_propietario, foto_url, activo)
                VALUES
                    (:nombre, :fechaNacimiento, :sexo, :color,
                     :idEspecie, :idRaza, :idPropietario, :fotoUrl, :activo)
                """;
        jdbc.update(sql, toParams(m));
    }

    private void update(Mascota m) {
        String sql = """
                UPDATE mascota
                   SET nombre           = :nombre,
                       fecha_nacimiento = :fechaNacimiento,
                       sexo             = :sexo,
                       color            = :color,
                       id_especie       = :idEspecie,
                       id_raza          = :idRaza,
                       id_propietario   = :idPropietario,
                       foto_url         = :fotoUrl,
                       activo           = :activo
                 WHERE id = :id
                """;
        jdbc.update(sql, toParams(m));
    }

    private MapSqlParameterSource toParams(Mascota m) {
        return new MapSqlParameterSource()
                .addValue("id",              m.getId())
                .addValue("nombre",          m.getNombre())
                .addValue("fechaNacimiento", m.getFechaNacimiento())
                .addValue("sexo",            m.getSexo())
                .addValue("color",           m.getColor())
                .addValue("idEspecie",       m.getIdEspecie())
                .addValue("idRaza",          m.getIdRaza())
                .addValue("idPropietario",   m.getIdPropietario())
                .addValue("fotoUrl",         m.getFotoUrl())
                .addValue("activo",          m.isActivo());
    }
}
