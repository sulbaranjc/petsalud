package com.example.petsalud.controller.catalogo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/catalogos/medicamentos")
public class MedicamentoController {

    @GetMapping
    public String lista(Model model) {
        return "catalogos/medicamentos";
    }
}
