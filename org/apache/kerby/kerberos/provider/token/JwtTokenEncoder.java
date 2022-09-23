// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.provider.token;

import java.security.PrivateKey;
import java.security.PublicKey;
import com.nimbusds.jose.crypto.DirectEncrypter;
import java.security.interfaces.RSAPublicKey;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import java.security.interfaces.ECPrivateKey;
import com.nimbusds.jose.crypto.ECDSASigner;
import java.security.interfaces.RSAPrivateKey;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JOSEException;
import java.text.ParseException;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jose.JWSHeader;
import org.apache.kerby.kerberos.kerb.KrbException;
import java.nio.charset.StandardCharsets;
import org.apache.kerby.kerberos.kerb.type.base.AuthToken;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import org.apache.kerby.kerberos.kerb.provider.TokenEncoder;

public class JwtTokenEncoder implements TokenEncoder
{
    private JWEAlgorithm jweAlgorithm;
    private EncryptionMethod encryptionMethod;
    private JWSAlgorithm jwsAlgorithm;
    private Object encryptionKey;
    private Object signKey;
    
    public JwtTokenEncoder() {
        this.jweAlgorithm = JWEAlgorithm.RSA_OAEP;
        this.encryptionMethod = EncryptionMethod.A128GCM;
        this.jwsAlgorithm = JWSAlgorithm.RS256;
    }
    
    @Override
    public byte[] encodeAsBytes(final AuthToken token) throws KrbException {
        final String tokenStr = this.encodeAsString(token);
        return tokenStr.getBytes(StandardCharsets.UTF_8);
    }
    
    @Override
    public String encodeAsString(final AuthToken token) throws KrbException {
        if (!(token instanceof JwtAuthToken)) {
            throw new KrbException("Unexpected AuthToken, not JwtAuthToken");
        }
        final JwtAuthToken jwtAuthToken = (JwtAuthToken)token;
        final JWT jwt = jwtAuthToken.getJwt();
        String tokenStr = null;
        if (this.signKey != null) {
            final JWSSigner signer = this.createSigner();
            SignedJWT signedJWT = null;
            try {
                signedJWT = new SignedJWT(new JWSHeader(this.jwsAlgorithm), jwt.getJWTClaimsSet());
            }
            catch (ParseException e) {
                throw new KrbException("Failed to get JWT claims set", e);
            }
            try {
                signedJWT.sign(signer);
            }
            catch (JOSEException e2) {
                throw new KrbException("Failed to sign the Signed JWT", e2);
            }
            if (this.encryptionKey != null) {
                final JWEObject jweObject = new JWEObject(new JWEHeader.Builder(this.jweAlgorithm, this.encryptionMethod).contentType("JWT").build(), new Payload(signedJWT));
                try {
                    jweObject.encrypt(this.createEncryptor());
                }
                catch (JOSEException e3) {
                    throw new KrbException("Failed to encrypt the JWE object", e3);
                }
                tokenStr = jweObject.serialize();
            }
            else {
                tokenStr = signedJWT.serialize();
            }
        }
        else if (this.encryptionKey != null) {
            final JWEHeader header = new JWEHeader(this.jweAlgorithm, this.encryptionMethod);
            EncryptedJWT encryptedJWT = null;
            try {
                encryptedJWT = new EncryptedJWT(header, jwt.getJWTClaimsSet());
            }
            catch (ParseException e) {
                throw new KrbException("Failed to get JWT claims set", e);
            }
            try {
                encryptedJWT.encrypt(this.createEncryptor());
            }
            catch (JOSEException e2) {
                throw new KrbException("Failed to encrypt the encrypted JWT", e2);
            }
            tokenStr = encryptedJWT.serialize();
        }
        else {
            tokenStr = jwt.serialize();
        }
        return tokenStr;
    }
    
    private JWSSigner createSigner() throws KrbException {
        if (RSASSASigner.SUPPORTED_ALGORITHMS.contains(this.jwsAlgorithm)) {
            if (!(this.signKey instanceof RSAPrivateKey)) {
                throw new KrbException("An RSAPrivateKey key must be specified for signature");
            }
            return new RSASSASigner((RSAPrivateKey)this.signKey);
        }
        else if (ECDSASigner.SUPPORTED_ALGORITHMS.contains(this.jwsAlgorithm)) {
            if (!(this.signKey instanceof ECPrivateKey)) {
                throw new KrbException("A ECPrivateKey key must be specified for signature");
            }
            return new ECDSASigner(((ECPrivateKey)this.signKey).getS());
        }
        else {
            if (!MACSigner.SUPPORTED_ALGORITHMS.contains(this.jwsAlgorithm)) {
                throw new KrbException("An unknown signature algorithm was specified");
            }
            if (!(this.signKey instanceof byte[])) {
                throw new KrbException("A byte[] key must be specified for signature");
            }
            return new MACSigner((byte[])this.signKey);
        }
    }
    
    private JWEEncrypter createEncryptor() throws KrbException, JOSEException {
        if (RSAEncrypter.SUPPORTED_ALGORITHMS.contains(this.jweAlgorithm)) {
            if (!(this.encryptionKey instanceof RSAPublicKey)) {
                throw new KrbException("An RSAPublicKey key must be specified for encryption");
            }
            return new RSAEncrypter((RSAPublicKey)this.encryptionKey);
        }
        else {
            if (!DirectEncrypter.SUPPORTED_ALGORITHMS.contains(this.jweAlgorithm)) {
                throw new KrbException("An unknown encryption algorithm was specified");
            }
            if (!(this.encryptionKey instanceof byte[])) {
                throw new KrbException("A byte[] key must be specified for encryption");
            }
            return new DirectEncrypter((byte[])this.encryptionKey);
        }
    }
    
    @Override
    public void setEncryptionKey(final PublicKey key) {
        this.encryptionKey = key;
    }
    
    @Override
    public void setEncryptionKey(final byte[] key) {
        if (key == null) {
            this.encryptionKey = new byte[0];
        }
        else {
            this.encryptionKey = key.clone();
        }
    }
    
    @Override
    public void setSignKey(final PrivateKey key) {
        this.signKey = key;
    }
    
    @Override
    public void setSignKey(final byte[] key) {
        if (key == null) {
            this.signKey = new byte[0];
        }
        else {
            this.signKey = key.clone();
        }
    }
    
    public JWEAlgorithm getJweAlgorithm() {
        return this.jweAlgorithm;
    }
    
    public void setJweAlgorithm(final JWEAlgorithm jweAlgorithm) {
        this.jweAlgorithm = jweAlgorithm;
    }
    
    public JWSAlgorithm getJwsAlgorithm() {
        return this.jwsAlgorithm;
    }
    
    public void setJwsAlgorithm(final JWSAlgorithm jwsAlgorithm) {
        this.jwsAlgorithm = jwsAlgorithm;
    }
    
    public EncryptionMethod getEncryptionMethod() {
        return this.encryptionMethod;
    }
    
    public void setEncryptionMethod(final EncryptionMethod encryptionMethod) {
        this.encryptionMethod = encryptionMethod;
    }
}
