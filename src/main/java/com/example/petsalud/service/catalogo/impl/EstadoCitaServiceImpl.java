package com.example.petsalud.service.catalogo.impl;

import com.example.petsalud.model.catalogo.EstadoCita;
import com.example.petsalud.repository.catalogo.EstadoCitaRepository;
import com.example.petsalud.service.catalogo.EstadoCitaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EstadoCitaServiceImpl implements EstadoCitaService {

    private final EstadoCitaRepository estadoCitaRepository;

    public EstadoCitaServiceImpl(EstadoCitaRepository estadoCitaRepository) {
        this.estadoCitaRepository = estadoCitaRepository;
    }

    @Override
    public List<EstadoCita> findAll() {
        return estadoCitaRepository.findAll();
    }

    @Override
    public Optional<EstadoCita> findByNombre(String nombre) {
        return estadoCitaRepository.findByNombre(nombre);
    }
}
