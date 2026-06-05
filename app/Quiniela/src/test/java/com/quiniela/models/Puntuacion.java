package com.quiniela.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Puntuacion")
public class Puntuacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Puntuacion")
    private Long idPuntuacion;

    @Column(name = "Puntos_Obtenidos")
    private Integer puntosObtenidos;

    @Column(name = "Fecha_Calculo")
    private LocalDate fechaCalculo;

    public Puntuacion() {}

    public Puntuacion(Integer puntosObtenidos, LocalDate fechaCalculo) {
        this.puntosObtenidos = puntosObtenidos;
        this.fechaCalculo = fechaCalculo;
    }

    public Long getIdPuntuacion() { return idPuntuacion; }

    public Integer getPuntosObtenidos() { return puntosObtenidos; }
    public void setPuntosObtenidos(Integer p) { this.puntosObtenidos = p; }

    public LocalDate getFechaCalculo() { return fechaCalculo; }
    public void setFechaCalculo(LocalDate f) { this.fechaCalculo = f; }
}