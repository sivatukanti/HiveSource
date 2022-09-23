// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.util.IntegerOverflowException;
import java.nio.ByteBuffer;
import com.nimbusds.jose.util.ByteUtils;
import java.nio.charset.Charset;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JWEHeader;

class AAD
{
    public static byte[] compute(final JWEHeader jweHeader) {
        return compute(jweHeader.toBase64URL());
    }
    
    public static byte[] compute(final Base64URL encodedJWEHeader) {
        return encodedJWEHeader.toString().getBytes(Charset.forName("ASCII"));
    }
    
    public static byte[] computeLength(final byte[] aad) throws IntegerOverflowException {
        final int bitLength = ByteUtils.safeBitLength(aad);
        return ByteBuffer.allocate(8).putLong(bitLength).array();
    }
}
