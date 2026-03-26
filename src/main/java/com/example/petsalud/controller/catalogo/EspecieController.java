package com.example.petsalud.controller.catalogo;

import com.example.petsalud.model.catalogo.Especie;
import com.example.petsalud.service.catalogo.EspecieService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/catalogos/especies")
public class EspecieController {

    private final EspecieService especieService;

    public EspecieController(EspecieService especieService) {
        this.especieService = especieService;
    }

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("especies", especieService.findAll());
        return "catalogos/especies/lista";
    }

    @GetMapping("/nueva")
    public String nuevaForm(Model model) {
        model.addAttribute("especie", new Especie());
        return "catalogos/especies/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute Especie especie,
                          BindingResult result,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            return "catalogos/especies/form";
        }
        especieService.save(especie);
        flash.addFlashAttribute("mensajeExito", "Especie guardada correctamente.");
        return "redirect:/catalogos/especies";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("especie", especieService.findById(id));
        return "catalogos/especies/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Integer id,
                             @Valid @ModelAttribute Especie especie,
                             BindingResult result,
                             RedirectAttributes flash) {
        if (result.hasErrors()) {
            return "catalogos/especies/form";
        }
        especie.setId(id);
        especieService.save(especie);
        flash.addFlashAttribute("mensajeExito", "Especie actualizada correctamente.");
        return "redirect:/catalogos/especies";
    }

    @PostMapping("/{id}/toggle")
    public String toggleActivo(@PathVariable Integer id, RedirectAttributes flash) {
        especieService.toggleActivo(id);
        flash.addFlashAttribute("mensajeExito", "Estado de la especie actualizado.");
        return "redirect:/catalogos/especies";
    }
}
