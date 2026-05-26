package com.quiniela.services;

import java.time.LocalDateTime;
import java.util.List;

import com.quiniela.dto.RankingDTO;
import com.quiniela.models.*;
import com.quiniela.repositories.*;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

import com.quiniela.dto.PronosticoDTO;

@Service
public class QuinielaServicioImpl implements QuinielaServicio {

    private final QuinielaRepositorio quinielaRepositorio;
    private final InscripcionRepositorio inscripcionRepositorio;
    private final PronosticoRepositorio pronosticoRepositorio;
    private final PartidoRepositorio partidoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final PuntuacionRepositorio puntuacionRepositorio;

    public QuinielaServicioImpl(QuinielaRepositorio quinielaRepositorio,
                                InscripcionRepositorio inscripcionRepositorio,
                                PronosticoRepositorio pronosticoRepositorio,
                                PartidoRepositorio partidoRepositorio,
                                UsuarioRepositorio usuarioRepositorio,
                                PuntuacionRepositorio puntuacionRepositorio) {   // ← agregar
        this.quinielaRepositorio = quinielaRepositorio;
        this.inscripcionRepositorio = inscripcionRepositorio;
        this.pronosticoRepositorio = pronosticoRepositorio;
        this.partidoRepositorio = partidoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.puntuacionRepositorio = puntuacionRepositorio; // ← agregar
    }

    @Override
    public List<Quiniela> obtenerQuinielasAbiertas() {
        return quinielaRepositorio.findByEstado("Abierta");
    }

    @Override
    public Quiniela obtenerQuinielaPorId(Long id) {
        return quinielaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiniela no encontrada"));
    }

    @Override
    public void inscribirUsuario(Long idQuiniela, String nombreUsuario) {
        Usuario usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario);
        Quiniela quiniela = obtenerQuinielaPorId(idQuiniela);

        if (estaInscrito(idQuiniela, nombreUsuario)) {
            throw new RuntimeException("Ya estás inscrito en esta quiniela");
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setUsuario(usuario);
        inscripcion.setQuiniela(quiniela);
        inscripcion.setAceptaReglas("SI");
        inscripcionRepositorio.save(inscripcion);
    }

    @Override
    public boolean estaInscrito(Long idQuiniela, String nombreUsuario) {
        Usuario usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario);
        Quiniela quiniela = obtenerQuinielaPorId(idQuiniela);
        return inscripcionRepositorio.existsByUsuarioAndQuiniela(usuario, quiniela);
    }

    @Override
    public void guardarPronosticos(Long idQuiniela, List<PronosticoDTO> pronosticos, String nombreUsuario) {
        Usuario usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario);
        Quiniela quiniela = obtenerQuinielaPorId(idQuiniela);

        for (PronosticoDTO dto : pronosticos) {
            Partido partido = partidoRepositorio.findById(dto.getIdPartido())
                    .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

            // Valida que el partido no haya iniciado
            if (partido.getFechaHora().isBefore(LocalDateTime.now())) {
                throw new RuntimeException(
                        "El partido " + dto.getIdPartido() + " ya inició, no puedes modificar el pronóstico");
            }

            // Valida que no exista el pronostico
            if (pronosticoRepositorio.existsByUsuarioAndPartidoAndQuiniela(usuario, partido, quiniela)) {
                throw new RuntimeException("Ya tienes un pronóstico para este partido");
            }

            Pronostico pronostico = new Pronostico();
            pronostico.setUsuario(usuario);
            pronostico.setPartido(partido);
            pronostico.setQuiniela(quiniela);
            pronostico.setGolesPrediccionLocal(dto.getGolesLocal());
            pronostico.setGolesPrediccionVisitante(dto.getGolesVisitante());
            pronostico.setFechaHoraIngreso(LocalDateTime.now());
            pronosticoRepositorio.save(pronostico);
        }
    }

    @Override
    public List<Pronostico> obtenerPronosticosUsuario(Long idQuiniela, String nombreUsuario) {
        Usuario usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario);
        Quiniela quiniela = obtenerQuinielaPorId(idQuiniela);
        return pronosticoRepositorio.findByUsuarioAndQuiniela(usuario, quiniela);
    }

    @Override
    public List<Inscripcion> obtenerInscripcionesUsuario(String nombreUsuario) {
        Usuario usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario);
        return inscripcionRepositorio.findByUsuario(usuario);
    }

    @Override
    public long contarPronosticosUsuario(String nombreUsuario) {
        Usuario usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario);
        return pronosticoRepositorio.countByUsuario(usuario);
    }

    @Override
    public void calcularPuntuaciones(Long idPartido) {
        Partido partido = partidoRepositorio.findById(idPartido)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        if (!"Finalizado".equalsIgnoreCase(partido.getEstado())) {
            throw new RuntimeException("El partido no ha finalizado aún");
        }

        List<Pronostico> pronosticos = pronosticoRepositorio.findAll().stream()
                .filter(p -> p.getPartido().getIdPartido().equals(idPartido))
                .collect(java.util.stream.Collectors.toList());

        int golesLocalReal = partido.getGolesLocal();
        int golesVisitanteReal = partido.getGolesVisitante();

        for (Pronostico p : pronosticos) {

            if (p.getPuntuacion() != null) continue;

            int puntos = 0;
            int predLocal = p.getGolesPrediccionLocal();
            int predVisitante = p.getGolesPrediccionVisitante();

            if (predLocal == golesLocalReal && predVisitante == golesVisitanteReal) {
                puntos = 3;

            } else {
                boolean realLocalGana    = golesLocalReal > golesVisitanteReal;
                boolean realVisitanteGana = golesVisitanteReal > golesLocalReal;
                boolean realEmpate       = golesLocalReal == golesVisitanteReal;

                boolean predLocalGana    = predLocal > predVisitante;
                boolean predVisitanteGana = predVisitante > predLocal;
                boolean predEmpate       = predLocal == predVisitante;

                if ((realLocalGana && predLocalGana) ||
                        (realVisitanteGana && predVisitanteGana) ||
                        (realEmpate && predEmpate)) {
                    puntos = 1;
                }
            }

            Puntuacion puntuacion = new Puntuacion(puntos, java.time.LocalDate.now());
            puntuacionRepositorio.save(puntuacion);

            p.setPuntuacion(puntuacion);
            pronosticoRepositorio.save(p);
        }
    }

    @Override
    public List<RankingDTO> obtenerRanking(Long idQuiniela) {
        Quiniela quiniela = obtenerQuinielaPorId(idQuiniela);

        List<Inscripcion> inscritos = inscripcionRepositorio.findByQuiniela(quiniela);

        List<RankingDTO> ranking = new java.util.ArrayList<>();

        for (Inscripcion ins : inscritos) {
            Usuario usuario = ins.getUsuario();

            List<Pronostico> pronosticos = pronosticoRepositorio
                    .findByQuinielaAndUsuario(quiniela, usuario);

            int total = pronosticos.stream()
                    .filter(p -> p.getPuntuacion() != null)
                    .mapToInt(p -> p.getPuntuacion().getPuntosObtenidos())
                    .sum();

            ranking.add(new RankingDTO(0, usuario.getNombreUsuario(), total));
        }

        ranking.sort((a, b) -> b.getPuntosTotal() - a.getPuntosTotal());

        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).setPosicion(i + 1);
        }

        return ranking;
    }
}
