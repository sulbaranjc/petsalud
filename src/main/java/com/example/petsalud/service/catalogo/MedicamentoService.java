package com.example.petsalud.service.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Medicamento;

import java.util.List;

public interface MedicamentoService {

    /** Lista completa de activos; usada en formularios de prescripción. */
    List<Medicamento> findAllActivos();

    Page<Medicamento> search(String q, Boolean activo,
                              int page, int pageSize,
                              String sortBy, String sortDir);

    Medicamento findById(Integer id);

    void save(Medicamento medicamento);

    void toggleActivo(Integer id);
}
