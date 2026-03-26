package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.catalogo.Raza;

import java.util.List;
import java.util.Optional;

public interface RazaRepository {

    List<Raza> findAllByOrderByNombreAsc();

    Optional<Raza> findById(Integer id);

    void save(Raza raza);
}
