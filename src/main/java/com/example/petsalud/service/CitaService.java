package com.example.petsalud.service;

import com.example.petsalud.model.Cita;
import com.example.petsalud.model.Page;

import java.time.LocalDate;

public interface CitaService {

    Page<Cita> search(String q, Integer idEstadoCita, Integer idVeterinario, LocalDate fecha,
                      int page, int pageSize, String sortBy, String sortDir);

    Cita findById(Integer id);

    void save(Cita cita);

    /** Cambia el estado de la cita a "Cancelada". */
    void cancelar(Integer id);
}
