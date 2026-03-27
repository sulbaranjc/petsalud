package com.example.petsalud.repository;

import com.example.petsalud.model.Consulta;
import com.example.petsalud.model.Page;

import java.util.Optional;

public interface ConsultaRepository {

    Page<Consulta> search(String q, Integer idVeterinario,
                          int page, int pageSize, String sortBy, String sortDir);

    Optional<Consulta> findById(Integer id);

    Optional<Consulta> findByIdCita(Integer idCita);

    /** Inserta la consulta y devuelve el ID generado. */
    Integer insert(Consulta consulta);

    void actualizarEstadoCita(Integer idCita, Integer idEstadoCita);
}
