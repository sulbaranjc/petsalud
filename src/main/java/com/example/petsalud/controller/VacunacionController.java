package com.example.petsalud.controller;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.VacunacionRow;
import com.example.petsalud.service.VacunacionService;
import com.example.petsalud.service.catalogo.VacunaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Controller
@RequestMapping("/vacunaciones")
public class VacunacionController {

    private final VacunacionService vacunacionService;
    private final VacunaService     vacunaService;

    public VacunacionController(VacunacionService vacunacionService, VacunaService vacunaService) {
        this.vacunacionService = vacunacionService;
        this.vacunaService     = vacunaService;
    }

    @GetMapping
    public String lista(
            @RequestParam(required = false)                String  q,
            @RequestParam(required = false)                Integer idVacuna,
            @RequestParam(required = false)                String  proximaDosis,
            @RequestParam(defaultValue = "fecha_consulta") String  sortBy,
            @RequestParam(defaultValue = "desc")           String  sortDir,
            @RequestParam(defaultValue = "1")              int     page,
            Model model) {

        int pageSize = 15;
        Page<VacunacionRow> pagina = vacunacionService.search(
                q, idVacuna, proximaDosis, page, pageSize, sortBy, sortDir);

        model.addAttribute("vacunaciones",       pagina.getContent());
        model.addAttribute("pagina",             pagina);
        model.addAttribute("ventanaPaginas",     calcularVentana(pagina.getPageNumber(), pagina.getTotalPages()));
        model.addAttribute("vacunas",            vacunaService.findAllActivas());
        model.addAttribute("q",                  q);
        model.addAttribute("idVacunaFiltro",     idVacuna);
        model.addAttribute("proximaDosisFiltro", proximaDosis);
        model.addAttribute("sortBy",             sortBy);
        model.addAttribute("sortDir",            sortDir);

        return "vacunaciones/lista";
    }

    private List<Integer> calcularVentana(int paginaActual, int totalPaginas) {
        if (totalPaginas <= 7) {
            List<Integer> todas = new ArrayList<>();
            for (int i = 1; i <= totalPaginas; i++) todas.add(i);
            return todas;
        }
        LinkedHashSet<Integer> conjunto = new LinkedHashSet<>();
        conjunto.add(1);
        for (int i = Math.max(2, paginaActual - 2);
             i <= Math.min(totalPaginas - 1, paginaActual + 2); i++) conjunto.add(i);
        conjunto.add(totalPaginas);

        List<Integer> ventana = new ArrayList<>();
        int anterior = 0;
        for (int p : conjunto) {
            if (anterior > 0 && p - anterior > 1) ventana.add(-1);
            ventana.add(p);
            anterior = p;
        }
        return ventana;
    }
}
