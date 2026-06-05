package com.quiniela.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Nombre_Completo")
public class NombreCompleto {

    @Id
    @Column(name = "Usuario")
    private Long usuario;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Segundo_Nombre")
    private String segundoNombre;

    @Column(name = "Primer_Apellido")
    private String primerApellido;

    @Column(name = "Segundo_Apellido")
    private String segundoApellido;

    @OneToOne  // ← esta anotación es la que faltaba
    @JoinColumn(name = "Usuario")
    @MapsId
    private Usuario usuarioRef;

    public NombreCompleto() {}

    public NombreCompleto(String nombre, String segundoNombre, String primerApellido, String segundoApellido, Usuario usuarioRef) {
        this.nombre = nombre;
        this.segundoNombre = segundoNombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.usuarioRef = usuarioRef;
    }

    public Long getUsuario() { return usuario; }
    public void setUsuario(Long usuario) { this.usuario = usuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSegundoNombre() { return segundoNombre; }
    public void setSegundoNombre(String segundoNombre) { this.segundoNombre = segundoNombre; }

    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }

    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }

    public Usuario getUsuarioRef() { return usuarioRef; }
    public void setUsuarioRef(Usuario usuarioRef) { this.usuarioRef = usuarioRef; }
}
