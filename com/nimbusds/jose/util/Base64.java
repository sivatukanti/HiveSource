// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import net.minidev.json.JSONValue;
import java.math.BigInteger;
import java.nio.charset.Charset;
import net.jcip.annotations.Immutable;
import java.io.Serializable;
import net.minidev.json.JSONAware;

@Immutable
public class Base64 implements JSONAware, Serializable
{
    private static final long serialVersionUID = 1L;
    public static final Charset CHARSET;
    private final String value;
    
    static {
        CHARSET = Charset.forName("UTF-8");
    }
    
    public Base64(final String base64) {
        if (base64 == null) {
            throw new IllegalArgumentException("The Base64 value must not be null");
        }
        this.value = base64;
    }
    
    public byte[] decode() {
        return Base64Codec.decode(this.value);
    }
    
    public BigInteger decodeToBigInteger() {
        return new BigInteger(1, this.decode());
    }
    
    public String decodeToString() {
        return new String(this.decode(), Base64.CHARSET);
    }
    
    @Override
    public String toJSONString() {
        return "\"" + JSONValue.escape(this.value) + "\"";
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    
    @Override
    public boolean equals(final Object object) {
        return object != null && object instanceof Base64 && this.toString().equals(object.toString());
    }
    
    public static Base64 encode(final byte[] bytes) {
        return new Base64(Base64Codec.encodeToString(bytes, false));
    }
    
    public static Base64 encode(final BigInteger bigInt) {
        return encode(BigIntegerUtils.toBytesUnsigned(bigInt));
    }
    
    public static Base64 encode(final String text) {
        return encode(text.getBytes(Base64.CHARSET));
    }
}
