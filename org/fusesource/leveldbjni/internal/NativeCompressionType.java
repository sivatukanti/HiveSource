// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

public enum NativeCompressionType
{
    kNoCompression(0), 
    kSnappyCompression(1);
    
    static final int t;
    final int value;
    
    private NativeCompressionType(final int value) {
        this.value = value;
    }
    
    static {
        t = NativeCompressionType.kNoCompression.value;
    }
}
