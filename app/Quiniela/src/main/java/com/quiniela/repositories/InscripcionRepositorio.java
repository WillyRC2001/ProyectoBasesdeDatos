package com.quiniela.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quiniela.models.Inscripcion;
import com.quiniela.models.Quiniela;
import com.quiniela.models.Usuario;

public interface InscripcionRepositorio extends JpaRepository<Inscripcion, Long> {
    boolean existsByUsuarioAndQuiniela(Usuario usuario, Quiniela quiniela);

    Inscripcion findByUsuarioAndQuiniela(Usuario usuario, Quiniela quiniela);

    List<Inscripcion> findByUsuario(Usuario usuario);
}
