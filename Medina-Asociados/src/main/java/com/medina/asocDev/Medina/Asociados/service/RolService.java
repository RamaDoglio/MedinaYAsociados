package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.RolDTO;
import com.medina.asocDev.Medina.Asociados.entity.Rol;
import com.medina.asocDev.Medina.Asociados.repo.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

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
