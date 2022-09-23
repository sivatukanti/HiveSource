// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

public final class DetectionUtil
{
    private static final int JAVA_VERSION;
    private static final boolean HAS_UNSAFE;
    private static final boolean IS_WINDOWS;
    
    public static boolean isWindows() {
        return DetectionUtil.IS_WINDOWS;
    }
    
    public static boolean hasUnsafe() {
        return DetectionUtil.HAS_UNSAFE;
    }
    
    public static int javaVersion() {
        return DetectionUtil.JAVA_VERSION;
    }
    
    private static boolean hasUnsafe(final ClassLoader loader) {
        final boolean noUnsafe = SystemPropertyUtil.getBoolean("io.netty.noUnsafe", false);
        if (noUnsafe) {
            return false;
        }
        boolean tryUnsafe;
        if (SystemPropertyUtil.contains("io.netty.tryUnsafe")) {
            tryUnsafe = SystemPropertyUtil.getBoolean("io.netty.tryUnsafe", true);
        }
        else {
            tryUnsafe = SystemPropertyUtil.getBoolean("org.jboss.netty.tryUnsafe", true);
        }
        if (!tryUnsafe) {
            return false;
        }
        try {
            final Class<?> unsafeClazz = Class.forName("sun.misc.Unsafe", true, loader);
            return hasUnsafeField(unsafeClazz);
        }
        catch (Exception e) {
            return false;
        }
    }
    
    private static boolean hasUnsafeField(final Class<?> unsafeClass) throws PrivilegedActionException {
        return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)new PrivilegedExceptionAction<Boolean>() {
            public Boolean run() throws Exception {
                unsafeClass.getDeclaredField("theUnsafe");
                return true;
            }
        });
    }
    
    private static int javaVersion0() {
        try {
            Class.forName("android.app.Application");
            return 6;
        }
        catch (ClassNotFoundException e) {
            try {
                Class.forName("java.util.concurrent.LinkedTransferQueue", false, BlockingQueue.class.getClassLoader());
                return 7;
            }
            catch (Exception e2) {
                try {
                    Class.forName("java.util.ArrayDeque", false, Queue.class.getClassLoader());
                    return 6;
                }
                catch (Exception e2) {
                    return 5;
                }
            }
        }
    }
    
    private DetectionUtil() {
    }
    
    static {
        JAVA_VERSION = javaVersion0();
        HAS_UNSAFE = hasUnsafe(AtomicInteger.class.getClassLoader());
        final String os = SystemPropertyUtil.get("os.name", "").toLowerCase();
        IS_WINDOWS = os.contains("win");
    }
}
