package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.RolDTO;
import com.medina.asocDev.Medina.Asociados.entity.Rol;
import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.repo.RolRepository;
import com.medina.asocDev.Medina.Asociados.repo.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 🔥 NUEVO: Asignar rol a usuario
    @Transactional
    public void asignarRol(Long userId, Long rolId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + userId));
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado: " + rolId));

        // Verificar si ya tiene el rol
        if (!usuario.getRolesUsuario().contains(rol)) {
            usuario.getRolesUsuario().add(rol);
            usuarioRepository.save(usuario);
        }
    }

    // 🔥 NUEVO: Remover rol de usuario
    @Transactional
    public void removerRol(Long userId, Long rolId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + userId));
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado: " + rolId));

        // Remover si existe
        if (usuario.getRolesUsuario().contains(rol)) {
            usuario.getRolesUsuario().remove(rol);
            usuarioRepository.save(usuario);
        }
    }

    // 🔥 NUEVO: Obtener roles de usuario
    public List<String> getRolesByUserId(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + userId));

        return usuario.getRolesUsuario().stream()
                .map(Rol::getNombre)
                .collect(Collectors.toList());
    }

    // Crear nuevo rol
    public RolDTO createRol(RolDTO rolDTO) {
        Rol rol = new Rol();
        rol.setNombre(rolDTO.getNombre());
        rol.setDescripcion(rolDTO.getDescripcion());

        Rol rolGuardado = rolRepository.save(rol);

        RolDTO result = new RolDTO();
        result.setIdRol(rolGuardado.getIdRol());
        result.setNombre(rolGuardado.getNombre());
        result.setDescripcion(rolGuardado.getDescripcion());

        return result;
    }

    // Obtener todos los roles
    public List<RolDTO> getAllRoles() {
        return rolRepository.findAll().stream()
                .map(rol -> {
                    RolDTO dto = new RolDTO();
                    dto.setIdRol(rol.getIdRol());
                    dto.setNombre(rol.getNombre());
                    dto.setDescripcion(rol.getDescripcion());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Obtener rol por ID
    public RolDTO getRolById(Long id) {
        Optional<Rol> rol = rolRepository.findById(id);
        return rol.map(r -> {
            RolDTO dto = new RolDTO();
            dto.setIdRol(r.getIdRol());
            dto.setNombre(r.getNombre());
            dto.setDescripcion(r.getDescripcion());
            return dto;
        }).orElse(null);
    }

    // Obtener rol por nombre
    public RolDTO getRolByNombre(String nombre) {
        Optional<Rol> rol = rolRepository.findByNombre(nombre);
        return rol.map(r -> {
            RolDTO dto = new RolDTO();
            dto.setIdRol(r.getIdRol());
            dto.setNombre(r.getNombre());
            dto.setDescripcion(r.getDescripcion());
            return dto;
        }).orElse(null);
    }

    // Actualizar rol
    public RolDTO updateRol(Long id, RolDTO rolDTO) {
        Optional<Rol> rolExistente = rolRepository.findById(id);
        if (rolExistente.isPresent()) {
            Rol rol = rolExistente.get();
            rol.setNombre(rolDTO.getNombre());
            rol.setDescripcion(rolDTO.getDescripcion());

            Rol rolActualizado = rolRepository.save(rol);
            RolDTO result = new RolDTO();
            result.setIdRol(rolActualizado.getIdRol());
            result.setNombre(rolActualizado.getNombre());
            result.setDescripcion(rolActualizado.getDescripcion());

            return result;
        }
        return null;
    }

    // Eliminar rol
    public boolean deleteRol(Long id) {
        if (rolRepository.existsById(id)) {
            rolRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
