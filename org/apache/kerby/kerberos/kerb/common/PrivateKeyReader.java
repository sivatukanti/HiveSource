// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.common;

import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import org.apache.kerby.util.Base64;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.io.InputStream;

public class PrivateKeyReader
{
    public static PrivateKey loadPrivateKey(final InputStream in) throws Exception {
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String readLine = null;
            final StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                }
                sb.append(readLine);
                sb.append('\r');
            }
            return loadPrivateKey(sb.toString());
        }
        catch (IOException e) {
            throw e;
        }
    }
    
    public static PrivateKey loadPrivateKey(final String privateKeyStr) throws Exception {
        try {
            final Base64 base64 = new Base64();
            final byte[] buffer = base64.decode(privateKeyStr);
            final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }
        catch (NoSuchAlgorithmException e) {
            throw e;
        }
        catch (InvalidKeySpecException e2) {
            throw e2;
        }
    }
}
