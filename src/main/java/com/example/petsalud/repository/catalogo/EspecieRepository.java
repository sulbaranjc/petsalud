package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.catalogo.Especie;

import java.util.List;
import java.util.Optional;

public interface EspecieRepository {

    List<Especie> findAllByOrderByNombreAsc();

    Optional<Especie> findById(Integer id);

    void save(Especie especie);
}
