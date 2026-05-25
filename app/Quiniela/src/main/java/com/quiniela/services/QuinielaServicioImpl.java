package com.quiniela.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.quiniela.dto.PronosticoDTO;
import com.quiniela.models.Inscripcion;
import com.quiniela.models.Partido;
import com.quiniela.models.Pronostico;
import com.quiniela.models.Quiniela;
import com.quiniela.models.Usuario;
import com.quiniela.repositories.InscripcionRepositorio;
import com.quiniela.repositories.PartidoRepositorio;
import com.quiniela.repositories.PronosticoRepositorio;
import com.quiniela.repositories.QuinielaRepositorio;
import com.quiniela.repositories.UsuarioRepositorio;

@Service
public class QuinielaServicioImpl implements QuinielaServicio {

    private final QuinielaRepositorio quinielaRepositorio;
    private final InscripcionRepositorio inscripcionRepositorio;
    private final PronosticoRepositorio pronosticoRepositorio;
    private final PartidoRepositorio partidoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public QuinielaServicioImpl(QuinielaRepositorio quinielaRepositorio,
            InscripcionRepositorio inscripcionRepositorio,
            PronosticoRepositorio pronosticoRepositorio,
            PartidoRepositorio partidoRepositorio,
            UsuarioRepositorio usuarioRepositorio) {
        this.quinielaRepositorio = quinielaRepositorio;
        this.inscripcionRepositorio = inscripcionRepositorio;
        this.pronosticoRepositorio = pronosticoRepositorio;
        this.partidoRepositorio = partidoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
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
}
