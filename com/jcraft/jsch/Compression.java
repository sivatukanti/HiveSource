// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface Compression
{
    public static final int INFLATER = 0;
    public static final int DEFLATER = 1;
    
    void init(final int p0, final int p1);
    
    byte[] compress(final byte[] p0, final int p1, final int[] p2);
    
    byte[] uncompress(final byte[] p0, final int p1, final int[] p2);
}
