package com.example.petsalud.service;

import com.example.petsalud.model.Mascota;

import java.util.List;

public interface MascotaService {

    List<Mascota> search(String q, Integer idEspecie, String sexo, Boolean activo);

    Mascota findById(Integer id);

    void save(Mascota mascota);

    void toggleActivo(Integer id);
}
