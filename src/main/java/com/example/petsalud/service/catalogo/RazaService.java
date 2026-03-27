package com.example.petsalud.service.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Raza;

import java.util.List;

public interface RazaService {

    /** Lista completa; usada en formularios de Mascota. */
    List<Raza> findAll();

    Page<Raza> search(String q, Integer idEspecie, Boolean activo,
                      int page, int pageSize, String sortBy, String sortDir);

    Raza findById(Integer id);

    void save(Raza raza);

    void toggleActivo(Integer id);
}
