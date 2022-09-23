// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class ByteUtils
{
    public static byte[] concat(final byte[]... byteArrays) {
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (final byte[] bytes : byteArrays) {
                if (bytes != null) {
                    baos.write(bytes);
                }
            }
            return baos.toByteArray();
        }
        catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
    
    public static byte[] subArray(final byte[] byteArray, final int beginIndex, final int length) {
        final byte[] subArray = new byte[length];
        System.arraycopy(byteArray, beginIndex, subArray, 0, subArray.length);
        return subArray;
    }
    
    public static int bitLength(final int byteLength) {
        return byteLength * 8;
    }
    
    public static int safeBitLength(final int byteLength) throws IntegerOverflowException {
        final long longResult = byteLength * 8L;
        if ((int)longResult != longResult) {
            throw new IntegerOverflowException();
        }
        return (int)longResult;
    }
    
    public static int bitLength(final byte[] byteArray) {
        if (byteArray == null) {
            return 0;
        }
        return bitLength(byteArray.length);
    }
    
    public static int safeBitLength(final byte[] byteArray) throws IntegerOverflowException {
        if (byteArray == null) {
            return 0;
        }
        return safeBitLength(byteArray.length);
    }
    
    public static int byteLength(final int bitLength) {
        return bitLength / 8;
    }
}
