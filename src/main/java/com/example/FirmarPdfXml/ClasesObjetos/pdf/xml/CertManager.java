package com.example.FirmarPdfXml.ClasesObjetos.pdf.xml;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * <p>CertManager class.</p>
 *
 * @author ccamañ
 * @version $Id: $Id
 */
public class CertManager {

    private final String certificado;

    private final String password;
    private KeyStore _keyStore = null;

    /**
     * <p>Constructor for CertManager.</p>
     *
     * @param certificado a {@link String} object
     * @param password a {@link String} object
     */
    public CertManager(String certificado, String password) {
        this.certificado = certificado;
        this.password=password;
    }

    private KeyStore getKeyStore()
    {
        if ( _keyStore == null )
        {
            try
            {
                _keyStore = KeyStore.getInstance("pkcs12");
                _keyStore.load( 
                    new FileInputStream(certificado), password.toCharArray() );
            }
            catch ( Exception ex )
            {
                ex.printStackTrace();
            }
        }
        
        return _keyStore;        
    }
    
    /**
     * <p>getCertificate.</p>
     *
     * @return a {@link X509Certificate} object
     */
    public X509Certificate getCertificate()
    {
        try {                            
            String alias = getKeyStore().aliases().nextElement();

            return (X509Certificate)getKeyStore().getCertificateChain(alias)[0];
            
        } catch ( Exception e ) {
            e.printStackTrace();
            return null;
        } 
    }
    
    /**
     * <p>getKey.</p>
     *
     * @return a {@link Key} object
     */
    public Key getKey()
    {
        try {                
            String alias = getKeyStore().aliases().nextElement();

            return getKeyStore().getKey(alias, password.toCharArray());
            
        } catch ( Exception e ) {
            e.printStackTrace();
            return null;
        } 
        
    }
}
