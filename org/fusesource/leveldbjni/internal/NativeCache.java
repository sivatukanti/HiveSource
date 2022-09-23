// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import org.fusesource.hawtjni.runtime.MethodFlag;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.JniArg;
import org.fusesource.hawtjni.runtime.ClassFlag;
import org.fusesource.hawtjni.runtime.JniClass;

public class NativeCache extends NativeObject
{
    public NativeCache(final long capacity) {
        super(CacheJNI.NewLRUCache(capacity));
    }
    
    public void delete() {
        this.assertAllocated();
        CacheJNI.delete(this.self);
        this.self = 0L;
    }
    
    @JniClass(name = "leveldb::Cache", flags = { ClassFlag.CPP })
    private static class CacheJNI
    {
        @JniMethod(cast = "leveldb::Cache *", accessor = "leveldb::NewLRUCache")
        public static final native long NewLRUCache(@JniArg(cast = "size_t") final long p0);
        
        @JniMethod(flags = { MethodFlag.CPP_DELETE })
        public static final native void delete(final long p0);
        
        static {
            NativeDB.LIBRARY.load();
        }
    }
}
