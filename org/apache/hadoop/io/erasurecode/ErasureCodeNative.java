// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode;

import org.apache.hadoop.util.NativeCodeLoader;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public final class ErasureCodeNative
{
    private static final Logger LOG;
    private static final String LOADING_FAILURE_REASON;
    
    private ErasureCodeNative() {
    }
    
    public static boolean isNativeCodeLoaded() {
        return ErasureCodeNative.LOADING_FAILURE_REASON == null;
    }
    
    public static void checkNativeCodeLoaded() {
        if (ErasureCodeNative.LOADING_FAILURE_REASON != null) {
            throw new RuntimeException(ErasureCodeNative.LOADING_FAILURE_REASON);
        }
    }
    
    public static native void loadLibrary();
    
    public static native String getLibraryName();
    
    public static String getLoadingFailureReason() {
        return ErasureCodeNative.LOADING_FAILURE_REASON;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ErasureCodeNative.class.getName());
        if (!NativeCodeLoader.isNativeCodeLoaded()) {
            LOADING_FAILURE_REASON = "hadoop native library cannot be loaded.";
        }
        else if (!NativeCodeLoader.buildSupportsIsal()) {
            LOADING_FAILURE_REASON = "libhadoop was built without ISA-L support";
        }
        else {
            String problem = null;
            try {
                loadLibrary();
            }
            catch (Throwable t) {
                problem = "Loading ISA-L failed: " + t.getMessage();
                ErasureCodeNative.LOG.warn(problem);
            }
            LOADING_FAILURE_REASON = problem;
        }
        if (ErasureCodeNative.LOADING_FAILURE_REASON != null) {
            ErasureCodeNative.LOG.warn("ISA-L support is not available in your platform... using builtin-java codec where applicable");
        }
    }
}
