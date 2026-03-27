package com.example.petsalud.repository;

import com.example.petsalud.model.Cita;
import com.example.petsalud.model.Page;

import java.time.LocalDate;
import java.util.Optional;

public interface CitaRepository {

    Page<Cita> search(String q, Integer idEstadoCita, Integer idVeterinario, LocalDate fecha,
                      int page, int pageSize, String sortBy, String sortDir);

    Optional<Cita> findById(Integer id);

    void save(Cita cita);
}
