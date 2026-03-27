package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Vacuna;

import java.util.List;
import java.util.Optional;

public interface VacunaRepository {

    /** Lista completa de activas; usada en formularios de vacunación. */
    List<Vacuna> findAllActivas();

    Page<Vacuna> search(String q, Boolean activo,
                        int page, int pageSize,
                        String sortBy, String sortDir);

    Optional<Vacuna> findById(Integer id);

    void save(Vacuna vacuna);
}
