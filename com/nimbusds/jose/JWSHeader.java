// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import java.util.HashMap;
import java.util.Iterator;
import com.nimbusds.jose.util.X509CertChainUtils;
import java.util.Collection;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.text.ParseException;
import net.minidev.json.JSONObject;
import java.util.Map;
import com.nimbusds.jose.util.Base64;
import java.util.List;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.jwk.JWK;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public final class JWSHeader extends CommonSEHeader
{
    private static final long serialVersionUID = 1L;
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    
    static {
        final Set<String> p = new HashSet<String>();
        p.add("alg");
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
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet((Set<? extends String>)p);
    }
    
    public JWSHeader(final JWSAlgorithm alg) {
        this(alg, null, null, null, null, null, null, null, null, null, null, null, null);
    }
    
    public JWSHeader(final JWSAlgorithm alg, final JOSEObjectType typ, final String cty, final Set<String> crit, final URI jku, final JWK jwk, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final String kid, final Map<String, Object> customParams, final Base64URL parsedBase64URL) {
        super(alg, typ, cty, crit, jku, jwk, x5u, x5t, x5t256, x5c, kid, customParams, parsedBase64URL);
        if (alg.getName().equals(Algorithm.NONE.getName())) {
            throw new IllegalArgumentException("The JWS algorithm \"alg\" cannot be \"none\"");
        }
    }
    
    public JWSHeader(final JWSHeader jwsHeader) {
        this(jwsHeader.getAlgorithm(), jwsHeader.getType(), jwsHeader.getContentType(), jwsHeader.getCriticalParams(), jwsHeader.getJWKURL(), jwsHeader.getJWK(), jwsHeader.getX509CertURL(), jwsHeader.getX509CertThumbprint(), jwsHeader.getX509CertSHA256Thumbprint(), jwsHeader.getX509CertChain(), jwsHeader.getKeyID(), jwsHeader.getCustomParams(), jwsHeader.getParsedBase64URL());
    }
    
    public static Set<String> getRegisteredParameterNames() {
        return JWSHeader.REGISTERED_PARAMETER_NAMES;
    }
    
    @Override
    public JWSAlgorithm getAlgorithm() {
        return (JWSAlgorithm)super.getAlgorithm();
    }
    
    public static JWSHeader parse(final JSONObject jsonObject) throws ParseException {
        return parse(jsonObject, null);
    }
    
    public static JWSHeader parse(final JSONObject jsonObject, final Base64URL parsedBase64URL) throws ParseException {
        final Algorithm alg = Header.parseAlgorithm(jsonObject);
        if (!(alg instanceof JWSAlgorithm)) {
            throw new ParseException("The algorithm \"alg\" header parameter must be for signatures", 0);
        }
        Builder header = new Builder((JWSAlgorithm)alg).parsedBase64URL(parsedBase64URL);
        for (final String name : ((HashMap<String, V>)jsonObject).keySet()) {
            if (!"alg".equals(name)) {
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
                else {
                    header = header.customParam(name, ((HashMap<K, Object>)jsonObject).get(name));
                }
            }
        }
        return header.build();
    }
    
    public static JWSHeader parse(final String jsonString) throws ParseException {
        return parse(jsonString, null);
    }
    
    public static JWSHeader parse(final String jsonString, final Base64URL parsedBase64URL) throws ParseException {
        return parse(JSONObjectUtils.parse(jsonString), parsedBase64URL);
    }
    
    public static JWSHeader parse(final Base64URL base64URL) throws ParseException {
        return parse(base64URL.decodeToString(), base64URL);
    }
    
    public static class Builder
    {
        private final JWSAlgorithm alg;
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
        private Map<String, Object> customParams;
        private Base64URL parsedBase64URL;
        
        public Builder(final JWSAlgorithm alg) {
            if (alg.getName().equals(Algorithm.NONE.getName())) {
                throw new IllegalArgumentException("The JWS algorithm \"alg\" cannot be \"none\"");
            }
            this.alg = alg;
        }
        
        public Builder(final JWSHeader jwsHeader) {
            this(jwsHeader.getAlgorithm());
            this.typ = jwsHeader.getType();
            this.cty = jwsHeader.getContentType();
            this.crit = jwsHeader.getCriticalParams();
            this.jku = jwsHeader.getJWKURL();
            this.jwk = jwsHeader.getJWK();
            this.x5u = jwsHeader.getX509CertURL();
            this.x5t = jwsHeader.getX509CertThumbprint();
            this.x5t256 = jwsHeader.getX509CertSHA256Thumbprint();
            this.x5c = (List<Base64>)jwsHeader.getX509CertChain();
            this.kid = jwsHeader.getKeyID();
            this.customParams = jwsHeader.getCustomParams();
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
        
        public Builder customParam(final String name, final Object value) {
            if (JWSHeader.getRegisteredParameterNames().contains(name)) {
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
        
        public JWSHeader build() {
            return new JWSHeader(this.alg, this.typ, this.cty, this.crit, this.jku, this.jwk, this.x5u, this.x5t, this.x5t256, this.x5c, this.kid, this.customParams, this.parsedBase64URL);
        }
    }
}
