package com.example.FirmarPdfXml;

import com.example.FirmarPdfXml.ClasesObjetos.pdf.Pdf;
import com.somospnt.signature.Signer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class TestPDF {
    String certificadoPath = "C:\\Users\\carlo\\Documents\\PruebasCertificado\\20918421J_CARLOS_CAMAÑ__1675970077290.p12";
    String pdfPath = "C:\\Users\\carlo\\Documents\\PruebasCertificado\\Pdf para Firmar.pdf";
    String password = "B3car27081996";
    String destino = "C:\\Users\\carlo\\Documents\\PruebasCertificado";
    @Test
    public void testStreamPdf() throws IOException {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("certificado", certificadoPath);
        parametros.put("password", password);
        Pdf pdf = new Pdf(pdfPath);
        ByteArrayInputStream byteArrayInputStream = pdf.firmarConCertificadoDigitalPdfStream(parametros);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayInputStream.transferTo(byteArrayOutputStream);
        byte[] signedPdfBytes = byteArrayOutputStream.toByteArray();

        String base64 = Base64.getEncoder().encodeToString(signedPdfBytes);
        System.out.println(base64);
    }
    @Test
    public void testFirmaPDF() throws IOException {

        Pdf pdf = new Pdf(pdfPath);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("certificado", certificadoPath);
        parametros.put("password", password);

        System.out.println(pdf.firmarConCertificadoDigitalPdf(parametros));
    }

    @Test
    public void testFirmaPDFPruebaP12() throws IOException {

        Pdf pdf = new Pdf(pdfPath);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("destino", destino);
        parametros.put("certificado", certificadoPath);
        parametros.put("password", password);

        System.out.println(pdf.firmarConCertificadoDigitalPdf(parametros));
    }


    @Test
    public void testPruebaFirmar() throws IOException {

        InputStream inputStream = new FileInputStream(pdfPath);
        FileOutputStream outputStream = new FileOutputStream("C:\\Users\\carlo\\Documents\\PruebasCertificado\\Pdf para FirmarFirmado.pdf");
        Signer signer = new Signer(certificadoPath, password, "Spain", "ITBusiness");
        signer.sign(inputStream, outputStream);
        inputStream.close();
        outputStream.close();

    }
}
