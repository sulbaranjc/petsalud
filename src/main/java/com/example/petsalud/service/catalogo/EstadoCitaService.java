package com.example.petsalud.service.catalogo;

import com.example.petsalud.model.catalogo.EstadoCita;

import java.util.List;
import java.util.Optional;

public interface EstadoCitaService {

    List<EstadoCita> findAll();

    Optional<EstadoCita> findByNombre(String nombre);
}
