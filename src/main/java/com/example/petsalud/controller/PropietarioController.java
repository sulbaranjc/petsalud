package com.example.petsalud.controller;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.Propietario;
import com.example.petsalud.service.PropietarioService;
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
@RequestMapping("/propietarios")
public class PropietarioController {

    private static final int PAGE_SIZE = 10;

    private final PropietarioService propietarioService;

    public PropietarioController(PropietarioService propietarioService) {
        this.propietarioService = propietarioService;
    }

    // Columnas válidas para ordenamiento — debe coincidir con la whitelist del repositorio
    private static final java.util.Set<String> SORT_COLS =
            java.util.Set.of("nombre", "documento", "telefono", "email", "mascotas");

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

        // Normalizar a null cuando vacíos para que Thymeleaf los omita en las URLs
        String qNorm      = (q      != null && !q.isBlank())      ? q.trim() : null;
        String activoNorm = (activo != null && !activo.isBlank()) ? activo   : null;

        // Validar sortBy y sortDir contra lista cerrada (seguridad adicional)
        String sortByNorm  = SORT_COLS.contains(sortBy)                    ? sortBy  : "nombre";
        String sortDirNorm = "desc".equalsIgnoreCase(sortDir)              ? "desc"  : "asc";

        Page<Propietario> pagina = propietarioService.search(
                qNorm, activoBool, page, PAGE_SIZE, sortByNorm, sortDirNorm);

        model.addAttribute("pagina",         pagina);
        model.addAttribute("propietarios",   pagina.getContent());
        model.addAttribute("ventanaPaginas", calcularVentana(pagina.getPageNumber(),
                                                             pagina.getTotalPages()));
        model.addAttribute("q",              qNorm);
        model.addAttribute("activoFiltro",   activoNorm);
        model.addAttribute("sortBy",         sortByNorm);
        model.addAttribute("sortDir",        sortDirNorm);
        return "propietarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("propietario", new Propietario());
        return "propietarios/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute Propietario propietario,
                          BindingResult result,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            return "propietarios/form";
        }
        propietarioService.save(propietario);
        flash.addFlashAttribute("mensajeExito", "Propietario registrado correctamente.");
        return "redirect:/propietarios";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("propietario", propietarioService.findById(id));
        return "propietarios/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Integer id,
                             @Valid @ModelAttribute Propietario propietario,
                             BindingResult result,
                             RedirectAttributes flash) {
        if (result.hasErrors()) {
            return "propietarios/form";
        }
        propietario.setId(id);
        propietarioService.save(propietario);
        flash.addFlashAttribute("mensajeExito", "Propietario actualizado correctamente.");
        return "redirect:/propietarios";
    }

    @PostMapping("/{id}/toggle")
    public String toggleActivo(@PathVariable Integer id, RedirectAttributes flash) {
        propietarioService.toggleActivo(id);
        flash.addFlashAttribute("mensajeExito", "Estado del propietario actualizado.");
        return "redirect:/propietarios";
    }

    /**
     * Calcula los números de página a mostrar en el control de paginación.
     * Siempre incluye la primera y la última página, más una ventana de ±2
     * alrededor de la página actual. Los saltos se representan con -1 (elipsis).
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
