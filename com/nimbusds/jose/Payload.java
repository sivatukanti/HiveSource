// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import java.text.ParseException;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.StandardCharset;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jose.util.Base64URL;
import net.minidev.json.JSONObject;
import net.jcip.annotations.Immutable;
import java.io.Serializable;

@Immutable
public final class Payload implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Origin origin;
    private final JSONObject jsonObject;
    private final String string;
    private final byte[] bytes;
    private final Base64URL base64URL;
    private final JWSObject jwsObject;
    private final SignedJWT signedJWT;
    
    private static String byteArrayToString(final byte[] bytes) {
        return (bytes != null) ? new String(bytes, StandardCharset.UTF_8) : null;
    }
    
    private static byte[] stringToByteArray(final String string) {
        return (byte[])((string != null) ? string.getBytes(StandardCharset.UTF_8) : null);
    }
    
    public Payload(final JSONObject jsonObject) {
        if (jsonObject == null) {
            throw new IllegalArgumentException("The JSON object must not be null");
        }
        this.jsonObject = jsonObject;
        this.string = null;
        this.bytes = null;
        this.base64URL = null;
        this.jwsObject = null;
        this.signedJWT = null;
        this.origin = Origin.JSON;
    }
    
    public Payload(final String string) {
        if (string == null) {
            throw new IllegalArgumentException("The string must not be null");
        }
        this.jsonObject = null;
        this.string = string;
        this.bytes = null;
        this.base64URL = null;
        this.jwsObject = null;
        this.signedJWT = null;
        this.origin = Origin.STRING;
    }
    
    public Payload(final byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("The byte array must not be null");
        }
        this.jsonObject = null;
        this.string = null;
        this.bytes = bytes;
        this.base64URL = null;
        this.jwsObject = null;
        this.signedJWT = null;
        this.origin = Origin.BYTE_ARRAY;
    }
    
    public Payload(final Base64URL base64URL) {
        if (base64URL == null) {
            throw new IllegalArgumentException("The Base64URL-encoded object must not be null");
        }
        this.jsonObject = null;
        this.string = null;
        this.bytes = null;
        this.base64URL = base64URL;
        this.jwsObject = null;
        this.signedJWT = null;
        this.origin = Origin.BASE64URL;
    }
    
    public Payload(final JWSObject jwsObject) {
        if (jwsObject == null) {
            throw new IllegalArgumentException("The JWS object must not be null");
        }
        if (jwsObject.getState() == JWSObject.State.UNSIGNED) {
            throw new IllegalArgumentException("The JWS object must be signed");
        }
        this.jsonObject = null;
        this.string = null;
        this.bytes = null;
        this.base64URL = null;
        this.jwsObject = jwsObject;
        this.signedJWT = null;
        this.origin = Origin.JWS_OBJECT;
    }
    
    public Payload(final SignedJWT signedJWT) {
        if (signedJWT == null) {
            throw new IllegalArgumentException("The signed JWT must not be null");
        }
        if (signedJWT.getState() == JWSObject.State.UNSIGNED) {
            throw new IllegalArgumentException("The JWT must be signed");
        }
        this.jsonObject = null;
        this.string = null;
        this.bytes = null;
        this.base64URL = null;
        this.signedJWT = signedJWT;
        this.jwsObject = signedJWT;
        this.origin = Origin.SIGNED_JWT;
    }
    
    public Origin getOrigin() {
        return this.origin;
    }
    
    public JSONObject toJSONObject() {
        if (this.jsonObject != null) {
            return this.jsonObject;
        }
        final String s = this.toString();
        if (s == null) {
            return null;
        }
        try {
            return JSONObjectUtils.parse(s);
        }
        catch (ParseException ex) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        if (this.string != null) {
            return this.string;
        }
        if (this.jwsObject != null) {
            if (this.jwsObject.getParsedString() != null) {
                return this.jwsObject.getParsedString();
            }
            return this.jwsObject.serialize();
        }
        else {
            if (this.jsonObject != null) {
                return this.jsonObject.toString();
            }
            if (this.bytes != null) {
                return byteArrayToString(this.bytes);
            }
            if (this.base64URL != null) {
                return this.base64URL.decodeToString();
            }
            return null;
        }
    }
    
    public byte[] toBytes() {
        if (this.bytes != null) {
            return this.bytes;
        }
        if (this.base64URL != null) {
            return this.base64URL.decode();
        }
        return stringToByteArray(this.toString());
    }
    
    public Base64URL toBase64URL() {
        if (this.base64URL != null) {
            return this.base64URL;
        }
        return Base64URL.encode(this.toBytes());
    }
    
    public JWSObject toJWSObject() {
        if (this.jwsObject != null) {
            return this.jwsObject;
        }
        try {
            return JWSObject.parse(this.toString());
        }
        catch (ParseException ex) {
            return null;
        }
    }
    
    public SignedJWT toSignedJWT() {
        if (this.signedJWT != null) {
            return this.signedJWT;
        }
        try {
            return SignedJWT.parse(this.toString());
        }
        catch (ParseException ex) {
            return null;
        }
    }
    
    public <T> T toType(final PayloadTransformer<T> transformer) {
        return transformer.transform(this);
    }
    
    public enum Origin
    {
        JSON("JSON", 0), 
        STRING("STRING", 1), 
        BYTE_ARRAY("BYTE_ARRAY", 2), 
        BASE64URL("BASE64URL", 3), 
        JWS_OBJECT("JWS_OBJECT", 4), 
        SIGNED_JWT("SIGNED_JWT", 5);
        
        private Origin(final String name, final int ordinal) {
        }
    }
}
