package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.catalogo.Raza;

import java.util.List;
import java.util.Optional;

public interface RazaRepository {

    List<Raza> findAllByOrderByNombreAsc();

    List<Raza> search(String nombre, Integer idEspecie, Boolean activo);

    Optional<Raza> findById(Integer id);

    void save(Raza raza);
}
