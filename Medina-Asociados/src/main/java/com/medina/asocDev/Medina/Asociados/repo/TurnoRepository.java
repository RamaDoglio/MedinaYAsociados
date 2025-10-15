package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    @Query("SELECT t FROM Turno t JOIN FETCH t.historialTurno WHERE t.idTurno = :idTurno")
    Optional<Turno> findTurnoConHistorial(@Param("idTurno") Long idTurno);

    //Listar todos los turnos de un cliente
    List<Turno> findByClienteTurno_IdUsuario(Long idCliente);

    //Listar todos los turnos de un abogado
    List<Turno> findByAbogadoTurno_IdUsuario(Long idAbogado);

    //Turnos futuros de un abogado (a partir de una fecha)
    List<Turno> findByAbogadoTurno_IdUsuarioAndHorarioTurno_FechaHoraInicioAfter(
            Long idAbogado, LocalDateTime fecha);

    //Turnos por estado (ej: Reservado, Cancelado, Terminado)
    @Query("SELECT t FROM Turno t WHERE t.estadoActual.nombreEstado = :nombre")
    List<Turno> findByEstadoActualNombre(@Param("nombre") String nombreEstado);

    //Turnos de un abogado en una fecha específica
    @Query("SELECT t FROM Turno t " +
            "WHERE t.abogadoTurno.idUsuario = :idAbogado " +
            "AND DATE(t.horarioTurno.fechaHoraInicio) = DATE(:fecha)")
    List<Turno> findTurnosDeAbogadoEnFecha(
            @Param("idAbogado") Long idAbogado,
            @Param("fecha") LocalDateTime fecha);

    //Turnos de un cliente en un rango de fechas
    List<Turno> findByClienteTurno_IdUsuarioAndHorarioTurno_FechaHoraInicioBetween(
            Long idCliente, LocalDateTime desde, LocalDateTime hasta);

    //Buscar turnos por especialidad
    List<Turno> findByEspecialidad_NombreEspecialidad(String nombreEspecialidad);

    //Buscar turnos por id
    // Buscar un turno por su ID (usando Optional para reflejar la unicidad)
    Optional<Turno> findByIdTurno(Long idTurno);

    @Query("SELECT t FROM Turno t WHERE t.abogadoTurno.id = :idAbogado AND DATE(t.horarioTurno) = :fecha AND t.estadoActual.nombreEstado <> 'CANCELADO' ")
    List<Turno> findTurnosOcupadosPorAbogadoEnFecha(
            @Param("idAbogado") Long idAbogado,
            @Param("fecha") LocalDate fecha);
}
