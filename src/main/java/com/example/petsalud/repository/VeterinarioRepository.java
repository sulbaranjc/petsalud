package com.example.petsalud.repository;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.Veterinario;

import java.util.List;
import java.util.Optional;

public interface VeterinarioRepository {

    /** Lista de veterinarios activos para usar en formularios de citas/consultas. */
    List<Veterinario> findAllActivos();

    Page<Veterinario> search(String q, Integer idEspecialidad, Boolean activo,
                              int page, int pageSize, String sortBy, String sortDir);

    Optional<Veterinario> findById(Integer id);

    void save(Veterinario veterinario);
}
