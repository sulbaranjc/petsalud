package com.example.petsalud.controller.catalogo;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Raza;
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
import java.util.Set;

@Controller
@RequestMapping("/catalogos/razas")
public class RazaController {

    private static final int PAGE_SIZE = 10;

    private static final Set<String> SORT_COLS = Set.of("nombre", "especie");

    private final RazaService razaService;
    private final EspecieService especieService;

    public RazaController(RazaService razaService, EspecieService especieService) {
        this.razaService = razaService;
        this.especieService = especieService;
    }

    @GetMapping
    public String lista(@RequestParam(required = false) String q,
                        @RequestParam(required = false) Integer idEspecie,
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

        Page<Raza> pagina = razaService.search(
                qNorm, idEspecie, activoBool, page, PAGE_SIZE, sortByNorm, sortDirNorm);

        model.addAttribute("pagina",          pagina);
        model.addAttribute("razas",           pagina.getContent());
        model.addAttribute("ventanaPaginas",  calcularVentana(pagina.getPageNumber(),
                                                              pagina.getTotalPages()));
        model.addAttribute("especies",        especieService.findAll());
        model.addAttribute("q",               qNorm);
        model.addAttribute("idEspecieFiltro", idEspecie);
        model.addAttribute("activoFiltro",    activoNorm);
        model.addAttribute("sortBy",          sortByNorm);
        model.addAttribute("sortDir",         sortDirNorm);
        return "catalogos/razas/lista";
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
        model.addAttribute("raza", new Raza());
        model.addAttribute("especies", especieService.findAll());
        return "catalogos/razas/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute Raza raza,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("especies", especieService.findAll());
            return "catalogos/razas/form";
        }
        razaService.save(raza);
        flash.addFlashAttribute("mensajeExito", "Raza guardada correctamente.");
        return "redirect:/catalogos/razas";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("raza", razaService.findById(id));
        model.addAttribute("especies", especieService.findAll());
        return "catalogos/razas/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Integer id,
                             @Valid @ModelAttribute Raza raza,
                             BindingResult result,
                             Model model,
                             RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("especies", especieService.findAll());
            return "catalogos/razas/form";
        }
        raza.setId(id);
        razaService.save(raza);
        flash.addFlashAttribute("mensajeExito", "Raza actualizada correctamente.");
        return "redirect:/catalogos/razas";
    }

    @PostMapping("/{id}/toggle")
    public String toggleActivo(@PathVariable Integer id, RedirectAttributes flash) {
        razaService.toggleActivo(id);
        flash.addFlashAttribute("mensajeExito", "Estado de la raza actualizado.");
        return "redirect:/catalogos/razas";
    }
}
