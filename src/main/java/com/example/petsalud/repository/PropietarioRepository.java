package com.example.petsalud.repository;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.Propietario;

import java.util.List;
import java.util.Optional;

public interface PropietarioRepository {

    List<Propietario> findAllByOrderByApellidoNombreAsc();

    Page<Propietario> search(String q, Boolean activo, int page, int pageSize);

    Optional<Propietario> findById(Integer id);

    void save(Propietario propietario);
}
