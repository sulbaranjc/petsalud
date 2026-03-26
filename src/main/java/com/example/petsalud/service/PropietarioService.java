package com.example.petsalud.service;

import com.example.petsalud.model.Propietario;

import java.util.List;

public interface PropietarioService {

    List<Propietario> findAll();

    Propietario findById(Integer id);

    void save(Propietario propietario);

    void toggleActivo(Integer id);
}
