package com.example.FirmarPdfXml.controller;

import com.example.FirmarPdfXml.services.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/evento")
    public ResponseEntity<?> eventoPost(
            @RequestBody Map<String, Object> parametros,
            @RequestParam(name = "categoria") String categoria,
            @RequestParam(name = "evento") String evento) {
        try {
            return ResponseEntity.ok(eventService.procesarEvento(categoria, evento, parametros));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

