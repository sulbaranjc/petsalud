package com.example.petsalud.repository;

import com.example.petsalud.model.ConsultaMedicamento;

import java.util.List;

public interface ConsultaMedicamentoRepository {

    void insert(ConsultaMedicamento cm);

    List<ConsultaMedicamento> findByIdConsulta(Integer idConsulta);
}
