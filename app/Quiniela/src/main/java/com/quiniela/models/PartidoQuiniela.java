package com.quiniela.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Partido_Quiniela")
@IdClass(PartidoQuinielaId.class)
public class PartidoQuiniela {

    @Id
    @ManyToOne
    @JoinColumn(name = "Partido")
    private Partido partido;

    @Id
    @ManyToOne
    @JoinColumn(name = "Quiniela")
    private Quiniela quiniela;

    public PartidoQuiniela() {}

    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }

    public Quiniela getQuiniela() { return quiniela; }
    public void setQuiniela(Quiniela quiniela) { this.quiniela = quiniela; }
}
