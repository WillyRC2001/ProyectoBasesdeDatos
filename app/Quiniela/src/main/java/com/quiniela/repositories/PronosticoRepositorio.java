package com.quiniela.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quiniela.models.Partido;
import com.quiniela.models.Pronostico;
import com.quiniela.models.Quiniela;
import com.quiniela.models.Usuario;

public interface PronosticoRepositorio extends JpaRepository<Pronostico, Long> {
    boolean existsByUsuarioAndPartidoAndQuiniela(Usuario usuario, Partido partido, Quiniela quiniela);

    List<Pronostico> findByUsuarioAndQuiniela(Usuario usuario, Quiniela quiniela);

    long countByUsuario(Usuario usuario);
}
