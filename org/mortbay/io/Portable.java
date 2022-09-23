// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io;

import org.mortbay.util.StringUtil;

public class Portable
{
    public static final String ALL_INTERFACES = "0.0.0.0";
    
    public static void arraycopy(final byte[] src, final int srcOffset, final byte[] dst, final int dstOffset, final int length) {
        System.arraycopy(src, srcOffset, dst, dstOffset, length);
    }
    
    public static void throwNotSupported() {
        throw new RuntimeException("Not Supported");
    }
    
    public static byte[] getBytes(final String s) {
        try {
            return s.getBytes(StringUtil.__ISO_8859_1);
        }
        catch (Exception e) {
            return s.getBytes();
        }
    }
}
