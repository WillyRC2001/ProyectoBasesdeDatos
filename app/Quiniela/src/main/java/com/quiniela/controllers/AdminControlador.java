package com.quiniela.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quiniela.dto.RankingDTO;
import com.quiniela.models.Partido;
import com.quiniela.models.Quiniela;
import com.quiniela.models.Usuario;
import com.quiniela.repositories.PartidoRepositorio;
import com.quiniela.repositories.QuinielaRepositorio;
import com.quiniela.repositories.UsuarioRepositorio;
import com.quiniela.services.QuinielaServicio;

@Controller
@RequestMapping("/admin")
public class AdminControlador {

    private final QuinielaServicio quinielaServicio;
    private final PartidoRepositorio partidoRepositorio;
    private final QuinielaRepositorio quinielaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public AdminControlador(QuinielaServicio quinielaServicio,
                            PartidoRepositorio partidoRepositorio,
                            QuinielaRepositorio quinielaRepositorio,
                            UsuarioRepositorio usuarioRepositorio) {
        this.quinielaServicio = quinielaServicio;
        this.partidoRepositorio = partidoRepositorio;
        this.quinielaRepositorio = quinielaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @GetMapping
    public String paginaAdmin() {
        return "admin";
    }

    @GetMapping("/usuarios")
    public String verUsuarios(Model model) {
        List<Usuario> usuarios = usuarioRepositorio.findAll();
        model.addAttribute("usuarios", usuarios);
        return "admin-usuarios";
    }

    @GetMapping("/partidos")
    public String verPartidos(Model model) {
        List<Partido> partidos = partidoRepositorio.findAll();
        model.addAttribute("partidos", partidos);
        return "admin-partidos";
    }

    @PostMapping("/partidos/{id}/resultado")
    public String actualizarResultado(@PathVariable Long id,
                                      @RequestParam Integer golesLocal,
                                      @RequestParam Integer golesVisitante,
                                      RedirectAttributes redirectAttributes) {
        try {
            Partido partido = partidoRepositorio.findById(id)
                    .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
            partido.setGolesLocal(golesLocal);
            partido.setGolesVisitante(golesVisitante);
            partido.setEstado("Finalizado");
            partidoRepositorio.save(partido);

            quinielaServicio.calcularPuntuaciones(id);

            redirectAttributes.addFlashAttribute("exito", "Resultado actualizado y puntos calculados");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/partidos";
    }

    @GetMapping("/quinielas/{id}/ranking")
    public String verRanking(@PathVariable Long id, Model model) {
        Quiniela quiniela = quinielaServicio.obtenerQuinielaPorId(id);
        List<RankingDTO> ranking = quinielaServicio.obtenerRanking(id);
        model.addAttribute("quiniela", quiniela);
        model.addAttribute("ranking", ranking);
        return "admin-ranking";
    }

    @GetMapping("/quinielas")
    public String verQuinielas(Model model) {
        List<Quiniela> quinielas = quinielaRepositorio.findAll();
        model.addAttribute("quinielas", quinielas);
        return "admin-quinielas";
    }
}