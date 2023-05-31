package com.example.FirmarPdfXml.ClasesObjetos.pdf.xml;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

public class KeyValueKeySelector extends KeySelector {
 
    public KeySelectorResult select(KeyInfo keyInfo,
                                    Purpose purpose, AlgorithmMethod method,
                                    XMLCryptoContext context) throws KeySelectorException {
 
        if (keyInfo == null) {
            throw new KeySelectorException("Null KeyInfo object!");
        }
        SignatureMethod sm = (SignatureMethod) method;
        List list = keyInfo.getContent();
 
        for (int i = 0; i < list.size(); i++) {
 
            XMLStructure xmlStructure = (XMLStructure) list.get(i);
 
            if (xmlStructure instanceof X509Data) {
 
                X509Data x509 = (X509Data) xmlStructure;
 
                for (Object content : x509.getContent()) {
                    if (content instanceof X509Certificate) {
                        PublicKey pk = ((X509Certificate) content)
                                .getPublicKey();
                        return new SimpleKeySelectorResult(pk);
 
                    }
                }
                return null;
            }
 
            if (xmlStructure instanceof X509Certificate) {
                PublicKey pk = ((X509Certificate) xmlStructure).getPublicKey();
                return new SimpleKeySelectorResult(pk);
            }
 
            PublicKey pk = ((X509Certificate) xmlStructure).getPublicKey();
            return new SimpleKeySelectorResult(pk);
 
        }
        throw new KeySelectorException("No KeyValue element found!");
    }
}
 
class SimpleKeySelectorResult implements KeySelectorResult {
    private PublicKey pk;
 
    SimpleKeySelectorResult(PublicKey pk) {
        this.pk = pk;
    }
 
    public Key getKey() {
        return pk;
    }
}