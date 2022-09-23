// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import java.math.BigInteger;
import net.jcip.annotations.Immutable;

@Immutable
public class Base64URL extends Base64
{
    public Base64URL(final String base64URL) {
        super(base64URL);
    }
    
    @Override
    public boolean equals(final Object object) {
        return object != null && object instanceof Base64URL && this.toString().equals(object.toString());
    }
    
    public static Base64URL encode(final byte[] bytes) {
        return new Base64URL(Base64Codec.encodeToString(bytes, true));
    }
    
    public static Base64URL encode(final BigInteger bigInt) {
        return encode(BigIntegerUtils.toBytesUnsigned(bigInt));
    }
    
    public static Base64URL encode(final String text) {
        return encode(text.getBytes(Base64URL.CHARSET));
    }
}
