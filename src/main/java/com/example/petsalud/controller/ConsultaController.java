package com.example.petsalud.controller;

import com.example.petsalud.model.Consulta;
import com.example.petsalud.model.ConsultaForm;
import com.example.petsalud.model.Page;
import com.example.petsalud.service.CitaService;
import com.example.petsalud.service.ConsultaService;
import com.example.petsalud.service.VeterinarioService;
import com.example.petsalud.service.catalogo.MedicamentoService;
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
@RequestMapping("/consultas")
public class ConsultaController {

    private static final int PAGE_SIZE = 10;
    private static final Set<String> SORT_COLS = Set.of("fecha", "mascota", "veterinario");

    private final ConsultaService    consultaService;
    private final CitaService        citaService;
    private final VeterinarioService veterinarioService;
    private final MedicamentoService medicamentoService;
    private final VacunaService      vacunaService;

    public ConsultaController(ConsultaService consultaService,
                               CitaService citaService,
                               VeterinarioService veterinarioService,
                               MedicamentoService medicamentoService,
                               VacunaService vacunaService) {
        this.consultaService    = consultaService;
        this.citaService        = citaService;
        this.veterinarioService = veterinarioService;
        this.medicamentoService = medicamentoService;
        this.vacunaService      = vacunaService;
    }

    // ── Lista ─────────────────────────────────────────────────────────────────

    @GetMapping
    public String lista(@RequestParam(required = false) String q,
                        @RequestParam(required = false) Integer idVeterinario,
                        @RequestParam(defaultValue = "fecha") String sortBy,
                        @RequestParam(defaultValue = "desc")   String sortDir,
                        @RequestParam(defaultValue = "1") int page,
                        Model model) {

        String qNorm       = (q != null && !q.isBlank()) ? q.trim() : null;
        String sortByNorm  = SORT_COLS.contains(sortBy) ? sortBy : "fecha";
        String sortDirNorm = "asc".equalsIgnoreCase(sortDir) ? "asc" : "desc";

        Page<Consulta> pagina = consultaService.search(
                qNorm, idVeterinario, page, PAGE_SIZE, sortByNorm, sortDirNorm);

        model.addAttribute("pagina",              pagina);
        model.addAttribute("consultas",           pagina.getContent());
        model.addAttribute("ventanaPaginas",      calcularVentana(pagina.getPageNumber(),
                                                                   pagina.getTotalPages()));
        model.addAttribute("q",                   qNorm);
        model.addAttribute("idVeterinarioFiltro", idVeterinario);
        model.addAttribute("sortBy",              sortByNorm);
        model.addAttribute("sortDir",             sortDirNorm);
        model.addAttribute("veterinarios",        veterinarioService.findAllActivos());
        return "consultas/lista";
    }

    // ── Nueva consulta (solo desde cita) ─────────────────────────────────────

    @GetMapping("/nueva")
    public String nuevaForm(@RequestParam Integer idCita,
                            Model model,
                            RedirectAttributes flash) {
        if (consultaService.findByIdCita(idCita).isPresent()) {
            flash.addFlashAttribute("mensajeError",
                    "Esta cita ya tiene una consulta registrada.");
            return "redirect:/citas";
        }
        ConsultaForm form = new ConsultaForm();
        form.setIdCita(idCita);
        cargarFormModel(model, form, idCita);
        return "consultas/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("consultaForm") ConsultaForm form,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            cargarFormModel(model, form, form.getIdCita());
            return "consultas/form";
        }
        consultaService.guardar(form);
        flash.addFlashAttribute("mensajeExito", "Consulta registrada y cita completada correctamente.");
        return "redirect:/citas";
    }

    // ── Detalle (read-only) ───────────────────────────────────────────────────

    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        Consulta consulta = consultaService.findById(id);
        model.addAttribute("consulta",     consulta);
        model.addAttribute("tratamientos", consultaService.findTratamientos(id));
        model.addAttribute("medicamentos", consultaService.findMedicamentos(id));
        model.addAttribute("vacunas",      consultaService.findVacunas(id));
        return "consultas/detalle";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void cargarFormModel(Model model, ConsultaForm form, Integer idCita) {
        model.addAttribute("consultaForm",  form);
        model.addAttribute("medicamentos",  medicamentoService.findAllActivos());
        model.addAttribute("vacunas",       vacunaService.findAllActivas());
        if (idCita != null) {
            model.addAttribute("cita", citaService.findById(idCita));
        }
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
