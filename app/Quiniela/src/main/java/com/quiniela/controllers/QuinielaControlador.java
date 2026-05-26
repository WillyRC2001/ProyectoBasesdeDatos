package com.quiniela.controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quiniela.dto.PronosticoDTO;
import com.quiniela.models.Pronostico;
import com.quiniela.models.Quiniela;
import com.quiniela.services.QuinielaServicio;

@Controller
@RequestMapping("/quinielas")
public class QuinielaControlador {

    private final QuinielaServicio quinielaServicio;

    public QuinielaControlador(QuinielaServicio quinielaServicio) {
        this.quinielaServicio = quinielaServicio;
    }

    // Lista de quinielas abiertas
    @GetMapping
    public String listarQuinielas(Model model, Principal principal) {
        List<Quiniela> quinielas = quinielaServicio.obtenerQuinielasAbiertas();
        model.addAttribute("quinielas", quinielas);

        // Verificar quinielas del user
        for (Quiniela q : quinielas) {
            model.addAttribute("inscrito_" + q.getIdQuiniela(),
                    quinielaServicio.estaInscrito(q.getIdQuiniela(), principal.getName()));
        }

        return "quinielas";
    }

    @GetMapping("/{id}")
    public String detalleQuiniela(@PathVariable Long id, Model model, Principal principal) {
        Quiniela quiniela = quinielaServicio.obtenerQuinielaPorId(id);
        boolean inscrito = quinielaServicio.estaInscrito(id, principal.getName());

        model.addAttribute("quiniela", quiniela);
        model.addAttribute("inscrito", inscrito);

        return "quiniela-detalle";
    }

    @PostMapping("/{id}/inscribir")
    public String inscribirse(@PathVariable Long id, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            quinielaServicio.inscribirUsuario(id, principal.getName());
            redirectAttributes.addFlashAttribute("exito", "Te has inscrito correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/quinielas/" + id;
    }

    @GetMapping("/{id}/pronosticos")
    public String verPronosticos(@PathVariable Long id, Model model, Principal principal) {
        if (!quinielaServicio.estaInscrito(id, principal.getName())) {
            return "redirect:/quinielas/" + id;
        }

        Quiniela quiniela = quinielaServicio.obtenerQuinielaPorId(id);
        List<Pronostico> pronosticosExistentes = quinielaServicio.obtenerPronosticosUsuario(id, principal.getName());

        Map<Long, Pronostico> mapaPronosticos = new HashMap<>();
        for (Pronostico p : pronosticosExistentes) {
            mapaPronosticos.put(p.getPartido().getIdPartido(), p);
        }

        model.addAttribute("quiniela", quiniela);
        model.addAttribute("mapaPronosticos", mapaPronosticos);

        return "pronosticos";
    }

    @PostMapping("/{id}/pronosticos")
    public String guardarPronosticos(@PathVariable Long id,
            @RequestParam List<Long> idPartido,
            @RequestParam List<Integer> golesLocal,
            @RequestParam List<Integer> golesVisitante,
            RedirectAttributes redirectAttributes,
            Principal principal) {
        try {
            List<PronosticoDTO> pronosticos = new java.util.ArrayList<>();
            for (int i = 0; i < idPartido.size(); i++) {
                PronosticoDTO dto = new PronosticoDTO();
                dto.setIdPartido(idPartido.get(i));
                dto.setGolesLocal(golesLocal.get(i));
                dto.setGolesVisitante(golesVisitante.get(i));
                pronosticos.add(dto);
            }
            quinielaServicio.guardarPronosticos(id, pronosticos, principal.getName());
            redirectAttributes.addFlashAttribute("exito", "Pronósticos guardados correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/quinielas/" + id + "/pronosticos";
    }

}

