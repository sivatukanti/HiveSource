// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public final class NativeCodeLoader
{
    private static final Logger LOG;
    private static boolean nativeCodeLoaded;
    
    private NativeCodeLoader() {
    }
    
    public static boolean isNativeCodeLoaded() {
        return NativeCodeLoader.nativeCodeLoaded;
    }
    
    public static native boolean buildSupportsSnappy();
    
    public static native boolean buildSupportsIsal();
    
    public static native boolean buildSupportsZstd();
    
    public static native boolean buildSupportsOpenssl();
    
    public static native String getLibraryName();
    
    static {
        LOG = LoggerFactory.getLogger(NativeCodeLoader.class);
        NativeCodeLoader.nativeCodeLoaded = false;
        if (NativeCodeLoader.LOG.isDebugEnabled()) {
            NativeCodeLoader.LOG.debug("Trying to load the custom-built native-hadoop library...");
        }
        try {
            System.loadLibrary("hadoop");
            NativeCodeLoader.LOG.debug("Loaded the native-hadoop library");
            NativeCodeLoader.nativeCodeLoaded = true;
        }
        catch (Throwable t) {
            if (NativeCodeLoader.LOG.isDebugEnabled()) {
                NativeCodeLoader.LOG.debug("Failed to load native-hadoop with error: " + t);
                NativeCodeLoader.LOG.debug("java.library.path=" + System.getProperty("java.library.path"));
            }
        }
        if (!NativeCodeLoader.nativeCodeLoaded) {
            NativeCodeLoader.LOG.warn("Unable to load native-hadoop library for your platform... using builtin-java classes where applicable");
        }
    }
}
