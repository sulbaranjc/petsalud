package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Raza;

import java.util.List;
import java.util.Optional;

public interface RazaRepository {

    /** Lista completa; usada en formularios de Mascota. */
    List<Raza> findAllByOrderByNombreAsc();

    Page<Raza> search(String q, Integer idEspecie, Boolean activo,
                      int page, int pageSize, String sortBy, String sortDir);

    Optional<Raza> findById(Integer id);

    void save(Raza raza);
}
