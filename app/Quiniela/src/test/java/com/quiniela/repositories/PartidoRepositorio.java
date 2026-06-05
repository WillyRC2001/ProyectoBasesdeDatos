package com.quiniela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quiniela.models.Partido;

public interface PartidoRepositorio extends JpaRepository<Partido, Long> {
}
