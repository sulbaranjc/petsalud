package com.example.petsalud.service;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.VacunacionRow;

public interface VacunacionService {

    Page<VacunacionRow> search(String q, Integer idVacuna, String proximaDosis,
                                int page, int pageSize, String sortBy, String sortDir);
}
