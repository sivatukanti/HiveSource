// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import net.minidev.json.JSONObject;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.text.ParseException;
import com.nimbusds.jose.util.Base64URL;
import java.io.Serializable;

public abstract class JOSEObject implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static final String MIME_TYPE_COMPACT = "application/jose; charset=UTF-8";
    public static final String MIME_TYPE_JS = "application/jose+json; charset=UTF-8";
    private Payload payload;
    private Base64URL[] parsedParts;
    
    protected JOSEObject() {
        this.payload = null;
        this.parsedParts = null;
    }
    
    protected JOSEObject(final Payload payload) {
        this.payload = payload;
    }
    
    public abstract Header getHeader();
    
    protected void setPayload(final Payload payload) {
        this.payload = payload;
    }
    
    public Payload getPayload() {
        return this.payload;
    }
    
    protected void setParsedParts(final Base64URL... parts) {
        this.parsedParts = parts;
    }
    
    public Base64URL[] getParsedParts() {
        return this.parsedParts;
    }
    
    public String getParsedString() {
        if (this.parsedParts == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        Base64URL[] parsedParts;
        for (int length = (parsedParts = this.parsedParts).length, i = 0; i < length; ++i) {
            final Base64URL part = parsedParts[i];
            if (sb.length() > 0) {
                sb.append('.');
            }
            if (part != null) {
                sb.append(part.toString());
            }
        }
        return sb.toString();
    }
    
    public abstract String serialize();
    
    public static Base64URL[] split(final String s) throws ParseException {
        final String t = s.trim();
        final int dot1 = t.indexOf(".");
        if (dot1 == -1) {
            throw new ParseException("Invalid serialized unsecured/JWS/JWE object: Missing part delimiters", 0);
        }
        final int dot2 = t.indexOf(".", dot1 + 1);
        if (dot2 == -1) {
            throw new ParseException("Invalid serialized unsecured/JWS/JWE object: Missing second delimiter", 0);
        }
        final int dot3 = t.indexOf(".", dot2 + 1);
        if (dot3 == -1) {
            final Base64URL[] parts = { new Base64URL(t.substring(0, dot1)), new Base64URL(t.substring(dot1 + 1, dot2)), new Base64URL(t.substring(dot2 + 1)) };
            return parts;
        }
        final int dot4 = t.indexOf(".", dot3 + 1);
        if (dot4 == -1) {
            throw new ParseException("Invalid serialized JWE object: Missing fourth delimiter", 0);
        }
        if (dot4 != -1 && t.indexOf(".", dot4 + 1) != -1) {
            throw new ParseException("Invalid serialized unsecured/JWS/JWE object: Too many part delimiters", 0);
        }
        final Base64URL[] parts2 = { new Base64URL(t.substring(0, dot1)), new Base64URL(t.substring(dot1 + 1, dot2)), new Base64URL(t.substring(dot2 + 1, dot3)), new Base64URL(t.substring(dot3 + 1, dot4)), new Base64URL(t.substring(dot4 + 1)) };
        return parts2;
    }
    
    public static JOSEObject parse(final String s) throws ParseException {
        final Base64URL[] parts = split(s);
        JSONObject jsonObject;
        try {
            jsonObject = JSONObjectUtils.parse(parts[0].decodeToString());
        }
        catch (ParseException e) {
            throw new ParseException("Invalid unsecured/JWS/JWE header: " + e.getMessage(), 0);
        }
        final Algorithm alg = Header.parseAlgorithm(jsonObject);
        if (alg.equals(Algorithm.NONE)) {
            return PlainObject.parse(s);
        }
        if (alg instanceof JWSAlgorithm) {
            return JWSObject.parse(s);
        }
        if (alg instanceof JWEAlgorithm) {
            return JWEObject.parse(s);
        }
        throw new AssertionError((Object)("Unexpected algorithm type: " + alg));
    }
}
