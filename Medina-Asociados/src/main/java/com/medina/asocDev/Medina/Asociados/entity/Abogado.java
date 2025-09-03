package com.medina.asocDev.Medina.Asociados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="abogados")
public class Abogado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAbogado;

    @NotBlank(message = "Es necesario una matricula")
    @Column(unique = true)
    private String matricula;

    private Long idUsuario; // mismo ID que Usuario

    @OneToOne
    @MapsId
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idDireccion", referencedColumnName = "idDireccion")
    private Direccion direccion;

    @OneToMany(mappedBy = "abogadoTurno", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Turno> turnosAbogado= new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "abogadoXespecialidad",
            joinColumns = @JoinColumn(name = "idAbogado"),
            inverseJoinColumns = @JoinColumn(name = "idEspecialidad")
    )
    private List<Especialidad> especialidadesAbogado= new ArrayList<>();
}
