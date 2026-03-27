package com.example.petsalud.repository;

import com.example.petsalud.model.Tratamiento;

import java.util.List;

public interface TratamientoRepository {

    void insert(Tratamiento tratamiento);

    List<Tratamiento> findByIdConsulta(Integer idConsulta);
}
