package com.example.FirmarPdfXml.ClasesObjetos.pdf.xml;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Xml {
    protected final InputStream streamXml;
    protected final String nombreXml;


    /**
     * <p>Constructor for Xml.</p>
     *
     * @param archivoXml a {@link String} object
     * @throws IOException if any.
     */
    public Xml(String archivoXml) throws IOException {

        this(new FileInputStream(archivoXml), new File(archivoXml).getName());
    }

    /**
     * <p>Constructor for Xml.</p>
     *
     * @param streamXml a {@link InputStream} object
     * @param nombre    a {@link String} object
     */
    public Xml(InputStream streamXml, String nombre) {

        this.streamXml = streamXml;
        nombreXml = nombre;
    }

    /**
     * Firma un Xml con un Certificado digital y su firma
     *
     * @param parametros archivo*, certificado* o certificadoStream*, password* , destino
     * @return Ruta del output
     * @throws IOException Error de acceso a ficheros
     */
    public String firmarXml(Map<String, Object> parametros) throws IOException {
        // Create a map of validated parameters ready for use
        Map<String, Object> comprobados;
        try {
            comprobados = comprobarParam(parametros);
        } catch (Exception e) {
            throw new RuntimeException("Error al comprobar los parametros " + e);
        }

        // Create the XML signer with the certificate parameters
        XmlDsigEnveloped xmlDsigEnveloped = new XmlDsigEnveloped(
                new CertManager(comprobados.get("certificado").toString(), comprobados.get("password").toString())
        );

        // Sign the XML document and convert it to a new XML document string
        accionFirmarXml(comprobados, xmlDsigEnveloped);
        return comprobados.get("destino").toString();
    }


    /**
     * Firma un Xml con un Certificado digital y su firma
     *
     * @param parametros archivo*, certificado* o certificadoStream* , password* , destino
     * @return ByteArrayInputStream del Xml firmado
     * @throws IOException Error de acceso a ficheros
     */
    public ByteArrayInputStream firmarXmlStream(Map<String, Object> parametros) throws IOException {
        // Create a map of validated parameters ready for use
        Map<String, Object> comprobados;
        try {
            comprobados = comprobarParam(parametros);
        } catch (Exception e) {
            throw new RuntimeException("Error al comprobar los parametros " + e);
        }

        // Create the XML signer with the certificate parameters
        XmlDsigEnveloped xmlDsigEnveloped = new XmlDsigEnveloped(
                new CertManager(comprobados.get("certificado").toString(), comprobados.get("password").toString())
        );

        // Create the XML document, sign it, and convert it to a new XML document
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(streamXml);
            Document newDoc;
            InputSource is = new InputSource(new StringReader(xmlDsigEnveloped.SignXml(doc)));
            newDoc = db.parse(is);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(newDoc);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (SAXException | IOException | TransformerException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Método encargado de firmar el Xml con los parámetros ya comprobados y las clases XmlDesigEnveloped
     *
     * @param comprobados      Parámetros comprobados
     * @param xmlDsigEnveloped Clase encargada de firmar y validar XML
     */
    private void accionFirmarXml(Map<String, Object> comprobados, XmlDsigEnveloped xmlDsigEnveloped) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(streamXml);
            Document newDoc;
            InputSource is = new InputSource(new StringReader(xmlDsigEnveloped.SignXml(doc)));
            newDoc = db.parse(is);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(newDoc);
            StreamResult result = new StreamResult(comprobados.get("destino").toString());
            transformer.transform(source, result);
        } catch (SAXException | IOException | TransformerException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Comprueba los parámetros introducidos en la función firmarXml
     *
     * @param parametros Colección de parámetros a comprobar
     * @return Colección de parámetros comprobados
     * @throws IOException Error de acceso a ficheros
     */
    private Map<String, Object> comprobarParam(Map<String, Object> parametros) throws Exception {
        // Si no hay certificado o contraseña, lanzar una excepción
        boolean stream = false;
        if (parametros.containsKey("certificadoStream") && parametros.containsKey("password")) {
            String certificadoStream = parametros.get("certificadoStream").toString();
            String password = parametros.get("password").toString();
            if (!certificadoStream.isEmpty() && !password.isEmpty()) {
                stream = true;
            } else {
                throw new IOException("No ha proporcionado un certificado o contraseña correctos");
            }
        } else {
            if (!parametros.containsKey("certificado") || !parametros.containsKey("password")) {
                throw new IOException("No ha proporcionado un certificado o contraseña correctos");
            }
        }

        String destino;
        if (!parametros.containsKey("destino") || parametros.get("destino") == null) {
            // Salida por defecto
            destino = defaultPath();
        } else {
            // Destino por parámetro destino
            destino = parametros.get("destino").toString();
        }

        // Crear mapa comprobado con los parámetros necesarios
        Map<String, Object> comprobados = new HashMap<>();
        if (stream) {
            String certificadoStream = parametros.get("certificadoStream").toString();
            InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(certificadoStream));
            File tempFile = File.createTempFile("certTemp", ".p12");
            try (OutputStream os = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                comprobados.put("certificado", tempFile);
                tempFile.deleteOnExit();
            }

        } else {
            comprobados.put("certificado", parametros.get("certificado").toString());
        }

        comprobados.put("password", parametros.get("password").toString());
        comprobados.put("destino", destino);

        return comprobados;
    }

    /**
     * Devuelve el path de downloads por defecto dependiendo del sistema operativo
     * @return string del path
     */
    private String defaultPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String downloadsPath = null;

        if (os.contains("win")) {
            // Windows
            String userProfile = System.getenv("USERPROFILE");
            downloadsPath = userProfile + "\\Downloads\\" + nombreXml + "_firmado.xml";
        } else if (os.contains("mac")) {
            // macOS
            String userHome = System.getProperty("user.home");
            downloadsPath = userHome + "/Downloads/" + nombreXml + "_firmado.xml";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            // Linux
            String userHome = System.getProperty("user.home");
            downloadsPath = userHome + "/Downloads/" + nombreXml + "_firmado.xml";
        } else {
            System.err.println("Unsupported operating system: " + os);
        }
        return downloadsPath;
    }

    public void cerrarStream() throws IOException {
        streamXml.close();
    }
}
