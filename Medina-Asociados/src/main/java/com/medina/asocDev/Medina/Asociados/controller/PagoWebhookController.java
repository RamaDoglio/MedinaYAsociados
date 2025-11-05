package com.medina.asocDev.Medina.Asociados.controller;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import com.medina.asocDev.Medina.Asociados.entity.Cobro;
import com.medina.asocDev.Medina.Asociados.repo.CobroRepository;
import com.medina.asocDev.Medina.Asociados.service.CobroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoWebhookController {

    private final CobroRepository cobroRepository;
    private final CobroService cobroService;

    public PagoWebhookController(CobroRepository cobroRepository, CobroService cobroService) {
        this.cobroRepository = cobroRepository;
        this.cobroService = cobroService;
    }

    // Soporte para notificaciones con query params (GET/POST)
    @RequestMapping(value = "/notificacion", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<String> recibirNotificacion(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String topic,
            @RequestBody(required = false) Map<String, Object> payload) {

        try {
            String tipo = topic;
            String paymentId = id;

            // Si vino por body (Webhooks v1)
            if (payload != null && payload.get("type") != null) {
                tipo = (String) payload.get("type");
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                paymentId = data != null ? (String) data.get("id") : paymentId;
            }

            if (!"payment".equals(tipo)) {
                return ResponseEntity.ok("Tipo ignorado: " + tipo);
            }
            if (paymentId == null) {
                return ResponseEntity.badRequest().body("Falta paymentId");
            }

            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.valueOf(paymentId));

            // externalReference = idCobro (String)
            Long idCobro = Long.valueOf(payment.getExternalReference());
            Cobro cobro = cobroRepository.findById(idCobro)
                    .orElseThrow(() -> new RuntimeException("Cobro no encontrado: " + idCobro));

            // Idempotencia: si ya guardamos paymentId y el cobro está PAGADO/REEMBOLSADO, no reprocesar
            String estadoCobro = cobro.getEstadoCobro() != null ? cobro.getEstadoCobro().getNombreEstado() : null;
            if (cobro.getPaymentId() != null &&
                    ("PAGADO".equals(estadoCobro) || "REEMBOLSADO".equals(estadoCobro))) {
                return ResponseEntity.ok("Evento duplicado ignorado");
            }

            // Guardar el paymentId real (una vez)
            cobro.setPaymentId(payment.getId());
            cobroRepository.save(cobro);

            String status = payment.getStatus(); // approved, refunded, in_process, rejected, cancelled, etc.
            switch (status) {
                case "approved" -> {
                    // Actualiza cobro y turno + historial
                    cobroService.marcarComoPagado(cobro);
                }
                case "refunded" -> {
                    // Solo estado interno; el refund real lo disparás desde cancelarTurno
                    cobroService.reembolsar(cobro);
                }
                case "in_process" -> {
                    // opcional: mapear a estado interno EN_PROCESO
                }
                case "rejected", "cancelled" -> {
                    // opcional: mapear a RECHAZADO / CANCELADO
                }
                default -> {
                    // Loguear estados no contemplados
                    System.out.println("Estado de pago no mapeado: " + status);
                }
            }

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}