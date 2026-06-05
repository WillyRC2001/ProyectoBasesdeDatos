package com.quiniela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.quiniela.models.CorreoElectronico;
import com.quiniela.models.Usuario;

public interface CorreoRepositorio extends JpaRepository<CorreoElectronico, Long> {
    boolean existsByCorreo(String correo);
    CorreoElectronico findByCorreo(String correo);
    CorreoElectronico findByUsuario(Usuario usuario);
}

