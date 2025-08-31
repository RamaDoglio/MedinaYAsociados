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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idCobro", nullable = false)
    private Cobro cobro;

    private LocalDateTime fecha;

    private String descripcionCobro;

    private Float subTotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idTipoCobro", nullable = false)
    private TipoCobro tipoCobro;
}
