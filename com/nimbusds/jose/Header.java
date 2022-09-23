// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import java.text.ParseException;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.util.ArrayList;
import net.minidev.json.JSONObject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import java.util.HashMap;
import com.nimbusds.jose.util.Base64URL;
import java.util.Map;
import java.util.Set;
import java.io.Serializable;

public abstract class Header implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Algorithm alg;
    private final JOSEObjectType typ;
    private final String cty;
    private final Set<String> crit;
    private final Map<String, Object> customParams;
    private static final Map<String, Object> EMPTY_CUSTOM_PARAMS;
    private final Base64URL parsedBase64URL;
    
    static {
        EMPTY_CUSTOM_PARAMS = Collections.unmodifiableMap((Map<? extends String, ?>)new HashMap<String, Object>());
    }
    
    protected Header(final Algorithm alg, final JOSEObjectType typ, final String cty, final Set<String> crit, final Map<String, Object> customParams, final Base64URL parsedBase64URL) {
        if (alg == null) {
            throw new IllegalArgumentException("The algorithm \"alg\" header parameter must not be null");
        }
        this.alg = alg;
        this.typ = typ;
        this.cty = cty;
        if (crit != null) {
            this.crit = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(crit));
        }
        else {
            this.crit = null;
        }
        if (customParams != null) {
            this.customParams = Collections.unmodifiableMap((Map<? extends String, ?>)new HashMap<String, Object>(customParams));
        }
        else {
            this.customParams = Header.EMPTY_CUSTOM_PARAMS;
        }
        this.parsedBase64URL = parsedBase64URL;
    }
    
    protected Header(final Header header) {
        this(header.getAlgorithm(), header.getType(), header.getContentType(), header.getCriticalParams(), header.getCustomParams(), header.getParsedBase64URL());
    }
    
    public Algorithm getAlgorithm() {
        return this.alg;
    }
    
    public JOSEObjectType getType() {
        return this.typ;
    }
    
    public String getContentType() {
        return this.cty;
    }
    
    public Set<String> getCriticalParams() {
        return this.crit;
    }
    
    public Object getCustomParam(final String name) {
        return this.customParams.get(name);
    }
    
    public Map<String, Object> getCustomParams() {
        return this.customParams;
    }
    
    public Base64URL getParsedBase64URL() {
        return this.parsedBase64URL;
    }
    
    public Set<String> getIncludedParams() {
        final Set<String> includedParameters = new HashSet<String>(this.getCustomParams().keySet());
        includedParameters.add("alg");
        if (this.getType() != null) {
            includedParameters.add("typ");
        }
        if (this.getContentType() != null) {
            includedParameters.add("cty");
        }
        if (this.getCriticalParams() != null && !this.getCriticalParams().isEmpty()) {
            includedParameters.add("crit");
        }
        return includedParameters;
    }
    
    public JSONObject toJSONObject() {
        final JSONObject o = new JSONObject(this.customParams);
        ((HashMap<String, String>)o).put("alg", this.alg.toString());
        if (this.typ != null) {
            ((HashMap<String, String>)o).put("typ", this.typ.toString());
        }
        if (this.cty != null) {
            ((HashMap<String, String>)o).put("cty", this.cty);
        }
        if (this.crit != null && !this.crit.isEmpty()) {
            ((HashMap<String, ArrayList>)o).put("crit", new ArrayList(this.crit));
        }
        return o;
    }
    
    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }
    
    public Base64URL toBase64URL() {
        if (this.parsedBase64URL == null) {
            return Base64URL.encode(this.toString());
        }
        return this.parsedBase64URL;
    }
    
    public static Algorithm parseAlgorithm(final JSONObject json) throws ParseException {
        final String algName = JSONObjectUtils.getString(json, "alg");
        if (algName.equals(Algorithm.NONE.getName())) {
            return Algorithm.NONE;
        }
        if (json.containsKey("enc")) {
            return JWEAlgorithm.parse(algName);
        }
        return JWSAlgorithm.parse(algName);
    }
    
    public static Header parse(final JSONObject jsonObject) throws ParseException {
        return parse(jsonObject, null);
    }
    
    public static Header parse(final JSONObject jsonObject, final Base64URL parsedBase64URL) throws ParseException {
        final Algorithm alg = parseAlgorithm(jsonObject);
        if (alg.equals(Algorithm.NONE)) {
            return PlainHeader.parse(jsonObject, parsedBase64URL);
        }
        if (alg instanceof JWSAlgorithm) {
            return JWSHeader.parse(jsonObject, parsedBase64URL);
        }
        if (alg instanceof JWEAlgorithm) {
            return JWEHeader.parse(jsonObject, parsedBase64URL);
        }
        throw new AssertionError((Object)("Unexpected algorithm type: " + alg));
    }
    
    public static Header parse(final String jsonString) throws ParseException {
        return parse(jsonString, null);
    }
    
    public static Header parse(final String jsonString, final Base64URL parsedBase64URL) throws ParseException {
        final JSONObject jsonObject = JSONObjectUtils.parse(jsonString);
        return parse(jsonObject, parsedBase64URL);
    }
    
    public static Header parse(final Base64URL base64URL) throws ParseException {
        return parse(base64URL.decodeToString(), base64URL);
    }
}
