package com.medina.asocDev.Medina.Asociados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name="tiposCobro")
public class TipoCobro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoCobro;

    @NotBlank(message = "El nombre del tipo cobro no debe estar en blanco")
    private String nombreTipoCobro;

    private String descTipoCobro;
}
