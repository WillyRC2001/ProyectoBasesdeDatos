package com.quiniela.services;

import com.quiniela.models.Quiniela;
import com.quiniela.models.Inscripcion;
import com.quiniela.models.Pronostico;
import com.quiniela.dto.PronosticoDTO;
import java.util.List;

public interface QuinielaServicio {
    List<Quiniela> obtenerQuinielasAbiertas();

    Quiniela obtenerQuinielaPorId(Long id);

    void inscribirUsuario(Long idQuiniela, String nombreUsuario);

    boolean estaInscrito(Long idQuiniela, String nombreUsuario);

    void guardarPronosticos(Long idQuiniela, List<PronosticoDTO> pronosticos, String nombreUsuario);

    List<Pronostico> obtenerPronosticosUsuario(Long idQuiniela, String nombreUsuario);

    List<Inscripcion> obtenerInscripcionesUsuario(String nombreUsuario);

    long contarPronosticosUsuario(String nombreUsuario);

}