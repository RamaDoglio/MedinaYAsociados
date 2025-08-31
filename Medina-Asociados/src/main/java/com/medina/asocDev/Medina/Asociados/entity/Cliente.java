package com.medina.asocDev.Medina.Asociados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

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

    @OneToMany(mappedBy = "clienteTurno", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Turno> listaTurnos= new ArrayList<>();
}
