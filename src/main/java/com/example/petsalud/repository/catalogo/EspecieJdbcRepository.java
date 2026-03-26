package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.catalogo.Especie;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class EspecieJdbcRepository implements EspecieRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public EspecieJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Especie> ROW_MAPPER = (rs, rowNum) -> {
        Especie e = new Especie();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre"));
        e.setDescripcion(rs.getString("descripcion"));
        e.setActivo(rs.getBoolean("activo"));
        return e;
    };

    @Override
    public List<Especie> findAllByOrderByNombreAsc() {
        String sql = "SELECT id, nombre, descripcion, activo FROM especie ORDER BY nombre ASC";
        return jdbc.query(sql, ROW_MAPPER);
    }

    @Override
    public Optional<Especie> findById(Integer id) {
        String sql = "SELECT id, nombre, descripcion, activo FROM especie WHERE id = :id";
        return jdbc.query(sql, Map.of("id", id), ROW_MAPPER)
                   .stream().findFirst();
    }

    @Override
    public void save(Especie especie) {
        if (especie.getId() == null) {
            insert(especie);
        } else {
            update(especie);
        }
    }

    private void insert(Especie especie) {
        String sql = """
                INSERT INTO especie (nombre, descripcion, activo)
                VALUES (:nombre, :descripcion, :activo)
                """;
        jdbc.update(sql, toParams(especie));
    }

    private void update(Especie especie) {
        String sql = """
                UPDATE especie
                   SET nombre      = :nombre,
                       descripcion = :descripcion,
                       activo      = :activo
                 WHERE id = :id
                """;
        jdbc.update(sql, toParams(especie));
    }

    private MapSqlParameterSource toParams(Especie especie) {
        return new MapSqlParameterSource()
                .addValue("id",          especie.getId())
                .addValue("nombre",      especie.getNombre())
                .addValue("descripcion", especie.getDescripcion())
                .addValue("activo",      especie.isActivo());
    }
}
