package com.quiniela.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Usuario")
    private Long idUsuario;

    @Column(name = "Rol_Usuario")
    private String rolUsuario;

    @Column(name = "Nombre_Usuario", unique = true)
    private String nombreUsuario;

    @Column(name = "Contraseña")
    private String contrasena;

    @Column(name = "Fecha_Nacimiento")
    private LocalDate fechaNacimiento;

    @OneToOne(mappedBy = "usuarioRef", cascade = CascadeType.ALL)  // ← corregido
    private NombreCompleto nombreCompleto;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private CorreoElectronico correoElectronico;

    public Usuario() {}

    public Usuario(String rolUsuario, String nombreUsuario, String contrasena, LocalDate fechaNacimiento) {
        this.rolUsuario = rolUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.fechaNacimiento = fechaNacimiento;
    }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getRolUsuario() { return rolUsuario; }
    public void setRolUsuario(String rolUsuario) { this.rolUsuario = rolUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public NombreCompleto getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(NombreCompleto nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public CorreoElectronico getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(CorreoElectronico correoElectronico) { this.correoElectronico = correoElectronico; }
}
