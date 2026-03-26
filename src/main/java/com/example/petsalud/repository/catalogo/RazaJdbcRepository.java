package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.catalogo.Raza;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class RazaJdbcRepository implements RazaRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public RazaJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Raza> ROW_MAPPER = (rs, rowNum) -> {
        Raza r = new Raza();
        r.setId(rs.getInt("id"));
        r.setNombre(rs.getString("nombre"));
        r.setIdEspecie(rs.getInt("id_especie"));
        r.setActivo(rs.getBoolean("activo"));
        r.setNombreEspecie(rs.getString("nombre_especie"));
        return r;
    };

    @Override
    public List<Raza> findAllByOrderByNombreAsc() {
        String sql = """
                SELECT r.id,
                       r.nombre,
                       r.id_especie,
                       r.activo,
                       e.nombre AS nombre_especie
                  FROM raza r
                  JOIN especie e ON e.id = r.id_especie
                 ORDER BY r.nombre ASC
                """;
        return jdbc.query(sql, ROW_MAPPER);
    }

    @Override
    public List<Raza> search(String nombre, Integer idEspecie, Boolean activo) {
        StringBuilder sql = new StringBuilder("""
                SELECT r.id,
                       r.nombre,
                       r.id_especie,
                       r.activo,
                       e.nombre AS nombre_especie
                  FROM raza r
                  JOIN especie e ON e.id = r.id_especie
                 WHERE 1=1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (nombre != null && !nombre.isBlank()) {
            sql.append(" AND r.nombre LIKE :nombre");
            params.addValue("nombre", "%" + nombre.trim() + "%");
        }
        if (idEspecie != null) {
            sql.append(" AND r.id_especie = :idEspecie");
            params.addValue("idEspecie", idEspecie);
        }
        if (activo != null) {
            sql.append(" AND r.activo = :activo");
            params.addValue("activo", activo);
        }

        sql.append(" ORDER BY r.nombre ASC");
        return jdbc.query(sql.toString(), params, ROW_MAPPER);
    }

    @Override
    public Optional<Raza> findById(Integer id) {
        String sql = """
                SELECT r.id,
                       r.nombre,
                       r.id_especie,
                       r.activo,
                       e.nombre AS nombre_especie
                  FROM raza r
                  JOIN especie e ON e.id = r.id_especie
                 WHERE r.id = :id
                """;
        return jdbc.query(sql, Map.of("id", id), ROW_MAPPER)
                   .stream().findFirst();
    }

    @Override
    public void save(Raza raza) {
        if (raza.getId() == null) {
            insert(raza);
        } else {
            update(raza);
        }
    }

    private void insert(Raza raza) {
        String sql = """
                INSERT INTO raza (nombre, id_especie, activo)
                VALUES (:nombre, :idEspecie, :activo)
                """;
        jdbc.update(sql, toParams(raza));
    }

    private void update(Raza raza) {
        String sql = """
                UPDATE raza
                   SET nombre     = :nombre,
                       id_especie = :idEspecie,
                       activo     = :activo
                 WHERE id = :id
                """;
        jdbc.update(sql, toParams(raza));
    }

    private MapSqlParameterSource toParams(Raza raza) {
        return new MapSqlParameterSource()
                .addValue("id",        raza.getId())
                .addValue("nombre",    raza.getNombre())
                .addValue("idEspecie", raza.getIdEspecie())
                .addValue("activo",    raza.isActivo());
    }
}
