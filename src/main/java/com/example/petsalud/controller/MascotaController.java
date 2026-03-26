package com.example.petsalud.controller;

import com.example.petsalud.model.Mascota;
import com.example.petsalud.service.MascotaService;
import com.example.petsalud.service.PropietarioService;
import com.example.petsalud.service.catalogo.EspecieService;
import com.example.petsalud.service.catalogo.RazaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    private final MascotaService      mascotaService;
    private final EspecieService      especieService;
    private final RazaService         razaService;
    private final PropietarioService  propietarioService;

    public MascotaController(MascotaService mascotaService,
                             EspecieService especieService,
                             RazaService razaService,
                             PropietarioService propietarioService) {
        this.mascotaService     = mascotaService;
        this.especieService     = especieService;
        this.razaService        = razaService;
        this.propietarioService = propietarioService;
    }

    // ── Lista con búsqueda y filtros ──────────────────────────────────────────

    @GetMapping
    public String lista(@RequestParam(required = false) String q,
                        @RequestParam(required = false) Integer idEspecie,
                        @RequestParam(required = false) String sexo,
                        @RequestParam(required = false) String activo,
                        Model model) {
        Boolean activoBool = "1".equals(activo) ? Boolean.TRUE
                           : "0".equals(activo) ? Boolean.FALSE
                           : null;
        model.addAttribute("mascotas",      mascotaService.search(q, idEspecie, sexo, activoBool));
        model.addAttribute("especies",      especieService.findAll());
        model.addAttribute("q",             Objects.toString(q, ""));
        model.addAttribute("idEspecieFiltro", idEspecie);
        model.addAttribute("sexoFiltro",    Objects.toString(sexo, ""));
        model.addAttribute("activoFiltro",  Objects.toString(activo, ""));
        return "mascotas/lista";
    }

    // ── Crear ─────────────────────────────────────────────────────────────────

    @GetMapping("/nueva")
    public String nuevaForm(Model model) {
        model.addAttribute("mascota", new Mascota());
        cargarReferencias(model);
        return "mascotas/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute Mascota mascota,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            cargarReferencias(model);
            return "mascotas/form";
        }
        mascotaService.save(mascota);
        flash.addFlashAttribute("mensajeExito", "Mascota registrada correctamente.");
        return "redirect:/mascotas";
    }

    // ── Editar ────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("mascota", mascotaService.findById(id));
        cargarReferencias(model);
        return "mascotas/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Integer id,
                             @Valid @ModelAttribute Mascota mascota,
                             BindingResult result,
                             Model model,
                             RedirectAttributes flash) {
        if (result.hasErrors()) {
            cargarReferencias(model);
            return "mascotas/form";
        }
        mascota.setId(id);
        mascotaService.save(mascota);
        flash.addFlashAttribute("mensajeExito", "Mascota actualizada correctamente.");
        return "redirect:/mascotas";
    }

    // ── Toggle activo ─────────────────────────────────────────────────────────

    @PostMapping("/{id}/toggle")
    public String toggleActivo(@PathVariable Integer id, RedirectAttributes flash) {
        mascotaService.toggleActivo(id);
        flash.addFlashAttribute("mensajeExito", "Estado de la mascota actualizado.");
        return "redirect:/mascotas";
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void cargarReferencias(Model model) {
        model.addAttribute("especies",     especieService.findAll());
        model.addAttribute("razas",        razaService.findAll());
        model.addAttribute("propietarios", propietarioService.findAll());
    }
}
