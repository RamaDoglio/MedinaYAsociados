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
    private Long idHorarioTurno;

    @OneToOne(mappedBy = "horarioTurno")
    private Turno turno;

    private LocalDateTime fechaHoraInicio;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEstado", nullable = false)
    private Estado estadoHorario;
}
