package com.medina.asocDev.Medina.Asociados.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="usuarioXturno")
public class UsuarioXTurno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTurno", nullable = false)
    private Turno turno;
}
