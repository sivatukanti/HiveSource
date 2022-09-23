// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.cert.CertificateEncodingException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class X509CertUtils
{
    private static final String PEM_BEGIN_MARKER = "-----BEGIN CERTIFICATE-----";
    private static final String PEM_END_MARKER = "-----END CERTIFICATE-----";
    
    public static X509Certificate parse(final byte[] derEncodedCert) {
        if (derEncodedCert == null || derEncodedCert.length == 0) {
            return null;
        }
        Certificate cert;
        try {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = cf.generateCertificate(new ByteArrayInputStream(derEncodedCert));
        }
        catch (CertificateException ex) {
            return null;
        }
        if (!(cert instanceof X509Certificate)) {
            return null;
        }
        return (X509Certificate)cert;
    }
    
    public static X509Certificate parse(final String pemEncodedCert) {
        if (pemEncodedCert == null || pemEncodedCert.isEmpty()) {
            return null;
        }
        final int markerStart = pemEncodedCert.indexOf("-----BEGIN CERTIFICATE-----");
        if (markerStart < 0) {
            return null;
        }
        String buf = pemEncodedCert.substring(markerStart + "-----BEGIN CERTIFICATE-----".length());
        final int markerEnd = buf.indexOf("-----END CERTIFICATE-----");
        if (markerEnd < 0) {
            return null;
        }
        buf = buf.substring(0, markerEnd);
        buf = buf.replaceAll("\\s", "");
        return parse(new Base64(buf).decode());
    }
    
    public static String toPEMString(final X509Certificate cert) {
        final StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN CERTIFICATE-----");
        sb.append('\n');
        try {
            sb.append(Base64.encode(cert.getEncoded()).toString());
        }
        catch (CertificateEncodingException ex) {
            return null;
        }
        sb.append('\n');
        sb.append("-----END CERTIFICATE-----");
        return sb.toString();
    }
    
    public static Base64URL computeSHA256Thumbprint(final X509Certificate cert) {
        try {
            final byte[] derEncodedCert = cert.getEncoded();
            final MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return Base64URL.encode(sha256.digest(derEncodedCert));
        }
        catch (NoSuchAlgorithmException | CertificateEncodingException ex) {
            return null;
        }
    }
}
