package com.quiniela.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Participacion")
public class Participacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Participacion")
    private Long idParticipacion;

    @ManyToOne
    @JoinColumn(name = "Equipo")
    private Equipo equipo;

    @ManyToOne
    @JoinColumn(name = "Partido")
    private Partido partido;

    @Column(name = "Local_O_Visitante")
    private String localOVisitante;

    public Participacion() {
    }

    public Participacion(Long idParticipacion, Equipo equipo, Partido partido, String localOVisitante) {
        this.idParticipacion = idParticipacion;
        this.equipo = equipo;
        this.partido = partido;
        this.localOVisitante = localOVisitante;
    }

    public Long getIdParticipacion() {
        return idParticipacion;
    }

    public void setIdParticipacion(Long idParticipacion) {
        this.idParticipacion = idParticipacion;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public Partido getPartido() {
        return partido;
    }

    public void setPartido(Partido partido) {
        this.partido = partido;
    }

    public String getLocalOVisitante() {
        return localOVisitante;
    }

    public void setLocalOVisitante(String localOVisitante) {
        this.localOVisitante = localOVisitante;
    }
}