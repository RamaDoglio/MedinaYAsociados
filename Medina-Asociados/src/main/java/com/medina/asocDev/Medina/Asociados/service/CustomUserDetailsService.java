package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.repo.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository userRepository;

    @Override
    @Cacheable(value = "users", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Name Not Found"));

        List<SimpleGrantedAuthority> authorities = usuario.getRol() != null && usuario.getRol().getNombre() != null
                ? List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre()))
                : List.of();

        return new User(usuario.getEmail(), usuario.getPassword(), authorities);
    }
}