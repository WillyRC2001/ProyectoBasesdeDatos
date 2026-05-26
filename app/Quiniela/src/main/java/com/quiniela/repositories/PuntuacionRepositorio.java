package com.quiniela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.quiniela.models.Puntuacion;

public interface PuntuacionRepositorio extends JpaRepository<Puntuacion, Long> {}