// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.common;

import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import org.apache.kerby.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import org.apache.commons.io.IOUtils;
import java.security.PublicKey;
import java.io.InputStream;

public class PublicKeyReader
{
    public static PublicKey loadPublicKey(final InputStream in) throws Exception {
        final byte[] keyBytes = IOUtils.toByteArray(in);
        try {
            return loadPublicKey(keyBytes);
        }
        catch (InvalidKeySpecException ex) {
            final Certificate cert = CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(keyBytes));
            return cert.getPublicKey();
        }
    }
    
    public static PublicKey loadPublicKey(final byte[] publicKeyBytes) throws Exception {
        String pubKey = new String(publicKeyBytes, StandardCharsets.UTF_8);
        if (pubKey.startsWith("-----BEGIN PUBLIC KEY-----")) {
            pubKey = pubKey.replace("-----BEGIN PUBLIC KEY-----", "");
            pubKey = pubKey.replace("-----END PUBLIC KEY-----", "");
            final Base64 base64 = new Base64();
            final byte[] buffer = base64.decode(pubKey.trim());
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return keyFactory.generatePublic(keySpec);
        }
        final KeyFactory keyFactory2 = KeyFactory.getInstance("RSA");
        final X509EncodedKeySpec keySpec2 = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory2.generatePublic(keySpec2);
    }
}
