// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.protocol;

public final class TSet
{
    public final byte elemType;
    public final int size;
    
    public TSet() {
        this((byte)0, 0);
    }
    
    public TSet(final byte t, final int s) {
        this.elemType = t;
        this.size = s;
    }
    
    public TSet(final TList list) {
        this(list.elemType, list.size);
    }
}
