// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import java.util.HashMap;
import com.nimbusds.jose.JWSAlgorithm;
import java.io.Serializable;
import java.security.KeyStoreException;
import java.security.Key;
import java.security.cert.Certificate;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.util.Collections;
import java.security.MessageDigest;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import com.nimbusds.jose.util.JSONObjectUtils;
import net.minidev.json.JSONObject;
import java.util.LinkedHashMap;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.ECPrivateKeySpec;
import java.security.GeneralSecurityException;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.ECPoint;
import com.nimbusds.jose.JOSEException;
import java.security.Provider;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.KeyStore;
import com.nimbusds.jose.util.Base64;
import java.util.List;
import java.net.URI;
import com.nimbusds.jose.Algorithm;
import java.util.Set;
import java.security.spec.ECParameterSpec;
import com.nimbusds.jose.crypto.utils.ECChecks;
import com.nimbusds.jose.util.BigIntegerUtils;
import java.math.BigInteger;
import java.security.PrivateKey;
import com.nimbusds.jose.util.Base64URL;
import net.jcip.annotations.Immutable;

@Immutable
public final class ECKey extends JWK implements AssymetricJWK
{
    private static final long serialVersionUID = 1L;
    private final Curve crv;
    private final Base64URL x;
    private final Base64URL y;
    private final Base64URL d;
    private final PrivateKey privateKey;
    
    public static Base64URL encodeCoordinate(final int fieldSize, final BigInteger coordinate) {
        final byte[] notPadded = BigIntegerUtils.toBytesUnsigned(coordinate);
        final int bytesToOutput = (fieldSize + 7) / 8;
        if (notPadded.length >= bytesToOutput) {
            return Base64URL.encode(notPadded);
        }
        final byte[] padded = new byte[bytesToOutput];
        System.arraycopy(notPadded, 0, padded, bytesToOutput - notPadded.length, notPadded.length);
        return Base64URL.encode(padded);
    }
    
    private static void ensurePublicCoordinatesOnCurve(final Curve crv, final Base64URL x, final Base64URL y) {
        final ECParameterSpec ecSpec = crv.toECParameterSpec();
        if (ecSpec == null) {
            throw new IllegalArgumentException("Unknown / unsupported curve: " + crv);
        }
        if (!ECChecks.isPointOnCurve(x.decodeToBigInteger(), y.decodeToBigInteger(), crv.toECParameterSpec())) {
            throw new IllegalArgumentException("Invalid EC JWK: The 'x' and 'y' public coordinates are not on the " + crv + " curve");
        }
    }
    
    public ECKey(final Curve crv, final Base64URL x, final Base64URL y, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        super(KeyType.EC, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
        if (crv == null) {
            throw new IllegalArgumentException("The curve must not be null");
        }
        this.crv = crv;
        if (x == null) {
            throw new IllegalArgumentException("The 'x' coordinate must not be null");
        }
        this.x = x;
        if (y == null) {
            throw new IllegalArgumentException("The 'y' coordinate must not be null");
        }
        ensurePublicCoordinatesOnCurve(crv, x, this.y = y);
        this.d = null;
        this.privateKey = null;
    }
    
    public ECKey(final Curve crv, final Base64URL x, final Base64URL y, final Base64URL d, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        super(KeyType.EC, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
        if (crv == null) {
            throw new IllegalArgumentException("The curve must not be null");
        }
        this.crv = crv;
        if (x == null) {
            throw new IllegalArgumentException("The 'x' coordinate must not be null");
        }
        this.x = x;
        if (y == null) {
            throw new IllegalArgumentException("The 'y' coordinate must not be null");
        }
        ensurePublicCoordinatesOnCurve(crv, x, this.y = y);
        if (d == null) {
            throw new IllegalArgumentException("The 'd' coordinate must not be null");
        }
        this.d = d;
        this.privateKey = null;
    }
    
    public ECKey(final Curve crv, final Base64URL x, final Base64URL y, final PrivateKey priv, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        super(KeyType.EC, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
        if (crv == null) {
            throw new IllegalArgumentException("The curve must not be null");
        }
        this.crv = crv;
        if (x == null) {
            throw new IllegalArgumentException("The 'x' coordinate must not be null");
        }
        this.x = x;
        if (y == null) {
            throw new IllegalArgumentException("The 'y' coordinate must not be null");
        }
        ensurePublicCoordinatesOnCurve(crv, x, this.y = y);
        this.d = null;
        this.privateKey = priv;
    }
    
    public ECKey(final Curve crv, final ECPublicKey pub, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        this(crv, encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineX()), encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineY()), use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
    }
    
    public ECKey(final Curve crv, final ECPublicKey pub, final ECPrivateKey priv, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        this(crv, encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineX()), encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineY()), encodeCoordinate(priv.getParams().getCurve().getField().getFieldSize(), priv.getS()), use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
    }
    
    public ECKey(final Curve crv, final ECPublicKey pub, final PrivateKey priv, final KeyUse use, final Set<KeyOperation> ops, final Algorithm alg, final String kid, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final KeyStore ks) {
        this(crv, encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineX()), encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineY()), priv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
    }
    
    public Curve getCurve() {
        return this.crv;
    }
    
    public Base64URL getX() {
        return this.x;
    }
    
    public Base64URL getY() {
        return this.y;
    }
    
    public Base64URL getD() {
        return this.d;
    }
    
    public ECPublicKey toECPublicKey() throws JOSEException {
        return this.toECPublicKey(null);
    }
    
    public ECPublicKey toECPublicKey(final Provider provider) throws JOSEException {
        final ECParameterSpec spec = this.crv.toECParameterSpec();
        if (spec == null) {
            throw new JOSEException("Couldn't get EC parameter spec for curve " + this.crv);
        }
        final ECPoint w = new ECPoint(this.x.decodeToBigInteger(), this.y.decodeToBigInteger());
        final ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(w, spec);
        try {
            KeyFactory keyFactory;
            if (provider == null) {
                keyFactory = KeyFactory.getInstance("EC");
            }
            else {
                keyFactory = KeyFactory.getInstance("EC", provider);
            }
            return (ECPublicKey)keyFactory.generatePublic(publicKeySpec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException ex2) {
            final GeneralSecurityException ex;
            final GeneralSecurityException e = ex;
            throw new JOSEException(e.getMessage(), e);
        }
    }
    
    public ECPrivateKey toECPrivateKey() throws JOSEException {
        return this.toECPrivateKey(null);
    }
    
    public ECPrivateKey toECPrivateKey(final Provider provider) throws JOSEException {
        if (this.d == null) {
            return null;
        }
        final ECParameterSpec spec = this.crv.toECParameterSpec();
        if (spec == null) {
            throw new JOSEException("Couldn't get EC parameter spec for curve " + this.crv);
        }
        final ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(this.d.decodeToBigInteger(), spec);
        try {
            KeyFactory keyFactory;
            if (provider == null) {
                keyFactory = KeyFactory.getInstance("EC");
            }
            else {
                keyFactory = KeyFactory.getInstance("EC", provider);
            }
            return (ECPrivateKey)keyFactory.generatePrivate(privateKeySpec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException ex2) {
            final GeneralSecurityException ex;
            final GeneralSecurityException e = ex;
            throw new JOSEException(e.getMessage(), e);
        }
    }
    
    @Override
    public PublicKey toPublicKey() throws JOSEException {
        return this.toECPublicKey();
    }
    
    @Override
    public PrivateKey toPrivateKey() throws JOSEException {
        final PrivateKey prv = this.toECPrivateKey();
        if (prv != null) {
            return prv;
        }
        return this.privateKey;
    }
    
    @Override
    public KeyPair toKeyPair() throws JOSEException {
        return this.toKeyPair(null);
    }
    
    public KeyPair toKeyPair(final Provider provider) throws JOSEException {
        if (this.privateKey != null) {
            return new KeyPair(this.toECPublicKey(provider), this.privateKey);
        }
        return new KeyPair(this.toECPublicKey(provider), this.toECPrivateKey(provider));
    }
    
    @Override
    public LinkedHashMap<String, ?> getRequiredParams() {
        final LinkedHashMap<String, String> requiredParams = new LinkedHashMap<String, String>();
        requiredParams.put("crv", this.crv.toString());
        requiredParams.put("kty", this.getKeyType().getValue());
        requiredParams.put("x", this.x.toString());
        requiredParams.put("y", this.y.toString());
        return requiredParams;
    }
    
    @Override
    public boolean isPrivate() {
        return this.d != null || this.privateKey != null;
    }
    
    @Override
    public int size() {
        final ECParameterSpec ecParameterSpec = this.crv.toECParameterSpec();
        if (ecParameterSpec == null) {
            throw new UnsupportedOperationException("Couldn't determine field size for curve " + this.crv.getName());
        }
        return ecParameterSpec.getCurve().getField().getFieldSize();
    }
    
    @Override
    public ECKey toPublicJWK() {
        return new ECKey(this.getCurve(), this.getX(), this.getY(), this.getKeyUse(), this.getKeyOperations(), this.getAlgorithm(), this.getKeyID(), this.getX509CertURL(), this.getX509CertThumbprint(), this.getX509CertSHA256Thumbprint(), this.getX509CertChain(), this.getKeyStore());
    }
    
    @Override
    public JSONObject toJSONObject() {
        final JSONObject o = super.toJSONObject();
        ((HashMap<String, String>)o).put("crv", this.crv.toString());
        ((HashMap<String, String>)o).put("x", this.x.toString());
        ((HashMap<String, String>)o).put("y", this.y.toString());
        if (this.d != null) {
            ((HashMap<String, String>)o).put("d", this.d.toString());
        }
        return o;
    }
    
    public static ECKey parse(final String s) throws ParseException {
        return parse(JSONObjectUtils.parse(s));
    }
    
    public static ECKey parse(final JSONObject jsonObject) throws ParseException {
        final Curve crv = Curve.parse(JSONObjectUtils.getString(jsonObject, "crv"));
        final Base64URL x = new Base64URL(JSONObjectUtils.getString(jsonObject, "x"));
        final Base64URL y = new Base64URL(JSONObjectUtils.getString(jsonObject, "y"));
        final KeyType kty = JWKMetadata.parseKeyType(jsonObject);
        if (kty != KeyType.EC) {
            throw new ParseException("The key type \"kty\" must be EC", 0);
        }
        Base64URL d = null;
        if (jsonObject.get("d") != null) {
            d = new Base64URL(JSONObjectUtils.getString(jsonObject, "d"));
        }
        try {
            if (d == null) {
                return new ECKey(crv, x, y, JWKMetadata.parseKeyUse(jsonObject), JWKMetadata.parseKeyOperations(jsonObject), JWKMetadata.parseAlgorithm(jsonObject), JWKMetadata.parseKeyID(jsonObject), JWKMetadata.parseX509CertURL(jsonObject), JWKMetadata.parseX509CertThumbprint(jsonObject), JWKMetadata.parseX509CertSHA256Thumbprint(jsonObject), JWKMetadata.parseX509CertChain(jsonObject), null);
            }
            return new ECKey(crv, x, y, d, JWKMetadata.parseKeyUse(jsonObject), JWKMetadata.parseKeyOperations(jsonObject), JWKMetadata.parseAlgorithm(jsonObject), JWKMetadata.parseKeyID(jsonObject), JWKMetadata.parseX509CertURL(jsonObject), JWKMetadata.parseX509CertThumbprint(jsonObject), JWKMetadata.parseX509CertSHA256Thumbprint(jsonObject), JWKMetadata.parseX509CertChain(jsonObject), null);
        }
        catch (IllegalArgumentException ex) {
            throw new ParseException(ex.getMessage(), 0);
        }
    }
    
    public static ECKey parse(final X509Certificate cert) throws JOSEException {
        if (!(cert.getPublicKey() instanceof ECPublicKey)) {
            throw new JOSEException("The public key of the X.509 certificate is not EC");
        }
        final ECPublicKey publicKey = (ECPublicKey)cert.getPublicKey();
        try {
            final JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder(cert);
            final String oid = certHolder.getSubjectPublicKeyInfo().getAlgorithm().getParameters().toString();
            final Curve crv = Curve.forOID(oid);
            if (crv == null) {
                throw new JOSEException("Couldn't determine EC JWK curve for OID " + oid);
            }
            final MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return new Builder(crv, publicKey).keyUse(KeyUse.from(cert)).keyID(cert.getSerialNumber().toString(10)).x509CertChain(Collections.singletonList(Base64.encode(cert.getEncoded()))).x509CertSHA256Thumbprint(Base64URL.encode(sha256.digest(cert.getEncoded()))).build();
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Couldn't encode x5t parameter: " + e.getMessage(), e);
        }
        catch (CertificateEncodingException e2) {
            throw new JOSEException("Couldn't encode x5c parameter: " + e2.getMessage(), e2);
        }
    }
    
    public static ECKey load(final KeyStore keyStore, final String alias, final char[] pin) throws KeyStoreException, JOSEException {
        final Certificate cert = keyStore.getCertificate(alias);
        if (cert == null || !(cert instanceof X509Certificate)) {
            return null;
        }
        final X509Certificate x509Cert = (X509Certificate)cert;
        if (!(x509Cert.getPublicKey() instanceof ECPublicKey)) {
            throw new JOSEException("Couldn't load EC JWK: The key algorithm is not EC");
        }
        ECKey ecJWK = parse(x509Cert);
        ecJWK = new Builder(ecJWK).keyID(alias).keyStore(keyStore).build();
        Key key;
        try {
            key = keyStore.getKey(alias, pin);
        }
        catch (UnrecoverableKeyException | NoSuchAlgorithmException ex2) {
            final GeneralSecurityException ex;
            final GeneralSecurityException e = ex;
            throw new JOSEException("Couldn't retrieve private EC key (bad pin?): " + e.getMessage(), e);
        }
        if (key instanceof ECPrivateKey) {
            return new Builder(ecJWK).privateKey((ECPrivateKey)key).build();
        }
        if (key instanceof PrivateKey && "EC".equalsIgnoreCase(key.getAlgorithm())) {
            return new Builder(ecJWK).privateKey((PrivateKey)key).build();
        }
        return ecJWK;
    }
    
    @Immutable
    public static class Curve implements Serializable
    {
        private static final long serialVersionUID = 1L;
        public static final Curve P_256;
        public static final Curve P_384;
        public static final Curve P_521;
        private final String name;
        private final String stdName;
        private final String oid;
        
        static {
            P_256 = new Curve("P-256", "secp256r1", "1.2.840.10045.3.1.7");
            P_384 = new Curve("P-384", "secp384r1", "1.3.132.0.34");
            P_521 = new Curve("P-521", "secp521r1", "1.3.132.0.35");
        }
        
        public Curve(final String name) {
            this(name, null, null);
        }
        
        public Curve(final String name, final String stdName, final String oid) {
            if (name == null) {
                throw new IllegalArgumentException("The JOSE cryptographic curve name must not be null");
            }
            this.name = name;
            this.stdName = stdName;
            this.oid = oid;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getStdName() {
            return this.stdName;
        }
        
        public String getOID() {
            return this.oid;
        }
        
        public ECParameterSpec toECParameterSpec() {
            return ECParameterTable.get(this);
        }
        
        @Override
        public String toString() {
            return this.getName();
        }
        
        @Override
        public boolean equals(final Object object) {
            return object instanceof Curve && this.toString().equals(object.toString());
        }
        
        public static Curve parse(final String s) {
            if (s == null || s.trim().isEmpty()) {
                throw new IllegalArgumentException("The cryptographic curve string must not be null or empty");
            }
            if (s.equals(Curve.P_256.getName())) {
                return Curve.P_256;
            }
            if (s.equals(Curve.P_384.getName())) {
                return Curve.P_384;
            }
            if (s.equals(Curve.P_521.getName())) {
                return Curve.P_521;
            }
            return new Curve(s);
        }
        
        public static Curve forStdName(final String stdName) {
            if ("secp256r1".equals(stdName) || "prime256v1".equals(stdName)) {
                return Curve.P_256;
            }
            if ("secp384r1".equals(stdName)) {
                return Curve.P_384;
            }
            if ("secp521r1".equals(stdName)) {
                return Curve.P_521;
            }
            return null;
        }
        
        public static Curve forOID(final String oid) {
            if (Curve.P_256.getOID().equals(oid)) {
                return Curve.P_256;
            }
            if (Curve.P_384.getOID().equals(oid)) {
                return Curve.P_384;
            }
            if (Curve.P_521.getOID().equals(oid)) {
                return Curve.P_521;
            }
            return null;
        }
        
        public static Curve forJWSAlgoritm(final JWSAlgorithm alg) {
            if (JWSAlgorithm.ES256.equals(alg)) {
                return Curve.P_256;
            }
            if (JWSAlgorithm.ES384.equals(alg)) {
                return Curve.P_384;
            }
            if (JWSAlgorithm.ES512.equals(alg)) {
                return Curve.P_521;
            }
            return null;
        }
        
        public static Curve forECParameterSpec(final ECParameterSpec spec) {
            return ECParameterTable.get(spec);
        }
    }
    
    public static class Builder
    {
        private final Curve crv;
        private final Base64URL x;
        private final Base64URL y;
        private Base64URL d;
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
        
        public Builder(final Curve crv, final Base64URL x, final Base64URL y) {
            if (crv == null) {
                throw new IllegalArgumentException("The curve must not be null");
            }
            this.crv = crv;
            if (x == null) {
                throw new IllegalArgumentException("The 'x' coordinate must not be null");
            }
            this.x = x;
            if (y == null) {
                throw new IllegalArgumentException("The 'y' coordinate must not be null");
            }
            this.y = y;
        }
        
        public Builder(final Curve crv, final ECPublicKey pub) {
            this(crv, ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineX()), ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineY()));
        }
        
        public Builder(final ECKey ecJWK) {
            this.crv = ecJWK.crv;
            this.x = ecJWK.x;
            this.y = ecJWK.y;
            this.d = ecJWK.d;
            this.priv = ecJWK.privateKey;
            this.use = ecJWK.getKeyUse();
            this.ops = ecJWK.getKeyOperations();
            this.alg = ecJWK.getAlgorithm();
            this.kid = ecJWK.getKeyID();
            this.x5u = ecJWK.getX509CertURL();
            this.x5t = ecJWK.getX509CertThumbprint();
            this.x5t256 = ecJWK.getX509CertSHA256Thumbprint();
            this.x5c = ecJWK.getX509CertChain();
            this.ks = ecJWK.getKeyStore();
        }
        
        public Builder d(final Base64URL d) {
            this.d = d;
            return this;
        }
        
        public Builder privateKey(final ECPrivateKey priv) {
            if (priv != null) {
                this.d = ECKey.encodeCoordinate(priv.getParams().getCurve().getField().getFieldSize(), priv.getS());
            }
            return this;
        }
        
        public Builder privateKey(final PrivateKey priv) {
            if (!"EC".equalsIgnoreCase(priv.getAlgorithm())) {
                throw new IllegalArgumentException("The private key algorithm must be EC");
            }
            this.priv = priv;
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
            requiredParams.put("crv", this.crv.toString());
            requiredParams.put("kty", KeyType.EC.getValue());
            requiredParams.put("x", this.x.toString());
            requiredParams.put("y", this.y.toString());
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
        
        public ECKey build() {
            try {
                if (this.d == null && this.priv == null) {
                    return new ECKey(this.crv, this.x, this.y, this.use, this.ops, this.alg, this.kid, this.x5u, this.x5t, this.x5t256, this.x5c, this.ks);
                }
                if (this.priv != null) {
                    return new ECKey(this.crv, this.x, this.y, this.priv, this.use, this.ops, this.alg, this.kid, this.x5u, this.x5t, this.x5t256, this.x5c, this.ks);
                }
                return new ECKey(this.crv, this.x, this.y, this.d, this.use, this.ops, this.alg, this.kid, this.x5u, this.x5t, this.x5t256, this.x5c, this.ks);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }
}
