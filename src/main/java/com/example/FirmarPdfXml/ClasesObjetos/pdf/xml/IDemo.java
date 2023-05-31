package com.example.FirmarPdfXml.ClasesObjetos.pdf.xml;

import java.io.InputStream;

/**
 * <p>IDemo interface.</p>
 *
 * @author ccamañ
 * @version $Id: $Id
 */
public interface IDemo {
    /**
     * <p>getName.</p>
     *
     * @return a {@link String} object
     */
    String getName();
 
    /**
     * <p>SignXml.</p>
     *
     * @param doc a {@link org.w3c.dom.Document} object
     * @return a {@link String} object
     */
    String SignXml( org.w3c.dom.Document doc );
 
    /**
     * <p>VerifyXml.</p>
     *
     * @param SignedXmlDocumentString a {@link String} object
     * @return a {@link Boolean} object
     */
    Boolean VerifyXml( String SignedXmlDocumentString );
    /**
     * <p>VerifyXmlFromStream.</p>
     *
     * @param SignedXmlDocumentStream a {@link InputStream} object
     * @return a {@link Boolean} object
     */
    Boolean VerifyXmlFromStream( InputStream SignedXmlDocumentStream );
}
