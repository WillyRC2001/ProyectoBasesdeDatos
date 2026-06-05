package com.quiniela.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quiniela.dto.RankingDTO;
import com.quiniela.models.Equipo;
import com.quiniela.models.Participacion;
import com.quiniela.models.Partido;
import com.quiniela.models.PartidoQuiniela;
import com.quiniela.models.Quiniela;
import com.quiniela.models.Usuario;
import com.quiniela.repositories.EquipoRepositorio;
import com.quiniela.repositories.ParticipacionRepositorio;
import com.quiniela.repositories.PartidoQuinielaRepositorio;
import com.quiniela.repositories.PartidoRepositorio;
import com.quiniela.repositories.QuinielaRepositorio;
import com.quiniela.services.QuinielaServicio;
import com.quiniela.services.UsuarioServicio;

@Controller
@RequestMapping("/admin")
public class AdminControlador {

    private final QuinielaServicio quinielaServicio;
    private final UsuarioServicio usuarioServicio;
    private final PartidoRepositorio partidoRepositorio;
    private final QuinielaRepositorio quinielaRepositorio;
    private final EquipoRepositorio equipoRepositorio;
    private final ParticipacionRepositorio participacionRepositorio;
    private final PartidoQuinielaRepositorio partidoQuinielaRepositorio;

    public AdminControlador(QuinielaServicio quinielaServicio,
                            UsuarioServicio usuarioServicio,
                            PartidoRepositorio partidoRepositorio,
                            QuinielaRepositorio quinielaRepositorio,
                            EquipoRepositorio equipoRepositorio,
                            ParticipacionRepositorio participacionRepositorio,
                            PartidoQuinielaRepositorio partidoQuinielaRepositorio) {
        this.quinielaServicio = quinielaServicio;
        this.usuarioServicio = usuarioServicio;
        this.partidoRepositorio = partidoRepositorio;
        this.quinielaRepositorio = quinielaRepositorio;
        this.equipoRepositorio = equipoRepositorio;
        this.participacionRepositorio = participacionRepositorio;
        this.partidoQuinielaRepositorio = partidoQuinielaRepositorio;
    }

    @GetMapping
    public String paginaAdmin() {
        return "admin";
    }

    @GetMapping("/usuarios")
    public String verUsuarios(Model model) {
        List<Usuario> usuarios = usuarioServicio.obtenerUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "admin-usuarios";
    }

    @PostMapping("/usuarios/{id}/editar")
    public String editarUsuario(@PathVariable Long id,
                                @RequestParam String nombreUsuario,
                                @RequestParam String rolUsuario,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNacimiento,
                                @RequestParam(required = false) String correo,
                                @RequestParam(required = false) String contrasena,
                                RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = new Usuario();
            usuario.setNombreUsuario(nombreUsuario);
            usuario.setRolUsuario(rolUsuario);
            usuario.setFechaNacimiento(fechaNacimiento);
            usuario.setContrasena(contrasena);
            usuarioServicio.actualizarUsuario(id, usuario, correo);
            redirectAttributes.addFlashAttribute("exito", "Usuario actualizado correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioServicio.eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("exito", "Usuario eliminado correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/partidos")
    public String verPartidos(Model model) {
        List<Partido> partidos = partidoRepositorio.findAll();
        model.addAttribute("partidos", partidos);
        model.addAttribute("equipos", equipoRepositorio.findAll());
        return "admin-partidos";
    }

    @PostMapping("/partidos")
    public String agregarPartido(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora,
                                 @RequestParam Long equipoLocalId,
                                 @RequestParam Long equipoVisitanteId,
                                 @RequestParam(defaultValue = "Pendiente") String estado,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (equipoLocalId.equals(equipoVisitanteId)) {
                throw new RuntimeException("El equipo local y visitante deben ser diferentes");
            }

            Equipo local = equipoRepositorio.findById(equipoLocalId)
                    .orElseThrow(() -> new RuntimeException("Equipo local no encontrado"));
            Equipo visitante = equipoRepositorio.findById(equipoVisitanteId)
                    .orElseThrow(() -> new RuntimeException("Equipo visitante no encontrado"));

            Partido partido = new Partido();
            partido.setFechaHora(fechaHora);
            partido.setEstado(estado == null || estado.isBlank() ? "Pendiente" : estado);
            partido.setGolesLocal(0);
            partido.setGolesVisitante(0);
            partidoRepositorio.save(partido);

            Participacion participacionLocal = new Participacion();
            participacionLocal.setPartido(partido);
            participacionLocal.setEquipo(local);
            participacionLocal.setLocalOVisitante("Local");
            participacionRepositorio.save(participacionLocal);

            Participacion participacionVisitante = new Participacion();
            participacionVisitante.setPartido(partido);
            participacionVisitante.setEquipo(visitante);
            participacionVisitante.setLocalOVisitante("Visitante");
            participacionRepositorio.save(participacionVisitante);

            redirectAttributes.addFlashAttribute("exito", "Partido agregado correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/partidos";
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
        model.addAttribute("partidos", partidoRepositorio.findAll());
        return "admin-quinielas";
    }

    @PostMapping("/quinielas")
    @Transactional
    public String agregarQuiniela(@RequestParam String nombreQuiniela,
                                  @RequestParam(required = false) String descripcion,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                                  @RequestParam String modalidad,
                                  @RequestParam String tipoPuntuacion,
                                  @RequestParam(defaultValue = "Abierta") String estado,
                                  @RequestParam(required = false) String reglasGenerales,
                                  @RequestParam(required = false) List<Long> partidosIds,
                                  RedirectAttributes redirectAttributes) {
        try {
            Quiniela quiniela = new Quiniela();
            cargarDatosQuiniela(quiniela, nombreQuiniela, descripcion, fechaInicio, fechaFin, modalidad,
                    tipoPuntuacion, estado, reglasGenerales);
            quinielaRepositorio.save(quiniela);
            guardarPartidosQuiniela(quiniela, partidosIds);
            redirectAttributes.addFlashAttribute("exito", "Quiniela agregada correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/quinielas";
    }

    @PostMapping("/quinielas/{id}/editar")
    @Transactional
    public String editarQuiniela(@PathVariable Long id,
                                 @RequestParam String nombreQuiniela,
                                 @RequestParam(required = false) String descripcion,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                                 @RequestParam String modalidad,
                                 @RequestParam String tipoPuntuacion,
                                 @RequestParam String estado,
                                 @RequestParam(required = false) String reglasGenerales,
                                 @RequestParam(required = false) List<Long> partidosIds,
                                 RedirectAttributes redirectAttributes) {
        try {
            Quiniela quiniela = quinielaRepositorio.findById(id)
                    .orElseThrow(() -> new RuntimeException("Quiniela no encontrada"));
            cargarDatosQuiniela(quiniela, nombreQuiniela, descripcion, fechaInicio, fechaFin, modalidad,
                    tipoPuntuacion, estado, reglasGenerales);
            quinielaRepositorio.save(quiniela);
            partidoQuinielaRepositorio.deleteByQuiniela(quiniela);
            guardarPartidosQuiniela(quiniela, partidosIds);
            redirectAttributes.addFlashAttribute("exito", "Quiniela actualizada correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/quinielas";
    }

    @PostMapping("/quinielas/{id}/cerrar")
    public String cerrarQuiniela(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Quiniela quiniela = quinielaRepositorio.findById(id)
                    .orElseThrow(() -> new RuntimeException("Quiniela no encontrada"));
            quiniela.setEstado("Cerrada");
            quinielaRepositorio.save(quiniela);
            redirectAttributes.addFlashAttribute("exito", "Quiniela cerrada correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/quinielas";
    }

    private void cargarDatosQuiniela(Quiniela quiniela,
                                     String nombreQuiniela,
                                     String descripcion,
                                     LocalDate fechaInicio,
                                     LocalDate fechaFin,
                                     String modalidad,
                                     String tipoPuntuacion,
                                     String estado,
                                     String reglasGenerales) {
        if (fechaFin.isBefore(fechaInicio)) {
            throw new RuntimeException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        quiniela.setNombreQuiniela(nombreQuiniela);
        quiniela.setDescripcion(descripcion);
        quiniela.setFechaInicio(fechaInicio);
        quiniela.setFechaFin(fechaFin);
        quiniela.setModalidad(modalidad);
        quiniela.setTipoPuntuacion(tipoPuntuacion);
        quiniela.setEstado(estado == null || estado.isBlank() ? "Abierta" : estado);
        quiniela.setReglasGenerales(reglasGenerales);
    }

    private void guardarPartidosQuiniela(Quiniela quiniela, List<Long> partidosIds) {
        if (partidosIds == null) {
            return;
        }

        for (Long idPartido : partidosIds) {
            Partido partido = partidoRepositorio.findById(idPartido)
                    .orElseThrow(() -> new RuntimeException("Partido no encontrado: " + idPartido));
            PartidoQuiniela partidoQuiniela = new PartidoQuiniela();
            partidoQuiniela.setQuiniela(quiniela);
            partidoQuiniela.setPartido(partido);
            partidoQuinielaRepositorio.save(partidoQuiniela);
        }
    }
}
