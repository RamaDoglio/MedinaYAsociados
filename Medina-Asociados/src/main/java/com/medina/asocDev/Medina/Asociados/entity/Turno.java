package com.medina.asocDev.Medina.Asociados.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name= "Turnos")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTurno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEspecialidad", nullable = false)
    private Especialidad especialidad;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idCobro", referencedColumnName = "idCobro")
    private Cobro cobro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEstado", nullable = false)
    private Estado estadoActual;

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
