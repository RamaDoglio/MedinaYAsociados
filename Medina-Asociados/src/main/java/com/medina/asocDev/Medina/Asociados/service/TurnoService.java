package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.CobroDTO;
import com.medina.asocDev.Medina.Asociados.dto.HorarioTurnoDTO;
import com.medina.asocDev.Medina.Asociados.dto.TurnoDTO;
import com.medina.asocDev.Medina.Asociados.entity.*;
import com.medina.asocDev.Medina.Asociados.repo.*;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private CobroRepository cobroRepository;

    @Autowired
    private CobroService cobroService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private HorarioTurnoRepository horarioTurnoRepository;

    // Crear turno (reserva)
    public TurnoDTO crearTurno(Turno turnoDTO) {

        LocalDateTime fechaTurno = turnoDTO.getHorarioTurno().getFechaHoraInicio();
        if (fechaTurno.isBefore(LocalDateTime.now().plusHours(24))) {
            throw new IllegalArgumentException("La reserva debe hacerse con al menos 24 horas de antelación");
        }

        // 2️⃣ Buscar entidades necesarias
        Usuario cliente = usuarioRepository.findById(turnoDTO.getClienteTurno().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Usuario abogado = usuarioRepository.findById(turnoDTO.getAbogadoTurno().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Abogado no encontrado"));
        Especialidad especialidad = especialidadRepository.findById(turnoDTO.getEspecialidad().getIdEspecialidad())
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));

        // 3️⃣ Estado inicial del turno
        Estado estadoPendientePago = estadoRepository.findByNombreAndAmbito("PENDIENTE_PAGO", "TURNO");

        // 4️⃣ Crear HorarioTurno (ya validado desde el front)
        HorarioTurnoDTO horarioDTO = HorarioTurnoService.createHorarioTurno(turnoDTO.getHorarioTurno());
        HorarioTurno horario = new HorarioTurno();
        horario.setIdHorarioTurno(horarioDTO.getIdHorarioTurno());
        horario.setFechaHoraInicio(horarioDTO.getFechaHoraInicio());

        // 5️⃣ Crear Cobro inicial (pendiente)
        CobroDTO cobroDTO = new CobroDTO();
        cobroDTO.setImporteTotal(turnoDTO.getCobro().getImporteTotal());
        cobroDTO.setIdEstado(turnoDTO.getCobro().getEstadoCobro().getIdEstado());
        CobroDTO cobroGuardado = cobroService.createCobro(cobroDTO);

        Cobro cobro = new Cobro();
        cobro.setIdCobro(cobroGuardado.getIdCobro());
        cobro.setImporteTotal(cobroGuardado.getImporteTotal());

        // 6️⃣ Crear y guardar el turno completo
        Turno turno = new Turno();
        turno.setClienteTurno(cliente);
        turno.setAbogadoTurno(abogado);
        turno.setEspecialidad(especialidad);
        turno.setEstadoActual(estadoPendientePago);
        turno.setHorarioTurno(horario);
        turno.setCobro(cobro);
        turno.setObservacionesCliente(turnoDTO.getObservacionesCliente());

        turnoRepository.save(turno);

        // 7️⃣ Devolver DTO
        return Utils.mapTurnoEntityToDTO(turno);
    }

    // Listar todos
    public List<Turno> listarTurnos() {
        return turnoRepository.findAll();
    }

    // Obtener por id
    public Turno obtenerPorId(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
    }

    // Actualizar observaciones
    public Turno actualizarTurno(Long id, Turno datos) {
        Turno turno = obtenerPorId(id);
        turno.setObservacionesCliente(datos.getObservacionesCliente());
        turno.setObservacionesAbogado(datos.getObservacionesAbogado());
        return turnoRepository.save(turno);
    }

    // Eliminar
    public void eliminarTurno(Long id) {
        turnoRepository.deleteById(id);
    }

    // Reprogramar
    public Turno reprogramarTurno(Long id, LocalDateTime nuevaFechaHora) {
        Turno turno = obtenerPorId(id);
        LocalDateTime fechaActual = turno.getHorarioTurno().getFechaHoraInicio();

        if (fechaActual.isBefore(LocalDateTime.now().plusHours(24))) {
            throw new IllegalArgumentException("No se puede reprogramar con menos de 24 horas de anticipación");
        }
        if (nuevaFechaHora.isBefore(LocalDateTime.now().plusHours(24))) {
            throw new IllegalArgumentException("La nueva fecha debe tener al menos 24 horas de antelación");
        }

        turno.getHorarioTurno().setFechaHoraInicio(nuevaFechaHora);
        turno.setEstadoActual(estadoRepository.findByNombreAndAmbito("REPROGRAMADO","TURNO"));

        return turnoRepository.save(turno);
    }

    // Cancelar
    public Turno cancelarTurno(Long id) {
        Turno turno = obtenerPorId(id);
        LocalDateTime fechaTurno = turno.getHorarioTurno().getFechaHoraInicio();

        if (fechaTurno.isBefore(LocalDateTime.now().plusHours(24))) {
            // No hay reembolso
            turno.setEstadoActual(estadoRepository.findByNombreAndAmbito("CANCELADO_SIN_REEMBOLSO","TURNO"));
        } else {
            // Con reembolso
            turno.setEstadoActual(estadoRepository.findByNombreAndAmbito("CANCELADO_CON_REEMBOLSO","TURNO"));

            if (turno.getCobro() != null) {
                cobroService.reembolsar(turno.getCobro().getIdCobro());
            }
        }

        return turnoRepository.save(turno);
    }
}


