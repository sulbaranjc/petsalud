package com.example.petsalud.service.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Vacuna;

import java.util.List;

public interface VacunaService {

    /** Lista completa de activas; usada en formularios de vacunación. */
    List<Vacuna> findAllActivas();

    Page<Vacuna> search(String q, Boolean activo,
                        int page, int pageSize,
                        String sortBy, String sortDir);

    Vacuna findById(Integer id);

    void save(Vacuna vacuna);

    void toggleActivo(Integer id);
}
