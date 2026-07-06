package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByEmail(String email);
	Optional<Usuario> findByDni(String dni);
    Optional<Usuario> findByTelefono(String telefono);
    Optional<Usuario> findByNombre(String nombre);
    @Query("SELECT u FROM Usuario u JOIN u.rolesUsuario r WHERE r.nombre = 'CLIENTE' AND u.dni LIKE CONCAT(:dni, '%')")
    List<Usuario> findByDniStartingWithAndRolCliente(@Param("dni") String dni);

    @Query(value = "SELECT u FROM Usuario u JOIN u.rolesUsuario r WHERE r.nombre = 'CLIENTE' AND u.dni LIKE CONCAT(:dni, '%')",
           countQuery = "SELECT COUNT(u) FROM Usuario u JOIN u.rolesUsuario r WHERE r.nombre = 'CLIENTE' AND u.dni LIKE CONCAT(:dni, '%')")
    Page<Usuario> findByDniStartingWithAndRolCliente(@Param("dni") String dni, Pageable pageable);
}
