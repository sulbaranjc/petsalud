package com.example.petsalud.controller;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.Veterinario;
import com.example.petsalud.service.FileStorageService;
import com.example.petsalud.service.VeterinarioService;
import com.example.petsalud.service.catalogo.EspecialidadService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/veterinarios")
public class VeterinarioController {

    private static final int PAGE_SIZE = 10;

    private static final Set<String> SORT_COLS =
            Set.of("nombre", "matricula", "especialidad", "telefono", "email");

    private final VeterinarioService  veterinarioService;
    private final EspecialidadService especialidadService;
    private final FileStorageService  fileStorageService;

    public VeterinarioController(VeterinarioService veterinarioService,
                                  EspecialidadService especialidadService,
                                  FileStorageService fileStorageService) {
        this.veterinarioService  = veterinarioService;
        this.especialidadService = especialidadService;
        this.fileStorageService  = fileStorageService;
    }

    // ── Lista con búsqueda, filtros, ordenamiento y paginación ────────────────

    @GetMapping
    public String lista(@RequestParam(required = false) String q,
                        @RequestParam(required = false) Integer idEspecialidad,
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

        Page<Veterinario> pagina = veterinarioService.search(
                qNorm, idEspecialidad, activoBool, page, PAGE_SIZE, sortByNorm, sortDirNorm);

        model.addAttribute("pagina",              pagina);
        model.addAttribute("veterinarios",        pagina.getContent());
        model.addAttribute("ventanaPaginas",      calcularVentana(pagina.getPageNumber(),
                                                                  pagina.getTotalPages()));
        model.addAttribute("especialidades",      especialidadService.findAllActivas());
        model.addAttribute("q",                   qNorm);
        model.addAttribute("idEspecialidadFiltro", idEspecialidad);
        model.addAttribute("activoFiltro",        activoNorm);
        model.addAttribute("sortBy",              sortByNorm);
        model.addAttribute("sortDir",             sortDirNorm);
        return "veterinarios/lista";
    }

    // ── Crear ─────────────────────────────────────────────────────────────────

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("veterinario", new Veterinario());
        model.addAttribute("especialidades", especialidadService.findAllActivas());
        return "veterinarios/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute Veterinario veterinario,
                          BindingResult result,
                          @RequestParam(value = "fotoFile", required = false) MultipartFile fotoFile,
                          Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("especialidades", especialidadService.findAllActivas());
            return "veterinarios/form";
        }
        if (fotoFile != null && !fotoFile.isEmpty()) {
            try {
                veterinario.setFotoUrl(fileStorageService.store(fotoFile, "veterinarios"));
            } catch (IOException e) {
                model.addAttribute("errorFoto", "No se pudo guardar la foto: " + e.getMessage());
                model.addAttribute("especialidades", especialidadService.findAllActivas());
                return "veterinarios/form";
            }
        }
        veterinarioService.save(veterinario);
        flash.addFlashAttribute("mensajeExito", "Veterinario registrado correctamente.");
        return "redirect:/veterinarios";
    }

    // ── Editar ────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("veterinario",   veterinarioService.findById(id));
        model.addAttribute("especialidades", especialidadService.findAllActivas());
        return "veterinarios/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Integer id,
                             @Valid @ModelAttribute Veterinario veterinario,
                             BindingResult result,
                             @RequestParam(value = "fotoFile", required = false) MultipartFile fotoFile,
                             Model model,
                             RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("especialidades", especialidadService.findAllActivas());
            return "veterinarios/form";
        }
        veterinario.setId(id);
        if (fotoFile != null && !fotoFile.isEmpty()) {
            try {
                veterinario.setFotoUrl(fileStorageService.store(fotoFile, "veterinarios"));
            } catch (IOException e) {
                model.addAttribute("errorFoto", "No se pudo guardar la foto: " + e.getMessage());
                model.addAttribute("especialidades", especialidadService.findAllActivas());
                return "veterinarios/form";
            }
        }
        // Si no se subió archivo nuevo, veterinario.fotoUrl conserva el valor del hidden field
        veterinarioService.save(veterinario);
        flash.addFlashAttribute("mensajeExito", "Veterinario actualizado correctamente.");
        return "redirect:/veterinarios";
    }

    // ── Toggle activo ─────────────────────────────────────────────────────────

    @PostMapping("/{id}/toggle")
    public String toggleActivo(@PathVariable Integer id, RedirectAttributes flash) {
        veterinarioService.toggleActivo(id);
        flash.addFlashAttribute("mensajeExito", "Estado del veterinario actualizado.");
        return "redirect:/veterinarios";
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
