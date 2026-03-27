package com.example.petsalud.repository;

import com.example.petsalud.model.Mascota;
import com.example.petsalud.model.Page;

import java.util.List;
import java.util.Optional;

public interface MascotaRepository {

    /** Lista de mascotas activas para usar en formularios de citas. */
    List<Mascota> findAllActivas();

    Page<Mascota> search(String q, Integer idEspecie, Integer idPropietario, String sexo, Boolean activo,
                         int page, int pageSize, String sortBy, String sortDir);

    Optional<Mascota> findById(Integer id);

    void save(Mascota mascota);
}
