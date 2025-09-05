package com.medina.asocDev.Medina.Asociados.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="historialesTurno")
public class HistorialTurno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorial;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idTurno", nullable = false)
    private Turno turno;

    private LocalDateTime fechaHoraInicio;

    private LocalDateTime fechaHoraFin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idEstado", nullable = false)
    private Estado estadoHistorial;
}
