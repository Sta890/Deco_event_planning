package com.onana.decoevent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Sonde de disponibilité publique.
 * Utilisée par l'hébergeur (health check Render) : elle doit répondre sans
 * authentification, même quand Swagger est désactivé en production.
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
