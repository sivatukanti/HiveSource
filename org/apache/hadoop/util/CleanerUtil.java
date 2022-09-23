// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.io.IOException;
import java.security.AccessController;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.invoke.MethodHandle;
import java.util.Objects;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;
import java.lang.invoke.MethodHandles;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public final class CleanerUtil
{
    public static final boolean UNMAP_SUPPORTED;
    public static final String UNMAP_NOT_SUPPORTED_REASON;
    private static final BufferCleaner CLEANER;
    
    private CleanerUtil() {
    }
    
    public static BufferCleaner getCleaner() {
        return CleanerUtil.CLEANER;
    }
    
    private static Object unmapHackImpl() {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            try {
                final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                final MethodHandle unmapper = lookup.findVirtual(unsafeClass, "invokeCleaner", MethodType.methodType(Void.TYPE, ByteBuffer.class));
                final Field f = unsafeClass.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                final Object theUnsafe = f.get(null);
                return newBufferCleaner(ByteBuffer.class, unmapper.bindTo(theUnsafe));
            }
            catch (SecurityException se) {
                throw se;
            }
            catch (ReflectiveOperationException | RuntimeException ex3) {
                final Exception ex;
                final Exception e = ex;
                final Class<?> directBufferClass = Class.forName("java.nio.DirectByteBuffer");
                final Method m = directBufferClass.getMethod("cleaner", (Class<?>[])new Class[0]);
                m.setAccessible(true);
                final MethodHandle directBufferCleanerMethod = lookup.unreflect(m);
                final Class<?> cleanerClass = directBufferCleanerMethod.type().returnType();
                final MethodHandle cleanMethod = lookup.findVirtual(cleanerClass, "clean", MethodType.methodType(Void.TYPE));
                final MethodHandle nonNullTest = lookup.findStatic(Objects.class, "nonNull", MethodType.methodType(Boolean.TYPE, Object.class)).asType(MethodType.methodType(Boolean.TYPE, cleanerClass));
                final MethodHandle noop = MethodHandles.dropArguments(MethodHandles.constant(Void.class, null).asType(MethodType.methodType(Void.TYPE)), 0, cleanerClass);
                final MethodHandle unmapper2 = MethodHandles.filterReturnValue(directBufferCleanerMethod, MethodHandles.guardWithTest(nonNullTest, cleanMethod, noop)).asType(MethodType.methodType(Void.TYPE, ByteBuffer.class));
                return newBufferCleaner(directBufferClass, unmapper2);
            }
            catch (ReflectiveOperationException ex4) {
                final RuntimeException ex2;
                final Exception e = ex2;
                return "Unmapping is not supported on this platform, because internal Java APIs are not compatible with this Hadoop version: " + e;
            }
        }
        catch (SecurityException ex5) {}
        catch (RuntimeException ex2) {}
    }
    
    private static BufferCleaner newBufferCleaner(final Class<?> unmappableBufferClass, final MethodHandle unmapper) {
        assert Objects.equals(MethodType.methodType(Void.TYPE, ByteBuffer.class), unmapper.type());
        final IllegalArgumentException ex;
        Throwable error;
        return buffer -> {
            if (!buffer.isDirect()) {
                throw new IllegalArgumentException("unmapping only works with direct buffers");
            }
            else if (!unmappableBufferClass.isInstance(buffer)) {
                new IllegalArgumentException("buffer is not an instance of " + unmappableBufferClass.getName());
                throw ex;
            }
            else {
                error = AccessController.doPrivileged(() -> {
                    try {
                        unmapper.invokeExact(buffer);
                        return null;
                    }
                    catch (Throwable t) {
                        return t;
                    }
                });
                if (error != null) {
                    throw new IOException("Unable to unmap the mapped buffer", error);
                }
            }
        };
    }
    
    static {
        final Object hack = AccessController.doPrivileged(CleanerUtil::unmapHackImpl);
        if (hack instanceof BufferCleaner) {
            CLEANER = (BufferCleaner)hack;
            UNMAP_SUPPORTED = true;
            UNMAP_NOT_SUPPORTED_REASON = null;
        }
        else {
            CLEANER = null;
            UNMAP_SUPPORTED = false;
            UNMAP_NOT_SUPPORTED_REASON = hack.toString();
        }
    }
    
    @FunctionalInterface
    public interface BufferCleaner
    {
        void freeBuffer(final ByteBuffer p0) throws IOException;
    }
}
