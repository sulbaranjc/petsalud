package com.example.petsalud.controller;

import com.example.petsalud.model.Propietario;
import com.example.petsalud.service.PropietarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/propietarios")
public class PropietarioController {

    private final PropietarioService propietarioService;

    public PropietarioController(PropietarioService propietarioService) {
        this.propietarioService = propietarioService;
    }

    @GetMapping
    public String lista(@RequestParam(required = false) String q,
                        @RequestParam(required = false) String activo,
                        Model model) {
        Boolean activoBool = "1".equals(activo) ? Boolean.TRUE
                           : "0".equals(activo) ? Boolean.FALSE
                           : null;
        model.addAttribute("propietarios", propietarioService.search(q, activoBool));
        model.addAttribute("q",            Objects.toString(q, ""));
        model.addAttribute("activoFiltro", Objects.toString(activo, ""));
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
}
