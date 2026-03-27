package com.example.petsalud.controller;

import com.example.petsalud.model.Mascota;
import com.example.petsalud.model.Page;
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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    private static final int PAGE_SIZE = 10;

    private static final java.util.Set<String> SORT_COLS =
            java.util.Set.of("nombre", "especie", "sexo", "propietario", "nacimiento");

    private final MascotaService     mascotaService;
    private final EspecieService     especieService;
    private final RazaService        razaService;
    private final PropietarioService propietarioService;

    public MascotaController(MascotaService mascotaService,
                             EspecieService especieService,
                             RazaService razaService,
                             PropietarioService propietarioService) {
        this.mascotaService     = mascotaService;
        this.especieService     = especieService;
        this.razaService        = razaService;
        this.propietarioService = propietarioService;
    }

    // ── Lista con búsqueda, filtros y paginación ──────────────────────────────

    @GetMapping
    public String lista(@RequestParam(required = false) String q,
                        @RequestParam(required = false) Integer idEspecie,
                        @RequestParam(required = false) String sexo,
                        @RequestParam(required = false) String activo,
                        @RequestParam(defaultValue = "nombre") String sortBy,
                        @RequestParam(defaultValue = "asc")    String sortDir,
                        @RequestParam(defaultValue = "1") int page,
                        Model model) {

        Boolean activoBool = "1".equals(activo) ? Boolean.TRUE
                           : "0".equals(activo) ? Boolean.FALSE
                           : null;

        // Normalizar filtros a null cuando vacíos para URLs de paginación limpias
        String qNorm      = (q      != null && !q.isBlank())      ? q.trim() : null;
        String sexoNorm   = (sexo   != null && !sexo.isBlank())   ? sexo     : null;
        String activoNorm = (activo != null && !activo.isBlank()) ? activo   : null;

        // Validar sortBy y sortDir contra lista cerrada
        String sortByNorm  = SORT_COLS.contains(sortBy)          ? sortBy  : "nombre";
        String sortDirNorm = "desc".equalsIgnoreCase(sortDir)    ? "desc"  : "asc";

        Page<Mascota> pagina = mascotaService.search(qNorm, idEspecie, sexoNorm, activoBool,
                                                     page, PAGE_SIZE, sortByNorm, sortDirNorm);

        model.addAttribute("pagina",          pagina);
        model.addAttribute("mascotas",        pagina.getContent());
        model.addAttribute("ventanaPaginas",  calcularVentana(pagina.getPageNumber(),
                                                              pagina.getTotalPages()));
        model.addAttribute("especies",        especieService.findAll());
        model.addAttribute("q",               qNorm);
        model.addAttribute("idEspecieFiltro", idEspecie);
        model.addAttribute("sexoFiltro",      sexoNorm);
        model.addAttribute("activoFiltro",    activoNorm);
        model.addAttribute("sortBy",          sortByNorm);
        model.addAttribute("sortDir",         sortDirNorm);
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

    // ── Helpers privados ──────────────────────────────────────────────────────

    private void cargarReferencias(Model model) {
        model.addAttribute("especies",     especieService.findAll());
        model.addAttribute("razas",        razaService.findAll());
        model.addAttribute("propietarios", propietarioService.findAll());
    }

    /**
     * Calcula los números de página a mostrar en el control de paginación.
     * Siempre incluye la primera y la última página, más una ventana de ±2
     * alrededor de la página actual. Los saltos se representan con -1 (elipsis).
     *
     * Ejemplos:
     *   pág 1 de 20  →  [1, 2, 3, -1, 20]
     *   pág 8 de 20  →  [1, -1, 6, 7, 8, 9, 10, -1, 20]
     *   pág 19 de 20 →  [1, -1, 17, 18, 19, 20]
     */
    private List<Integer> calcularVentana(int paginaActual, int totalPaginas) {
        if (totalPaginas <= 7) {
            List<Integer> todas = new ArrayList<>();
            for (int i = 1; i <= totalPaginas; i++) todas.add(i);
            return todas;
        }

        LinkedHashSet<Integer> conjunto = new LinkedHashSet<>();
        conjunto.add(1);
        for (int i = Math.max(2, paginaActual - 2);
             i <= Math.min(totalPaginas - 1, paginaActual + 2); i++) {
            conjunto.add(i);
        }
        conjunto.add(totalPaginas);

        List<Integer> ventana = new ArrayList<>();
        int anterior = 0;
        for (int p : conjunto) {
            if (anterior > 0 && p - anterior > 1) ventana.add(-1); // -1 = elipsis
            ventana.add(p);
            anterior = p;
        }
        return ventana;
    }
}
