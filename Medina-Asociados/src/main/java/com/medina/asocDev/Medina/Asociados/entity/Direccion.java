package com.medina.asocDev.Medina.Asociados.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name="direcciones")
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDireccion;

    @NotBlank(message = "La calle no debe estar en blanco")
    private String calle;

    @NotBlank(message = "El número no debe estar en blanco")
    private Integer numeroCalle;

    @NotBlank(message = "El departamento no debe estar en blanco")
    private String dpto;

    @NotBlank(message = "La localidad no debe estar en blanco")
    private Localidad localidad;

    private String provincia="Córdoba";
}
