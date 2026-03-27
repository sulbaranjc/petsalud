package com.example.petsalud.repository.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Medicamento;

import java.util.List;
import java.util.Optional;

public interface MedicamentoRepository {

    /** Lista completa de activos; usada en formularios de prescripción. */
    List<Medicamento> findAllActivos();

    Page<Medicamento> search(String q, Boolean activo,
                              int page, int pageSize,
                              String sortBy, String sortDir);

    Optional<Medicamento> findById(Integer id);

    void save(Medicamento medicamento);
}
