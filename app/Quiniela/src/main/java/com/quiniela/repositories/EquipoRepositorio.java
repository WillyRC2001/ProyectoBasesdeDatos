package com.quiniela.repositories;

import com.quiniela.models.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipoRepositorio extends JpaRepository<Equipo, Long> {
}
