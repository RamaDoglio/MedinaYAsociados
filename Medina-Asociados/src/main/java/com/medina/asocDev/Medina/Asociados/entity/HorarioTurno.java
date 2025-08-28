package com.medina.asocDev.Medina.Asociados.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="horariosTurno")
public class HorarioTurno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHorario;

    private LocalDateTime fechaHoraInicio;

    private LocalDateTime fechaHoraFin;

    private Estado estadoHorario;
}
