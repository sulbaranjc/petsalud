package com.example.petsalud.controller.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Vacuna;
import com.example.petsalud.service.catalogo.VacunaService;
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
@RequestMapping("/catalogos/vacunas")
public class VacunaController {

    private static final int PAGE_SIZE = 10;

    private static final Set<String> SORT_COLS = Set.of("nombre", "laboratorio");

    private final VacunaService vacunaService;

    public VacunaController(VacunaService vacunaService) {
        this.vacunaService = vacunaService;
    }

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

        Page<Vacuna> pagina = vacunaService.search(
                qNorm, activoBool, page, PAGE_SIZE, sortByNorm, sortDirNorm);

        model.addAttribute("pagina",         pagina);
        model.addAttribute("vacunas",        pagina.getContent());
        model.addAttribute("ventanaPaginas", calcularVentana(pagina.getPageNumber(),
                                                              pagina.getTotalPages()));
        model.addAttribute("q",              qNorm);
        model.addAttribute("activoFiltro",   activoNorm);
        model.addAttribute("sortBy",         sortByNorm);
        model.addAttribute("sortDir",        sortDirNorm);
        return "catalogos/vacunas/lista";
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

    @GetMapping("/nueva")
    public String nuevaForm(Model model) {
        model.addAttribute("vacuna", new Vacuna());
        return "catalogos/vacunas/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute Vacuna vacuna,
                          BindingResult result,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            return "catalogos/vacunas/form";
        }
        vacunaService.save(vacuna);
        flash.addFlashAttribute("mensajeExito", "Vacuna guardada correctamente.");
        return "redirect:/catalogos/vacunas";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("vacuna", vacunaService.findById(id));
        return "catalogos/vacunas/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Integer id,
                             @Valid @ModelAttribute Vacuna vacuna,
                             BindingResult result,
                             RedirectAttributes flash) {
        if (result.hasErrors()) {
            return "catalogos/vacunas/form";
        }
        vacuna.setId(id);
        vacunaService.save(vacuna);
        flash.addFlashAttribute("mensajeExito", "Vacuna actualizada correctamente.");
        return "redirect:/catalogos/vacunas";
    }

    @PostMapping("/{id}/toggle")
    public String toggleActivo(@PathVariable Integer id, RedirectAttributes flash) {
        vacunaService.toggleActivo(id);
        flash.addFlashAttribute("mensajeExito", "Estado de la vacuna actualizado.");
        return "redirect:/catalogos/vacunas";
    }
}
