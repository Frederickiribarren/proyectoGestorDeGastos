package com.example.proyectspring.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyectspring.dao.IUsuarioDao;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.UsuarioService;

import jakarta.transaction.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    private IUsuarioDao usuarioDao;

   @Transactional
    public Usuario saveUsuario(Usuario usuario) {
        return usuarioDao.save(usuario);
    }

    @Transactional
    public void delete(Long id) {
        usuarioDao.deleteById(id);
    }

    @Transactional
    public Usuario findOne(Long id) {
        return usuarioDao.findById(id).orElse(null);
    }

    @Transactional
    public List<Usuario> findAll() {
        return (List<Usuario>) usuarioDao.findAll();
    }

}