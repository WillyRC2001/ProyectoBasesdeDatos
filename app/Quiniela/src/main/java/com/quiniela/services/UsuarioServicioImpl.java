package com.quiniela.services;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quiniela.dto.UsuarioRegistroDTO;
import com.quiniela.models.CorreoElectronico;
import com.quiniela.models.NombreCompleto;
import com.quiniela.models.Usuario;
import com.quiniela.repositories.CorreoRepositorio;
import com.quiniela.repositories.InscripcionRepositorio;
import com.quiniela.repositories.NombreCompletoRepositorio;
import com.quiniela.repositories.PronosticoRepositorio;
import com.quiniela.repositories.UsuarioRepositorio;

@Service
public class UsuarioServicioImpl implements UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final CorreoRepositorio correoRepositorio;
    private final NombreCompletoRepositorio nombreCompletoRepositorio;
    private final PronosticoRepositorio pronosticoRepositorio;
    private final InscripcionRepositorio inscripcionRepositorio;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioServicioImpl(UsuarioRepositorio usuarioRepositorio,
                               CorreoRepositorio correoRepositorio,
                               NombreCompletoRepositorio nombreCompletoRepositorio,
                               PronosticoRepositorio pronosticoRepositorio,
                               InscripcionRepositorio inscripcionRepositorio,
                               BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.correoRepositorio = correoRepositorio;
        this.nombreCompletoRepositorio = nombreCompletoRepositorio;
        this.pronosticoRepositorio = pronosticoRepositorio;
        this.inscripcionRepositorio = inscripcionRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario guardarUsuario(UsuarioRegistroDTO registroDTO) {

        if (registroDTO.getFechaNacimiento() == null ||
                LocalDate.now().minusYears(18).isBefore(registroDTO.getFechaNacimiento())) {
            throw new RuntimeException("Debes tener al menos 18 años para registrarte");
        }

        if (correoRepositorio.existsByCorreo(registroDTO.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        if (usuarioRepositorio.existsByNombreUsuario(registroDTO.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        Usuario usuario = new Usuario(
            "ROLE_USER",
            registroDTO.getNombreUsuario(),
            passwordEncoder.encode(registroDTO.getContrasena()),
            registroDTO.getFechaNacimiento()
        );
        usuarioRepositorio.save(usuario);

        NombreCompleto nombreCompleto = new NombreCompleto(
            registroDTO.getNombre(),
            registroDTO.getSegundoNombre(),
            registroDTO.getPrimerApellido(),
            registroDTO.getSegundoApellido(),
            usuario
        );
        nombreCompletoRepositorio.save(nombreCompleto);

        CorreoElectronico correo = new CorreoElectronico(
            registroDTO.getCorreo(),
            usuario
        );
        correoRepositorio.save(correo);

        return usuario;
    }

    @Override
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepositorio.findAll();
    }

    @Override
    public Usuario obtenerUsuarioPorId(Long idUsuario) {
        return usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    @Transactional
    public Usuario actualizarUsuario(Long idUsuario, Usuario datos, String correoNuevo) {
        Usuario usuario = obtenerUsuarioPorId(idUsuario);
        Usuario usuarioConMismoNombre = usuarioRepositorio.findByNombreUsuario(datos.getNombreUsuario());

        if (usuarioConMismoNombre != null && !usuarioConMismoNombre.getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        usuario.setNombreUsuario(datos.getNombreUsuario());
        usuario.setFechaNacimiento(datos.getFechaNacimiento());
        usuario.setRolUsuario(datos.getRolUsuario());

        if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(datos.getContrasena()));
        }

        if (correoNuevo != null && !correoNuevo.isBlank()) {
            CorreoElectronico correoExistente = correoRepositorio.findByCorreo(correoNuevo);
            if (correoExistente != null && !correoExistente.getUsuario().getIdUsuario().equals(idUsuario)) {
                throw new RuntimeException("El correo ya está registrado");
            }

            CorreoElectronico correoActual = correoRepositorio.findByUsuario(usuario);
            if (correoActual == null) {
                correoActual = new CorreoElectronico(correoNuevo, usuario);
            } else {
                correoActual.setCorreo(correoNuevo);
            }
            correoRepositorio.save(correoActual);
        }

        return usuarioRepositorio.save(usuario);
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long idUsuario) {
        Usuario usuario = obtenerUsuarioPorId(idUsuario);
        pronosticoRepositorio.deleteByUsuario(usuario);
        inscripcionRepositorio.deleteByUsuario(usuario);
        usuarioRepositorio.delete(usuario);
    }

    @Override
    public UserDetails loadUserByUsername(String valor) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.findByNombreUsuario(valor);

        if (usuario == null) {
            CorreoElectronico correo = correoRepositorio.findByCorreo(valor);
            if (correo != null) {
                usuario = correo.getUsuario();
            }
        }

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + valor);
        }

        Collection<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(usuario.getRolUsuario())
        );

        return new User(
            usuario.getNombreUsuario(),
            usuario.getContrasena(),
            authorities
        );
    }
}
