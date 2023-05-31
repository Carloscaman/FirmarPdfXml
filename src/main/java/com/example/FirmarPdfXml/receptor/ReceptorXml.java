package com.example.FirmarPdfXml.receptor;

import com.example.FirmarPdfXml.ClasesObjetos.pdf.xml.Xml;
import com.example.FirmarPdfXml.model.ReceptorEventos;

import javax.naming.ConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;

public class ReceptorXml implements ReceptorEventos {

    @Override
    public String procesarEvento(String evento, Map<String, Object> parametros) throws Exception {
        switch (evento) {
            case "FIRMAR_XML" -> {
                Xml xml = new Xml(parametros.get("archivo").toString());
                return xml.firmarXml(parametros);
            }
            case "FIRMAR_XML_STREAM" -> {
                byte[] streamBytes = Base64.getDecoder().decode(parametros.get("stream").toString());
                ByteArrayInputStream inputStream = new ByteArrayInputStream(streamBytes);
                Xml xmlStream = new Xml(inputStream, parametros.get("nombre").toString());
                ByteArrayInputStream firmarXmlStream = xmlStream.firmarXmlStream(parametros);
                ByteArrayOutputStream firmadoXmlStream = new ByteArrayOutputStream();
                firmarXmlStream.transferTo(firmadoXmlStream);
                byte[] resultBytes = firmadoXmlStream.toByteArray();
                return Base64.getEncoder().encodeToString(resultBytes);
            }
        }
        throw new ConfigurationException("No se ha configurado el evento: " + evento);
    }
}
