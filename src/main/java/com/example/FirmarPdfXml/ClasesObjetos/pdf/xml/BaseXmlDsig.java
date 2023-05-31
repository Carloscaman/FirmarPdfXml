package com.example.FirmarPdfXml.ClasesObjetos.pdf.xml;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.util.Iterator;

/**
 * <p>Abstract BaseXmlDsig class.</p>
 *
 * @author ccamañ
 * @version $Id: $Id
 */
public abstract class BaseXmlDsig implements IDemo {
 
    /**
     * <p>getName.</p>
     *
     * @return a {@link String} object
     */
    public abstract String getName();
 
    /** {@inheritDoc} */
    public abstract String SignXml(Document doc);
 
    /** {@inheritDoc} */
    public Boolean VerifyXml(String SignedXmlDocumentString) {
        try
        {
            InputStream stream =
              new ByteArrayInputStream(SignedXmlDocumentString.getBytes(StandardCharsets.UTF_8));
            return VerifyXmlFromStream( stream );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
            return false;
        }
    }
 
    /** {@inheritDoc} */
    public Boolean VerifyXmlFromStream(InputStream SignedXmlDocumentStream) {
        try
        {
          DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();
          dbf.setNamespaceAware(true);
          
          Document sourceDoc = 
                  dbf
                      .newDocumentBuilder()
                      .parse( SignedXmlDocumentStream );          
          
          NodeList nl =
                  sourceDoc.getElementsByTagNameNS(XMLSignature.XMLNS,
                      "Signature");          
          if (nl.getLength() == 0) {
                throw new Exception("Cannot find Signature element");
          }
 
          String providerName = System.getProperty(
                    "jsr105Provider",
                    "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
          
          XMLSignatureFactory fac = 
                     XMLSignatureFactory.getInstance("DOM",
                    (Provider) Class.forName(providerName).getConstructor().newInstance());
          DOMValidateContext valContext = new DOMValidateContext
                    (new KeyValueKeySelector(), nl.item(0));          
          
          XMLSignature signature = 
                    fac.unmarshalXMLSignature(valContext);
          boolean coreValidity = signature.validate(valContext); 
 
          if (!coreValidity) {
 
                // optional. Java allows me to get more information
                // on failed verification
 
                System.out.println("Signature failed core validation!");
                boolean sv = signature.getSignatureValue().validate(valContext);
 
                System.out.println("Signature validation status: " + sv);
 
                // Check the validation status of each Reference
                Iterator<Reference> i = signature.getSignedInfo().getReferences().iterator();
                for (int j = 0; i.hasNext(); j++) 
                {
                    boolean refValid = i.next().validate(valContext);
                    System.out.println("Reference (" + j + ") validation status: "
                            + refValid);    
                }
                
                return false;
          } 
          else 
          {
                return true;
          }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return false;
        }                
    }
 
}
