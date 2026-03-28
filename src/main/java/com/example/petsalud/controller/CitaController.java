package com.example.petsalud.controller;

import com.example.petsalud.model.Cita;
import com.example.petsalud.model.Page;
import com.example.petsalud.service.CitaService;
import com.example.petsalud.service.MascotaService;
import com.example.petsalud.service.VeterinarioService;
import com.example.petsalud.service.catalogo.EstadoCitaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/citas")
public class CitaController {

    private static final int PAGE_SIZE = 10;

    private static final Set<String> SORT_COLS = Set.of("fecha_hora", "mascota", "veterinario");

    private final CitaService       citaService;
    private final MascotaService    mascotaService;
    private final VeterinarioService veterinarioService;
    private final EstadoCitaService  estadoCitaService;

    public CitaController(CitaService citaService,
                          MascotaService mascotaService,
                          VeterinarioService veterinarioService,
                          EstadoCitaService estadoCitaService) {
        this.citaService        = citaService;
        this.mascotaService     = mascotaService;
        this.veterinarioService = veterinarioService;
        this.estadoCitaService  = estadoCitaService;
    }

    // ── Lista ─────────────────────────────────────────────────────────────────

    @GetMapping
    public String lista(@RequestParam(required = false) String q,
                        @RequestParam(required = false) Integer idEstadoCita,
                        @RequestParam(required = false) Integer idVeterinario,
                        @RequestParam(required = false) String fecha,
                        @RequestParam(defaultValue = "fecha_hora") String sortBy,
                        @RequestParam(defaultValue = "desc")        String sortDir,
                        @RequestParam(defaultValue = "1") int page,
                        Model model) {

        String   qNorm          = (q     != null && !q.isBlank())     ? q.trim()  : null;
        String   sortByNorm     = SORT_COLS.contains(sortBy)           ? sortBy    : "fecha_hora";
        String   sortDirNorm    = "asc".equalsIgnoreCase(sortDir)      ? "asc"     : "desc";
        LocalDate fechaDate     = (fecha != null && !fecha.isBlank())
                                  ? LocalDate.parse(fecha) : null;

        Page<Cita> pagina = citaService.search(
                qNorm, idEstadoCita, idVeterinario, fechaDate,
                page, PAGE_SIZE, sortByNorm, sortDirNorm);

        model.addAttribute("pagina",            pagina);
        model.addAttribute("citas",             pagina.getContent());
        model.addAttribute("ventanaPaginas",    calcularVentana(pagina.getPageNumber(),
                                                                 pagina.getTotalPages()));
        model.addAttribute("q",                 qNorm);
        model.addAttribute("idEstadoFiltro",    idEstadoCita);
        model.addAttribute("idVeterinarioFiltro", idVeterinario);
        model.addAttribute("fechaFiltro",       fecha != null && !fecha.isBlank() ? fecha : null);
        model.addAttribute("sortBy",            sortByNorm);
        model.addAttribute("sortDir",           sortDirNorm);
        model.addAttribute("estados",           estadoCitaService.findAll());
        model.addAttribute("veterinarios",      veterinarioService.findAllActivos());
        return "citas/lista";
    }

    // ── Crear ─────────────────────────────────────────────────────────────────

    @GetMapping("/nueva")
    public String nuevaForm(@RequestParam(required = false) Integer idMascota,
                            Model model) {
        Cita cita = new Cita();
        estadoCitaService.findByNombre("Pendiente")
                .ifPresent(e -> cita.setIdEstadoCita(e.getId()));
        if (idMascota != null) cita.setIdMascota(idMascota);
        cargarFormModel(model, cita);
        return "citas/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute Cita cita,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            cargarFormModel(model, cita);
            return "citas/form";
        }
        citaService.save(cita);
        flash.addFlashAttribute("mensajeExito", "Cita guardada correctamente.");
        return "redirect:/citas";
    }

    // ── Editar ────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model) {
        cargarFormModel(model, citaService.findById(id));
        return "citas/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Integer id,
                             @Valid @ModelAttribute Cita cita,
                             BindingResult result,
                             Model model,
                             RedirectAttributes flash) {
        if (result.hasErrors()) {
            cargarFormModel(model, cita);
            return "citas/form";
        }
        cita.setId(id);
        citaService.save(cita);
        flash.addFlashAttribute("mensajeExito", "Cita actualizada correctamente.");
        return "redirect:/citas";
    }

    // ── Cancelar ──────────────────────────────────────────────────────────────

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Integer id, RedirectAttributes flash) {
        citaService.cancelar(id);
        flash.addFlashAttribute("mensajeExito", "Cita cancelada.");
        return "redirect:/citas";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void cargarFormModel(Model model, Cita cita) {
        model.addAttribute("cita",         cita);
        model.addAttribute("mascotas",     mascotaService.findAllActivas());
        model.addAttribute("veterinarios", veterinarioService.findAllActivos());
        model.addAttribute("estados",      estadoCitaService.findAll());
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
