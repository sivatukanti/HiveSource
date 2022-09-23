// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import org.fusesource.hawtjni.runtime.ArgFlag;
import org.fusesource.hawtjni.runtime.JniArg;
import org.fusesource.hawtjni.runtime.MethodFlag;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.FieldFlag;
import org.fusesource.hawtjni.runtime.PointerMath;
import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.ClassFlag;
import org.fusesource.hawtjni.runtime.JniClass;

@JniClass(name = "leveldb::Slice", flags = { ClassFlag.STRUCT, ClassFlag.CPP })
class NativeSlice
{
    @JniField(cast = "const char*")
    private long data_;
    @JniField(cast = "size_t")
    private long size_;
    
    public NativeSlice() {
    }
    
    public NativeSlice(final long data, final long length) {
        this.data_ = data;
        this.size_ = length;
    }
    
    public NativeSlice(final NativeBuffer buffer) {
        this(buffer.pointer(), buffer.capacity());
    }
    
    public static NativeSlice create(final NativeBuffer buffer) {
        if (buffer == null) {
            return null;
        }
        return new NativeSlice(buffer);
    }
    
    public long data() {
        return this.data_;
    }
    
    public NativeSlice data(final long data) {
        this.data_ = data;
        return this;
    }
    
    public long size() {
        return this.size_;
    }
    
    public NativeSlice size(final long size) {
        this.size_ = size;
        return this;
    }
    
    public NativeSlice set(final NativeSlice buffer) {
        this.size_ = buffer.size_;
        this.data_ = buffer.data_;
        return this;
    }
    
    public NativeSlice set(final NativeBuffer buffer) {
        this.size_ = buffer.capacity();
        this.data_ = buffer.pointer();
        return this;
    }
    
    public byte[] toByteArray() {
        if (this.size_ > 2147483647L) {
            throw new ArrayIndexOutOfBoundsException("Native slice is larger than the maximum Java array");
        }
        final byte[] rc = new byte[(int)this.size_];
        NativeBuffer.NativeBufferJNI.buffer_copy(this.data_, 0L, rc, 0L, rc.length);
        return rc;
    }
    
    static NativeBuffer arrayCreate(final int dimension) {
        return NativeBuffer.create(dimension * SliceJNI.SIZEOF);
    }
    
    void write(final long buffer, final int index) {
        SliceJNI.memmove(PointerMath.add(buffer, SliceJNI.SIZEOF * index), this, SliceJNI.SIZEOF);
    }
    
    void read(final long buffer, final int index) {
        SliceJNI.memmove(this, PointerMath.add(buffer, SliceJNI.SIZEOF * index), SliceJNI.SIZEOF);
    }
    
    @JniClass(name = "leveldb::Slice", flags = { ClassFlag.CPP })
    static class SliceJNI
    {
        @JniField(flags = { FieldFlag.CONSTANT }, accessor = "sizeof(struct leveldb::Slice)")
        static int SIZEOF;
        
        @JniMethod(flags = { MethodFlag.CPP_DELETE })
        public static final native void delete(final long p0);
        
        public static final native void memmove(@JniArg(cast = "void *") final long p0, @JniArg(cast = "const void *", flags = { ArgFlag.NO_OUT, ArgFlag.CRITICAL }) final NativeSlice p1, @JniArg(cast = "size_t") final long p2);
        
        public static final native void memmove(@JniArg(cast = "void *", flags = { ArgFlag.NO_IN, ArgFlag.CRITICAL }) final NativeSlice p0, @JniArg(cast = "const void *") final long p1, @JniArg(cast = "size_t") final long p2);
        
        @JniMethod(flags = { MethodFlag.CONSTANT_INITIALIZER })
        private static final native void init();
        
        static {
            NativeDB.LIBRARY.load();
            init();
        }
    }
}
