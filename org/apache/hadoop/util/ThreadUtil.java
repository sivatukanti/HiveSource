// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Evolving
public class ThreadUtil
{
    private static final Logger LOG;
    
    public static void sleepAtLeastIgnoreInterrupts(final long millis) {
        final long start = Time.now();
        while (Time.now() - start < millis) {
            final long timeToSleep = millis - (Time.now() - start);
            try {
                Thread.sleep(timeToSleep);
            }
            catch (InterruptedException ie) {
                ThreadUtil.LOG.warn("interrupted while sleeping", ie);
            }
        }
    }
    
    public static InputStream getResourceAsStream(final String resourceName) throws IOException {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            throw new IOException("Can not read resource file '" + resourceName + "' because class loader of the current thread is null");
        }
        return getResourceAsStream(cl, resourceName);
    }
    
    public static InputStream getResourceAsStream(final ClassLoader cl, final String resourceName) throws IOException {
        if (cl == null) {
            throw new IOException("Can not read resource file '" + resourceName + "' because given class loader is null");
        }
        final InputStream is = cl.getResourceAsStream(resourceName);
        if (is == null) {
            throw new IOException("Can not read resource file '" + resourceName + "'");
        }
        return is;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ThreadUtil.class);
    }
}
