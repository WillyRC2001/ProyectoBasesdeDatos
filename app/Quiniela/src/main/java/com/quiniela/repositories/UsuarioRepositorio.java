package com.quiniela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.quiniela.models.Usuario;


public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    boolean existsByNombreUsuario(String nombreUsuario);
    Usuario findByNombreUsuario(String nombreUsuario);
}
