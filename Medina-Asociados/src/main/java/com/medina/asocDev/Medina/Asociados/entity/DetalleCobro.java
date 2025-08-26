package com.medina.asocDev.Medina.Asociados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="detallesCobro")
public class DetalleCobro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalleCobro;

    private LocalDateTime fecha;

    private String descripcionCobro;

    private Float subTotal;

    private TipoCobro tipoCobro;
}
