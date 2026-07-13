package com.medina.asocDev.Medina.Asociados.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "El número no debe estar en blanco")
    private Integer numeroCalle;

    private String dpto;

    private String piso;

    @ManyToOne
    @JoinColumn(name = "idLocalidad")
    private Localidad localidad;

    private String provincia="Córdoba";
}
