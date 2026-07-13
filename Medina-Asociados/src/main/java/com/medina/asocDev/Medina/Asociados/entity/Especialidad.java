package com.medina.asocDev.Medina.Asociados.entity;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name="especialidades")
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEspecialidad;

    private String nombreEspecialidad;

    private String descripcionEspecialidad;
}
