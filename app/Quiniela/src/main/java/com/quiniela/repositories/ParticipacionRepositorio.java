package com.quiniela.repositories;

import com.quiniela.models.Participacion;
import com.quiniela.models.Partido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipacionRepositorio extends JpaRepository<Participacion, Long> {
    void deleteByPartido(Partido partido);
}
