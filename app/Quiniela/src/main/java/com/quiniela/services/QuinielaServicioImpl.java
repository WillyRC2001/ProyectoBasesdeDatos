package com.quiniela.services;

import java.time.LocalDateTime;
import java.util.List;

import com.quiniela.dto.RankingDTO;
import com.quiniela.models.*;
import com.quiniela.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;
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
    private final JdbcTemplate jdbcTemplate;

    public QuinielaServicioImpl(QuinielaRepositorio quinielaRepositorio,
                                InscripcionRepositorio inscripcionRepositorio,
                                PronosticoRepositorio pronosticoRepositorio,
                                PartidoRepositorio partidoRepositorio,
                                UsuarioRepositorio usuarioRepositorio,
                                PuntuacionRepositorio puntuacionRepositorio,
                                JdbcTemplate jdbcTemplate) {
        this.quinielaRepositorio = quinielaRepositorio;
        this.inscripcionRepositorio = inscripcionRepositorio;
        this.pronosticoRepositorio = pronosticoRepositorio;
        this.partidoRepositorio = partidoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.puntuacionRepositorio = puntuacionRepositorio;
        this.jdbcTemplate = jdbcTemplate;
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

    //Usando el Script
    @Override
    public void inscribirUsuario(Long idQuiniela, String nombreUsuario) {
        Usuario usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario);

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        jdbcTemplate.update(
                "EXEC PR_INSCRIBIRSE_QUINIELA ?, ?, ?",
                usuario.getIdUsuario(),
                idQuiniela,
                "Sí"
        );
    }

    @Override
    public boolean estaInscrito(Long idQuiniela, String nombreUsuario) {
        Usuario usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario);
        Quiniela quiniela = obtenerQuinielaPorId(idQuiniela);
        return inscripcionRepositorio.existsByUsuarioAndQuiniela(usuario, quiniela);
    }

    //Usando el Script
    @Override
    public void guardarPronosticos(Long idQuiniela, List<PronosticoDTO> pronosticos, String nombreUsuario) {
        Usuario usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario);

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        for (PronosticoDTO dto : pronosticos) {
            try {
                jdbcTemplate.update(
                        "EXEC PR_CREAR_PRONOSTICO ?, ?, ?, ?, ?",
                        usuario.getIdUsuario(),
                        idQuiniela,
                        dto.getIdPartido(),
                        dto.getGolesLocal(),
                        dto.getGolesVisitante()
                );
            } catch (DataAccessException e) {
                throw new RuntimeException("No se pudo guardar el pronóstico: " + obtenerMensajeSql(e));
            }
        }
    }
    private String obtenerMensajeSql(DataAccessException e) {
        Throwable cause = e.getRootCause();

        if (cause != null && cause.getMessage() != null) {
            return cause.getMessage();
        }

        return e.getMessage();
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

    //Usando el Script
    @Override
    public List<RankingDTO> obtenerRanking(Long idQuiniela) {
        String sql = """
        SELECT 
            Nombre_Usuario,
            Puntaje_Total
        FROM VW_RANKING_QUINIELA
        WHERE ID_Quiniela = ?
        ORDER BY Puntaje_Total DESC
    """;

        List<RankingDTO> ranking = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new RankingDTO(
                        rowNum + 1,
                        rs.getString("Nombre_Usuario"),
                        rs.getInt("Puntaje_Total")
                ),
                idQuiniela
        );

        return ranking;
    }
}
