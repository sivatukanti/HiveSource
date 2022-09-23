// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import org.fusesource.hawtjni.runtime.ArgFlag;
import org.fusesource.hawtjni.runtime.JniArg;
import org.fusesource.hawtjni.runtime.MethodFlag;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.ClassFlag;
import org.fusesource.hawtjni.runtime.JniClass;

public class NativeWriteBatch extends NativeObject
{
    public NativeWriteBatch() {
        super(WriteBatchJNI.create());
    }
    
    public void delete() {
        this.assertAllocated();
        WriteBatchJNI.delete(this.self);
        this.self = 0L;
    }
    
    public void put(final byte[] key, final byte[] value) {
        NativeDB.checkArgNotNull(key, "key");
        NativeDB.checkArgNotNull(value, "value");
        final NativeBuffer keyBuffer = NativeBuffer.create(key);
        try {
            final NativeBuffer valueBuffer = NativeBuffer.create(value);
            try {
                this.put(keyBuffer, valueBuffer);
            }
            finally {
                valueBuffer.delete();
            }
        }
        finally {
            keyBuffer.delete();
        }
    }
    
    private void put(final NativeBuffer keyBuffer, final NativeBuffer valueBuffer) {
        this.put(new NativeSlice(keyBuffer), new NativeSlice(valueBuffer));
    }
    
    private void put(final NativeSlice keySlice, final NativeSlice valueSlice) {
        this.assertAllocated();
        WriteBatchJNI.Put(this.self, keySlice, valueSlice);
    }
    
    public void delete(final byte[] key) {
        NativeDB.checkArgNotNull(key, "key");
        final NativeBuffer keyBuffer = NativeBuffer.create(key);
        try {
            this.delete(keyBuffer);
        }
        finally {
            keyBuffer.delete();
        }
    }
    
    private void delete(final NativeBuffer keyBuffer) {
        this.delete(new NativeSlice(keyBuffer));
    }
    
    private void delete(final NativeSlice keySlice) {
        this.assertAllocated();
        WriteBatchJNI.Delete(this.self, keySlice);
    }
    
    public void clear() {
        this.assertAllocated();
        WriteBatchJNI.Clear(this.self);
    }
    
    @JniClass(name = "leveldb::WriteBatch", flags = { ClassFlag.CPP })
    private static class WriteBatchJNI
    {
        @JniMethod(flags = { MethodFlag.CPP_NEW })
        public static final native long create();
        
        @JniMethod(flags = { MethodFlag.CPP_DELETE })
        public static final native void delete(final long p0);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        static final native void Put(final long p0, @JniArg(flags = { ArgFlag.BY_VALUE, ArgFlag.NO_OUT }) final NativeSlice p1, @JniArg(flags = { ArgFlag.BY_VALUE, ArgFlag.NO_OUT }) final NativeSlice p2);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        static final native void Delete(final long p0, @JniArg(flags = { ArgFlag.BY_VALUE, ArgFlag.NO_OUT }) final NativeSlice p1);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        static final native void Clear(final long p0);
        
        static {
            NativeDB.LIBRARY.load();
        }
    }
}
