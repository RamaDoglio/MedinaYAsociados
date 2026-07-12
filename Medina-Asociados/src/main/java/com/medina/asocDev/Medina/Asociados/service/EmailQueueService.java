package com.medina.asocDev.Medina.Asociados.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailQueueService {

    @Async
    public void enviarConDelay(Runnable emailTask) {
        try {
            Thread.sleep(1200);
            emailTask.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}