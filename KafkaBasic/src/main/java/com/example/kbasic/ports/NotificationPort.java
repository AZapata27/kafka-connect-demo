package com.example.kbasic.ports;

import com.example.kbasic.model.Compra;

/**
 * Puerto de salida para notificaciones simples.
 * No proveemos implementación: la charla puede simular o explicar cómo inyectarla.
 */
public interface NotificationPort {
    void notifyCompraCreated(Compra compra);
}
