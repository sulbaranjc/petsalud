package com.example.petsalud.service.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Especialidad;

import java.util.List;

public interface EspecialidadService {

    List<Especialidad> findAllActivas();

    Page<Especialidad> search(String q, Boolean activo, int page, int pageSize,
                               String sortBy, String sortDir);

    Especialidad findById(Integer id);

    void save(Especialidad especialidad);

    void toggleActivo(Integer id);
}
