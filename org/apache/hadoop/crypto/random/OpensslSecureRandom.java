// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.random;

import org.apache.hadoop.util.NativeCodeLoader;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import org.apache.hadoop.util.PerformanceAdvisory;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;
import java.util.Random;

@InterfaceAudience.Private
public class OpensslSecureRandom extends Random
{
    private static final long serialVersionUID = -7828193502768789584L;
    private static final Logger LOG;
    private SecureRandom fallback;
    private static boolean nativeEnabled;
    
    public static boolean isNativeCodeLoaded() {
        return OpensslSecureRandom.nativeEnabled;
    }
    
    public OpensslSecureRandom() {
        this.fallback = null;
        if (!OpensslSecureRandom.nativeEnabled) {
            PerformanceAdvisory.LOG.debug("Build does not support openssl, falling back to Java SecureRandom.");
            this.fallback = new SecureRandom();
        }
    }
    
    @Override
    public void nextBytes(final byte[] bytes) {
        if (!OpensslSecureRandom.nativeEnabled || !this.nextRandBytes(bytes)) {
            this.fallback.nextBytes(bytes);
        }
    }
    
    @Override
    public void setSeed(final long seed) {
    }
    
    @Override
    protected final int next(final int numBits) {
        Preconditions.checkArgument(numBits >= 0 && numBits <= 32);
        final int numBytes = (numBits + 7) / 8;
        final byte[] b = new byte[numBytes];
        int next = 0;
        this.nextBytes(b);
        for (int i = 0; i < numBytes; ++i) {
            next = (next << 8) + (b[i] & 0xFF);
        }
        return next >>> numBytes * 8 - numBits;
    }
    
    private static native void initSR();
    
    private native boolean nextRandBytes(final byte[] p0);
    
    static {
        LOG = LoggerFactory.getLogger(OpensslSecureRandom.class.getName());
        OpensslSecureRandom.nativeEnabled = false;
        if (NativeCodeLoader.isNativeCodeLoaded() && NativeCodeLoader.buildSupportsOpenssl()) {
            try {
                initSR();
                OpensslSecureRandom.nativeEnabled = true;
            }
            catch (Throwable t) {
                OpensslSecureRandom.LOG.error("Failed to load Openssl SecureRandom", t);
            }
        }
    }
}
