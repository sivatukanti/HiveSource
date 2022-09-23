// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal;

import java.nio.ByteBuffer;
import java.lang.reflect.Method;

public final class ByteBufferUtil
{
    private static final boolean CLEAN_SUPPORTED;
    private static final Method directBufferCleaner;
    private static final Method directBufferCleanerClean;
    
    public static void destroy(final ByteBuffer buffer) {
        if (ByteBufferUtil.CLEAN_SUPPORTED && buffer.isDirect()) {
            try {
                final Object cleaner = ByteBufferUtil.directBufferCleaner.invoke(buffer, new Object[0]);
                ByteBufferUtil.directBufferCleanerClean.invoke(cleaner, new Object[0]);
            }
            catch (Exception ex) {}
        }
    }
    
    private ByteBufferUtil() {
    }
    
    static {
        Method directBufferCleanerX = null;
        Method directBufferCleanerCleanX = null;
        boolean v;
        try {
            directBufferCleanerX = Class.forName("java.nio.DirectByteBuffer").getMethod("cleaner", (Class<?>[])new Class[0]);
            directBufferCleanerX.setAccessible(true);
            directBufferCleanerCleanX = Class.forName("sun.misc.Cleaner").getMethod("clean", (Class<?>[])new Class[0]);
            directBufferCleanerCleanX.setAccessible(true);
            v = true;
        }
        catch (Exception e) {
            v = false;
        }
        CLEAN_SUPPORTED = v;
        directBufferCleaner = directBufferCleanerX;
        directBufferCleanerClean = directBufferCleanerCleanX;
    }
}
