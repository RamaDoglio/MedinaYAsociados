package com.medina.asocDev.Medina.Asociados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="cobros")
public class Cobro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCobro;

    private Float importeTotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idEstado", nullable = false)
    private Estado estadoCobro;
}
