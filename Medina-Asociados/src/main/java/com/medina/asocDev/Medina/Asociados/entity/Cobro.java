package com.medina.asocDev.Medina.Asociados.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="cobros")
public class Cobro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCobro;

    @OneToOne(mappedBy = "cobro")
    private Turno turno;

    private Float importeTotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idEstado", nullable = false)
    private Estado estadoCobro;

    @Column(name = "payment_id")
    private Long paymentId; // ID real de Mercado Pago
}
