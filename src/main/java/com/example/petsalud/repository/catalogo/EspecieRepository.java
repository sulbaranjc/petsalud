package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Especie;

import java.util.List;
import java.util.Optional;

public interface EspecieRepository {

    /** Lista completa ordenada; usada en formularios de Raza y Mascota. */
    List<Especie> findAllByOrderByNombreAsc();

    Page<Especie> search(String q, Boolean activo, int page, int pageSize,
                          String sortBy, String sortDir);

    Optional<Especie> findById(Integer id);

    void save(Especie especie);
}
