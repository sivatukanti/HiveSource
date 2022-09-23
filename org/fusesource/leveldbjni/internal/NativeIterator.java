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

public class NativeIterator extends NativeObject
{
    NativeIterator(final long self) {
        super(self);
    }
    
    public void delete() {
        this.assertAllocated();
        IteratorJNI.delete(this.self);
        this.self = 0L;
    }
    
    public boolean isValid() {
        this.assertAllocated();
        return IteratorJNI.Valid(this.self);
    }
    
    private void checkStatus() throws NativeDB.DBException {
        NativeDB.checkStatus(IteratorJNI.status(this.self));
    }
    
    public void seekToFirst() {
        this.assertAllocated();
        IteratorJNI.SeekToFirst(this.self);
    }
    
    public void seekToLast() {
        this.assertAllocated();
        IteratorJNI.SeekToLast(this.self);
    }
    
    public void seek(final byte[] key) throws NativeDB.DBException {
        NativeDB.checkArgNotNull(key, "key");
        final NativeBuffer keyBuffer = NativeBuffer.create(key);
        try {
            this.seek(keyBuffer);
        }
        finally {
            keyBuffer.delete();
        }
    }
    
    private void seek(final NativeBuffer keyBuffer) throws NativeDB.DBException {
        this.seek(new NativeSlice(keyBuffer));
    }
    
    private void seek(final NativeSlice keySlice) throws NativeDB.DBException {
        this.assertAllocated();
        IteratorJNI.Seek(this.self, keySlice);
        this.checkStatus();
    }
    
    public void next() throws NativeDB.DBException {
        this.assertAllocated();
        IteratorJNI.Next(this.self);
        this.checkStatus();
    }
    
    public void prev() throws NativeDB.DBException {
        this.assertAllocated();
        IteratorJNI.Prev(this.self);
        this.checkStatus();
    }
    
    public byte[] key() throws NativeDB.DBException {
        this.assertAllocated();
        final long slice_ptr = IteratorJNI.key(this.self);
        this.checkStatus();
        try {
            final NativeSlice slice = new NativeSlice();
            slice.read(slice_ptr, 0);
            return slice.toByteArray();
        }
        finally {
            NativeSlice.SliceJNI.delete(slice_ptr);
        }
    }
    
    public byte[] value() throws NativeDB.DBException {
        this.assertAllocated();
        final long slice_ptr = IteratorJNI.value(this.self);
        this.checkStatus();
        try {
            final NativeSlice slice = new NativeSlice();
            slice.read(slice_ptr, 0);
            return slice.toByteArray();
        }
        finally {
            NativeSlice.SliceJNI.delete(slice_ptr);
        }
    }
    
    @JniClass(name = "leveldb::Iterator", flags = { ClassFlag.CPP })
    private static class IteratorJNI
    {
        @JniMethod(flags = { MethodFlag.CPP_DELETE })
        public static final native void delete(final long p0);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        static final native boolean Valid(final long p0);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        static final native void SeekToFirst(final long p0);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        static final native void SeekToLast(final long p0);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        static final native void Seek(final long p0, @JniArg(flags = { ArgFlag.BY_VALUE, ArgFlag.NO_OUT }) final NativeSlice p1);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        static final native void Next(final long p0);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        static final native void Prev(final long p0);
        
        @JniMethod(copy = "leveldb::Slice", flags = { MethodFlag.CPP_METHOD })
        static final native long key(final long p0);
        
        @JniMethod(copy = "leveldb::Slice", flags = { MethodFlag.CPP_METHOD })
        static final native long value(final long p0);
        
        @JniMethod(copy = "leveldb::Status", flags = { MethodFlag.CPP_METHOD })
        static final native long status(final long p0);
        
        static {
            NativeDB.LIBRARY.load();
        }
    }
}
