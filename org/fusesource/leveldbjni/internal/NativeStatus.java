// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import org.fusesource.hawtjni.runtime.MethodFlag;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.ClassFlag;
import org.fusesource.hawtjni.runtime.JniClass;

class NativeStatus extends NativeObject
{
    public NativeStatus(final long self) {
        super(self);
    }
    
    public void delete() {
        this.assertAllocated();
        StatusJNI.delete(this.self);
        this.self = 0L;
    }
    
    public boolean isOk() {
        this.assertAllocated();
        return StatusJNI.ok(this.self);
    }
    
    public boolean isNotFound() {
        this.assertAllocated();
        return StatusJNI.IsNotFound(this.self);
    }
    
    @Override
    public String toString() {
        this.assertAllocated();
        final long strptr = StatusJNI.ToString(this.self);
        if (strptr == 0L) {
            return null;
        }
        final NativeStdString rc = new NativeStdString(strptr);
        try {
            return rc.toString();
        }
        finally {
            rc.delete();
        }
    }
    
    @JniClass(name = "leveldb::Status", flags = { ClassFlag.CPP })
    static class StatusJNI
    {
        @JniMethod(flags = { MethodFlag.CPP_DELETE })
        public static final native void delete(final long p0);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        public static final native boolean ok(final long p0);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        public static final native boolean IsNotFound(final long p0);
        
        @JniMethod(copy = "std::string", flags = { MethodFlag.CPP_METHOD })
        public static final native long ToString(final long p0);
        
        static {
            NativeDB.LIBRARY.load();
        }
    }
}
