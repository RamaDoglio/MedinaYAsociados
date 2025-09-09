package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.repo.UsuarioRepository;
import com.medina.asocDev.Medina.Asociados.repo.RolRepository;
import com.medina.asocDev.Medina.Asociados.entity.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario create(Usuario usuario) {
        // Validar email único
        if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Validar password
        if (usuario.getPassword().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }

        // Encriptar password
        String hashedPassword = new BCryptPasswordEncoder().encode(usuario.getPassword());
        usuario.setPassword(hashedPassword);

        // Asignar automáticamente el rol CLIENTE
        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));
        usuario.setRol(rolCliente);
        
        // Guardar en BD
        return usuarioRepository.save(usuario);
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario update(Long id, Usuario usuarioDetails) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar email único (excepto para el propio usuario)
        usuarioRepository.findByEmail(usuarioDetails.getEmail())
            .filter(u -> !u.getIdUsuario().equals(id))
            .ifPresent(v -> { throw new RuntimeException("El correo electrónico ya está registrado"); });

        // Validar dni único (excepto para el propio usuario)
        usuarioRepository.findByDni(usuarioDetails.getDni())
            .filter(u -> !u.getIdUsuario().equals(id))
            .ifPresent(v -> { throw new RuntimeException("El DNI ya está registrado"); });

        usuario.setNombre(usuarioDetails.getNombre());
        usuario.setApellido(usuarioDetails.getApellido());
        usuario.setDni(usuarioDetails.getDni());
        usuario.setTelefono(usuarioDetails.getTelefono());
        usuario.setEmail(usuarioDetails.getEmail());
        usuario.setPassword(usuarioDetails.getPassword());
        // El rol se mantiene como CLIENTE

        return usuarioRepository.save(usuario);
        }
}
