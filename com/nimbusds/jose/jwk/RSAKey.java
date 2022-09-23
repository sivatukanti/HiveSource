// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import java.util.HashMap;
import java.io.Serializable;
import java.security.KeyStoreException;
import java.security.Key;
import java.security.cert.Certificate;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.text.ParseException;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.util.Iterator;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import com.nimbusds.jose.util.IntegerOverflowException;
import com.nimbusds.jose.util.ByteUtils;
import java.util.LinkedHashMap;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAMultiPrimePrivateCrtKeySpec;
import java.security.spec.RSAOtherPrimeInfo;
import java.security.spec.RSAPrivateKeySpec;
import java.security.GeneralSecurityException;
import java.math.BigInteger;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import com.nimbusds.jose.JOSEException;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.security.interfaces.RSAMultiPrimePrivateCrtKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.security.KeyStore;
import com.nimbusds.jose.util.Base64;
import java.net.URI;
import com.nimbusds.jose.Algorithm;
import java.util.Set;
import java.security.PrivateKey;
import java.util.List;
import com.nimbusds.jose.util.Base64URL;
import net.jcip.annotations.Immutable;

@Immutable
public final class RSAKey extends JWK implements AssymetricJWK
{
    private static final long serialVersionUID = 1L;
    private final Base64URL n;
    private final Base64URL e;
    private final Base64URL d;
    private final Base64URL p;
    private final Base64URL q;
    private final Base64URL dp;
    private final Base64URL dq;
    private final Base64URL qi;
    private final List<OtherPrimesInfo> oth;
    private final PrivateKey privateKey;
    
    public RSAKey(final Base64URL n, final Base64URL e, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        this(n, e, null, null, null, null, null, null, null, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
    }
    
    public RSAKey(final Base64URL n, final Base64URL e, final Base64URL d, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        this(n, e, d, null, null, null, null, null, null, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
        if (d == null) {
            throw new IllegalArgumentException("The private exponent must not be null");
        }
    }
    
    public RSAKey(final Base64URL n, final Base64URL e, final Base64URL p, final Base64URL q, final Base64URL dp, final Base64URL dq, final Base64URL qi, final List<OtherPrimesInfo> oth, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        this(n, e, null, p, q, dp, dq, qi, oth, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
        if (p == null) {
            throw new IllegalArgumentException("The first prime factor must not be null");
        }
        if (q == null) {
            throw new IllegalArgumentException("The second prime factor must not be null");
        }
        if (dp == null) {
            throw new IllegalArgumentException("The first factor CRT exponent must not be null");
        }
        if (dq == null) {
            throw new IllegalArgumentException("The second factor CRT exponent must not be null");
        }
        if (qi == null) {
            throw new IllegalArgumentException("The first CRT coefficient must not be null");
        }
    }
    
    @Deprecated
    public RSAKey(final Base64URL n, final Base64URL e, final Base64URL d, final Base64URL p, final Base64URL q, final Base64URL dp, final Base64URL dq, final Base64URL qi, final List<OtherPrimesInfo> oth, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c) {
        this(n, e, d, p, q, dp, dq, qi, oth, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null);
    }
    
    public RSAKey(final Base64URL n, final Base64URL e, final Base64URL d, final Base64URL p, final Base64URL q, final Base64URL dp, final Base64URL dq, final Base64URL qi, final List<OtherPrimesInfo> oth, final PrivateKey prv, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        super(KeyType.RSA, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
        if (n == null) {
            throw new IllegalArgumentException("The modulus value must not be null");
        }
        this.n = n;
        if (e == null) {
            throw new IllegalArgumentException("The public exponent value must not be null");
        }
        this.e = e;
        this.d = d;
        if (p != null && q != null && dp != null && dq != null && qi != null) {
            this.p = p;
            this.q = q;
            this.dp = dp;
            this.dq = dq;
            this.qi = qi;
            if (oth != null) {
                this.oth = Collections.unmodifiableList((List<? extends OtherPrimesInfo>)oth);
            }
            else {
                this.oth = Collections.emptyList();
            }
        }
        else if (p == null && q == null && dp == null && dq == null && qi == null && oth == null) {
            this.p = null;
            this.q = null;
            this.dp = null;
            this.dq = null;
            this.qi = null;
            this.oth = Collections.emptyList();
        }
        else if (p != null || q != null || dp != null || dq != null || qi != null) {
            if (p == null) {
                throw new IllegalArgumentException("Incomplete second private (CRT) representation: The first prime factor must not be null");
            }
            if (q == null) {
                throw new IllegalArgumentException("Incomplete second private (CRT) representation: The second prime factor must not be null");
            }
            if (dp == null) {
                throw new IllegalArgumentException("Incomplete second private (CRT) representation: The first factor CRT exponent must not be null");
            }
            if (dq == null) {
                throw new IllegalArgumentException("Incomplete second private (CRT) representation: The second factor CRT exponent must not be null");
            }
            throw new IllegalArgumentException("Incomplete second private (CRT) representation: The first CRT coefficient must not be null");
        }
        else {
            this.p = null;
            this.q = null;
            this.dp = null;
            this.dq = null;
            this.qi = null;
            this.oth = Collections.emptyList();
        }
        this.privateKey = prv;
    }
    
    public RSAKey(final RSAPublicKey pub, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        this(Base64URL.encode(pub.getModulus()), Base64URL.encode(pub.getPublicExponent()), use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
    }
    
    public RSAKey(final RSAPublicKey pub, final RSAPrivateKey priv, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        this(Base64URL.encode(pub.getModulus()), Base64URL.encode(pub.getPublicExponent()), Base64URL.encode(priv.getPrivateExponent()), use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
    }
    
    public RSAKey(final RSAPublicKey pub, final RSAPrivateCrtKey priv, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        this(Base64URL.encode(pub.getModulus()), Base64URL.encode(pub.getPublicExponent()), Base64URL.encode(priv.getPrivateExponent()), Base64URL.encode(priv.getPrimeP()), Base64URL.encode(priv.getPrimeQ()), Base64URL.encode(priv.getPrimeExponentP()), Base64URL.encode(priv.getPrimeExponentQ()), Base64URL.encode(priv.getCrtCoefficient()), null, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
    }
    
    public RSAKey(final RSAPublicKey pub, final RSAMultiPrimePrivateCrtKey priv, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        this(Base64URL.encode(pub.getModulus()), Base64URL.encode(pub.getPublicExponent()), Base64URL.encode(priv.getPrivateExponent()), Base64URL.encode(priv.getPrimeP()), Base64URL.encode(priv.getPrimeQ()), Base64URL.encode(priv.getPrimeExponentP()), Base64URL.encode(priv.getPrimeExponentQ()), Base64URL.encode(priv.getCrtCoefficient()), OtherPrimesInfo.toList(priv.getOtherPrimeInfo()), null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
    }
    
    public RSAKey(final RSAPublicKey pub, final PrivateKey priv, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        this(Base64URL.encode(pub.getModulus()), Base64URL.encode(pub.getPublicExponent()), null, null, null, null, null, null, null, priv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
    }
    
    public Base64URL getModulus() {
        return this.n;
    }
    
    public Base64URL getPublicExponent() {
        return this.e;
    }
    
    public Base64URL getPrivateExponent() {
        return this.d;
    }
    
    public Base64URL getFirstPrimeFactor() {
        return this.p;
    }
    
    public Base64URL getSecondPrimeFactor() {
        return this.q;
    }
    
    public Base64URL getFirstFactorCRTExponent() {
        return this.dp;
    }
    
    public Base64URL getSecondFactorCRTExponent() {
        return this.dq;
    }
    
    public Base64URL getFirstCRTCoefficient() {
        return this.qi;
    }
    
    public List<OtherPrimesInfo> getOtherPrimes() {
        return this.oth;
    }
    
    public RSAPublicKey toRSAPublicKey() throws JOSEException {
        final BigInteger modulus = this.n.decodeToBigInteger();
        final BigInteger exponent = this.e.decodeToBigInteger();
        final RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        try {
            final KeyFactory factory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey)factory.generatePublic(spec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException ex2) {
            final GeneralSecurityException ex;
            final GeneralSecurityException e = ex;
            throw new JOSEException(e.getMessage(), e);
        }
    }
    
    public RSAPrivateKey toRSAPrivateKey() throws JOSEException {
        if (this.d == null) {
            return null;
        }
        final BigInteger modulus = this.n.decodeToBigInteger();
        final BigInteger privateExponent = this.d.decodeToBigInteger();
        RSAPrivateKeySpec spec;
        if (this.p == null) {
            spec = new RSAPrivateKeySpec(modulus, privateExponent);
        }
        else {
            final BigInteger publicExponent = this.e.decodeToBigInteger();
            final BigInteger primeP = this.p.decodeToBigInteger();
            final BigInteger primeQ = this.q.decodeToBigInteger();
            final BigInteger primeExponentP = this.dp.decodeToBigInteger();
            final BigInteger primeExponentQ = this.dq.decodeToBigInteger();
            final BigInteger crtCoefficient = this.qi.decodeToBigInteger();
            if (this.oth != null && !this.oth.isEmpty()) {
                final RSAOtherPrimeInfo[] otherInfo = new RSAOtherPrimeInfo[this.oth.size()];
                for (int i = 0; i < this.oth.size(); ++i) {
                    final OtherPrimesInfo opi = this.oth.get(i);
                    final BigInteger otherPrime = opi.getPrimeFactor().decodeToBigInteger();
                    final BigInteger otherPrimeExponent = opi.getFactorCRTExponent().decodeToBigInteger();
                    final BigInteger otherCrtCoefficient = opi.getFactorCRTCoefficient().decodeToBigInteger();
                    otherInfo[i] = new RSAOtherPrimeInfo(otherPrime, otherPrimeExponent, otherCrtCoefficient);
                }
                spec = new RSAMultiPrimePrivateCrtKeySpec(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient, otherInfo);
            }
            else {
                spec = new RSAPrivateCrtKeySpec(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient);
            }
        }
        try {
            final KeyFactory factory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey)factory.generatePrivate(spec);
        }
        catch (InvalidKeySpecException | NoSuchAlgorithmException ex2) {
            final GeneralSecurityException ex;
            final GeneralSecurityException e = ex;
            throw new JOSEException(e.getMessage(), e);
        }
    }
    
    @Override
    public PublicKey toPublicKey() throws JOSEException {
        return this.toRSAPublicKey();
    }
    
    @Override
    public PrivateKey toPrivateKey() throws JOSEException {
        final PrivateKey prv = this.toRSAPrivateKey();
        if (prv != null) {
            return prv;
        }
        return this.privateKey;
    }
    
    @Override
    public KeyPair toKeyPair() throws JOSEException {
        return new KeyPair(this.toRSAPublicKey(), this.toPrivateKey());
    }
    
    @Override
    public LinkedHashMap<String, ?> getRequiredParams() {
        final LinkedHashMap<String, String> requiredParams = new LinkedHashMap<String, String>();
        requiredParams.put("e", this.e.toString());
        requiredParams.put("kty", this.getKeyType().getValue());
        requiredParams.put("n", this.n.toString());
        return requiredParams;
    }
    
    @Override
    public boolean isPrivate() {
        return this.d != null || this.p != null || this.privateKey != null;
    }
    
    @Override
    public int size() {
        try {
            return ByteUtils.safeBitLength(this.n.decode());
        }
        catch (IntegerOverflowException e) {
            throw new ArithmeticException(e.getMessage());
        }
    }
    
    @Override
    public RSAKey toPublicJWK() {
        return new RSAKey(this.getModulus(), this.getPublicExponent(), this.getKeyUse(), this.getKeyOperations(), this.getAlgorithm(), this.getKeyID(), this.getX509CertURL(), this.getX509CertThumbprint(), this.getX509CertSHA256Thumbprint(), this.getX509CertChain(), this.getKeyStore());
    }
    
    @Override
    public JSONObject toJSONObject() {
        final JSONObject o = super.toJSONObject();
        ((HashMap<String, String>)o).put("n", this.n.toString());
        ((HashMap<String, String>)o).put("e", this.e.toString());
        if (this.d != null) {
            ((HashMap<String, String>)o).put("d", this.d.toString());
        }
        if (this.p != null) {
            ((HashMap<String, String>)o).put("p", this.p.toString());
        }
        if (this.q != null) {
            ((HashMap<String, String>)o).put("q", this.q.toString());
        }
        if (this.dp != null) {
            ((HashMap<String, String>)o).put("dp", this.dp.toString());
        }
        if (this.dq != null) {
            ((HashMap<String, String>)o).put("dq", this.dq.toString());
        }
        if (this.qi != null) {
            ((HashMap<String, String>)o).put("qi", this.qi.toString());
        }
        if (this.oth != null && !this.oth.isEmpty()) {
            final JSONArray a = new JSONArray();
            for (final OtherPrimesInfo other : this.oth) {
                final JSONObject oo = new JSONObject();
                ((HashMap<String, String>)oo).put("r", other.r.toString());
                ((HashMap<String, String>)oo).put("d", other.d.toString());
                ((HashMap<String, String>)oo).put("t", other.t.toString());
                ((ArrayList<JSONObject>)a).add(oo);
            }
            ((HashMap<String, JSONArray>)o).put("oth", a);
        }
        return o;
    }
    
    public static RSAKey parse(final String s) throws ParseException {
        return parse(JSONObjectUtils.parse(s));
    }
    
    public static RSAKey parse(final JSONObject jsonObject) throws ParseException {
        final Base64URL n = new Base64URL(JSONObjectUtils.getString(jsonObject, "n"));
        final Base64URL e = new Base64URL(JSONObjectUtils.getString(jsonObject, "e"));
        final KeyType kty = KeyType.parse(JSONObjectUtils.getString(jsonObject, "kty"));
        if (kty != KeyType.RSA) {
            throw new ParseException("The key type \"kty\" must be RSA", 0);
        }
        Base64URL d = null;
        if (jsonObject.containsKey("d")) {
            d = new Base64URL(JSONObjectUtils.getString(jsonObject, "d"));
        }
        Base64URL p = null;
        if (jsonObject.containsKey("p")) {
            p = new Base64URL(JSONObjectUtils.getString(jsonObject, "p"));
        }
        Base64URL q = null;
        if (jsonObject.containsKey("q")) {
            q = new Base64URL(JSONObjectUtils.getString(jsonObject, "q"));
        }
        Base64URL dp = null;
        if (jsonObject.containsKey("dp")) {
            dp = new Base64URL(JSONObjectUtils.getString(jsonObject, "dp"));
        }
        Base64URL dq = null;
        if (jsonObject.containsKey("dq")) {
            dq = new Base64URL(JSONObjectUtils.getString(jsonObject, "dq"));
        }
        Base64URL qi = null;
        if (jsonObject.containsKey("qi")) {
            qi = new Base64URL(JSONObjectUtils.getString(jsonObject, "qi"));
        }
        List<OtherPrimesInfo> oth = null;
        if (jsonObject.containsKey("oth")) {
            final JSONArray arr = JSONObjectUtils.getJSONArray(jsonObject, "oth");
            oth = new ArrayList<OtherPrimesInfo>(arr.size());
            for (final Object o : arr) {
                if (o instanceof JSONObject) {
                    final JSONObject otherJson = (JSONObject)o;
                    final Base64URL r = new Base64URL(JSONObjectUtils.getString(otherJson, "r"));
                    final Base64URL odq = new Base64URL(JSONObjectUtils.getString(otherJson, "dq"));
                    final Base64URL t = new Base64URL(JSONObjectUtils.getString(otherJson, "t"));
                    final OtherPrimesInfo prime = new OtherPrimesInfo(r, odq, t);
                    oth.add(prime);
                }
            }
        }
        try {
            return new RSAKey(n, e, d, p, q, dp, dq, qi, oth, null, JWKMetadata.parseKeyUse(jsonObject), JWKMetadata.parseKeyOperations(jsonObject), JWKMetadata.parseAlgorithm(jsonObject), JWKMetadata.parseKeyID(jsonObject), JWKMetadata.parseX509CertURL(jsonObject), JWKMetadata.parseX509CertThumbprint(jsonObject), JWKMetadata.parseX509CertSHA256Thumbprint(jsonObject), JWKMetadata.parseX509CertChain(jsonObject), null);
        }
        catch (IllegalArgumentException ex) {
            throw new ParseException(ex.getMessage(), 0);
        }
    }
    
    public static RSAKey parse(final X509Certificate cert) throws JOSEException {
        if (!(cert.getPublicKey() instanceof RSAPublicKey)) {
            throw new JOSEException("The public key of the X.509 certificate is not RSA");
        }
        final RSAPublicKey publicKey = (RSAPublicKey)cert.getPublicKey();
        try {
            final MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return new Builder(publicKey).keyUse(KeyUse.from(cert)).keyID(cert.getSerialNumber().toString(10)).x509CertChain(Collections.singletonList(Base64.encode(cert.getEncoded()))).x509CertSHA256Thumbprint(Base64URL.encode(sha256.digest(cert.getEncoded()))).build();
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Couldn't encode x5t parameter: " + e.getMessage(), e);
        }
        catch (CertificateEncodingException e2) {
            throw new JOSEException("Couldn't encode x5c parameter: " + e2.getMessage(), e2);
        }
    }
    
    public static RSAKey load(final KeyStore keyStore, final String alias, final char[] pin) throws KeyStoreException, JOSEException {
        final Certificate cert = keyStore.getCertificate(alias);
        if (cert == null || !(cert instanceof X509Certificate)) {
            return null;
        }
        final X509Certificate x509Cert = (X509Certificate)cert;
        if (!(x509Cert.getPublicKey() instanceof RSAPublicKey)) {
            throw new JOSEException("Couldn't load RSA JWK: The key algorithm is not RSA");
        }
        RSAKey rsaJWK = parse(x509Cert);
        rsaJWK = new Builder(rsaJWK).keyID(alias).keyStore(keyStore).build();
        Key key;
        try {
            key = keyStore.getKey(alias, pin);
        }
        catch (UnrecoverableKeyException | NoSuchAlgorithmException ex2) {
            final GeneralSecurityException ex;
            final GeneralSecurityException e = ex;
            throw new JOSEException("Couldn't retrieve private RSA key (bad pin?): " + e.getMessage(), e);
        }
        if (key instanceof RSAPrivateKey) {
            return new Builder(rsaJWK).privateKey((RSAPrivateKey)key).build();
        }
        if (key instanceof PrivateKey && "RSA".equalsIgnoreCase(key.getAlgorithm())) {
            return new Builder(rsaJWK).privateKey((PrivateKey)key).build();
        }
        return rsaJWK;
    }
    
    @Immutable
    public static class OtherPrimesInfo implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private final Base64URL r;
        private final Base64URL d;
        private final Base64URL t;
        
        public OtherPrimesInfo(final Base64URL r, final Base64URL d, final Base64URL t) {
            if (r == null) {
                throw new IllegalArgumentException("The prime factor must not be null");
            }
            this.r = r;
            if (d == null) {
                throw new IllegalArgumentException("The factor CRT exponent must not be null");
            }
            this.d = d;
            if (t == null) {
                throw new IllegalArgumentException("The factor CRT coefficient must not be null");
            }
            this.t = t;
        }
        
        public OtherPrimesInfo(final RSAOtherPrimeInfo oth) {
            this.r = Base64URL.encode(oth.getPrime());
            this.d = Base64URL.encode(oth.getExponent());
            this.t = Base64URL.encode(oth.getCrtCoefficient());
        }
        
        public Base64URL getPrimeFactor() {
            return this.r;
        }
        
        public Base64URL getFactorCRTExponent() {
            return this.d;
        }
        
        public Base64URL getFactorCRTCoefficient() {
            return this.t;
        }
        
        public static List<OtherPrimesInfo> toList(final RSAOtherPrimeInfo[] othArray) {
            final List<OtherPrimesInfo> list = new ArrayList<OtherPrimesInfo>();
            if (othArray == null) {
                return list;
            }
            for (final RSAOtherPrimeInfo oth : othArray) {
                list.add(new OtherPrimesInfo(oth));
            }
            return list;
        }
    }
    
    public static class Builder
    {
        private final Base64URL n;
        private final Base64URL e;
        private Base64URL d;
        private Base64URL p;
        private Base64URL q;
        private Base64URL dp;
        private Base64URL dq;
        private Base64URL qi;
        private List<OtherPrimesInfo> oth;
        private PrivateKey priv;
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
        
        public Builder(final Base64URL n, final Base64URL e) {
            if (n == null) {
                throw new IllegalArgumentException("The modulus value must not be null");
            }
            this.n = n;
            if (e == null) {
                throw new IllegalArgumentException("The public exponent value must not be null");
            }
            this.e = e;
        }
        
        public Builder(final RSAPublicKey pub) {
            this.n = Base64URL.encode(pub.getModulus());
            this.e = Base64URL.encode(pub.getPublicExponent());
        }
        
        public Builder(final RSAKey rsaJWK) {
            this.n = rsaJWK.n;
            this.e = rsaJWK.e;
            this.d = rsaJWK.d;
            this.p = rsaJWK.p;
            this.q = rsaJWK.q;
            this.dp = rsaJWK.dp;
            this.dq = rsaJWK.dq;
            this.qi = rsaJWK.qi;
            this.oth = rsaJWK.oth;
            this.priv = rsaJWK.privateKey;
            this.use = rsaJWK.getKeyUse();
            this.ops = rsaJWK.getKeyOperations();
            this.alg = rsaJWK.getAlgorithm();
            this.kid = rsaJWK.getKeyID();
            this.x5u = rsaJWK.getX509CertURL();
            this.x5t = rsaJWK.getX509CertThumbprint();
            this.x5t256 = rsaJWK.getX509CertSHA256Thumbprint();
            this.x5c = rsaJWK.getX509CertChain();
            this.ks = rsaJWK.getKeyStore();
        }
        
        public Builder privateExponent(final Base64URL d) {
            this.d = d;
            return this;
        }
        
        public Builder privateKey(final RSAPrivateKey priv) {
            if (priv instanceof RSAPrivateCrtKey) {
                return this.privateKey((RSAPrivateCrtKey)priv);
            }
            if (priv instanceof RSAMultiPrimePrivateCrtKey) {
                return this.privateKey((RSAMultiPrimePrivateCrtKey)priv);
            }
            this.d = Base64URL.encode(priv.getPrivateExponent());
            return this;
        }
        
        public Builder privateKey(final PrivateKey priv) {
            if (!"RSA".equalsIgnoreCase(priv.getAlgorithm())) {
                throw new IllegalArgumentException("The private key algorithm must be RSA");
            }
            this.priv = priv;
            return this;
        }
        
        public Builder firstPrimeFactor(final Base64URL p) {
            this.p = p;
            return this;
        }
        
        public Builder secondPrimeFactor(final Base64URL q) {
            this.q = q;
            return this;
        }
        
        public Builder firstFactorCRTExponent(final Base64URL dp) {
            this.dp = dp;
            return this;
        }
        
        public Builder secondFactorCRTExponent(final Base64URL dq) {
            this.dq = dq;
            return this;
        }
        
        public Builder firstCRTCoefficient(final Base64URL qi) {
            this.qi = qi;
            return this;
        }
        
        public Builder otherPrimes(final List<OtherPrimesInfo> oth) {
            this.oth = oth;
            return this;
        }
        
        public Builder privateKey(final RSAPrivateCrtKey priv) {
            this.d = Base64URL.encode(priv.getPrivateExponent());
            this.p = Base64URL.encode(priv.getPrimeP());
            this.q = Base64URL.encode(priv.getPrimeQ());
            this.dp = Base64URL.encode(priv.getPrimeExponentP());
            this.dq = Base64URL.encode(priv.getPrimeExponentQ());
            this.qi = Base64URL.encode(priv.getCrtCoefficient());
            return this;
        }
        
        public Builder privateKey(final RSAMultiPrimePrivateCrtKey priv) {
            this.d = Base64URL.encode(priv.getPrivateExponent());
            this.p = Base64URL.encode(priv.getPrimeP());
            this.q = Base64URL.encode(priv.getPrimeQ());
            this.dp = Base64URL.encode(priv.getPrimeExponentP());
            this.dq = Base64URL.encode(priv.getPrimeExponentQ());
            this.qi = Base64URL.encode(priv.getCrtCoefficient());
            this.oth = OtherPrimesInfo.toList(priv.getOtherPrimeInfo());
            return this;
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
            requiredParams.put("e", this.e.toString());
            requiredParams.put("kty", KeyType.RSA.getValue());
            requiredParams.put("n", this.n.toString());
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
        
        public RSAKey build() {
            try {
                return new RSAKey(this.n, this.e, this.d, this.p, this.q, this.dp, this.dq, this.qi, this.oth, this.priv, this.use, this.ops, this.alg, this.kid, this.x5u, this.x5t, this.x5t256, this.x5c, this.ks);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }
}
