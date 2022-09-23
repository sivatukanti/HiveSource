// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

public class MemoryLimitException extends XZIOException
{
    private static final long serialVersionUID = 3L;
    private final int memoryNeeded;
    private final int memoryLimit;
    
    public MemoryLimitException(final int n, final int n2) {
        super("" + n + " KiB of memory would be needed; limit was " + n2 + " KiB");
        this.memoryNeeded = n;
        this.memoryLimit = n2;
    }
    
    public int getMemoryNeeded() {
        return this.memoryNeeded;
    }
    
    public int getMemoryLimit() {
        return this.memoryLimit;
    }
}
