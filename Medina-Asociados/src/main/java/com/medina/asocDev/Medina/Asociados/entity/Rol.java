package com.medina.asocDev.Medina.Asociados.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="rol")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;

    private String nombre;

    private String descripcion;

    @ManyToMany
    @JoinTable(
            name = "rolXpermiso",
            joinColumns = @JoinColumn(name = "idRol"),
            inverseJoinColumns = @JoinColumn(name = "idPermiso")
    )
    private List<Permiso> permisos = new ArrayList<>();
}
