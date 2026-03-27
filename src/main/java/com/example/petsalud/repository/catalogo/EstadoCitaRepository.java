package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.catalogo.EstadoCita;

import java.util.List;
import java.util.Optional;

public interface EstadoCitaRepository {

    List<EstadoCita> findAll();

    Optional<EstadoCita> findByNombre(String nombre);
}
