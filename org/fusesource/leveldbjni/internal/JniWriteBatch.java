// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import org.iq80.leveldb.WriteBatch;

public class JniWriteBatch implements WriteBatch
{
    private final NativeWriteBatch writeBatch;
    
    JniWriteBatch(final NativeWriteBatch writeBatch) {
        this.writeBatch = writeBatch;
    }
    
    public void close() {
        this.writeBatch.delete();
    }
    
    public WriteBatch put(final byte[] key, final byte[] value) {
        this.writeBatch.put(key, value);
        return this;
    }
    
    public WriteBatch delete(final byte[] key) {
        this.writeBatch.delete(key);
        return this;
    }
    
    public NativeWriteBatch writeBatch() {
        return this.writeBatch;
    }
}
