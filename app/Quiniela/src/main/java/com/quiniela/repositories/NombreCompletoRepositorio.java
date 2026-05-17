package com.quiniela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quiniela.models.NombreCompleto;

public interface NombreCompletoRepositorio extends JpaRepository<NombreCompleto, Long> {
}
