package com.example.petsalud.repository;

import com.example.petsalud.model.DashboardCitaRow;
import com.example.petsalud.model.DashboardConsultaRow;
import com.example.petsalud.model.DashboardStats;

import java.util.List;

public interface DashboardRepository {
    DashboardStats stats();
    List<DashboardCitaRow> citasHoy(int limit);
    List<DashboardConsultaRow> consultasRecientes(int limit);
}
