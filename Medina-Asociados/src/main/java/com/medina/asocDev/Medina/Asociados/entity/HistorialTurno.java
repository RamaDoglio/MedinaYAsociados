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

    private LocalDateTime fechaHoraInicio;

    private LocalDateTime fechaHoraFin;

    private Estado estadoHistorial;
}
