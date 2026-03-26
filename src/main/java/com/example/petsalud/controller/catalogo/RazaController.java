package com.example.petsalud.controller.catalogo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/catalogos/razas")
public class RazaController {

    @GetMapping
    public String lista(Model model) {
        return "catalogos/razas";
    }
}
