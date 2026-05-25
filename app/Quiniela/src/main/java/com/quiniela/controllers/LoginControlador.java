package com.quiniela.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import com.quiniela.models.Inscripcion;
import com.quiniela.services.QuinielaServicio;

@Controller
public class LoginControlador {

    private final QuinielaServicio quinielaServicio;

    public LoginControlador(QuinielaServicio quinielaServicio) {
        this.quinielaServicio = quinielaServicio;
    }

    @GetMapping("/login")
    public String iniciarSesion() {
        return "login";
    }

    @GetMapping("/")
    public String paginaInicio(Model model, Principal principal) {
        List<Inscripcion> inscripciones = quinielaServicio.obtenerInscripcionesUsuario(principal.getName());
        long totalPronosticos = quinielaServicio.contarPronosticosUsuario(principal.getName());

        model.addAttribute("inscripciones", inscripciones);
        model.addAttribute("totalPronosticos", totalPronosticos);
        return "index";
    }

    @GetMapping("/index") 
    public String index() {
        return "index";
    }

}
