// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.util.List;
import java.io.DataInput;
import java.io.DataOutput;
import org.apache.hadoop.io.Writable;
import java.io.IOException;
import org.apache.hadoop.io.serializer.Deserializer;
import org.apache.hadoop.io.serializer.Serializer;
import java.io.InputStream;
import org.slf4j.Logger;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import java.lang.management.ThreadInfo;
import java.io.PrintStream;
import java.lang.reflect.Method;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Constructor;
import java.util.Map;
import org.apache.hadoop.io.serializer.SerializationFactory;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class ReflectionUtils
{
    private static final Class<?>[] EMPTY_ARRAY;
    private static volatile SerializationFactory serialFactory;
    private static final Map<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE;
    private static ThreadMXBean threadBean;
    private static long previousLogTime;
    private static final ThreadLocal<CopyInCopyOutBuffer> CLONE_BUFFERS;
    
    public static void setConf(final Object theObject, final Configuration conf) {
        if (conf != null) {
            if (theObject instanceof Configurable) {
                ((Configurable)theObject).setConf(conf);
            }
            setJobConf(theObject, conf);
        }
    }
    
    private static void setJobConf(final Object theObject, final Configuration conf) {
        try {
            final Class<?> jobConfClass = conf.getClassByNameOrNull("org.apache.hadoop.mapred.JobConf");
            if (jobConfClass == null) {
                return;
            }
            final Class<?> jobConfigurableClass = conf.getClassByNameOrNull("org.apache.hadoop.mapred.JobConfigurable");
            if (jobConfigurableClass == null) {
                return;
            }
            if (jobConfClass.isAssignableFrom(conf.getClass()) && jobConfigurableClass.isAssignableFrom(theObject.getClass())) {
                final Method configureMethod = jobConfigurableClass.getMethod("configure", jobConfClass);
                configureMethod.invoke(theObject, conf);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error in configuring object", e);
        }
    }
    
    public static <T> T newInstance(final Class<T> theClass, final Configuration conf) {
        T result;
        try {
            Constructor<T> meth = (Constructor<T>)ReflectionUtils.CONSTRUCTOR_CACHE.get(theClass);
            if (meth == null) {
                meth = theClass.getDeclaredConstructor(ReflectionUtils.EMPTY_ARRAY);
                meth.setAccessible(true);
                ReflectionUtils.CONSTRUCTOR_CACHE.put(theClass, meth);
            }
            result = meth.newInstance(new Object[0]);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        setConf(result, conf);
        return result;
    }
    
    public static void setContentionTracing(final boolean val) {
        ReflectionUtils.threadBean.setThreadContentionMonitoringEnabled(val);
    }
    
    private static String getTaskName(final long id, final String name) {
        if (name == null) {
            return Long.toString(id);
        }
        return id + " (" + name + ")";
    }
    
    public static synchronized void printThreadInfo(final PrintStream stream, final String title) {
        final int STACK_DEPTH = 20;
        final boolean contention = ReflectionUtils.threadBean.isThreadContentionMonitoringEnabled();
        final long[] threadIds = ReflectionUtils.threadBean.getAllThreadIds();
        stream.println("Process Thread Dump: " + title);
        stream.println(threadIds.length + " active threads");
        for (final long tid : threadIds) {
            final ThreadInfo info = ReflectionUtils.threadBean.getThreadInfo(tid, 20);
            if (info == null) {
                stream.println("  Inactive");
            }
            else {
                stream.println("Thread " + getTaskName(info.getThreadId(), info.getThreadName()) + ":");
                final Thread.State state = info.getThreadState();
                stream.println("  State: " + state);
                stream.println("  Blocked count: " + info.getBlockedCount());
                stream.println("  Waited count: " + info.getWaitedCount());
                if (contention) {
                    stream.println("  Blocked time: " + info.getBlockedTime());
                    stream.println("  Waited time: " + info.getWaitedTime());
                }
                if (state == Thread.State.WAITING) {
                    stream.println("  Waiting on " + info.getLockName());
                }
                else if (state == Thread.State.BLOCKED) {
                    stream.println("  Blocked on " + info.getLockName());
                    stream.println("  Blocked by " + getTaskName(info.getLockOwnerId(), info.getLockOwnerName()));
                }
                stream.println("  Stack:");
                for (final StackTraceElement frame : info.getStackTrace()) {
                    stream.println("    " + frame.toString());
                }
            }
        }
        stream.flush();
    }
    
    public static void logThreadInfo(final Log log, final String title, final long minInterval) {
        boolean dumpStack = false;
        if (log.isInfoEnabled()) {
            synchronized (ReflectionUtils.class) {
                final long now = Time.monotonicNow();
                if (now - ReflectionUtils.previousLogTime >= minInterval * 1000L) {
                    ReflectionUtils.previousLogTime = now;
                    dumpStack = true;
                }
            }
            if (dumpStack) {
                try {
                    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    printThreadInfo(new PrintStream(buffer, false, "UTF-8"), title);
                    log.info(buffer.toString(Charset.defaultCharset().name()));
                }
                catch (UnsupportedEncodingException ex) {}
            }
        }
    }
    
    public static void logThreadInfo(final Logger log, final String title, final long minInterval) {
        boolean dumpStack = false;
        if (log.isInfoEnabled()) {
            synchronized (ReflectionUtils.class) {
                final long now = Time.monotonicNow();
                if (now - ReflectionUtils.previousLogTime >= minInterval * 1000L) {
                    ReflectionUtils.previousLogTime = now;
                    dumpStack = true;
                }
            }
            if (dumpStack) {
                try {
                    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    printThreadInfo(new PrintStream(buffer, false, "UTF-8"), title);
                    log.info(buffer.toString(Charset.defaultCharset().name()));
                }
                catch (UnsupportedEncodingException ex) {}
            }
        }
    }
    
    public static <T> Class<T> getClass(final T o) {
        return (Class<T>)o.getClass();
    }
    
    static void clearCache() {
        ReflectionUtils.CONSTRUCTOR_CACHE.clear();
    }
    
    static int getCacheSize() {
        return ReflectionUtils.CONSTRUCTOR_CACHE.size();
    }
    
    private static SerializationFactory getFactory(final Configuration conf) {
        if (ReflectionUtils.serialFactory == null) {
            ReflectionUtils.serialFactory = new SerializationFactory(conf);
        }
        return ReflectionUtils.serialFactory;
    }
    
    public static <T> T copy(final Configuration conf, final T src, T dst) throws IOException {
        final CopyInCopyOutBuffer buffer = ReflectionUtils.CLONE_BUFFERS.get();
        buffer.outBuffer.reset();
        final SerializationFactory factory = getFactory(conf);
        final Class<T> cls = (Class<T>)src.getClass();
        final Serializer<T> serializer = factory.getSerializer(cls);
        serializer.open(buffer.outBuffer);
        serializer.serialize(src);
        buffer.moveData();
        final Deserializer<T> deserializer = factory.getDeserializer(cls);
        deserializer.open(buffer.inBuffer);
        dst = deserializer.deserialize(dst);
        return dst;
    }
    
    @Deprecated
    public static void cloneWritableInto(final Writable dst, final Writable src) throws IOException {
        final CopyInCopyOutBuffer buffer = ReflectionUtils.CLONE_BUFFERS.get();
        buffer.outBuffer.reset();
        src.write(buffer.outBuffer);
        buffer.moveData();
        dst.readFields(buffer.inBuffer);
    }
    
    public static List<Field> getDeclaredFieldsIncludingInherited(Class<?> clazz) {
        final List<Field> fields = new ArrayList<Field>();
        while (clazz != null) {
            for (final Field field : clazz.getDeclaredFields()) {
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
    
    public static List<Method> getDeclaredMethodsIncludingInherited(Class<?> clazz) {
        final List<Method> methods = new ArrayList<Method>();
        while (clazz != null) {
            for (final Method method : clazz.getDeclaredMethods()) {
                methods.add(method);
            }
            clazz = clazz.getSuperclass();
        }
        return methods;
    }
    
    static {
        EMPTY_ARRAY = new Class[0];
        ReflectionUtils.serialFactory = null;
        CONSTRUCTOR_CACHE = new ConcurrentHashMap<Class<?>, Constructor<?>>();
        ReflectionUtils.threadBean = ManagementFactory.getThreadMXBean();
        ReflectionUtils.previousLogTime = 0L;
        CLONE_BUFFERS = new ThreadLocal<CopyInCopyOutBuffer>() {
            @Override
            protected synchronized CopyInCopyOutBuffer initialValue() {
                return new CopyInCopyOutBuffer();
            }
        };
    }
    
    private static class CopyInCopyOutBuffer
    {
        DataOutputBuffer outBuffer;
        DataInputBuffer inBuffer;
        
        private CopyInCopyOutBuffer() {
            this.outBuffer = new DataOutputBuffer();
            this.inBuffer = new DataInputBuffer();
        }
        
        void moveData() {
            this.inBuffer.reset(this.outBuffer.getData(), this.outBuffer.getLength());
        }
    }
}
