package com.medina.asocDev.Medina.Asociados.entity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String nombre;

    private String apellido;

    @Column(unique = true)
    private String dni;

    @Column(unique = true)
    private String telefono;

    @Column(unique = true)
    private String email;

    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRol", nullable = false)
    private Rol rol;

    @OneToMany(mappedBy = "iDTurno", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Turno> listaTurnos= new ArrayList<>();
}
