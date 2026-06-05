package com.quiniela.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Correo_Electronico")
public class CorreoElectronico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Correo")
    private Long idCorreo;

    @Column(name = "Correo", unique = true)
    private String correo;

    @OneToOne
    @JoinColumn(name = "ID_Usuario")
    private Usuario usuario;

    public CorreoElectronico() {}

    public CorreoElectronico(String correo, Usuario usuario) {
        this.correo = correo;
        this.usuario = usuario;
    }

    public Long getIdCorreo() { return idCorreo; }
    public void setIdCorreo(Long idCorreo) { this.idCorreo = idCorreo; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
