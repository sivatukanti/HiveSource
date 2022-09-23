// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.common;

public class StreamFlags
{
    public int checkType;
    public long backwardSize;
    
    public StreamFlags() {
        this.checkType = -1;
        this.backwardSize = -1L;
    }
}
