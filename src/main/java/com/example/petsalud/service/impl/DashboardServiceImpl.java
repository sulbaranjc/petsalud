package com.example.petsalud.service.impl;

import com.example.petsalud.model.DashboardCitaRow;
import com.example.petsalud.model.DashboardConsultaRow;
import com.example.petsalud.model.DashboardStats;
import com.example.petsalud.repository.DashboardRepository;
import com.example.petsalud.service.DashboardService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardServiceImpl(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    @Override
    public DashboardStats stats() {
        return dashboardRepository.stats();
    }

    @Override
    public List<DashboardCitaRow> citasHoy() {
        return dashboardRepository.citasHoy(8);
    }

    @Override
    public List<DashboardConsultaRow> consultasRecientes() {
        return dashboardRepository.consultasRecientes(5);
    }
}
