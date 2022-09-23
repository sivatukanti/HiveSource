// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import java.util.concurrent.atomic.AtomicInteger;
import org.fusesource.hawtjni.runtime.ArgFlag;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.JniArg;
import org.fusesource.hawtjni.runtime.JniClass;
import org.fusesource.hawtjni.runtime.PointerMath;

public class NativeBuffer extends NativeObject
{
    private final Allocation allocation;
    private final long capacity;
    private static final ThreadLocal<Pool> CURRENT_POOL;
    
    public static NativeBuffer create(final long capacity) {
        final Pool pool = NativeBuffer.CURRENT_POOL.get();
        if (pool == null) {
            final Allocation allocation = new Allocation(capacity);
            return new NativeBuffer(allocation, allocation.self, capacity);
        }
        return pool.create(capacity);
    }
    
    public static void pushMemoryPool(final int size) {
        final Pool original = NativeBuffer.CURRENT_POOL.get();
        final Pool next = new Pool(size, original);
        NativeBuffer.CURRENT_POOL.set(next);
    }
    
    public static void popMemoryPool() {
        final Pool next = NativeBuffer.CURRENT_POOL.get();
        next.delete();
        if (next.prev == null) {
            NativeBuffer.CURRENT_POOL.remove();
        }
        else {
            NativeBuffer.CURRENT_POOL.set(next.prev);
        }
    }
    
    public static NativeBuffer create(final byte[] data) {
        if (data == null) {
            return null;
        }
        return create(data, 0, data.length);
    }
    
    public static NativeBuffer create(final String data) {
        return create(cbytes(data));
    }
    
    public static NativeBuffer create(final byte[] data, final int offset, final int length) {
        final NativeBuffer rc = create(length);
        rc.write(0L, data, offset, length);
        return rc;
    }
    
    private NativeBuffer(final Allocation allocation, final long self, final long capacity) {
        super(self);
        this.capacity = capacity;
        (this.allocation = allocation).retain();
    }
    
    public NativeBuffer slice(final long offset, final long length) {
        this.assertAllocated();
        if (length < 0L) {
            throw new IllegalArgumentException("length cannot be negative");
        }
        if (offset < 0L) {
            throw new IllegalArgumentException("offset cannot be negative");
        }
        if (offset + length >= this.capacity) {
            throw new ArrayIndexOutOfBoundsException("offset + length exceed the length of this buffer");
        }
        return new NativeBuffer(this.allocation, PointerMath.add(this.self, offset), length);
    }
    
    static byte[] cbytes(final String strvalue) {
        final byte[] value = strvalue.getBytes();
        final byte[] rc = new byte[value.length + 1];
        System.arraycopy(value, 0, rc, 0, value.length);
        return rc;
    }
    
    public NativeBuffer head(final long length) {
        return this.slice(0L, length);
    }
    
    public NativeBuffer tail(final long length) {
        if (this.capacity - length < 0L) {
            throw new ArrayIndexOutOfBoundsException("capacity-length cannot be less than zero");
        }
        return this.slice(this.capacity - length, length);
    }
    
    public void delete() {
        this.allocation.release();
    }
    
    public long capacity() {
        return this.capacity;
    }
    
    public void write(final long at, final byte[] source, final int offset, final int length) {
        this.assertAllocated();
        if (length < 0) {
            throw new IllegalArgumentException("length cannot be negative");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset cannot be negative");
        }
        if (at < 0L) {
            throw new IllegalArgumentException("at cannot be negative");
        }
        if (at + length > this.capacity) {
            throw new ArrayIndexOutOfBoundsException("at + length exceeds the capacity of this object");
        }
        if (offset + length > source.length) {
            throw new ArrayIndexOutOfBoundsException("offset + length exceed the length of the source buffer");
        }
        NativeBufferJNI.buffer_copy(source, offset, this.self, at, length);
    }
    
    public void read(final long at, final byte[] target, final int offset, final int length) {
        this.assertAllocated();
        if (length < 0) {
            throw new IllegalArgumentException("length cannot be negative");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset cannot be negative");
        }
        if (at < 0L) {
            throw new IllegalArgumentException("at cannot be negative");
        }
        if (at + length > this.capacity) {
            throw new ArrayIndexOutOfBoundsException("at + length exceeds the capacity of this object");
        }
        if (offset + length > target.length) {
            throw new ArrayIndexOutOfBoundsException("offset + length exceed the length of the target buffer");
        }
        NativeBufferJNI.buffer_copy(this.self, at, target, offset, length);
    }
    
    public byte[] toByteArray() {
        if (this.capacity > 2147483647L) {
            throw new OutOfMemoryError("Native buffer larger than the largest allowed Java byte[]");
        }
        final byte[] rc = new byte[(int)this.capacity];
        this.read(0L, rc, 0, rc.length);
        return rc;
    }
    
    static {
        CURRENT_POOL = new ThreadLocal<Pool>();
    }
    
    @JniClass
    static class NativeBufferJNI
    {
        @JniMethod(cast = "void *")
        public static final native long malloc(@JniArg(cast = "size_t") final long p0);
        
        public static final native void free(@JniArg(cast = "void *") final long p0);
        
        public static final native void buffer_copy(@JniArg(cast = "const void *", flags = { ArgFlag.NO_OUT, ArgFlag.CRITICAL }) final byte[] p0, @JniArg(cast = "size_t") final long p1, @JniArg(cast = "void *") final long p2, @JniArg(cast = "size_t") final long p3, @JniArg(cast = "size_t") final long p4);
        
        public static final native void buffer_copy(@JniArg(cast = "const void *") final long p0, @JniArg(cast = "size_t") final long p1, @JniArg(cast = "void *", flags = { ArgFlag.NO_IN, ArgFlag.CRITICAL }) final byte[] p2, @JniArg(cast = "size_t") final long p3, @JniArg(cast = "size_t") final long p4);
        
        static {
            NativeDB.LIBRARY.load();
        }
    }
    
    private static class Allocation extends NativeObject
    {
        private final AtomicInteger retained;
        
        private Allocation(final long size) {
            super(NativeBufferJNI.malloc(size));
            this.retained = new AtomicInteger(0);
        }
        
        void retain() {
            this.assertAllocated();
            this.retained.incrementAndGet();
        }
        
        void release() {
            this.assertAllocated();
            final int r = this.retained.decrementAndGet();
            if (r < 0) {
                throw new Error("The object has already been deleted.");
            }
            if (r == 0) {
                NativeBufferJNI.free(this.self);
                this.self = 0L;
            }
        }
    }
    
    private static class Pool
    {
        private final Pool prev;
        Allocation allocation;
        long pos;
        long remaining;
        int chunk;
        
        public Pool(final int chunk, final Pool prev) {
            this.chunk = chunk;
            this.prev = prev;
        }
        
        NativeBuffer create(final long size) {
            if (size >= this.chunk) {
                final Allocation allocation = new Allocation(size);
                return new NativeBuffer(allocation, allocation.self, size, null);
            }
            if (this.remaining < size) {
                this.delete();
            }
            if (this.allocation == null) {
                this.allocate();
            }
            final NativeBuffer rc = new NativeBuffer(this.allocation, this.pos, size, null);
            this.pos = PointerMath.add(this.pos, size);
            this.remaining -= size;
            return rc;
        }
        
        private void allocate() {
            (this.allocation = new Allocation((long)this.chunk)).retain();
            this.remaining = this.chunk;
            this.pos = this.allocation.self;
        }
        
        public void delete() {
            if (this.allocation != null) {
                this.allocation.release();
                this.allocation = null;
            }
        }
    }
}
