package com.medina.asocDev.Medina.Asociados.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.medina.asocDev.Medina.Asociados.entity.Cobro;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MercadoPagoService {

    @Value("${mp.access-token}")
    private String accessToken;

    @Value("${mp.notification-url}")
    private String notificationUrl;

    @Value("${mp.back-url.success}")
    private String backUrlSuccess;

    @Value("${mp.back-url.failure}")
    private String backUrlFailure;

    @Value("${mp.back-url.pending}")
    private String backUrlPending;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public String crearPreferencia(Cobro cobro, Turno turno) throws Exception {
        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title("Turno con " + turno.getAbogadoTurno().getNombre())
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(cobro.getImporteTotal()))
                .currencyId("ARS")
                .build();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(backUrlSuccess)
                .failure(backUrlFailure)
                .pending(backUrlPending)
                .build();

        PreferenceRequest request = PreferenceRequest.builder()
                .items(List.of(item))
                .externalReference(String.valueOf(cobro.getIdCobro()))
                .backUrls(backUrls)
                .autoReturn("approved")
                .notificationUrl(notificationUrl)
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(request);
        return preference.getInitPoint();
    }

    public void reembolsarPago(Long paymentId) {
        try {
            PaymentClient client = new PaymentClient();
            client.refund(paymentId);
        } catch (MPApiException e) {
            String responseBody = e.getApiResponse() != null ? e.getApiResponse().getContent() : "sin respuesta";
            System.err.println("MP refund error - paymentId: " + paymentId + ", response: " + responseBody);
            throw new RuntimeException("Error al reembolsar en Mercado Pago: " + responseBody, e);
        } catch (MPException e) {
            throw new RuntimeException("Error de conexión con Mercado Pago", e);
        }
    }

    public Payment obtenerPago(Long paymentId) {
        try {
            PaymentClient client = new PaymentClient();
            return client.get(paymentId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener pago de Mercado Pago", e);
        }
    }
}