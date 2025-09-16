package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.PermisosDTO;
import com.medina.asocDev.Medina.Asociados.entity.Permiso;
import com.medina.asocDev.Medina.Asociados.repo.PermisoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermisoService {

    @Autowired
    private PermisoRepository permisoRepository;

    // Crear nuevo permiso
    public PermisosDTO createPermiso(PermisosDTO permisosDTO) {
        Permiso permiso = new Permiso();
        permiso.setNombre(permisosDTO.getNombre());
        permiso.setDescripcion(permisosDTO.getDescripcion());

        Permiso permisoGuardado = permisoRepository.save(permiso);

        PermisosDTO result = new PermisosDTO();
        result.setIdPermiso(permisoGuardado.getIdPermiso());
        result.setNombre(permisoGuardado.getNombre());
        result.setDescripcion(permisoGuardado.getDescripcion());

        return result;
    }

    // Obtener todos los permisos
    public List<PermisosDTO> getAllPermisos() {
        return permisoRepository.findAll().stream()
                .map(permiso -> {
                    PermisosDTO dto = new PermisosDTO();
                    dto.setIdPermiso(permiso.getIdPermiso());
                    dto.setNombre(permiso.getNombre());
                    dto.setDescripcion(permiso.getDescripcion());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Obtener permiso por ID
    public PermisosDTO getPermisoById(Long id) {
        Optional<Permiso> permiso = permisoRepository.findById(id);
        return permiso.map(perm -> {
            PermisosDTO dto = new PermisosDTO();
            dto.setIdPermiso(perm.getIdPermiso());
            dto.setNombre(perm.getNombre());
            dto.setDescripcion(perm.getDescripcion());
            return dto;
        }).orElse(null);
    }

    // Obtener permiso por nombre
    public PermisosDTO getPermisoByNombre(String nombre) {
        Optional<Permiso> permiso = permisoRepository.findByNombre(nombre);
        return permiso.map(perm -> {
            PermisosDTO dto = new PermisosDTO();
            dto.setIdPermiso(perm.getIdPermiso());
            dto.setNombre(perm.getNombre());
            dto.setDescripcion(perm.getDescripcion());
            return dto;
        }).orElse(null);
    }

    // Actualizar permiso
    public PermisosDTO updatePermiso(Long id, PermisosDTO permisosDTO) {
        Optional<Permiso> permisoExistente = permisoRepository.findById(id);
        if (permisoExistente.isPresent()) {
            Permiso permiso = permisoExistente.get();
            permiso.setNombre(permisosDTO.getNombre());
            permiso.setDescripcion(permisosDTO.getDescripcion());

            Permiso permisoActualizado = permisoRepository.save(permiso);
            PermisosDTO result = new PermisosDTO();
            result.setIdPermiso(permisoActualizado.getIdPermiso());
            result.setNombre(permisoActualizado.getNombre());
            result.setDescripcion(permisoActualizado.getDescripcion());

            return result;
        }
        return null;
    }

    // Eliminar permiso
    public boolean deletePermiso(Long id) {
        if (permisoRepository.existsById(id)) {
            permisoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
