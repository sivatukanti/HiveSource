// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

public class RawField
{
    private byte[] data;
    
    public RawField(final byte[] data) {
        this.data = data;
    }
    
    public byte[] getData() {
        return this.data;
    }
}
