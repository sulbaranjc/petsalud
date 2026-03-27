package com.example.petsalud.service;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.Veterinario;

import java.util.List;

public interface VeterinarioService {

    List<Veterinario> findAllActivos();

    Page<Veterinario> search(String q, Integer idEspecialidad, Boolean activo,
                              int page, int pageSize, String sortBy, String sortDir);

    Veterinario findById(Integer id);

    void save(Veterinario veterinario);

    void toggleActivo(Integer id);
}
