// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

public class IntegerUtils
{
    public static byte[] toBytes(final int intValue) {
        final byte[] res = { (byte)(intValue >>> 24), (byte)(intValue >>> 16 & 0xFF), (byte)(intValue >>> 8 & 0xFF), (byte)(intValue & 0xFF) };
        return res;
    }
    
    private IntegerUtils() {
    }
}
