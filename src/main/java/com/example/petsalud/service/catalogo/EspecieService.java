package com.example.petsalud.service.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Especie;

import java.util.List;

public interface EspecieService {

    /** Lista completa; usada en formularios de Raza y Mascota. */
    List<Especie> findAll();

    Page<Especie> search(String q, Boolean activo, int page, int pageSize,
                          String sortBy, String sortDir);

    Especie findById(Integer id);

    void save(Especie especie);

    void toggleActivo(Integer id);
}
