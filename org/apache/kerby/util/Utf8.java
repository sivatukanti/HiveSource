// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.util;

import java.nio.charset.StandardCharsets;

public final class Utf8
{
    private Utf8() {
    }
    
    public static String toString(final byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
    public static byte[] toBytes(final String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }
}
