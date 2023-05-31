package com.example.FirmarPdfXml.receptor;

import com.example.FirmarPdfXml.ClasesObjetos.pdf.Pdf;
import com.example.FirmarPdfXml.model.ReceptorEventos;

import javax.naming.ConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;

public class ReceptorPdf implements ReceptorEventos {

    @Override
    public String procesarEvento(String evento, Map<String, Object> parametros) throws Exception {
        switch (evento) {
            case "FIRMAR_PDF" -> {
                Pdf pdf = new Pdf(parametros.get("archivo").toString());
                return pdf.firmarConCertificadoDigitalPdf(parametros);
            }
            case "FIRMAR_PDF_STREAM" -> {
                byte[] streamBytes = Base64.getDecoder().decode(parametros.get("stream").toString());
                InputStream inputStream = new ByteArrayInputStream(streamBytes);
                Pdf pdfStream = new Pdf(inputStream, parametros.get("nombre").toString());
                ByteArrayInputStream firmarPdfStream = pdfStream.firmarConCertificadoDigitalPdfStream(parametros);
                ByteArrayOutputStream firmadoPdfStream = new ByteArrayOutputStream();
                firmarPdfStream.transferTo(firmadoPdfStream);
                byte[] resultBytes = firmadoPdfStream.toByteArray();
                return Base64.getEncoder().encodeToString(resultBytes);
            }
        }
        throw new ConfigurationException("No se ha configurado el evento: " + evento);
    }
}
