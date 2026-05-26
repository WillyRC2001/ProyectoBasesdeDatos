package com.quiniela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.quiniela.models.CorreoElectronico;

public interface CorreoRepositorio extends JpaRepository<CorreoElectronico, Long> {
    boolean existsByCorreo(String correo);
    CorreoElectronico findByCorreo(String correo);
}
