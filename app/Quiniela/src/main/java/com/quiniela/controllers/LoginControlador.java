package com.quiniela.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginControlador {

    @GetMapping("/login")
    public String iniciarSesion() {
        return "login";
    }
    

    @GetMapping("/")
    public String paginaInicio() {
        return "index";
    }

    @GetMapping("/index")  // ← agrega esto
    public String index() {
        return "index";
    }
    
}
