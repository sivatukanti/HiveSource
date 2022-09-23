// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jwt;

import java.util.HashMap;
import java.util.ArrayList;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.util.Collection;
import net.minidev.json.JSONArray;
import java.util.Iterator;
import net.minidev.json.JSONObject;
import com.nimbusds.jose.util.DateUtils;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;
import java.io.Serializable;

@Immutable
public final class JWTClaimsSet implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final String ISSUER_CLAIM = "iss";
    private static final String SUBJECT_CLAIM = "sub";
    private static final String AUDIENCE_CLAIM = "aud";
    private static final String EXPIRATION_TIME_CLAIM = "exp";
    private static final String NOT_BEFORE_CLAIM = "nbf";
    private static final String ISSUED_AT_CLAIM = "iat";
    private static final String JWT_ID_CLAIM = "jti";
    private static final Set<String> REGISTERED_CLAIM_NAMES;
    private final Map<String, Object> claims;
    
    static {
        final Set<String> n = new HashSet<String>();
        n.add("iss");
        n.add("sub");
        n.add("aud");
        n.add("exp");
        n.add("nbf");
        n.add("iat");
        n.add("jti");
        REGISTERED_CLAIM_NAMES = Collections.unmodifiableSet((Set<? extends String>)n);
    }
    
    private JWTClaimsSet(final Map<String, Object> claims) {
        (this.claims = new LinkedHashMap<String, Object>()).putAll(claims);
    }
    
    public static Set<String> getRegisteredNames() {
        return JWTClaimsSet.REGISTERED_CLAIM_NAMES;
    }
    
    public String getIssuer() {
        try {
            return this.getStringClaim("iss");
        }
        catch (ParseException ex) {
            return null;
        }
    }
    
    public String getSubject() {
        try {
            return this.getStringClaim("sub");
        }
        catch (ParseException ex) {
            return null;
        }
    }
    
    public List<String> getAudience() {
        List<String> aud;
        try {
            aud = this.getStringListClaim("aud");
        }
        catch (ParseException ex) {
            return Collections.emptyList();
        }
        return (aud != null) ? Collections.unmodifiableList((List<? extends String>)aud) : Collections.emptyList();
    }
    
    public Date getExpirationTime() {
        try {
            return this.getDateClaim("exp");
        }
        catch (ParseException ex) {
            return null;
        }
    }
    
    public Date getNotBeforeTime() {
        try {
            return this.getDateClaim("nbf");
        }
        catch (ParseException ex) {
            return null;
        }
    }
    
    public Date getIssueTime() {
        try {
            return this.getDateClaim("iat");
        }
        catch (ParseException ex) {
            return null;
        }
    }
    
    public String getJWTID() {
        try {
            return this.getStringClaim("jti");
        }
        catch (ParseException ex) {
            return null;
        }
    }
    
    public Object getClaim(final String name) {
        return this.claims.get(name);
    }
    
    public String getStringClaim(final String name) throws ParseException {
        final Object value = this.getClaim(name);
        if (value == null || value instanceof String) {
            return (String)value;
        }
        throw new ParseException("The \"" + name + "\" claim is not a String", 0);
    }
    
    public String[] getStringArrayClaim(final String name) throws ParseException {
        final Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        List<?> list;
        try {
            list = (List<?>)this.getClaim(name);
        }
        catch (ClassCastException ex) {
            throw new ParseException("The \"" + name + "\" claim is not a list / JSON array", 0);
        }
        final String[] stringArray = new String[list.size()];
        for (int i = 0; i < stringArray.length; ++i) {
            try {
                stringArray[i] = (String)list.get(i);
            }
            catch (ClassCastException ex2) {
                throw new ParseException("The \"" + name + "\" claim is not a list / JSON array of strings", 0);
            }
        }
        return stringArray;
    }
    
    public List<String> getStringListClaim(final String name) throws ParseException {
        final String[] stringArray = this.getStringArrayClaim(name);
        if (stringArray == null) {
            return null;
        }
        return Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])stringArray));
    }
    
    public URI getURIClaim(final String name) throws ParseException {
        final String uriString = this.getStringClaim(name);
        if (uriString == null) {
            return null;
        }
        try {
            return new URI(uriString);
        }
        catch (URISyntaxException e) {
            throw new ParseException("The \"" + name + "\" claim is not a URI: " + e.getMessage(), 0);
        }
    }
    
    public Boolean getBooleanClaim(final String name) throws ParseException {
        final Object value = this.getClaim(name);
        if (value == null || value instanceof Boolean) {
            return (Boolean)value;
        }
        throw new ParseException("The \"" + name + "\" claim is not a Boolean", 0);
    }
    
    public Integer getIntegerClaim(final String name) throws ParseException {
        final Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        throw new ParseException("The \"" + name + "\" claim is not an Integer", 0);
    }
    
    public Long getLongClaim(final String name) throws ParseException {
        final Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number)value).longValue();
        }
        throw new ParseException("The \"" + name + "\" claim is not a Number", 0);
    }
    
    public Date getDateClaim(final String name) throws ParseException {
        final Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return (Date)value;
        }
        if (value instanceof Number) {
            return DateUtils.fromSecondsSinceEpoch(((Number)value).longValue());
        }
        throw new ParseException("The \"" + name + "\" claim is not a Date", 0);
    }
    
    public Float getFloatClaim(final String name) throws ParseException {
        final Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number)value).floatValue();
        }
        throw new ParseException("The \"" + name + "\" claim is not a Float", 0);
    }
    
    public Double getDoubleClaim(final String name) throws ParseException {
        final Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number)value).doubleValue();
        }
        throw new ParseException("The \"" + name + "\" claim is not a Double", 0);
    }
    
    public JSONObject getJSONObjectClaim(final String name) throws ParseException {
        final Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof JSONObject) {
            return (JSONObject)value;
        }
        if (value instanceof Map) {
            final JSONObject jsonObject = new JSONObject();
            final Map<?, ?> map = (Map<?, ?>)value;
            for (final Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String) {
                    jsonObject.put((String)entry.getKey(), entry.getValue());
                }
            }
            return jsonObject;
        }
        throw new ParseException("The \"" + name + "\" claim is not a JSON object or Map", 0);
    }
    
    public Map<String, Object> getClaims() {
        return Collections.unmodifiableMap((Map<? extends String, ?>)this.claims);
    }
    
    public JSONObject toJSONObject() {
        final JSONObject o = new JSONObject();
        for (final Map.Entry<String, Object> claim : this.claims.entrySet()) {
            if (claim.getValue() instanceof Date) {
                final Date dateValue = claim.getValue();
                ((HashMap<String, Long>)o).put(claim.getKey(), DateUtils.toSecondsSinceEpoch(dateValue));
            }
            else if ("aud".equals(claim.getKey())) {
                final List<String> audList = this.getAudience();
                if (audList == null || audList.isEmpty()) {
                    continue;
                }
                if (audList.size() == 1) {
                    ((HashMap<String, String>)o).put("aud", audList.get(0));
                }
                else {
                    final JSONArray audArray = new JSONArray();
                    audArray.addAll(audList);
                    ((HashMap<String, JSONArray>)o).put("aud", audArray);
                }
            }
            else {
                if (claim.getValue() == null) {
                    continue;
                }
                o.put(claim.getKey(), claim.getValue());
            }
        }
        return o;
    }
    
    @Override
    public String toString() {
        return this.toJSONObject().toJSONString();
    }
    
    public <T> T toType(final JWTClaimsSetTransformer<T> transformer) {
        return transformer.transform(this);
    }
    
    public static JWTClaimsSet parse(final JSONObject json) throws ParseException {
        final Builder builder = new Builder();
        for (final String name : ((HashMap<String, V>)json).keySet()) {
            if (name.equals("iss")) {
                builder.issuer(JSONObjectUtils.getString(json, "iss"));
            }
            else if (name.equals("sub")) {
                builder.subject(JSONObjectUtils.getString(json, "sub"));
            }
            else if (name.equals("aud")) {
                final Object audValue = ((HashMap<K, Object>)json).get("aud");
                if (audValue instanceof String) {
                    final List<String> singleAud = new ArrayList<String>();
                    singleAud.add(JSONObjectUtils.getString(json, "aud"));
                    builder.audience(singleAud);
                }
                else {
                    if (!(audValue instanceof List)) {
                        continue;
                    }
                    builder.audience(JSONObjectUtils.getStringList(json, "aud"));
                }
            }
            else if (name.equals("exp")) {
                builder.expirationTime(new Date(JSONObjectUtils.getLong(json, "exp") * 1000L));
            }
            else if (name.equals("nbf")) {
                builder.notBeforeTime(new Date(JSONObjectUtils.getLong(json, "nbf") * 1000L));
            }
            else if (name.equals("iat")) {
                builder.issueTime(new Date(JSONObjectUtils.getLong(json, "iat") * 1000L));
            }
            else if (name.equals("jti")) {
                builder.jwtID(JSONObjectUtils.getString(json, "jti"));
            }
            else {
                builder.claim(name, ((HashMap<K, Object>)json).get(name));
            }
        }
        return builder.build();
    }
    
    public static JWTClaimsSet parse(final String s) throws ParseException {
        return parse(JSONObjectUtils.parse(s));
    }
    
    public static class Builder
    {
        private final Map<String, Object> claims;
        
        public Builder() {
            this.claims = new LinkedHashMap<String, Object>();
        }
        
        public Builder(final JWTClaimsSet jwtClaimsSet) {
            (this.claims = new LinkedHashMap<String, Object>()).putAll(jwtClaimsSet.claims);
        }
        
        public Builder issuer(final String iss) {
            this.claims.put("iss", iss);
            return this;
        }
        
        public Builder subject(final String sub) {
            this.claims.put("sub", sub);
            return this;
        }
        
        public Builder audience(final List<String> aud) {
            this.claims.put("aud", aud);
            return this;
        }
        
        public Builder audience(final String aud) {
            if (aud == null) {
                this.claims.put("aud", null);
            }
            else {
                this.claims.put("aud", Collections.singletonList(aud));
            }
            return this;
        }
        
        public Builder expirationTime(final Date exp) {
            this.claims.put("exp", exp);
            return this;
        }
        
        public Builder notBeforeTime(final Date nbf) {
            this.claims.put("nbf", nbf);
            return this;
        }
        
        public Builder issueTime(final Date iat) {
            this.claims.put("iat", iat);
            return this;
        }
        
        public Builder jwtID(final String jti) {
            this.claims.put("jti", jti);
            return this;
        }
        
        public Builder claim(final String name, final Object value) {
            this.claims.put(name, value);
            return this;
        }
        
        public JWTClaimsSet build() {
            return new JWTClaimsSet(this.claims, null);
        }
    }
}
