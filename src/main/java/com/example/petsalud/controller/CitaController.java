package com.example.petsalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/citas")
public class CitaController {

    @GetMapping
    public String lista(Model model) {
        return "citas/lista";
    }
}
