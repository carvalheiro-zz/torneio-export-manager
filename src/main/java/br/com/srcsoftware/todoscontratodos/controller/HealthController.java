package br.com.srcsoftware.todoscontratodos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        // Retorno leve apenas para manter a instância ativa no Render
        return Map.of("status", "UP", "message", "Torneio Manager is awake");
    }
}