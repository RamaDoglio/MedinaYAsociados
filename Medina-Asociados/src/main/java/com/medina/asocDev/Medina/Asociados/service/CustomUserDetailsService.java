package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.CustomUserDetails;
import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.repo.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository userRepository;

    @Override
    @Cacheable(value = "users", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (usuario.getRol() != null && usuario.getRol().getNombre() != null) {
            authorities.add(new SimpleGrantedAuthority(usuario.getRol().getNombre()));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // 🔥 AGREGAR ESTO: CustomUserDetails con ID
        return new CustomUserDetails(
                usuario.getIdUsuario(),  // ← ID del usuario
                usuario.getEmail(),
                usuario.getPassword(),
                authorities
        );
    }
}