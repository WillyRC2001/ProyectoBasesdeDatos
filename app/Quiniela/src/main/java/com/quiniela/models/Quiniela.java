package com.quiniela.models;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "Quiniela")
public class Quiniela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Quiniela")
    private Long idQuiniela;

    @Column(name = "Nombre_Quiniela")
    private String nombreQuiniela;

    @Column(name = "Descripcion")
    private String descripcion;

    @Column(name = "Fecha_Inicio")
    private LocalDate fechaInicio;

    @Column(name = "Fecha_Fin")
    private LocalDate fechaFin;

    @Column(name = "Modalidad")
    private String modalidad;

    @Column(name = "Tipo_Puntuacion")
    private String tipoPuntuacion;

    @Column(name = "Estado")
    private String estado;

    @Column(name = "Reglas_Generales")
    private String reglasGenerales;

    @OneToMany(mappedBy = "quiniela", fetch = FetchType.EAGER)
    private List<PartidoQuiniela> partidoQuinielas;

    public Quiniela() {
    }

    public Quiniela(Long idQuiniela, String nombreQuiniela, String descripcion, LocalDate fechaInicio,
            LocalDate fechaFin, String modalidad, String tipoPuntuacion, String estado, String reglasGenerales) {
        this.idQuiniela = idQuiniela;
        this.nombreQuiniela = nombreQuiniela;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.modalidad = modalidad;
        this.tipoPuntuacion = tipoPuntuacion;
        this.estado = estado;
        this.reglasGenerales = reglasGenerales;
    }

    public Long getIdQuiniela() {
        return idQuiniela;
    }

    public void setIdQuiniela(Long idQuiniela) {
        this.idQuiniela = idQuiniela;
    }

    public String getNombreQuiniela() {
        return nombreQuiniela;
    }

    public void setNombreQuiniela(String nombreQuiniela) {
        this.nombreQuiniela = nombreQuiniela;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public String getTipoPuntuacion() {
        return tipoPuntuacion;
    }

    public void setTipoPuntuacion(String tipoPuntuacion) {
        this.tipoPuntuacion = tipoPuntuacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getReglasGenerales() {
        return reglasGenerales;
    }

    public void setReglasGenerales(String reglasGenerales) {
        this.reglasGenerales = reglasGenerales;
    }

    public List<PartidoQuiniela> getPartidoQuinielas() {
        return partidoQuinielas;
    }

    public void setPartidoQuinielas(List<PartidoQuiniela> partidoQuinielas) {
        this.partidoQuinielas = partidoQuinielas;
    }

    
}
