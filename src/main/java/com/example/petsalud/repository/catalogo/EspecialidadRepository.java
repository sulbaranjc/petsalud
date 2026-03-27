package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Especialidad;

import java.util.List;
import java.util.Optional;

public interface EspecialidadRepository {

    /** Lista ordenada para usar como opciones en formularios (solo activas). */
    List<Especialidad> findAllActivas();

    Page<Especialidad> search(String q, Boolean activo, int page, int pageSize,
                               String sortBy, String sortDir);

    Optional<Especialidad> findById(Integer id);

    void save(Especialidad especialidad);
}
