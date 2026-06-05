package com.quiniela.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Inscripcion")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Inscripcion")
    private Long idInscripcion;

    @ManyToOne
    @JoinColumn(name = "Usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "Quiniela")
    private Quiniela quiniela;

    @Column(name = "Acepta_Reglas")
    private String aceptaReglas;

    public Inscripcion() {
    }

    public Inscripcion(Long idInscripcion, Usuario usuario, Quiniela quiniela, String aceptaReglas) {
        this.idInscripcion = idInscripcion;
        this.usuario = usuario;
        this.quiniela = quiniela;
        this.aceptaReglas = aceptaReglas;
    }

    public Long getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(Long idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Quiniela getQuiniela() {
        return quiniela;
    }

    public void setIdQuiniela(Quiniela quiniela) {
        this.quiniela = quiniela;
    }

    public String getAceptaReglas() {
        return aceptaReglas;
    }

    public void setAceptaReglas(String aceptaReglas) {
        this.aceptaReglas = aceptaReglas;
    }

    public void setQuiniela(Quiniela quiniela) {
        this.quiniela = quiniela;
    }

    
}
