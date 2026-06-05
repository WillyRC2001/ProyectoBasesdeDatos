package com.quiniela.repositories;

import com.quiniela.models.Partido;
import com.quiniela.models.PartidoQuiniela;
import com.quiniela.models.PartidoQuinielaId;
import com.quiniela.models.Quiniela;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartidoQuinielaRepositorio extends JpaRepository<PartidoQuiniela, PartidoQuinielaId> {
    void deleteByPartido(Partido partido);

    void deleteByQuiniela(Quiniela quiniela);
}
