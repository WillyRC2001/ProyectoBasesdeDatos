package com.quiniela.repositories;

import com.quiniela.models.Quiniela;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuinielaRepositorio extends JpaRepository<Quiniela, Long> {
    List<Quiniela> findByEstado(String estado);

}
