// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.typed;

public abstract class TypedArrayDecoder
{
    public abstract boolean decodeValue(final String p0) throws IllegalArgumentException;
    
    public abstract boolean decodeValue(final char[] p0, final int p1, final int p2) throws IllegalArgumentException;
    
    public abstract int getCount();
    
    public abstract boolean hasRoom();
}
