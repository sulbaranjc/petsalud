package com.example.petsalud.service;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.Propietario;

import java.util.List;

public interface PropietarioService {

    List<Propietario> findAll();

    Page<Propietario> search(String q, Boolean activo, int page, int pageSize);

    Propietario findById(Integer id);

    void save(Propietario propietario);

    void toggleActivo(Integer id);
}
