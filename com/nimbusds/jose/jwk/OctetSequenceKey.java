// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import java.util.HashMap;
import java.security.KeyStoreException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import com.nimbusds.jose.JOSEException;
import java.text.ParseException;
import com.nimbusds.jose.util.JSONObjectUtils;
import net.minidev.json.JSONObject;
import com.nimbusds.jose.util.IntegerOverflowException;
import com.nimbusds.jose.util.ByteUtils;
import java.util.LinkedHashMap;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.KeyStore;
import com.nimbusds.jose.util.Base64;
import java.util.List;
import java.net.URI;
import com.nimbusds.jose.Algorithm;
import java.util.Set;
import com.nimbusds.jose.util.Base64URL;
import net.jcip.annotations.Immutable;

@Immutable
public final class OctetSequenceKey extends JWK implements SecretJWK
{
    private static final long serialVersionUID = 1L;
    private final Base64URL k;
    
    public OctetSequenceKey(final Base64URL k, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        super(KeyType.OCT, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
        if (k == null) {
            throw new IllegalArgumentException("The key value must not be null");
        }
        this.k = k;
    }
    
    public Base64URL getKeyValue() {
        return this.k;
    }
    
    public byte[] toByteArray() {
        return this.getKeyValue().decode();
    }
    
    @Override
    public SecretKey toSecretKey() {
        return this.toSecretKey("NONE");
    }
    
    public SecretKey toSecretKey(final String jcaAlg) {
        return new SecretKeySpec(this.toByteArray(), jcaAlg);
    }
    
    @Override
    public LinkedHashMap<String, ?> getRequiredParams() {
        final LinkedHashMap<String, String> requiredParams = new LinkedHashMap<String, String>();
        requiredParams.put("k", this.k.toString());
        requiredParams.put("kty", this.getKeyType().toString());
        return requiredParams;
    }
    
    @Override
    public boolean isPrivate() {
        return true;
    }
    
    @Override
    public OctetSequenceKey toPublicJWK() {
        return null;
    }
    
    @Override
    public int size() {
        try {
            return ByteUtils.safeBitLength(this.k.decode());
        }
        catch (IntegerOverflowException e) {
            throw new ArithmeticException(e.getMessage());
        }
    }
    
    @Override
    public JSONObject toJSONObject() {
        final JSONObject o = super.toJSONObject();
        ((HashMap<String, String>)o).put("k", this.k.toString());
        return o;
    }
    
    public static OctetSequenceKey parse(final String s) throws ParseException {
        return parse(JSONObjectUtils.parse(s));
    }
    
    public static OctetSequenceKey parse(final JSONObject jsonObject) throws ParseException {
        final Base64URL k = new Base64URL(JSONObjectUtils.getString(jsonObject, "k"));
        final KeyType kty = JWKMetadata.parseKeyType(jsonObject);
        if (kty != KeyType.OCT) {
            throw new ParseException("The key type \"kty\" must be oct", 0);
        }
        return new OctetSequenceKey(k, JWKMetadata.parseKeyUse(jsonObject), JWKMetadata.parseKeyOperations(jsonObject), JWKMetadata.parseAlgorithm(jsonObject), JWKMetadata.parseKeyID(jsonObject), JWKMetadata.parseX509CertURL(jsonObject), JWKMetadata.parseX509CertThumbprint(jsonObject), JWKMetadata.parseX509CertSHA256Thumbprint(jsonObject), JWKMetadata.parseX509CertChain(jsonObject), null);
    }
    
    public static OctetSequenceKey load(final KeyStore keyStore, final String alias, final char[] pin) throws KeyStoreException, JOSEException {
        Key key;
        try {
            key = keyStore.getKey(alias, pin);
        }
        catch (UnrecoverableKeyException | NoSuchAlgorithmException ex2) {
            final GeneralSecurityException ex;
            final GeneralSecurityException e = ex;
            throw new JOSEException("Couldn't retrieve secret key (bad pin?): " + e.getMessage(), e);
        }
        if (!(key instanceof SecretKey)) {
            return null;
        }
        return new Builder((SecretKey)key).keyID(alias).keyStore(keyStore).build();
    }
    
    public static class Builder
    {
        private final Base64URL k;
        private KeyUse use;
        private Set<KeyOperation> ops;
        private Algorithm alg;
        private String kid;
        private URI x5u;
        @Deprecated
        private Base64URL x5t;
        private Base64URL x5t256;
        private List<Base64> x5c;
        private KeyStore ks;
        
        public Builder(final Base64URL k) {
            if (k == null) {
                throw new IllegalArgumentException("The key value must not be null");
            }
            this.k = k;
        }
        
        public Builder(final byte[] key) {
            this(Base64URL.encode(key));
            if (key.length == 0) {
                throw new IllegalArgumentException("The key must have a positive length");
            }
        }
        
        public Builder(final SecretKey secretKey) {
            this(secretKey.getEncoded());
        }
        
        public Builder keyUse(final KeyUse use) {
            this.use = use;
            return this;
        }
        
        public Builder keyOperations(final Set<KeyOperation> ops) {
            this.ops = ops;
            return this;
        }
        
        public Builder algorithm(final Algorithm alg) {
            this.alg = alg;
            return this;
        }
        
        public Builder keyID(final String kid) {
            this.kid = kid;
            return this;
        }
        
        public Builder keyIDFromThumbprint() throws JOSEException {
            return this.keyIDFromThumbprint("SHA-256");
        }
        
        public Builder keyIDFromThumbprint(final String hashAlg) throws JOSEException {
            final LinkedHashMap<String, String> requiredParams = new LinkedHashMap<String, String>();
            requiredParams.put("k", this.k.toString());
            requiredParams.put("kty", KeyType.OCT.getValue());
            this.kid = ThumbprintUtils.compute(hashAlg, requiredParams).toString();
            return this;
        }
        
        public Builder x509CertURL(final URI x5u) {
            this.x5u = x5u;
            return this;
        }
        
        @Deprecated
        public Builder x509CertThumbprint(final Base64URL x5t) {
            this.x5t = x5t;
            return this;
        }
        
        public Builder x509CertSHA256Thumbprint(final Base64URL x5t256) {
            this.x5t256 = x5t256;
            return this;
        }
        
        public Builder x509CertChain(final List<Base64> x5c) {
            this.x5c = x5c;
            return this;
        }
        
        public Builder keyStore(final KeyStore keyStore) {
            this.ks = keyStore;
            return this;
        }
        
        public OctetSequenceKey build() {
            try {
                return new OctetSequenceKey(this.k, this.use, this.ops, this.alg, this.kid, this.x5u, this.x5t, this.x5t256, this.x5c, this.ks);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }
}
