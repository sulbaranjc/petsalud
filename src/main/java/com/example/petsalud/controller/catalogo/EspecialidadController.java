package com.example.petsalud.controller.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Especialidad;
import com.example.petsalud.service.catalogo.EspecialidadService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/catalogos/especialidades")
public class EspecialidadController {

    private static final int PAGE_SIZE = 10;

    private static final Set<String> SORT_COLS = Set.of("nombre", "veterinarios");

    private final EspecialidadService especialidadService;

    public EspecialidadController(EspecialidadService especialidadService) {
        this.especialidadService = especialidadService;
    }

    // ── Lista con búsqueda, filtros, ordenamiento y paginación ────────────────

    @GetMapping
    public String lista(@RequestParam(required = false) String q,
                        @RequestParam(required = false) String activo,
                        @RequestParam(defaultValue = "nombre") String sortBy,
                        @RequestParam(defaultValue = "asc")    String sortDir,
                        @RequestParam(defaultValue = "1") int page,
                        Model model) {

        Boolean activoBool = "1".equals(activo) ? Boolean.TRUE
                           : "0".equals(activo) ? Boolean.FALSE
                           : null;
        String qNorm       = (q      != null && !q.isBlank())      ? q.trim() : null;
        String activoNorm  = (activo != null && !activo.isBlank()) ? activo   : null;
        String sortByNorm  = SORT_COLS.contains(sortBy)            ? sortBy   : "nombre";
        String sortDirNorm = "desc".equalsIgnoreCase(sortDir)      ? "desc"   : "asc";

        Page<Especialidad> pagina = especialidadService.search(
                qNorm, activoBool, page, PAGE_SIZE, sortByNorm, sortDirNorm);

        model.addAttribute("pagina",         pagina);
        model.addAttribute("especialidades", pagina.getContent());
        model.addAttribute("ventanaPaginas", calcularVentana(pagina.getPageNumber(),
                                                             pagina.getTotalPages()));
        model.addAttribute("q",              qNorm);
        model.addAttribute("activoFiltro",   activoNorm);
        model.addAttribute("sortBy",         sortByNorm);
        model.addAttribute("sortDir",        sortDirNorm);
        return "catalogos/especialidades/lista";
    }

    // ── Crear ─────────────────────────────────────────────────────────────────

    @GetMapping("/nueva")
    public String nuevaForm(Model model) {
        model.addAttribute("especialidad", new Especialidad());
        return "catalogos/especialidades/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute Especialidad especialidad,
                          BindingResult result,
                          RedirectAttributes flash) {
        if (result.hasErrors()) return "catalogos/especialidades/form";
        especialidadService.save(especialidad);
        flash.addFlashAttribute("mensajeExito", "Especialidad registrada correctamente.");
        return "redirect:/catalogos/especialidades";
    }

    // ── Editar ────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("especialidad", especialidadService.findById(id));
        return "catalogos/especialidades/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Integer id,
                             @Valid @ModelAttribute Especialidad especialidad,
                             BindingResult result,
                             RedirectAttributes flash) {
        if (result.hasErrors()) return "catalogos/especialidades/form";
        especialidad.setId(id);
        especialidadService.save(especialidad);
        flash.addFlashAttribute("mensajeExito", "Especialidad actualizada correctamente.");
        return "redirect:/catalogos/especialidades";
    }

    // ── Toggle activo ─────────────────────────────────────────────────────────

    @PostMapping("/{id}/toggle")
    public String toggleActivo(@PathVariable Integer id, RedirectAttributes flash) {
        especialidadService.toggleActivo(id);
        flash.addFlashAttribute("mensajeExito", "Estado de la especialidad actualizado.");
        return "redirect:/catalogos/especialidades";
    }

    // ── Ventana de paginación ─────────────────────────────────────────────────

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
