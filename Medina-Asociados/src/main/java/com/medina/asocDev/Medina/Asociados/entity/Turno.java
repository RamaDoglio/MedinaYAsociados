package com.medina.asocDev.Medina.Asociados.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name= "Turnos")
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTurno;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEstado", nullable = false)
    private Estado estadoActual;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idEspecialidad", referencedColumnName = "idEspecialidad")
    private Especialidad especialidad;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idCobro", referencedColumnName = "idCobro")
    private Cobro cobro;

    private String observacionesCliente;

    private String observacionesAbogado;

    private LocalDateTime horarioTurno;

    @ManyToOne
    @JoinColumn(name = "idCliente")
    private Usuario clienteTurno;

    @ManyToOne
    @JoinColumn(name = "idAbogado")
    private Usuario abogadoTurno;

    @OneToMany(mappedBy = "turno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialTurno> historialTurno = new ArrayList<>();
}
