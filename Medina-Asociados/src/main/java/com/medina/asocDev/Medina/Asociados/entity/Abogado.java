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

    @OneToOne
    @MapsId
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
            name = "abogadoXespecialidad",
            joinColumns = @JoinColumn(name = "idAbogado"),
            inverseJoinColumns = @JoinColumn(name = "idEspecialidad")
    )
    private List<Especialidad> especialidadesAbogado= new ArrayList<>();
}
