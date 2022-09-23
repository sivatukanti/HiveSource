// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.io.UnsupportedEncodingException;

public class Internal
{
    public static String stringDefaultValue(final String bytes) {
        try {
            return new String(bytes.getBytes("ISO-8859-1"), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Java VM does not support a standard character set.", e);
        }
    }
    
    public static ByteString bytesDefaultValue(final String bytes) {
        try {
            return ByteString.copyFrom(bytes.getBytes("ISO-8859-1"));
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Java VM does not support a standard character set.", e);
        }
    }
    
    public static boolean isValidUtf8(final ByteString byteString) {
        return byteString.isValidUtf8();
    }
    
    public interface EnumLiteMap<T extends EnumLite>
    {
        T findValueByNumber(final int p0);
    }
    
    public interface EnumLite
    {
        int getNumber();
    }
}
