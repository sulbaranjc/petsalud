package com.example.petsalud.service;

import com.example.petsalud.model.DashboardCitaRow;
import com.example.petsalud.model.DashboardConsultaRow;
import com.example.petsalud.model.DashboardStats;

import java.util.List;

public interface DashboardService {
    DashboardStats stats();
    List<DashboardCitaRow> citasHoy();
    List<DashboardConsultaRow> consultasRecientes();
}
