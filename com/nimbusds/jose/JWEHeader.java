// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import java.util.HashMap;
import java.util.Iterator;
import com.nimbusds.jose.util.X509CertChainUtils;
import java.util.Collection;
import java.text.ParseException;
import com.nimbusds.jose.util.JSONObjectUtils;
import net.minidev.json.JSONObject;
import java.util.Map;
import com.nimbusds.jose.util.Base64;
import java.util.List;
import com.nimbusds.jose.jwk.JWK;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.jwk.ECKey;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public final class JWEHeader extends CommonSEHeader
{
    private static final long serialVersionUID = 1L;
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    private final EncryptionMethod enc;
    private final ECKey epk;
    private final CompressionAlgorithm zip;
    private final Base64URL apu;
    private final Base64URL apv;
    private final Base64URL p2s;
    private final int p2c;
    private final Base64URL iv;
    private final Base64URL tag;
    
    static {
        final Set<String> p = new HashSet<String>();
        p.add("alg");
        p.add("enc");
        p.add("epk");
        p.add("zip");
        p.add("jku");
        p.add("jwk");
        p.add("x5u");
        p.add("x5t");
        p.add("x5t#S256");
        p.add("x5c");
        p.add("kid");
        p.add("typ");
        p.add("cty");
        p.add("crit");
        p.add("apu");
        p.add("apv");
        p.add("p2s");
        p.add("p2c");
        p.add("iv");
        p.add("authTag");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet((Set<? extends String>)p);
    }
    
    public JWEHeader(final JWEAlgorithm alg, final EncryptionMethod enc) {
        this(alg, enc, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, null, null, null, null);
    }
    
    public JWEHeader(final Algorithm alg, final EncryptionMethod enc, final JOSEObjectType typ, final String cty, final Set<String> crit, final URI jku, final JWK jwk, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final String kid, final ECKey epk, final CompressionAlgorithm zip, final Base64URL apu, final Base64URL apv, final Base64URL p2s, final int p2c, final Base64URL iv, final Base64URL tag, final Map<String, Object> customParams, final Base64URL parsedBase64URL) {
        super(alg, typ, cty, crit, jku, jwk, x5u, x5t, x5t256, x5c, kid, customParams, parsedBase64URL);
        if (alg.getName().equals(Algorithm.NONE.getName())) {
            throw new IllegalArgumentException("The JWE algorithm cannot be \"none\"");
        }
        if (enc == null) {
            throw new IllegalArgumentException("The encryption method \"enc\" parameter must not be null");
        }
        this.enc = enc;
        this.epk = epk;
        this.zip = zip;
        this.apu = apu;
        this.apv = apv;
        this.p2s = p2s;
        this.p2c = p2c;
        this.iv = iv;
        this.tag = tag;
    }
    
    public JWEHeader(final JWEHeader jweHeader) {
        this(jweHeader.getAlgorithm(), jweHeader.getEncryptionMethod(), jweHeader.getType(), jweHeader.getContentType(), jweHeader.getCriticalParams(), jweHeader.getJWKURL(), jweHeader.getJWK(), jweHeader.getX509CertURL(), jweHeader.getX509CertThumbprint(), jweHeader.getX509CertSHA256Thumbprint(), jweHeader.getX509CertChain(), jweHeader.getKeyID(), jweHeader.getEphemeralPublicKey(), jweHeader.getCompressionAlgorithm(), jweHeader.getAgreementPartyUInfo(), jweHeader.getAgreementPartyVInfo(), jweHeader.getPBES2Salt(), jweHeader.getPBES2Count(), jweHeader.getIV(), jweHeader.getAuthTag(), jweHeader.getCustomParams(), jweHeader.getParsedBase64URL());
    }
    
    public static Set<String> getRegisteredParameterNames() {
        return JWEHeader.REGISTERED_PARAMETER_NAMES;
    }
    
    @Override
    public JWEAlgorithm getAlgorithm() {
        return (JWEAlgorithm)super.getAlgorithm();
    }
    
    public EncryptionMethod getEncryptionMethod() {
        return this.enc;
    }
    
    public ECKey getEphemeralPublicKey() {
        return this.epk;
    }
    
    public CompressionAlgorithm getCompressionAlgorithm() {
        return this.zip;
    }
    
    public Base64URL getAgreementPartyUInfo() {
        return this.apu;
    }
    
    public Base64URL getAgreementPartyVInfo() {
        return this.apv;
    }
    
    public Base64URL getPBES2Salt() {
        return this.p2s;
    }
    
    public int getPBES2Count() {
        return this.p2c;
    }
    
    public Base64URL getIV() {
        return this.iv;
    }
    
    public Base64URL getAuthTag() {
        return this.tag;
    }
    
    @Override
    public Set<String> getIncludedParams() {
        final Set<String> includedParameters = super.getIncludedParams();
        if (this.enc != null) {
            includedParameters.add("enc");
        }
        if (this.epk != null) {
            includedParameters.add("epk");
        }
        if (this.zip != null) {
            includedParameters.add("zip");
        }
        if (this.apu != null) {
            includedParameters.add("apu");
        }
        if (this.apv != null) {
            includedParameters.add("apv");
        }
        if (this.p2s != null) {
            includedParameters.add("p2s");
        }
        if (this.p2c > 0) {
            includedParameters.add("p2c");
        }
        if (this.iv != null) {
            includedParameters.add("iv");
        }
        if (this.tag != null) {
            includedParameters.add("tag");
        }
        return includedParameters;
    }
    
    @Override
    public JSONObject toJSONObject() {
        final JSONObject o = super.toJSONObject();
        if (this.enc != null) {
            ((HashMap<String, String>)o).put("enc", this.enc.toString());
        }
        if (this.epk != null) {
            ((HashMap<String, JSONObject>)o).put("epk", this.epk.toJSONObject());
        }
        if (this.zip != null) {
            ((HashMap<String, String>)o).put("zip", this.zip.toString());
        }
        if (this.apu != null) {
            ((HashMap<String, String>)o).put("apu", this.apu.toString());
        }
        if (this.apv != null) {
            ((HashMap<String, String>)o).put("apv", this.apv.toString());
        }
        if (this.p2s != null) {
            ((HashMap<String, String>)o).put("p2s", this.p2s.toString());
        }
        if (this.p2c > 0) {
            ((HashMap<String, Integer>)o).put("p2c", this.p2c);
        }
        if (this.iv != null) {
            ((HashMap<String, String>)o).put("iv", this.iv.toString());
        }
        if (this.tag != null) {
            ((HashMap<String, String>)o).put("tag", this.tag.toString());
        }
        return o;
    }
    
    private static EncryptionMethod parseEncryptionMethod(final JSONObject json) throws ParseException {
        return EncryptionMethod.parse(JSONObjectUtils.getString(json, "enc"));
    }
    
    public static JWEHeader parse(final JSONObject jsonObject) throws ParseException {
        return parse(jsonObject, null);
    }
    
    public static JWEHeader parse(final JSONObject jsonObject, final Base64URL parsedBase64URL) throws ParseException {
        final Algorithm alg = Header.parseAlgorithm(jsonObject);
        if (!(alg instanceof JWEAlgorithm)) {
            throw new ParseException("The algorithm \"alg\" header parameter must be for encryption", 0);
        }
        final EncryptionMethod enc = parseEncryptionMethod(jsonObject);
        Builder header = new Builder((JWEAlgorithm)alg, enc).parsedBase64URL(parsedBase64URL);
        for (final String name : ((HashMap<String, V>)jsonObject).keySet()) {
            if (!"alg".equals(name) && !"enc".equals(name)) {
                if ("typ".equals(name)) {
                    header = header.type(new JOSEObjectType(JSONObjectUtils.getString(jsonObject, name)));
                }
                else if ("cty".equals(name)) {
                    header = header.contentType(JSONObjectUtils.getString(jsonObject, name));
                }
                else if ("crit".equals(name)) {
                    header = header.criticalParams(new HashSet<String>(JSONObjectUtils.getStringList(jsonObject, name)));
                }
                else if ("jku".equals(name)) {
                    header = header.jwkURL(JSONObjectUtils.getURI(jsonObject, name));
                }
                else if ("jwk".equals(name)) {
                    header = header.jwk(JWK.parse(JSONObjectUtils.getJSONObject(jsonObject, name)));
                }
                else if ("x5u".equals(name)) {
                    header = header.x509CertURL(JSONObjectUtils.getURI(jsonObject, name));
                }
                else if ("x5t".equals(name)) {
                    header = header.x509CertThumbprint(new Base64URL(JSONObjectUtils.getString(jsonObject, name)));
                }
                else if ("x5t#S256".equals(name)) {
                    header = header.x509CertSHA256Thumbprint(new Base64URL(JSONObjectUtils.getString(jsonObject, name)));
                }
                else if ("x5c".equals(name)) {
                    header = header.x509CertChain(X509CertChainUtils.parseX509CertChain(JSONObjectUtils.getJSONArray(jsonObject, name)));
                }
                else if ("kid".equals(name)) {
                    header = header.keyID(JSONObjectUtils.getString(jsonObject, name));
                }
                else if ("epk".equals(name)) {
                    header = header.ephemeralPublicKey(ECKey.parse(JSONObjectUtils.getJSONObject(jsonObject, name)));
                }
                else if ("zip".equals(name)) {
                    header = header.compressionAlgorithm(new CompressionAlgorithm(JSONObjectUtils.getString(jsonObject, name)));
                }
                else if ("apu".equals(name)) {
                    header = header.agreementPartyUInfo(new Base64URL(JSONObjectUtils.getString(jsonObject, name)));
                }
                else if ("apv".equals(name)) {
                    header = header.agreementPartyVInfo(new Base64URL(JSONObjectUtils.getString(jsonObject, name)));
                }
                else if ("p2s".equals(name)) {
                    header = header.pbes2Salt(new Base64URL(JSONObjectUtils.getString(jsonObject, name)));
                }
                else if ("p2c".equals(name)) {
                    header = header.pbes2Count(JSONObjectUtils.getInt(jsonObject, name));
                }
                else if ("iv".equals(name)) {
                    header = header.iv(new Base64URL(JSONObjectUtils.getString(jsonObject, name)));
                }
                else if ("tag".equals(name)) {
                    header = header.authTag(new Base64URL(JSONObjectUtils.getString(jsonObject, name)));
                }
                else {
                    header = header.customParam(name, ((HashMap<K, Object>)jsonObject).get(name));
                }
            }
        }
        return header.build();
    }
    
    public static JWEHeader parse(final String jsonString) throws ParseException {
        return parse(JSONObjectUtils.parse(jsonString), null);
    }
    
    public static JWEHeader parse(final String jsonString, final Base64URL parsedBase64URL) throws ParseException {
        return parse(JSONObjectUtils.parse(jsonString), parsedBase64URL);
    }
    
    public static JWEHeader parse(final Base64URL base64URL) throws ParseException {
        return parse(base64URL.decodeToString(), base64URL);
    }
    
    public static class Builder
    {
        private final JWEAlgorithm alg;
        private final EncryptionMethod enc;
        private JOSEObjectType typ;
        private String cty;
        private Set<String> crit;
        private URI jku;
        private JWK jwk;
        private URI x5u;
        @Deprecated
        private Base64URL x5t;
        private Base64URL x5t256;
        private List<Base64> x5c;
        private String kid;
        private ECKey epk;
        private CompressionAlgorithm zip;
        private Base64URL apu;
        private Base64URL apv;
        private Base64URL p2s;
        private int p2c;
        private Base64URL iv;
        private Base64URL tag;
        private Map<String, Object> customParams;
        private Base64URL parsedBase64URL;
        
        public Builder(final JWEAlgorithm alg, final EncryptionMethod enc) {
            if (alg.getName().equals(Algorithm.NONE.getName())) {
                throw new IllegalArgumentException("The JWE algorithm \"alg\" cannot be \"none\"");
            }
            this.alg = alg;
            if (enc == null) {
                throw new IllegalArgumentException("The encryption method \"enc\" parameter must not be null");
            }
            this.enc = enc;
        }
        
        public Builder(final JWEHeader jweHeader) {
            this(jweHeader.getAlgorithm(), jweHeader.getEncryptionMethod());
            this.typ = jweHeader.getType();
            this.cty = jweHeader.getContentType();
            this.crit = jweHeader.getCriticalParams();
            this.customParams = jweHeader.getCustomParams();
            this.jku = jweHeader.getJWKURL();
            this.jwk = jweHeader.getJWK();
            this.x5u = jweHeader.getX509CertURL();
            this.x5t = jweHeader.getX509CertThumbprint();
            this.x5t256 = jweHeader.getX509CertSHA256Thumbprint();
            this.x5c = (List<Base64>)jweHeader.getX509CertChain();
            this.kid = jweHeader.getKeyID();
            this.epk = jweHeader.getEphemeralPublicKey();
            this.zip = jweHeader.getCompressionAlgorithm();
            this.apu = jweHeader.getAgreementPartyUInfo();
            this.apv = jweHeader.getAgreementPartyVInfo();
            this.p2s = jweHeader.getPBES2Salt();
            this.p2c = jweHeader.getPBES2Count();
            this.iv = jweHeader.getIV();
            this.tag = jweHeader.getAuthTag();
            this.customParams = jweHeader.getCustomParams();
        }
        
        public Builder type(final JOSEObjectType typ) {
            this.typ = typ;
            return this;
        }
        
        public Builder contentType(final String cty) {
            this.cty = cty;
            return this;
        }
        
        public Builder criticalParams(final Set<String> crit) {
            this.crit = crit;
            return this;
        }
        
        public Builder jwkURL(final URI jku) {
            this.jku = jku;
            return this;
        }
        
        public Builder jwk(final JWK jwk) {
            this.jwk = jwk;
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
        
        public Builder keyID(final String kid) {
            this.kid = kid;
            return this;
        }
        
        public Builder ephemeralPublicKey(final ECKey epk) {
            this.epk = epk;
            return this;
        }
        
        public Builder compressionAlgorithm(final CompressionAlgorithm zip) {
            this.zip = zip;
            return this;
        }
        
        public Builder agreementPartyUInfo(final Base64URL apu) {
            this.apu = apu;
            return this;
        }
        
        public Builder agreementPartyVInfo(final Base64URL apv) {
            this.apv = apv;
            return this;
        }
        
        public Builder pbes2Salt(final Base64URL p2s) {
            this.p2s = p2s;
            return this;
        }
        
        public Builder pbes2Count(final int p2c) {
            if (p2c < 0) {
                throw new IllegalArgumentException("The PBES2 count parameter must not be negative");
            }
            this.p2c = p2c;
            return this;
        }
        
        public Builder iv(final Base64URL iv) {
            this.iv = iv;
            return this;
        }
        
        public Builder authTag(final Base64URL tag) {
            this.tag = tag;
            return this;
        }
        
        public Builder customParam(final String name, final Object value) {
            if (JWEHeader.getRegisteredParameterNames().contains(name)) {
                throw new IllegalArgumentException("The parameter name \"" + name + "\" matches a registered name");
            }
            if (this.customParams == null) {
                this.customParams = new HashMap<String, Object>();
            }
            this.customParams.put(name, value);
            return this;
        }
        
        public Builder customParams(final Map<String, Object> customParameters) {
            this.customParams = customParameters;
            return this;
        }
        
        public Builder parsedBase64URL(final Base64URL base64URL) {
            this.parsedBase64URL = base64URL;
            return this;
        }
        
        public JWEHeader build() {
            return new JWEHeader(this.alg, this.enc, this.typ, this.cty, this.crit, this.jku, this.jwk, this.x5u, this.x5t, this.x5t256, this.x5c, this.kid, this.epk, this.zip, this.apu, this.apv, this.p2s, this.p2c, this.iv, this.tag, this.customParams, this.parsedBase64URL);
        }
    }
}
