package com.example.FirmarPdfXml.ClasesObjetos.pdf;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import com.somospnt.signature.Signer;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * <p>Pdf class.</p>
 *
 * @author ccamañ
 * @version $Id: $Id
 */
public class Pdf {

    protected final PDDocument documentoPDF;
    protected final String nombrePDF;
    public final Integer paginas;

    /**
     * <p>Constructor for Pdf.</p>
     *
     * @param archivoPDF a {@link String} object
     * @throws IOException if any.
     */
    public Pdf(String archivoPDF) throws IOException {

        this(new FileInputStream(archivoPDF), new File(archivoPDF).getName());
    }

    /**
     * <p>Constructor for Pdf.</p>
     *
     * @param streamPDF a {@link InputStream} object
     * @param nombre    a {@link String} object
     * @throws IOException if any.
     */
    public Pdf(InputStream streamPDF, String nombre) throws IOException {

        documentoPDF = PDDocument.load(streamPDF);
        paginas = documentoPDF.getNumberOfPages();
        nombrePDF = nombre;
    }

    /**
     * Con la coleccion de parametros incluyendo certificado y contraseña, firma el pdf y guarda la copia firmada
     *
     * @param parametros certificado* o certificadoStream*, password* , destino , ubicacion , razonFirma
     * @return String nombre ruta output
     * @throws IOException Error de acceso a ficheros
     */
    public String firmarConCertificadoDigitalPdf(Map<String, Object> parametros) throws IOException {
        // Create a map of validated parameters ready for use
        Map<String, Object> comprobados = comprobarParam(parametros);

        // Create the FileOutputStream where the PDF will be saved
        try (FileOutputStream outputStream = new FileOutputStream(comprobados.get("destino").toString())) {

            Signer signer = GenerarSigner(comprobados);

            // Save the PDF to a temporary file
            File tempFile = Files.createTempFile("temp", ".pdf").toFile();
            documentoPDF.save(tempFile);

            // Create the FileInputStream to read the temporary file and obtain the PDF stream again
            FileInputStream inputStream = new FileInputStream(tempFile);

            // Sign the PDF and handle any exceptions like incorrect password, etc.
            signer.sign(inputStream, outputStream);

            inputStream.close();

        } finally {
            documentoPDF.close();
        }
        return comprobados.get("destino").toString();
    }


    /**
     * Con la coleccion de parametros incluyendo certificado y contraseña, firma el pdf y guarda la copia firmada
     *
     * @param parametros certificado* o certificadoStream* , password* , destino , ubicacion , razonFirma
     * @return ByteArrayInputStream del pdf Firmado
     * @throws IOException Error de acceso a ficheros
     */
    public ByteArrayInputStream firmarConCertificadoDigitalPdfStream(Map<String, Object> parametros) throws IOException {
        Map<String, Object> comprobados = comprobarParam(parametros);

        try {
            Signer signer = GenerarSigner(comprobados);

            File tempFile = Files.createTempFile("temp", ".pdf").toFile();
            documentoPDF.save(tempFile);

            FileInputStream inputStream = new FileInputStream(tempFile);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            signer.sign(inputStream, outputStream);

            inputStream.close();
            byte[] byteArray = outputStream.toByteArray();

            return new ByteArrayInputStream(byteArray);

        } finally {
            documentoPDF.close();
        }
    }


    /**
     * Comprueba los parametros introducidos en la función de firmar PDF
     *
     * @param parametros Colección Parametros
     * @return Colección de parametros comprobados
     * @throws IOException Error de acceso a ficheros
     */
    private Map<String, Object> comprobarParam(Map<String, Object> parametros) throws IOException {
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
            if (!parametros.containsKey("certificado") || parametros.get("certificado").toString().isEmpty() ||
                    !parametros.containsKey("password") || parametros.get("password").toString().isEmpty()) {
                throw new IOException("No ha proporcionado un certificado o contraseña correctos");
            }
        }

        // Obtener los valores de los parámetros
        String destino;
        if (!parametros.containsKey("destino") || parametros.get("destino") == null) {
            // Salida por defecto
            destino = defaultPath();
        } else {
            // Salida por parámetro destino
            destino = parametros.get("destino").toString();
        }
        String ubicacion = parametros.get("ubicacion").toString();
        String razonFirma = parametros.get("razonFirma").toString();

        // Crear un nuevo mapa para los parámetros comprobados
        Map<String, Object> comprobados = new HashMap<>();

        if (stream) {
            String certificadoStream = parametros.get("certificadoStream").toString();
            byte[] certificadoBytes = Base64.getDecoder().decode(certificadoStream);
            File tempFile = File.createTempFile("certTemp", ".p12");
            try (OutputStream os = new FileOutputStream(tempFile)) {
                os.write(certificadoBytes);
                comprobados.put("certificado", tempFile);
                tempFile.deleteOnExit();
            }
        } else {
            comprobados.put("certificado", parametros.get("certificado").toString());
        }

        comprobados.put("password", parametros.get("password").toString());
        comprobados.put("destino", destino);
        comprobados.put("ubicacion", ubicacion);
        comprobados.put("razonFirma", razonFirma);

        return comprobados;
    }

    /**
     * @param comprobados Parametros comprovados
     * @return Objeto signer con los parametros puestos
     */
    private static Signer GenerarSigner(Map<String, Object> comprobados) {
        return new Signer(
                comprobados.get("certificado").toString(),
                comprobados.get("password").toString(),
                comprobados.get("ubicacion").toString(),
                comprobados.get("razonFirma").toString()
        );
    }

    /**
     * Devuelve el path de downloads por defecto dependiendo del sistema operativo
     *
     * @return string del path
     */
    private String defaultPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String downloadsPath = null;

        if (os.contains("win")) {
            // Windows
            String userProfile = System.getenv("USERPROFILE");
            downloadsPath = userProfile + "\\Downloads\\" + nombrePDF + "_firmado.pdf";
        } else if (os.contains("mac")) {
            // macOS
            String userHome = System.getProperty("user.home");
            downloadsPath = userHome + "/Downloads/" + nombrePDF + "_firmado.pdf";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            // Linux
            String userHome = System.getProperty("user.home");
            downloadsPath = userHome + "/Downloads/" + nombrePDF + "_firmado.pdf";
        } else {
            System.err.println("Unsupported operating system: " + os);
        }
        return downloadsPath;
    }
}

