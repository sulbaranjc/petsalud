package com.example.petsalud.service;

import com.example.petsalud.model.Mascota;
import com.example.petsalud.model.Page;

import java.util.List;

public interface MascotaService {

    /** Lista de mascotas activas para usar en formularios de citas. */
    List<Mascota> findAllActivas();

    Page<Mascota> search(String q, Integer idEspecie, Integer idPropietario, String sexo, Boolean activo,
                         int page, int pageSize, String sortBy, String sortDir);

    Mascota findById(Integer id);

    void save(Mascota mascota);

    void toggleActivo(Integer id);
}
