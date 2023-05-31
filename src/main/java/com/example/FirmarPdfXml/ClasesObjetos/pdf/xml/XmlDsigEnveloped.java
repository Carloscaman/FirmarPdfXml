package com.example.FirmarPdfXml.ClasesObjetos.pdf.xml;

import org.w3c.dom.Document;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import java.util.Collections;

public class XmlDsigEnveloped extends BaseXmlDsig implements IDemo {

    private final CertManager manager;

    public XmlDsigEnveloped(CertManager manager) {
        this.manager = manager;
    }

    public String getName() {
        return "XmlDsigEnveloped";
    }

    /**
     * <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/security/xmldsig/XMLDigitalSignature.html">...</a>
     */
    public String SignXml(Document doc) {
        try {
            XMLSignatureFactory fac =
                    XMLSignatureFactory.getInstance("DOM");

            Reference ref =
                    fac.newReference("",
                            fac.newDigestMethod(DigestMethod.SHA256, null),
                            Collections.singletonList(
                                    fac.newTransform(Transform.ENVELOPED,
                                            (TransformParameterSpec) null)),
                            null, null);
            SignedInfo si = fac.newSignedInfo
                    (fac.newCanonicalizationMethod
                                    (CanonicalizationMethod.EXCLUSIVE,
                                            (C14NMethodParameterSpec) null),
                            fac.newSignatureMethod(SignatureMethod.RSA_SHA1,
                                    null),
                            Collections.singletonList(ref));
            KeyInfoFactory kif = fac.getKeyInfoFactory();
            X509Certificate cert = manager.getCertificate();
            X509Data x509d = kif.newX509Data(Collections.singletonList(cert));
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(x509d));
            DOMSignContext dsc =
                    new DOMSignContext(manager.getKey(), doc.getDocumentElement());
            XMLSignature signature = fac.newXMLSignature(si, ki);
            signature.sign(dsc);
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();

            trans.transform(
                    new DOMSource(doc),
                    new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}