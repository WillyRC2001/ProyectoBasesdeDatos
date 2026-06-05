package com.quiniela.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.quiniela.dto.UsuarioRegistroDTO;
import com.quiniela.models.Usuario;

public interface UsuarioServicio extends UserDetailsService{
    Usuario guardarUsuario(UsuarioRegistroDTO registroDTO);

    List<Usuario> obtenerUsuarios();

    Usuario obtenerUsuarioPorId(Long idUsuario);

    Usuario actualizarUsuario(Long idUsuario, Usuario usuario, String correo);

    void eliminarUsuario(Long idUsuario);
}
