// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

class NativeObject
{
    protected long self;
    
    protected NativeObject(final long self) {
        this.self = self;
        if (self == 0L) {
            throw new OutOfMemoryError("Failure allocating native heap memory");
        }
    }
    
    long pointer() {
        return this.self;
    }
    
    public boolean isAllocated() {
        return this.self != 0L;
    }
    
    protected void assertAllocated() {
        assert this.isAllocated() : "This object has been deleted";
    }
}
