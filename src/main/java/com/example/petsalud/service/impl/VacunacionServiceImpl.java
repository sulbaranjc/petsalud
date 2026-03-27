package com.example.petsalud.service.impl;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.VacunacionRow;
import com.example.petsalud.repository.ConsultaVacunaRepository;
import com.example.petsalud.service.VacunacionService;
import org.springframework.stereotype.Service;

@Service
public class VacunacionServiceImpl implements VacunacionService {

    private final ConsultaVacunaRepository consultaVacunaRepository;

    public VacunacionServiceImpl(ConsultaVacunaRepository consultaVacunaRepository) {
        this.consultaVacunaRepository = consultaVacunaRepository;
    }

    @Override
    public Page<VacunacionRow> search(String q, Integer idVacuna, String proximaDosis,
                                      int page, int pageSize, String sortBy, String sortDir) {
        return consultaVacunaRepository.searchReport(q, idVacuna, proximaDosis,
                page, pageSize, sortBy, sortDir);
    }
}
