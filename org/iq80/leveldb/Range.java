// 
// Decompiled by Procyon v0.5.36
// 

package org.iq80.leveldb;

public class Range
{
    private final byte[] start;
    private final byte[] limit;
    
    public byte[] limit() {
        return this.limit;
    }
    
    public byte[] start() {
        return this.start;
    }
    
    public Range(final byte[] start, final byte[] limit) {
        Options.checkArgNotNull(start, "start");
        Options.checkArgNotNull(limit, "limit");
        this.limit = limit;
        this.start = start;
    }
}
