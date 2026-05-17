package com.quiniela.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminControlador {

    @GetMapping("/admin")
    public String paginaAdmin() {
        return "admin";
    }
    
}


