package com.example.proyectspring.service;

import java.util.List;

import com.example.proyectspring.entity.Usuario;

public interface UsuarioService {
    public List<Usuario> findAll();

    public Usuario saveUsuario(Usuario usuario);

    public Usuario findOne(Long id);

    public void delete(Long id);
}
