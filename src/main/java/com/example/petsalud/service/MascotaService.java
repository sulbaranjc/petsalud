package com.example.petsalud.service;

import com.example.petsalud.model.Mascota;
import com.example.petsalud.model.Page;

public interface MascotaService {

    Page<Mascota> search(String q, Integer idEspecie, String sexo, Boolean activo,
                         int page, int pageSize);

    Mascota findById(Integer id);

    void save(Mascota mascota);

    void toggleActivo(Integer id);
}
