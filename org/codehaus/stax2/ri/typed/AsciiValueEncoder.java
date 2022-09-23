// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.typed;

public abstract class AsciiValueEncoder
{
    protected static final int MIN_CHARS_WITHOUT_FLUSH = 64;
    
    protected AsciiValueEncoder() {
    }
    
    public final boolean bufferNeedsFlush(final int n) {
        return n < 64;
    }
    
    public abstract boolean isCompleted();
    
    public abstract int encodeMore(final char[] p0, final int p1, final int p2);
    
    public abstract int encodeMore(final byte[] p0, final int p1, final int p2);
}
