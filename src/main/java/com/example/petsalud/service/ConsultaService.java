package com.example.petsalud.service;

import com.example.petsalud.model.*;

import java.util.List;
import java.util.Optional;

public interface ConsultaService {

    Page<Consulta> search(String q, Integer idVeterinario,
                          int page, int pageSize, String sortBy, String sortDir);

    Consulta findById(Integer id);

    Optional<Consulta> findByIdCita(Integer idCita);

    /** Guarda la consulta completa (datos clínicos + tratamientos + medicamentos + vacunas)
     *  y marca la cita como Completada. Todo en una sola transacción. */
    void guardar(ConsultaForm form);

    List<Tratamiento>         findTratamientos(Integer idConsulta);
    List<ConsultaMedicamento> findMedicamentos(Integer idConsulta);
    List<ConsultaVacuna>      findVacunas(Integer idConsulta);
}
