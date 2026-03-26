package com.example.petsalud.repository;

import com.example.petsalud.model.Mascota;

import java.util.List;
import java.util.Optional;

public interface MascotaRepository {

    List<Mascota> search(String q, Integer idEspecie, String sexo, Boolean activo);

    Optional<Mascota> findById(Integer id);

    void save(Mascota mascota);
}
