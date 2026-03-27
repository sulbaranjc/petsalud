package com.example.petsalud.repository;

import com.example.petsalud.model.ConsultaVacuna;

import java.util.List;

public interface ConsultaVacunaRepository {

    void insert(ConsultaVacuna cv);

    List<ConsultaVacuna> findByIdConsulta(Integer idConsulta);
}
