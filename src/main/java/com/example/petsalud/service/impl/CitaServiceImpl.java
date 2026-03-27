package com.example.petsalud.service.impl;

import com.example.petsalud.model.Cita;
import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.EstadoCita;
import com.example.petsalud.repository.CitaRepository;
import com.example.petsalud.service.CitaService;
import com.example.petsalud.service.catalogo.EstadoCitaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CitaServiceImpl implements CitaService {

    private final CitaRepository     citaRepository;
    private final EstadoCitaService  estadoCitaService;

    public CitaServiceImpl(CitaRepository citaRepository, EstadoCitaService estadoCitaService) {
        this.citaRepository    = citaRepository;
        this.estadoCitaService = estadoCitaService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Cita> search(String q, Integer idEstadoCita, Integer idVeterinario, LocalDate fecha,
                              int page, int pageSize, String sortBy, String sortDir) {
        return citaRepository.search(q, idEstadoCita, idVeterinario, fecha, page, pageSize, sortBy, sortDir);
    }

    @Override
    @Transactional(readOnly = true)
    public Cita findById(Integer id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cita no encontrada con id: " + id));
    }

    @Override
    public void save(Cita cita) {
        citaRepository.save(cita);
    }

    @Override
    public void cancelar(Integer id) {
        Cita cita = findById(id);
        EstadoCita cancelada = estadoCitaService.findByNombre("Cancelada")
                .orElseThrow(() -> new IllegalStateException("Estado 'Cancelada' no encontrado en el catálogo"));
        cita.setIdEstadoCita(cancelada.getId());
        citaRepository.save(cita);
    }
}
