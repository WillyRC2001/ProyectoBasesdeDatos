package com.quiniela.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Pronostico")
public class Pronostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Pronostico")
    private Long idPronostico;

    @ManyToOne
    @JoinColumn(name = "Partido")
    private Partido partido;

    @ManyToOne
    @JoinColumn(name = "Usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "Quiniela")
    private Quiniela quiniela;

    @Column(name = "Goles_Prediccion_Local")
    private Integer golesPrediccionLocal;

    @Column(name = "Goles_Prediccion_Visitante")
    private Integer golesPrediccionVisitante;

    @Column(name = "Fecha_Hora_Ingreso")
    private LocalDateTime fechaHoraIngreso;

    public Pronostico() {}

    public Long getIdPronostico() { return idPronostico; }
    public void setIdPronostico(Long idPronostico) { this.idPronostico = idPronostico; }

    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Quiniela getQuiniela() { return quiniela; }
    public void setQuiniela(Quiniela quiniela) { this.quiniela = quiniela; }

    public Integer getGolesPrediccionLocal() { return golesPrediccionLocal; }
    public void setGolesPrediccionLocal(Integer golesPrediccionLocal) { this.golesPrediccionLocal = golesPrediccionLocal; }

    public Integer getGolesPrediccionVisitante() { return golesPrediccionVisitante; }
    public void setGolesPrediccionVisitante(Integer golesPrediccionVisitante) { this.golesPrediccionVisitante = golesPrediccionVisitante; }

    public LocalDateTime getFechaHoraIngreso() { return fechaHoraIngreso; }
    public void setFechaHoraIngreso(LocalDateTime fechaHoraIngreso) { this.fechaHoraIngreso = fechaHoraIngreso; }
}
