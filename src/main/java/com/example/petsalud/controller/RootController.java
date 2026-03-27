package com.example.petsalud.controller;

import com.example.petsalud.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class RootController {

    private final DashboardService dashboardService;

    public RootController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("stats",               dashboardService.stats());
        model.addAttribute("citasHoy",            dashboardService.citasHoy());
        model.addAttribute("consultasRecientes",  dashboardService.consultasRecientes());
        model.addAttribute("today",               LocalDate.now());
        return "index";
    }
}
