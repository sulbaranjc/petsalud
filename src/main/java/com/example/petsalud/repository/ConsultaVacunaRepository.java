package com.example.petsalud.repository;

import com.example.petsalud.model.ConsultaVacuna;
import com.example.petsalud.model.Page;
import com.example.petsalud.model.VacunacionRow;

import java.util.List;

public interface ConsultaVacunaRepository {

    void insert(ConsultaVacuna cv);

    List<ConsultaVacuna> findByIdConsulta(Integer idConsulta);

    Page<VacunacionRow> searchReport(String q, Integer idVacuna, String proximaDosis,
                                     int page, int pageSize, String sortBy, String sortDir);
}
