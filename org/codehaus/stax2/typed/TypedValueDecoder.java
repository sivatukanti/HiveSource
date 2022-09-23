// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.typed;

public abstract class TypedValueDecoder
{
    public abstract void decode(final String p0) throws IllegalArgumentException;
    
    public abstract void decode(final char[] p0, final int p1, final int p2) throws IllegalArgumentException;
    
    public abstract void handleEmptyValue() throws IllegalArgumentException;
}
