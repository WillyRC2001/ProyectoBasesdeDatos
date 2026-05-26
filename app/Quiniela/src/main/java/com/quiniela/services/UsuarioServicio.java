package com.quiniela.services;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.quiniela.dto.UsuarioRegistroDTO;
import com.quiniela.models.Usuario;

public interface UsuarioServicio extends UserDetailsService{
    public Usuario guardarUsuario(UsuarioRegistroDTO registroDTO);
}
