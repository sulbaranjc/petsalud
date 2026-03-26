package com.example.petsalud.repository;

import com.example.petsalud.model.Propietario;

import java.util.List;
import java.util.Optional;

public interface PropietarioRepository {

    List<Propietario> findAllByOrderByApellidoNombreAsc();

    Optional<Propietario> findById(Integer id);

    void save(Propietario propietario);
}
