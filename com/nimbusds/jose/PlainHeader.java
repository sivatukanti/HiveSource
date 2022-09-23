// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.text.ParseException;
import net.minidev.json.JSONObject;
import com.nimbusds.jose.util.Base64URL;
import java.util.Map;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public final class PlainHeader extends Header
{
    private static final long serialVersionUID = 1L;
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    
    static {
        final Set<String> p = new HashSet<String>();
        p.add("alg");
        p.add("typ");
        p.add("cty");
        p.add("crit");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet((Set<? extends String>)p);
    }
    
    public PlainHeader() {
        this(null, null, null, null, null);
    }
    
    public PlainHeader(final JOSEObjectType typ, final String cty, final Set<String> crit, final Map<String, Object> customParams, final Base64URL parsedBase64URL) {
        super(Algorithm.NONE, typ, cty, crit, customParams, parsedBase64URL);
    }
    
    public PlainHeader(final PlainHeader plainHeader) {
        this(plainHeader.getType(), plainHeader.getContentType(), plainHeader.getCriticalParams(), plainHeader.getCustomParams(), plainHeader.getParsedBase64URL());
    }
    
    public static Set<String> getRegisteredParameterNames() {
        return PlainHeader.REGISTERED_PARAMETER_NAMES;
    }
    
    @Override
    public Algorithm getAlgorithm() {
        return Algorithm.NONE;
    }
    
    public static PlainHeader parse(final JSONObject jsonObject) throws ParseException {
        return parse(jsonObject, null);
    }
    
    public static PlainHeader parse(final JSONObject jsonObject, final Base64URL parsedBase64URL) throws ParseException {
        final Algorithm alg = Header.parseAlgorithm(jsonObject);
        if (alg != Algorithm.NONE) {
            throw new ParseException("The algorithm \"alg\" header parameter must be \"none\"", 0);
        }
        Builder header = new Builder().parsedBase64URL(parsedBase64URL);
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
                else {
                    header = header.customParam(name, ((HashMap<K, Object>)jsonObject).get(name));
                }
            }
        }
        return header.build();
    }
    
    public static PlainHeader parse(final String jsonString) throws ParseException {
        return parse(jsonString, null);
    }
    
    public static PlainHeader parse(final String jsonString, final Base64URL parsedBase64URL) throws ParseException {
        return parse(JSONObjectUtils.parse(jsonString), parsedBase64URL);
    }
    
    public static PlainHeader parse(final Base64URL base64URL) throws ParseException {
        return parse(base64URL.decodeToString(), base64URL);
    }
    
    public static class Builder
    {
        private JOSEObjectType typ;
        private String cty;
        private Set<String> crit;
        private Map<String, Object> customParams;
        private Base64URL parsedBase64URL;
        
        public Builder() {
        }
        
        public Builder(final PlainHeader plainHeader) {
            this.typ = plainHeader.getType();
            this.cty = plainHeader.getContentType();
            this.crit = plainHeader.getCriticalParams();
            this.customParams = plainHeader.getCustomParams();
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
        
        public Builder customParam(final String name, final Object value) {
            if (PlainHeader.getRegisteredParameterNames().contains(name)) {
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
        
        public PlainHeader build() {
            return new PlainHeader(this.typ, this.cty, this.crit, this.customParams, this.parsedBase64URL);
        }
    }
}
