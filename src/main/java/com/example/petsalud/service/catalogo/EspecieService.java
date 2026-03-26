package com.example.petsalud.service.catalogo;

import com.example.petsalud.model.catalogo.Especie;

import java.util.List;

public interface EspecieService {

    List<Especie> findAll();

    Especie findById(Integer id);

    void save(Especie especie);

    void toggleActivo(Integer id);
}
