package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    @Query("SELECT t FROM Turno t JOIN FETCH t.historialTurno WHERE t.iDTurno = :iDTurno")
    Optional<Turno> findTurnoConHistorial(@Param("iDTurno") Long iDTurno);

    // 🔹 1. Listar todos los turnos de un cliente
    List<Turno> findByClienteTurno_IdUsuario(Long idCliente);

    // 🔹 2. Listar todos los turnos de un abogado
    List<Turno> findByAbogadoTurno_IdUsuario(Long idAbogado);

    // 🔹 3. Turnos futuros de un abogado (a partir de una fecha)
    List<Turno> findByAbogadoTurno_IdUsuarioAndHorarioTurno_FechaHoraInicioAfter(
            Long idAbogado, LocalDateTime fecha);

    // 🔹 4. Turnos por estado (ej: Reservado, Cancelado, Terminado)
    @Query("SELECT t FROM Turno t WHERE t.estadoActual.estadoNombre = :nombre")
    List<Turno> findByEstadoActualNombre(@Param("nombre") String nombre);

    // 🔹 5. Turnos de un abogado en una fecha específica
    @Query("SELECT t FROM Turno t " +
            "WHERE t.abogadoTurno.idUsuario = :idAbogado " +
            "AND DATE(t.horarioTurno.fechaHoraInicio) = DATE(:fecha)")
    List<Turno> findTurnosDeAbogadoEnFecha(
            @Param("idAbogado") Long idAbogado,
            @Param("fecha") LocalDateTime fecha);

    // 🔹 6. Turnos de un cliente en un rango de fechas
    List<Turno> findByClienteTurno_IdUsuarioAndHorarioTurno_FechaHoraInicioBetween(
            Long idCliente, LocalDateTime desde, LocalDateTime hasta);

    // 🔹 7. Buscar turnos por especialidad
    List<Turno> findByEspecialidad_Nombre(String nombreEspecialidad);
}
