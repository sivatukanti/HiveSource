// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import org.fusesource.hawtjni.runtime.PointerMath;
import org.fusesource.hawtjni.runtime.MethodFlag;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.ArgFlag;
import org.fusesource.hawtjni.runtime.JniArg;
import org.fusesource.hawtjni.runtime.FieldFlag;
import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.ClassFlag;
import org.fusesource.hawtjni.runtime.JniClass;

public class NativeRange
{
    private final byte[] start;
    private final byte[] limit;
    
    public byte[] limit() {
        return this.limit;
    }
    
    public byte[] start() {
        return this.start;
    }
    
    public NativeRange(final byte[] start, final byte[] limit) {
        NativeDB.checkArgNotNull(start, "start");
        NativeDB.checkArgNotNull(limit, "limit");
        this.limit = limit;
        this.start = start;
    }
    
    @JniClass(name = "leveldb::Range", flags = { ClassFlag.STRUCT, ClassFlag.CPP })
    public static class RangeJNI
    {
        @JniField(flags = { FieldFlag.CONSTANT }, accessor = "sizeof(struct leveldb::Range)")
        static int SIZEOF;
        @JniField
        NativeSlice start;
        @JniField(flags = { FieldFlag.FIELD_SKIP })
        NativeBuffer start_buffer;
        @JniField
        NativeSlice limit;
        @JniField(flags = { FieldFlag.FIELD_SKIP })
        NativeBuffer limit_buffer;
        
        public static final native void memmove(@JniArg(cast = "void *") final long p0, @JniArg(cast = "const void *", flags = { ArgFlag.NO_OUT, ArgFlag.CRITICAL }) final RangeJNI p1, @JniArg(cast = "size_t") final long p2);
        
        public static final native void memmove(@JniArg(cast = "void *", flags = { ArgFlag.NO_IN, ArgFlag.CRITICAL }) final RangeJNI p0, @JniArg(cast = "const void *") final long p1, @JniArg(cast = "size_t") final long p2);
        
        @JniMethod(flags = { MethodFlag.CONSTANT_INITIALIZER })
        private static final native void init();
        
        public RangeJNI(final NativeRange range) {
            this.start = new NativeSlice();
            this.limit = new NativeSlice();
            this.start_buffer = NativeBuffer.create(range.start());
            this.start.set(this.start_buffer);
            try {
                this.limit_buffer = NativeBuffer.create(range.limit());
            }
            catch (OutOfMemoryError e) {
                this.start_buffer.delete();
                throw e;
            }
            this.limit.set(this.limit_buffer);
        }
        
        public void delete() {
            this.start_buffer.delete();
            this.limit_buffer.delete();
        }
        
        static NativeBuffer arrayCreate(final int dimension) {
            return NativeBuffer.create(dimension * RangeJNI.SIZEOF);
        }
        
        void arrayWrite(final long buffer, final int index) {
            memmove(PointerMath.add(buffer, RangeJNI.SIZEOF * index), this, RangeJNI.SIZEOF);
        }
        
        void arrayRead(final long buffer, final int index) {
            memmove(this, PointerMath.add(buffer, RangeJNI.SIZEOF * index), RangeJNI.SIZEOF);
        }
        
        static {
            NativeDB.LIBRARY.load();
            init();
        }
    }
}
