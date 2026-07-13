package com.medina.asocDev.Medina.Asociados.dto;

public class PagarTurnoResponse {
    private TurnoDTO turno;
    private String initPoint;

    public PagarTurnoResponse(TurnoDTO turno, String initPoint) {
        this.turno = turno;
        this.initPoint = initPoint;
    }
    public TurnoDTO getTurno() { return turno; }
    public String getInitPoint() { return initPoint; }
}