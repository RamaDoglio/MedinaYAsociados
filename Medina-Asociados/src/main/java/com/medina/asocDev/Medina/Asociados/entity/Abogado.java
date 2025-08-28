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

    @NotBlank(message = "El nombre no debe estar en blanco")
    private String nombre;

    @NotBlank(message = "El apellido no debe estar en blanco")
    private String apellido;

    @NotBlank(message = "El DNI no debe estar en blanco")
    @Column(unique = true)
    private Integer DNI;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idDireccion", referencedColumnName = "idDireccion")
    private Direccion direccion;

    @NotBlank(message = "El número de telefono no debe estar en blanco")
    @Column(unique = true)
    private String telefono;

    @NotBlank(message = "El email no debe estar en blanco")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "La contraseña no debe estar en blanco")
    private String password;

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
