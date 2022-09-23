// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import org.fusesource.hawtjni.runtime.MethodFlag;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.ClassFlag;
import org.fusesource.hawtjni.runtime.JniClass;

class NativeStdString extends NativeObject
{
    public NativeStdString(final long self) {
        super(self);
    }
    
    public NativeStdString() {
        super(StdStringJNI.create());
    }
    
    public void delete() {
        this.assertAllocated();
        StdStringJNI.delete(this.self);
        this.self = 0L;
    }
    
    @Override
    public String toString() {
        return new String(this.toByteArray());
    }
    
    public long length() {
        this.assertAllocated();
        return StdStringJNI.length(this.self);
    }
    
    public byte[] toByteArray() {
        final long l = this.length();
        if (l > 2147483647L) {
            throw new ArrayIndexOutOfBoundsException("Native string is larger than the maximum Java array");
        }
        final byte[] rc = new byte[(int)l];
        NativeBuffer.NativeBufferJNI.buffer_copy(StdStringJNI.c_str_ptr(this.self), 0L, rc, 0L, rc.length);
        return rc;
    }
    
    @JniClass(name = "std::string", flags = { ClassFlag.CPP })
    private static class StdStringJNI
    {
        @JniMethod(flags = { MethodFlag.CPP_NEW })
        public static final native long create();
        
        @JniMethod(flags = { MethodFlag.CPP_NEW })
        public static final native long create(final String p0);
        
        @JniMethod(flags = { MethodFlag.CPP_DELETE })
        static final native void delete(final long p0);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD }, accessor = "c_str", cast = "const char*")
        public static final native long c_str_ptr(final long p0);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD }, cast = "size_t")
        public static final native long length(final long p0);
        
        static {
            NativeDB.LIBRARY.load();
        }
    }
}
