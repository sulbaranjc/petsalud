package com.example.petsalud.repository;

import com.example.petsalud.model.Mascota;
import com.example.petsalud.model.Page;

import java.util.Optional;

public interface MascotaRepository {

    Page<Mascota> search(String q, Integer idEspecie, String sexo, Boolean activo,
                         int page, int pageSize);

    Optional<Mascota> findById(Integer id);

    void save(Mascota mascota);
}
