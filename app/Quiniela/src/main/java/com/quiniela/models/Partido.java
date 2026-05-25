package com.quiniela.models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "Partido")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Partido")
    private Long idPartido;

    @Column(name = "Estado")
    private String estado;

    @Column(name = "Fecha_Hora")
    private LocalDateTime fechaHora;

    @Column(name = "Goles_Local")
    private Integer golesLocal;

    @Column(name = "Goles_Visitante")
    private Integer golesVisitante;

    @OneToMany(mappedBy = "partido", fetch = FetchType.EAGER)
    private List<Participacion> participaciones;

    public Partido() {
    }

    public Partido(Long idPartido, String estado, LocalDateTime fechaHora, Integer golesLocal, Integer golesVisitante,
            List<Participacion> participaciones) {
        this.idPartido = idPartido;
        this.estado = estado;
        this.fechaHora = fechaHora;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.participaciones = participaciones;
    }

    public Long getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(Long idPartido) {
        this.idPartido = idPartido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Integer getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(Integer golesLocal) {
        this.golesLocal = golesLocal;
    }

    public Integer getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(Integer golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    public List<Participacion> getParticipaciones() {
        return participaciones;
    }

    public void setParticipaciones(List<Participacion> participaciones) {
        this.participaciones = participaciones;
    }
}
