package com.quiniela.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Equipo")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Equipo")
    private Long idEquipo;

    @Column(name = "Pais")
    private String pais;

    @Column(name = "Nombre_Equipo")
    private String nombreEquipo;

    public Equipo() {
    }

    public Equipo(Long idEquipo, String pais, String nombreEquipo) {
        this.idEquipo = idEquipo;
        this.pais = pais;
        this.nombreEquipo = nombreEquipo;
    }

    public Long getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Long idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }
}
