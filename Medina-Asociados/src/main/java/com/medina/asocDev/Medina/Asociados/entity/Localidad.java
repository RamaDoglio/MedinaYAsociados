package com.medina.asocDev.Medina.Asociados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name="localidades")
public class Localidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLocalidad;

    @NotBlank(message = "El nombre no debe estar en blanco")
    private String nombreLocalidad;

    @NotBlank(message = "El codigo postal no debe estar en blanco")
    private String codigoPostal;
}
