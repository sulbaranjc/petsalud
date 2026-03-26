package com.example.petsalud.service.catalogo;

import com.example.petsalud.model.catalogo.Raza;

import java.util.List;

public interface RazaService {

    List<Raza> findAll();

    Raza findById(Integer id);

    void save(Raza raza);

    void toggleActivo(Integer id);
}
