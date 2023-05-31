package com.example.FirmarPdfXml.services;

import com.example.FirmarPdfXml.model.ReceptorEventos;
import com.example.FirmarPdfXml.receptor.ReceptorPdf;
import com.example.FirmarPdfXml.receptor.ReceptorXml;
import org.springframework.stereotype.Service;

import javax.naming.ConfigurationException;
import java.util.Map;

@Service
public class EventService {
    public String procesarEvento(String categoria, String evento, Map<String,Object> parametros) throws Exception {
        var receptor=obtenerReceptor(categoria);
        if(receptor==null) throw  new ConfigurationException("No se ha configurado un receptor para la categoría: " + categoria );

        return receptor.procesarEvento(evento,parametros);
    }

    private ReceptorEventos obtenerReceptor(String categoria)
    {
        return switch (categoria) {
            case "PDF" -> new ReceptorPdf();
            case "XML" -> new ReceptorXml();
            default -> null;
        };
    }
}
