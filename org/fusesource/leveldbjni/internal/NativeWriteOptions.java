// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.ClassFlag;
import org.fusesource.hawtjni.runtime.JniClass;

@JniClass(name = "leveldb::WriteOptions", flags = { ClassFlag.STRUCT, ClassFlag.CPP })
public class NativeWriteOptions
{
    @JniField
    boolean sync;
    
    public boolean sync() {
        return this.sync;
    }
    
    public NativeWriteOptions sync(final boolean sync) {
        this.sync = sync;
        return this;
    }
}
