// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import java.util.HashMap;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.util.Iterator;
import java.util.ArrayList;
import net.minidev.json.JSONObject;
import com.nimbusds.jose.JOSEException;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.security.KeyStore;
import com.nimbusds.jose.util.Base64;
import java.util.List;
import com.nimbusds.jose.util.Base64URL;
import java.net.URI;
import com.nimbusds.jose.Algorithm;
import java.util.Set;
import java.io.Serializable;
import net.minidev.json.JSONAware;

public abstract class JWK implements JSONAware, Serializable
{
    private static final long serialVersionUID = 1L;
    public static final String MIME_TYPE = "application/jwk+json; charset=UTF-8";
    private final KeyType kty;
    private final KeyUse use;
    private final Set<KeyOperation> ops;
    private final Algorithm alg;
    private final String kid;
    private final URI x5u;
    @Deprecated
    private final Base64URL x5t;
    private Base64URL x5t256;
    private final List<Base64> x5c;
    private final KeyStore keyStore;
    
    protected JWK(final KeyType kty, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        if (kty == null) {
            throw new IllegalArgumentException("The key type \"kty\" parameter must not be null");
        }
        this.kty = kty;
        if (!KeyUseAndOpsConsistency.areConsistent(use, ops)) {
            throw new IllegalArgumentException("The key use \"use\" and key options \"key_opts\" parameters are not consistent, see RFC 7517, section 4.3");
        }
        this.use = use;
        this.ops = ops;
        this.alg = alg;
        this.kid = kid;
        this.x5u = x5u;
        this.x5t = x5t;
        this.x5t256 = x5t256;
        this.x5c = x5c;
        this.keyStore = ks;
    }
    
    public KeyType getKeyType() {
        return this.kty;
    }
    
    public KeyUse getKeyUse() {
        return this.use;
    }
    
    public Set<KeyOperation> getKeyOperations() {
        return this.ops;
    }
    
    public Algorithm getAlgorithm() {
        return this.alg;
    }
    
    public String getKeyID() {
        return this.kid;
    }
    
    public URI getX509CertURL() {
        return this.x5u;
    }
    
    @Deprecated
    public Base64URL getX509CertThumbprint() {
        return this.x5t;
    }
    
    public Base64URL getX509CertSHA256Thumbprint() {
        return this.x5t256;
    }
    
    public List<Base64> getX509CertChain() {
        if (this.x5c == null) {
            return null;
        }
        return Collections.unmodifiableList((List<? extends Base64>)this.x5c);
    }
    
    public KeyStore getKeyStore() {
        return this.keyStore;
    }
    
    public abstract LinkedHashMap<String, ?> getRequiredParams();
    
    public Base64URL computeThumbprint() throws JOSEException {
        return this.computeThumbprint("SHA-256");
    }
    
    public Base64URL computeThumbprint(final String hashAlg) throws JOSEException {
        return ThumbprintUtils.compute(hashAlg, this);
    }
    
    public abstract boolean isPrivate();
    
    public abstract JWK toPublicJWK();
    
    public abstract int size();
    
    public JSONObject toJSONObject() {
        final JSONObject o = new JSONObject();
        ((HashMap<String, String>)o).put("kty", this.kty.getValue());
        if (this.use != null) {
            ((HashMap<String, String>)o).put("use", this.use.identifier());
        }
        if (this.ops != null) {
            final List<String> sl = new ArrayList<String>(this.ops.size());
            for (final KeyOperation op : this.ops) {
                sl.add(op.identifier());
            }
            ((HashMap<String, List<String>>)o).put("key_ops", sl);
        }
        if (this.alg != null) {
            ((HashMap<String, String>)o).put("alg", this.alg.getName());
        }
        if (this.kid != null) {
            ((HashMap<String, String>)o).put("kid", this.kid);
        }
        if (this.x5u != null) {
            ((HashMap<String, String>)o).put("x5u", this.x5u.toString());
        }
        if (this.x5t != null) {
            ((HashMap<String, String>)o).put("x5t", this.x5t.toString());
        }
        if (this.x5t256 != null) {
            ((HashMap<String, String>)o).put("x5t#S256", this.x5t256.toString());
        }
        if (this.x5c != null) {
            ((HashMap<String, List<Base64>>)o).put("x5c", this.x5c);
        }
        return o;
    }
    
    @Override
    public String toJSONString() {
        return this.toJSONObject().toString();
    }
    
    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }
    
    public static JWK parse(final String s) throws ParseException {
        return parse(JSONObjectUtils.parse(s));
    }
    
    public static JWK parse(final JSONObject jsonObject) throws ParseException {
        final KeyType kty = KeyType.parse(JSONObjectUtils.getString(jsonObject, "kty"));
        if (kty == KeyType.EC) {
            return ECKey.parse(jsonObject);
        }
        if (kty == KeyType.RSA) {
            return RSAKey.parse(jsonObject);
        }
        if (kty == KeyType.OCT) {
            return OctetSequenceKey.parse(jsonObject);
        }
        throw new ParseException("Unsupported key type \"kty\" parameter: " + kty, 0);
    }
    
    public static JWK parse(final X509Certificate cert) throws JOSEException {
        if (cert.getPublicKey() instanceof RSAPublicKey) {
            return RSAKey.parse(cert);
        }
        if (cert.getPublicKey() instanceof ECPublicKey) {
            return ECKey.parse(cert);
        }
        throw new JOSEException("Unsupported public key algorithm: " + cert.getPublicKey().getAlgorithm());
    }
    
    public static JWK load(final KeyStore keyStore, final String alias, final char[] pin) throws KeyStoreException, JOSEException {
        final Certificate cert = keyStore.getCertificate(alias);
        if (cert == null) {
            return OctetSequenceKey.load(keyStore, alias, pin);
        }
        if (cert.getPublicKey() instanceof RSAPublicKey) {
            return RSAKey.load(keyStore, alias, pin);
        }
        if (cert.getPublicKey() instanceof ECPublicKey) {
            return ECKey.load(keyStore, alias, pin);
        }
        throw new JOSEException("Unsupported public key algorithm: " + cert.getPublicKey().getAlgorithm());
    }
}
