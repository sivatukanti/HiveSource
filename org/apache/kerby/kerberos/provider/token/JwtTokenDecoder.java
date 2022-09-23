// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.provider.token;

import java.util.Date;
import java.util.Iterator;
import java.security.PublicKey;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import java.security.interfaces.ECPublicKey;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import java.security.interfaces.RSAPublicKey;
import com.nimbusds.jose.JWSVerifier;
import java.security.PrivateKey;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.RSADecrypter;
import java.security.interfaces.RSAPrivateKey;
import com.nimbusds.jose.JWEDecrypter;
import org.apache.kerby.kerberos.kerb.KrbException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.PlainJWT;
import java.text.ParseException;
import com.nimbusds.jwt.JWTParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.kerby.kerberos.kerb.type.base.AuthToken;
import java.util.List;
import org.apache.kerby.kerberos.kerb.provider.TokenDecoder;

public class JwtTokenDecoder implements TokenDecoder
{
    private Object decryptionKey;
    private Object verifyKey;
    private List<String> audiences;
    private boolean signed;
    
    public JwtTokenDecoder() {
        this.audiences = null;
        this.signed = false;
    }
    
    @Override
    public AuthToken decodeFromBytes(final byte[] content) throws IOException {
        final String tokenStr = new String(content, StandardCharsets.UTF_8);
        return this.decodeFromString(tokenStr);
    }
    
    @Override
    public AuthToken decodeFromString(final String content) throws IOException {
        JWT jwt = null;
        try {
            jwt = JWTParser.parse(content);
        }
        catch (ParseException e) {
            throw new IOException("Failed to parse JWT token string", e);
        }
        if (jwt instanceof PlainJWT) {
            final PlainJWT plainObject = (PlainJWT)jwt;
            try {
                if (this.verifyToken(jwt)) {
                    return new JwtAuthToken(plainObject.getJWTClaimsSet());
                }
                return null;
            }
            catch (ParseException e2) {
                throw new IOException("Failed to get JWT claims set", e2);
            }
        }
        if (jwt instanceof EncryptedJWT) {
            final EncryptedJWT encryptedJWT = (EncryptedJWT)jwt;
            this.decryptEncryptedJWT(encryptedJWT);
            final SignedJWT signedJWT = encryptedJWT.getPayload().toSignedJWT();
            if (signedJWT != null) {
                final boolean success = this.verifySignedJWT(signedJWT) && this.verifyToken(signedJWT);
                if (success) {
                    try {
                        this.signed = true;
                        return new JwtAuthToken(signedJWT.getJWTClaimsSet());
                    }
                    catch (ParseException e3) {
                        throw new IOException("Failed to get JWT claims set", e3);
                    }
                }
                return null;
            }
            try {
                if (this.verifyToken(encryptedJWT)) {
                    return new JwtAuthToken(encryptedJWT.getJWTClaimsSet());
                }
                return null;
            }
            catch (ParseException e4) {
                throw new IOException("Failed to get JWT claims set", e4);
            }
        }
        if (jwt instanceof SignedJWT) {
            final SignedJWT signedJWT2 = (SignedJWT)jwt;
            final boolean success2 = this.verifySignedJWT(signedJWT2) && this.verifyToken(signedJWT2);
            if (success2) {
                try {
                    this.signed = true;
                    return new JwtAuthToken(signedJWT2.getJWTClaimsSet());
                }
                catch (ParseException e4) {
                    throw new IOException("Failed to get JWT claims set", e4);
                }
            }
            return null;
        }
        throw new IOException("Unexpected JWT type: " + jwt);
    }
    
    public void decryptEncryptedJWT(final EncryptedJWT encryptedJWT) throws IOException {
        try {
            final JWEDecrypter decrypter = this.getDecrypter();
            encryptedJWT.decrypt(decrypter);
        }
        catch (JOSEException | KrbException ex2) {
            final Exception ex;
            final Exception e = ex;
            throw new IOException("Failed to decrypt the encrypted JWT", e);
        }
    }
    
    private JWEDecrypter getDecrypter() throws JOSEException, KrbException {
        if (this.decryptionKey instanceof RSAPrivateKey) {
            return new RSADecrypter((RSAPrivateKey)this.decryptionKey);
        }
        if (this.decryptionKey instanceof byte[]) {
            return new DirectDecrypter((byte[])this.decryptionKey);
        }
        throw new KrbException("An unknown decryption key was specified");
    }
    
    @Override
    public void setDecryptionKey(final PrivateKey key) {
        this.decryptionKey = key;
    }
    
    @Override
    public void setDecryptionKey(final byte[] key) {
        if (key == null) {
            this.decryptionKey = new byte[0];
        }
        else {
            this.decryptionKey = key.clone();
        }
    }
    
    public boolean verifySignedJWT(final SignedJWT signedJWT) throws IOException {
        try {
            final JWSVerifier verifier = this.getVerifier();
            return signedJWT.verify(verifier);
        }
        catch (JOSEException | KrbException ex2) {
            final Exception ex;
            final Exception e = ex;
            throw new IOException("Failed to verify the signed JWT", e);
        }
    }
    
    private JWSVerifier getVerifier() throws JOSEException, KrbException {
        if (this.verifyKey instanceof RSAPublicKey) {
            return new RSASSAVerifier((RSAPublicKey)this.verifyKey);
        }
        if (this.verifyKey instanceof ECPublicKey) {
            final ECPublicKey ecPublicKey = (ECPublicKey)this.verifyKey;
            return new ECDSAVerifier(ecPublicKey.getW().getAffineX(), ecPublicKey.getW().getAffineY());
        }
        if (this.verifyKey instanceof byte[]) {
            return new MACVerifier((byte[])this.verifyKey);
        }
        throw new KrbException("An unknown verify key was specified");
    }
    
    @Override
    public void setVerifyKey(final PublicKey key) {
        this.verifyKey = key;
    }
    
    @Override
    public void setVerifyKey(final byte[] key) {
        if (key == null) {
            this.verifyKey = new byte[0];
        }
        else {
            this.verifyKey = key.clone();
        }
    }
    
    public void setAudiences(final List<String> auds) {
        this.audiences = auds;
    }
    
    private boolean verifyToken(final JWT jwtToken) throws IOException {
        final boolean audValid = this.verifyAudiences(jwtToken);
        final boolean expValid = this.verifyExpiration(jwtToken);
        return audValid && expValid;
    }
    
    private boolean verifyAudiences(final JWT jwtToken) throws IOException {
        boolean valid = false;
        try {
            final List<String> tokenAudiences = (List<String>)jwtToken.getJWTClaimsSet().getAudience();
            if (this.audiences == null) {
                valid = true;
            }
            else {
                for (final String audience : tokenAudiences) {
                    if (this.audiences.contains(audience)) {
                        valid = true;
                        break;
                    }
                }
            }
        }
        catch (ParseException e) {
            throw new IOException("Failed to get JWT claims set", e);
        }
        return valid;
    }
    
    private boolean verifyExpiration(final JWT jwtToken) throws IOException {
        try {
            final Date expire = jwtToken.getJWTClaimsSet().getExpirationTime();
            if (expire != null && new Date().after(expire)) {
                return false;
            }
            final Date notBefore = jwtToken.getJWTClaimsSet().getNotBeforeTime();
            if (notBefore != null && new Date().before(notBefore)) {
                return false;
            }
        }
        catch (ParseException e) {
            throw new IOException("Failed to get JWT claims set", e);
        }
        return true;
    }
    
    @Override
    public boolean isSigned() {
        return this.signed;
    }
}
