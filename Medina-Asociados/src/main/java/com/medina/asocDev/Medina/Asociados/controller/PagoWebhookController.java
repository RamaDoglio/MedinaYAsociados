package com.medina.asocDev.Medina.Asociados.controller;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import com.medina.asocDev.Medina.Asociados.entity.Cobro;
import com.medina.asocDev.Medina.Asociados.repo.CobroRepository;
import com.medina.asocDev.Medina.Asociados.service.CobroService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoWebhookController {

    private final CobroRepository cobroRepository;
    private final CobroService cobroService;
    private final String frontendUrl;

    public PagoWebhookController(CobroRepository cobroRepository, CobroService cobroService,
                                 @Value("${mp.frontend-url}") String frontendUrl) {
        this.cobroRepository = cobroRepository;
        this.cobroService = cobroService;
        this.frontendUrl = frontendUrl;
    }

    @GetMapping("/redirect")
    public void redirectPago(
            @RequestParam Long turnoId,
            @RequestParam String result,
            HttpServletResponse response) throws IOException {
        String redirectUrl = frontendUrl + "/payment/" + turnoId + "/result?status=" + result;
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.write("<html><body>" +
                "<p>Pago exitoso. Redirigiendo...</p>" +
                "<script>window.location.href='" + redirectUrl + "';</script>" +
                "<a href='" + redirectUrl + "'>Haga clic aquí si no es redirigido automáticamente</a>" +
                "</body></html>");
        writer.flush();
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

            System.out.println("Notificación recibida - tipo: " + tipo + ", paymentId: " + paymentId);

            if (!"payment".equals(tipo)) {
                return ResponseEntity.ok("Tipo ignorado: " + tipo);
            }
            if (paymentId == null) {
                return ResponseEntity.badRequest().body("Falta paymentId");
            }

            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.valueOf(paymentId));

            String extRef = payment.getExternalReference();
            System.out.println("Payment obtenido - ID: " + payment.getId() + ", externalReference: " + extRef + ", status: " + payment.getStatus());

            if (extRef == null) {
                return ResponseEntity.badRequest().body("El payment no tiene externalReference");
            }

            // externalReference = idCobro (String)
            Long idCobro = Long.valueOf(extRef);
            Cobro cobro = cobroRepository.findById(idCobro)
                    .orElseThrow(() -> new RuntimeException("Cobro no encontrado: " + idCobro));

            // Idempotencia: si ya tiene paymentId, el pago ya fue procesado
            if (cobro.getPaymentId() != null) {
                return ResponseEntity.ok("Evento duplicado ignorado");
            }

            // Guardar el paymentId real (una vez) y obtener entidad managed
            cobro.setPaymentId(payment.getId());
            cobro = cobroRepository.save(cobro);

            String status = payment.getStatus(); // approved, refunded, in_process, rejected, cancelled, etc.
            switch (status) {
                case "approved" -> {
                    System.out.println("Procesando pago aprobado para cobro ID: " + idCobro);
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
