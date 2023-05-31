package com.example.FirmarPdfXml;

import com.example.FirmarPdfXml.ClasesObjetos.pdf.xml.Xml;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TestXml {
    String certificadoPath = "C:\\Users\\carlo\\Documents\\PruebasCertificado\\20918421J_CARLOS_CAMAÑ__1675970077290.p12";
    String xmlPath = "C:\\Users\\carlo\\Documents\\PruebasCertificado\\xmlFirmar.xml";
    String password = "B3car27081996";
    String destino = "C:\\Users\\carlo\\Documents\\PruebasCertificado";
    @Test
    public void testFirmarXmlStream() {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("destino", destino);
        parametros.put("certificado", certificadoPath);
        parametros.put("password", password);

        try {

            Xml xml = new Xml(xmlPath);
            ByteArrayInputStream byteArrayInputStream = xml.firmarXmlStream(parametros);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayInputStream.transferTo(byteArrayOutputStream);
            byte[] signedXmlBytes = byteArrayOutputStream.toByteArray();

            String base64 = Base64.getEncoder().encodeToString(signedXmlBytes);
            System.out.println(base64);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void verStreamXml() {
        String outputFilePath = "C:\\Users\\carlo\\Documents\\PruebasCertificado\\xmlFirmado.xml";

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("destino", destino);
        parametros.put("certificado", certificadoPath);
        parametros.put("password", password);

        try {

            Xml xml = new Xml(xmlPath);
            ByteArrayInputStream byteArrayInputStream = xml.firmarXmlStream(parametros);
            xml.cerrarStream();

            File outputFile = new File(outputFilePath);

            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = byteArrayInputStream.read(buffer)) != -1) {
                    bufferedOutputStream.write(buffer, 0, bytesRead);
                }

                bufferedOutputStream.flush();
            }

            byteArrayInputStream.close();

            System.out.println("File saved successfully.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testFirmarXml() {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("certificado", certificadoPath);
        parametros.put("password", password);


        try {
            Xml xml = new Xml(xmlPath);
            System.out.println(xml.firmarXml(parametros));
            xml.cerrarStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
