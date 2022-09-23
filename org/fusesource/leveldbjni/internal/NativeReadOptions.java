// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.ClassFlag;
import org.fusesource.hawtjni.runtime.JniClass;

@JniClass(name = "leveldb::ReadOptions", flags = { ClassFlag.STRUCT, ClassFlag.CPP })
public class NativeReadOptions
{
    @JniField
    private boolean verify_checksums;
    @JniField
    private boolean fill_cache;
    @JniField(cast = "const leveldb::Snapshot*")
    private long snapshot;
    
    public NativeReadOptions() {
        this.verify_checksums = false;
        this.fill_cache = true;
        this.snapshot = 0L;
    }
    
    public boolean fillCache() {
        return this.fill_cache;
    }
    
    public NativeReadOptions fillCache(final boolean fill_cache) {
        this.fill_cache = fill_cache;
        return this;
    }
    
    public NativeSnapshot snapshot() {
        if (this.snapshot == 0L) {
            return null;
        }
        return new NativeSnapshot(this.snapshot);
    }
    
    public NativeReadOptions snapshot(final NativeSnapshot snapshot) {
        if (snapshot == null) {
            this.snapshot = 0L;
        }
        else {
            this.snapshot = snapshot.pointer();
        }
        return this;
    }
    
    public boolean verifyChecksums() {
        return this.verify_checksums;
    }
    
    public NativeReadOptions verifyChecksums(final boolean verify_checksums) {
        this.verify_checksums = verify_checksums;
        return this;
    }
}
