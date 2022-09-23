// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import java.util.HashMap;
import net.minidev.json.JSONObject;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import com.nimbusds.jose.util.Base64;
import java.util.List;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.jwk.JWK;
import java.net.URI;

abstract class CommonSEHeader extends Header
{
    private static final long serialVersionUID = 1L;
    private final URI jku;
    private final JWK jwk;
    private final URI x5u;
    private final Base64URL x5t;
    private final Base64URL x5t256;
    private final List<Base64> x5c;
    private final String kid;
    
    protected CommonSEHeader(final Algorithm alg, final JOSEObjectType typ, final String cty, final Set<String> crit, final URI jku, final JWK jwk, final URI x5u, final Base64URL x5t, final Base64URL x5t256, final List<Base64> x5c, final String kid, final Map<String, Object> customParams, final Base64URL parsedBase64URL) {
        super(alg, typ, cty, crit, customParams, parsedBase64URL);
        this.jku = jku;
        this.jwk = jwk;
        this.x5u = x5u;
        this.x5t = x5t;
        this.x5t256 = x5t256;
        if (x5c != null) {
            this.x5c = Collections.unmodifiableList((List<? extends Base64>)new ArrayList<Base64>(x5c));
        }
        else {
            this.x5c = null;
        }
        this.kid = kid;
    }
    
    public URI getJWKURL() {
        return this.jku;
    }
    
    public JWK getJWK() {
        return this.jwk;
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
        return this.x5c;
    }
    
    public String getKeyID() {
        return this.kid;
    }
    
    @Override
    public Set<String> getIncludedParams() {
        final Set<String> includedParameters = super.getIncludedParams();
        if (this.jku != null) {
            includedParameters.add("jku");
        }
        if (this.jwk != null) {
            includedParameters.add("jwk");
        }
        if (this.x5u != null) {
            includedParameters.add("x5u");
        }
        if (this.x5t != null) {
            includedParameters.add("x5t");
        }
        if (this.x5t256 != null) {
            includedParameters.add("x5t#S256");
        }
        if (this.x5c != null && !this.x5c.isEmpty()) {
            includedParameters.add("x5c");
        }
        if (this.kid != null) {
            includedParameters.add("kid");
        }
        return includedParameters;
    }
    
    @Override
    public JSONObject toJSONObject() {
        final JSONObject o = super.toJSONObject();
        if (this.jku != null) {
            ((HashMap<String, String>)o).put("jku", this.jku.toString());
        }
        if (this.jwk != null) {
            ((HashMap<String, JSONObject>)o).put("jwk", this.jwk.toJSONObject());
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
        if (this.x5c != null && !this.x5c.isEmpty()) {
            ((HashMap<String, List<Base64>>)o).put("x5c", this.x5c);
        }
        if (this.kid != null) {
            ((HashMap<String, String>)o).put("kid", this.kid);
        }
        return o;
    }
}
