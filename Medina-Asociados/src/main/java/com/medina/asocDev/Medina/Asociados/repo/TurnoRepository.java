package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.dto.EstadisticaDTO;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    @Query("SELECT t FROM Turno t JOIN FETCH t.historialTurno WHERE t.idTurno = :idTurno")
    Optional<Turno> findTurnoConHistorial(@Param("idTurno") Long idTurno);


    List<Turno> findByClienteTurno_IdUsuario(Long idCliente);
    Page<Turno> findByClienteTurno_IdUsuario(Long idCliente, Pageable pageable);


    List<Turno> findByAbogadoTurno_IdUsuario(Long idAbogado);
    Page<Turno> findByAbogadoTurno_IdUsuario(Long idAbogado, Pageable pageable);


    List<Turno> findByAbogadoTurno_IdUsuarioAndHorarioTurnoAfter(Long idAbogado, LocalDateTime fecha);


    @Query("SELECT t FROM Turno t WHERE t.estadoActual.nombreEstado = :nombre")
    List<Turno> findByEstadoActualNombre(@Param("nombre") String nombreEstado);


    @Query("SELECT t FROM Turno t " +
            "WHERE t.abogadoTurno.idUsuario = :idAbogado " +
            "AND DATE(t.horarioTurno) = DATE(:fecha)")
    List<Turno> findTurnosDeAbogadoEnFecha(
            @Param("idAbogado") Long idAbogado,
            @Param("fecha") LocalDateTime fecha);


    List<Turno> findByEspecialidad_NombreEspecialidad(String nombreEspecialidad);


    Optional<Turno> findByIdTurno(Long idTurno);


    @Query("SELECT t FROM Turno t " +
            "WHERE t.abogadoTurno.idUsuario = :idAbogado " +
            "AND DATE(t.horarioTurno) = :fecha " +
            "AND t.estadoActual.nombreEstado <> 'CANCELADO'")
    List<Turno> findTurnosOcupadosPorAbogadoEnFecha(
            @Param("idAbogado") Long idAbogado,
            @Param("fecha") LocalDate fecha);


    List<Turno> findByEstadoActualNombreEstadoIn(List<String> nombres);


    List<Turno> findByEstadoActualNombreEstado(String nombre);


    List<Turno> findByHorarioTurnoBetween(LocalDateTime desde, LocalDateTime hasta);

    @Query("SELECT t FROM Turno t WHERE t.abogadoTurno.idUsuario = :idAbogado " +
            "AND t.horarioTurno >= :fechaDesde " +
            "AND t.horarioTurno <= :fechaHasta " +
            "AND (:estado = '' OR t.estadoActual.nombreEstado = :estado) " +
            "AND (:cliente = '' OR CAST(t.clienteTurno.nombre AS text) LIKE CONCAT('%', :cliente, '%') " +
            "     OR CAST(t.clienteTurno.apellido AS text) LIKE CONCAT('%', :cliente, '%'))")
    Page<Turno> buscarTurnosAbogado(@Param("idAbogado") Long idAbogado,
                                    @Param("fechaDesde") LocalDateTime fechaDesde,
                                    @Param("fechaHasta") LocalDateTime fechaHasta,
                                    @Param("estado") String estado,
                                    @Param("cliente") String cliente,
                                    Pageable pageable);

    @Query("SELECT new com.medina.asocDev.Medina.Asociados.dto.EstadisticaDTO(" +
            "CASE " +
            "WHEN t.estadoActual.nombreEstado = 'FINALIZADO' THEN 'COMPLETADOS' " +
            "WHEN t.estadoActual.nombreEstado = 'NO_ASISTIO' THEN 'INASISTENCIAS' " +
            "WHEN t.estadoActual.nombreEstado IN ('CANCELADO_SIN_REEMBOLSO', 'CANCELADO_CON_REEMBOLSO', 'EXPIRO_PAGO') THEN 'CANCELADOS' " +
            "END, COUNT(t)) " +
            "FROM Turno t " +
            "WHERE t.estadoActual.nombreEstado IN ('FINALIZADO', 'NO_ASISTIO', 'CANCELADO_SIN_REEMBOLSO', 'CANCELADO_CON_REEMBOLSO', 'EXPIRO_PAGO') " +
            "GROUP BY CASE " +
            "WHEN t.estadoActual.nombreEstado = 'FINALIZADO' THEN 'COMPLETADOS' " +
            "WHEN t.estadoActual.nombreEstado = 'NO_ASISTIO' THEN 'INASISTENCIAS' " +
            "WHEN t.estadoActual.nombreEstado IN ('CANCELADO_SIN_REEMBOLSO', 'CANCELADO_CON_REEMBOLSO', 'EXPIRO_PAGO') THEN 'CANCELADOS' " +
            "END")
    List<EstadisticaDTO> getVolumenTurnosPorEstado();

    @Query("SELECT new com.medina.asocDev.Medina.Asociados.dto.EstadisticaDTO(t.especialidad.nombreEspecialidad, COUNT(t)) " +
            "FROM Turno t GROUP BY t.especialidad.nombreEspecialidad")
    List<EstadisticaDTO> getTurnosPorEspecialidad();

    @Query("SELECT new com.medina.asocDev.Medina.Asociados.dto.EstadisticaDTO(t.estadoActual.nombreEstado, COUNT(t)) " +
            "FROM Turno t " +
            "WHERE t.clienteTurno.idUsuario = :idCliente " +
            "GROUP BY t.estadoActual.nombreEstado")
    List<EstadisticaDTO> countTurnosByClienteAndEstados(@Param("idCliente") Long idCliente);
}