package com.medina.asocDev.Medina.Asociados.controller;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import com.medina.asocDev.Medina.Asociados.entity.Cobro;
import com.medina.asocDev.Medina.Asociados.repo.CobroRepository;
import com.medina.asocDev.Medina.Asociados.service.CobroService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
        response.sendRedirect(redirectUrl);
    }


    @Transactional
    @RequestMapping(value = "/notificacion", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<String> recibirNotificacion(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String topic,
            @RequestBody(required = false) Map<String, Object> payload) {

        try {
            String tipo = topic;
            String paymentId = id;


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


            Long idCobro = Long.valueOf(extRef);
            Cobro cobro = cobroRepository.findByIdWithLock(idCobro)
                    .orElseThrow(() -> new RuntimeException("Cobro no encontrado: " + idCobro));


            if (cobro.getPaymentId() != null) {
                return ResponseEntity.ok("Evento duplicado ignorado");
            }


            cobro.setPaymentId(payment.getId());
            cobro = cobroRepository.save(cobro);

            String status = payment.getStatus();
            switch (status) {
                case "approved" -> {
                    System.out.println("Procesando pago aprobado para cobro ID: " + idCobro);
                    cobroService.marcarComoPagado(cobro);
                }
                case "refunded" -> {
                    cobroService.reembolsar(cobro);
                }
                case "in_process" -> {

                }
                case "rejected", "cancelled" -> {
                }
                default -> {
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
