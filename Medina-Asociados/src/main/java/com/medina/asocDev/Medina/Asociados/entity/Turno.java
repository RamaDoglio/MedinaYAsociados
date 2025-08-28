package com.medina.asocDev.Medina.Asociados.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name= "Turnos")
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTurno;

    private Estado estadoActual;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idEspecialidad", referencedColumnName = "idEspecialidad")
    private Especialidad especialidad;

    private Cobro cobro;

    private String observaciones;

    private HorarioTurno horarioTurno;

    private Cliente clienteTurno;
    @ManyToOne
    @JoinColumn(name = "iDAbogado")
    private Abogado abogadoTurno;

    private List<HistorialTurno> historialTurno= new ArrayList<>();
}
