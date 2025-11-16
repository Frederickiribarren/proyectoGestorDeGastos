package com.example.proyectspring.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.proyectspring.dao.IUsuarioDao;
import com.example.proyectspring.entity.Usuario;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUsuarioDao usuarioDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioDao.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Verificar si el usuario está activo
        if (!usuario.isActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo");
        }

        // Crear lista de autoridades (roles)
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (usuario.getRol() != null && !usuario.getRol().isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // Retornar UserDetails con email como username
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!usuario.isActivo())
                .build();
    }
}
