package com.example.FirmarPdfXml.model;

import java.util.Map;

public interface ReceptorEventos {

    String procesarEvento(String evento, Map<String,Object> parametros) throws Exception;

}
