package com.example.petsalud.controller.catalogo;

import com.example.petsalud.model.catalogo.Raza;
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
@RequestMapping("/catalogos/razas")
public class RazaController {

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
                        Model model) {
        Boolean activoBool = "1".equals(activo) ? Boolean.TRUE
                           : "0".equals(activo) ? Boolean.FALSE
                           : null;
        model.addAttribute("razas",         razaService.search(q, idEspecie, activoBool));
        model.addAttribute("especies",      especieService.findAll());
        model.addAttribute("q",             Objects.toString(q, ""));
        model.addAttribute("idEspecieFiltro", idEspecie);
        model.addAttribute("activoFiltro",  Objects.toString(activo, ""));
        return "catalogos/razas/lista";
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
