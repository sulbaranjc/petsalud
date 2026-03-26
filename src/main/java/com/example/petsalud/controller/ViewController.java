package com.example.petsalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de navegación: registra todas las rutas de vistas.
 * Sin lógica de negocio — solo devuelve el nombre de la plantilla.
 */
@Controller
public class ViewController {

    // ── Operativo ────────────────────────────────────────────────────────────

    @GetMapping("/citas")
    public String citas() {
        return "citas/lista";
    }

    @GetMapping("/consultas")
    public String consultas() {
        return "consultas/lista";
    }

    // ── Pacientes ────────────────────────────────────────────────────────────

    @GetMapping("/mascotas")
    public String mascotas() {
        return "mascotas/lista";
    }

    @GetMapping("/propietarios")
    public String propietarios() {
        return "propietarios/lista";
    }

    @GetMapping("/vacunaciones")
    public String vacunaciones() {
        return "vacunaciones/lista";
    }

    // ── Personal ─────────────────────────────────────────────────────────────

    @GetMapping("/veterinarios")
    public String veterinarios() {
        return "veterinarios/lista";
    }

    // ── Catálogos ─────────────────────────────────────────────────────────────

    @GetMapping("/catalogos/especies")
    public String especies() {
        return "catalogos/especies";
    }

    @GetMapping("/catalogos/razas")
    public String razas() {
        return "catalogos/razas";
    }

    @GetMapping("/catalogos/especialidades")
    public String especialidades() {
        return "catalogos/especialidades";
    }

    @GetMapping("/catalogos/medicamentos")
    public String medicamentos() {
        return "catalogos/medicamentos";
    }

    @GetMapping("/catalogos/vacunas")
    public String vacunas() {
        return "catalogos/vacunas";
    }
}
