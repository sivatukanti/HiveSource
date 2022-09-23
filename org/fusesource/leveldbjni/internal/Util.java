// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import org.fusesource.hawtjni.runtime.ArgFlag;
import org.fusesource.hawtjni.runtime.FieldFlag;
import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.MethodFlag;
import org.fusesource.hawtjni.runtime.JniArg;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.ClassFlag;
import org.fusesource.hawtjni.runtime.JniClass;
import java.io.IOException;
import java.io.File;

public class Util
{
    public static void link(final File source, final File target) throws IOException {
        if (UtilJNI.ON_WINDOWS == 1) {
            if (UtilJNI.CreateHardLinkW(target.getCanonicalPath(), source.getCanonicalPath(), 0L) == 0) {
                throw new IOException("link failed");
            }
        }
        else if (UtilJNI.link(source.getCanonicalPath(), target.getCanonicalPath()) != 0) {
            throw new IOException("link failed: " + strerror());
        }
    }
    
    static int errno() {
        return UtilJNI.errno();
    }
    
    static String strerror() {
        return string(UtilJNI.strerror(errno()));
    }
    
    static String string(final long ptr) {
        if (ptr == 0L) {
            return null;
        }
        return new String(new NativeSlice(ptr, UtilJNI.strlen(ptr)).toByteArray());
    }
    
    @JniClass(name = "leveldb::Env", flags = { ClassFlag.CPP })
    static class EnvJNI
    {
        @JniMethod(cast = "leveldb::Env *", accessor = "leveldb::Env::Default")
        public static final native long Default();
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        public static final native void Schedule(final long p0, @JniArg(cast = "void (*)(void*)") final long p1, @JniArg(cast = "void *") final long p2);
        
        static {
            NativeDB.LIBRARY.load();
        }
    }
    
    @JniClass(flags = { ClassFlag.CPP })
    static class UtilJNI
    {
        @JniField(flags = { FieldFlag.CONSTANT }, accessor = "1", conditional = "defined(_WIN32) || defined(_WIN64)")
        static int ON_WINDOWS;
        
        @JniMethod(flags = { MethodFlag.CONSTANT_INITIALIZER })
        private static final native void init();
        
        @JniMethod(conditional = "!defined(_WIN32) && !defined(_WIN64)")
        static final native int link(@JniArg(cast = "const char*") final String p0, @JniArg(cast = "const char*") final String p1);
        
        @JniMethod(conditional = "defined(_WIN32) || defined(_WIN64)")
        static final native int CreateHardLinkW(@JniArg(cast = "LPCWSTR", flags = { ArgFlag.POINTER_ARG, ArgFlag.UNICODE }) final String p0, @JniArg(cast = "LPCWSTR", flags = { ArgFlag.POINTER_ARG, ArgFlag.UNICODE }) final String p1, @JniArg(cast = "LPSECURITY_ATTRIBUTES", flags = { ArgFlag.POINTER_ARG }) final long p2);
        
        @JniMethod(flags = { MethodFlag.CONSTANT_GETTER })
        public static final native int errno();
        
        @JniMethod(cast = "char *")
        public static final native long strerror(final int p0);
        
        public static final native int strlen(@JniArg(cast = "const char *") final long p0);
        
        static {
            NativeDB.LIBRARY.load();
            init();
        }
    }
}
