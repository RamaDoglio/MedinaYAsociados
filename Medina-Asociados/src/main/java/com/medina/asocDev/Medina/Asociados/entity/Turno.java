package com.medina.asocDev.Medina.Asociados.entity;

@Data
@Entity
@Table(name= "Turnos")
public class Turno {
    @Id
    @GenerateValue(strategy = GenerationType.IDENTITY)
    private Long idTurno;

    private estadoActual
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    private String observaciones;

}
