// 
// Decompiled by Procyon v0.5.36
// 

package org.iq80.leveldb;

public class ReadOptions
{
    private boolean verifyChecksums;
    private boolean fillCache;
    private Snapshot snapshot;
    
    public ReadOptions() {
        this.verifyChecksums = false;
        this.fillCache = true;
    }
    
    public Snapshot snapshot() {
        return this.snapshot;
    }
    
    public ReadOptions snapshot(final Snapshot snapshot) {
        this.snapshot = snapshot;
        return this;
    }
    
    public boolean fillCache() {
        return this.fillCache;
    }
    
    public ReadOptions fillCache(final boolean fillCache) {
        this.fillCache = fillCache;
        return this;
    }
    
    public boolean verifyChecksums() {
        return this.verifyChecksums;
    }
    
    public ReadOptions verifyChecksums(final boolean verifyChecksums) {
        this.verifyChecksums = verifyChecksums;
        return this;
    }
}
