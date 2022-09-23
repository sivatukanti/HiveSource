// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.util;

import java.security.PublicKey;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import javax.servlet.ServletException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;

public class CertificateUtil
{
    private static final String PEM_HEADER = "-----BEGIN CERTIFICATE-----\n";
    private static final String PEM_FOOTER = "\n-----END CERTIFICATE-----";
    
    public static RSAPublicKey parseRSAPublicKey(final String pem) throws ServletException {
        final String fullPem = "-----BEGIN CERTIFICATE-----\n" + pem + "\n-----END CERTIFICATE-----";
        PublicKey key = null;
        try {
            final CertificateFactory fact = CertificateFactory.getInstance("X.509");
            final ByteArrayInputStream is = new ByteArrayInputStream(fullPem.getBytes("UTF8"));
            final X509Certificate cer = (X509Certificate)fact.generateCertificate(is);
            key = cer.getPublicKey();
        }
        catch (CertificateException ce) {
            String message = null;
            if (pem.startsWith("-----BEGIN CERTIFICATE-----\n")) {
                message = "CertificateException - be sure not to include PEM header and footer in the PEM configuration element.";
            }
            else {
                message = "CertificateException - PEM may be corrupt";
            }
            throw new ServletException(message, ce);
        }
        catch (UnsupportedEncodingException uee) {
            throw new ServletException(uee);
        }
        return (RSAPublicKey)key;
    }
}
