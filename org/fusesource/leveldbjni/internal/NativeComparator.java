// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import org.fusesource.hawtjni.runtime.ArgFlag;
import org.fusesource.hawtjni.runtime.JniArg;
import org.fusesource.hawtjni.runtime.MethodFlag;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.FieldFlag;
import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.ClassFlag;
import org.fusesource.hawtjni.runtime.JniClass;

public abstract class NativeComparator extends NativeObject
{
    private NativeBuffer name_buffer;
    private long globalRef;
    public static final NativeComparator BYTEWISE_COMPARATOR;
    
    public NativeComparator() {
        super(ComparatorJNI.create());
        try {
            this.name_buffer = NativeBuffer.create(this.name());
            this.globalRef = NativeDB.DBJNI.NewGlobalRef(this);
            if (this.globalRef == 0L) {
                throw new RuntimeException("jni call failed: NewGlobalRef");
            }
            final ComparatorJNI struct = new ComparatorJNI();
            struct.compare_method = NativeDB.DBJNI.GetMethodID(this.getClass(), "compare", "(JJ)I");
            if (struct.compare_method == 0L) {
                throw new RuntimeException("jni call failed: GetMethodID");
            }
            struct.target = this.globalRef;
            struct.name = this.name_buffer.pointer();
            ComparatorJNI.memmove(this.self, struct, ComparatorJNI.SIZEOF);
        }
        catch (RuntimeException e) {
            this.delete();
            throw e;
        }
    }
    
    NativeComparator(final long ptr) {
        super(ptr);
    }
    
    public void delete() {
        if (this.name_buffer != null) {
            this.name_buffer.delete();
            this.name_buffer = null;
        }
        if (this.globalRef != 0L) {
            NativeDB.DBJNI.DeleteGlobalRef(this.globalRef);
            this.globalRef = 0L;
        }
    }
    
    private int compare(final long ptr1, final long ptr2) {
        final NativeSlice s1 = new NativeSlice();
        s1.read(ptr1, 0);
        final NativeSlice s2 = new NativeSlice();
        s2.read(ptr2, 0);
        return this.compare(s1.toByteArray(), s2.toByteArray());
    }
    
    public abstract int compare(final byte[] p0, final byte[] p1);
    
    public abstract String name();
    
    static {
        BYTEWISE_COMPARATOR = new NativeComparator(ComparatorJNI.BYTEWISE_COMPARATOR) {
            @Override
            public void delete() {
            }
            
            @Override
            public int compare(final byte[] key1, final byte[] key2) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public String name() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    @JniClass(name = "JNIComparator", flags = { ClassFlag.STRUCT, ClassFlag.CPP })
    public static class ComparatorJNI
    {
        @JniField(cast = "jobject", flags = { FieldFlag.POINTER_FIELD })
        long target;
        @JniField(cast = "jmethodID", flags = { FieldFlag.POINTER_FIELD })
        long compare_method;
        @JniField(cast = "const char *")
        long name;
        @JniField(flags = { FieldFlag.CONSTANT }, accessor = "sizeof(struct JNIComparator)")
        static int SIZEOF;
        @JniField(flags = { FieldFlag.CONSTANT }, cast = "const Comparator*", accessor = "leveldb::BytewiseComparator()")
        private static long BYTEWISE_COMPARATOR;
        
        @JniMethod(flags = { MethodFlag.CPP_NEW })
        public static final native long create();
        
        @JniMethod(flags = { MethodFlag.CPP_DELETE })
        public static final native void delete(final long p0);
        
        public static final native void memmove(@JniArg(cast = "void *") final long p0, @JniArg(cast = "const void *", flags = { ArgFlag.NO_OUT, ArgFlag.CRITICAL }) final ComparatorJNI p1, @JniArg(cast = "size_t") final long p2);
        
        public static final native void memmove(@JniArg(cast = "void *", flags = { ArgFlag.NO_IN, ArgFlag.CRITICAL }) final ComparatorJNI p0, @JniArg(cast = "const void *") final long p1, @JniArg(cast = "size_t") final long p2);
        
        @JniMethod(flags = { MethodFlag.CONSTANT_INITIALIZER })
        private static final native void init();
        
        static {
            NativeDB.LIBRARY.load();
            init();
        }
    }
}
