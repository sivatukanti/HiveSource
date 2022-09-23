// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.util;

import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.StatusReporter;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.conf.Configuration;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;

public class ContextUtil
{
    private static final boolean useV21;
    private static final Constructor<?> JOB_CONTEXT_CONSTRUCTOR;
    private static final Constructor<?> TASK_CONTEXT_CONSTRUCTOR;
    private static final Constructor<?> MAP_CONTEXT_CONSTRUCTOR;
    private static final Constructor<?> MAP_CONTEXT_IMPL_CONSTRUCTOR;
    private static final Constructor<?> GENERIC_COUNTER_CONSTRUCTOR;
    private static final Field READER_FIELD;
    private static final Field WRITER_FIELD;
    private static final Field OUTER_MAP_FIELD;
    private static final Field WRAPPED_CONTEXT_FIELD;
    private static final Method GET_CONFIGURATION_METHOD;
    private static final Method GET_COUNTER_METHOD;
    private static final Method INCREMENT_COUNTER_METHOD;
    
    public static JobContext newJobContext(final Configuration conf, final JobID jobId) {
        try {
            return (JobContext)ContextUtil.JOB_CONTEXT_CONSTRUCTOR.newInstance(conf, jobId);
        }
        catch (InstantiationException e) {
            throw new IllegalArgumentException("Can't instantiate JobContext", e);
        }
        catch (IllegalAccessException e2) {
            throw new IllegalArgumentException("Can't instantiate JobContext", e2);
        }
        catch (InvocationTargetException e3) {
            throw new IllegalArgumentException("Can't instantiate JobContext", e3);
        }
    }
    
    public static TaskAttemptContext newTaskAttemptContext(final Configuration conf, final TaskAttemptID taskAttemptId) {
        try {
            return (TaskAttemptContext)ContextUtil.TASK_CONTEXT_CONSTRUCTOR.newInstance(conf, taskAttemptId);
        }
        catch (InstantiationException e) {
            throw new IllegalArgumentException("Can't instantiate TaskAttemptContext", e);
        }
        catch (IllegalAccessException e2) {
            throw new IllegalArgumentException("Can't instantiate TaskAttemptContext", e2);
        }
        catch (InvocationTargetException e3) {
            throw new IllegalArgumentException("Can't instantiate TaskAttemptContext", e3);
        }
    }
    
    public static Counter newGenericCounter(final String name, final String displayName, final long value) {
        try {
            return (Counter)ContextUtil.GENERIC_COUNTER_CONSTRUCTOR.newInstance(name, displayName, value);
        }
        catch (InstantiationException e) {
            throw new IllegalArgumentException("Can't instantiate Counter", e);
        }
        catch (IllegalAccessException e2) {
            throw new IllegalArgumentException("Can't instantiate Counter", e2);
        }
        catch (InvocationTargetException e3) {
            throw new IllegalArgumentException("Can't instantiate Counter", e3);
        }
    }
    
    public static Configuration getConfiguration(final JobContext context) {
        try {
            return (Configuration)ContextUtil.GET_CONFIGURATION_METHOD.invoke(context, new Object[0]);
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Can't invoke method", e);
        }
        catch (InvocationTargetException e2) {
            throw new IllegalArgumentException("Can't invoke method", e2);
        }
    }
    
    public static Counter getCounter(final TaskInputOutputContext context, final String groupName, final String counterName) {
        return (Counter)invoke(ContextUtil.GET_COUNTER_METHOD, context, groupName, counterName);
    }
    
    private static Object invoke(final Method method, final Object obj, final Object... args) {
        try {
            return method.invoke(obj, args);
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Can't invoke method " + method.getName(), e);
        }
        catch (InvocationTargetException e2) {
            throw new IllegalArgumentException("Can't invoke method " + method.getName(), e2);
        }
    }
    
    public static void incrementCounter(final Counter counter, final long increment) {
        invoke(ContextUtil.INCREMENT_COUNTER_METHOD, counter, increment);
    }
    
    static {
        boolean v21 = true;
        final String PACKAGE = "org.apache.hadoop.mapreduce";
        try {
            Class.forName("org.apache.hadoop.mapreduce.task.JobContextImpl");
        }
        catch (ClassNotFoundException cnfe) {
            v21 = false;
        }
        useV21 = v21;
        Class<?> jobContextCls;
        Class<?> taskContextCls;
        Class<?> taskIOContextCls;
        Class<?> mapContextCls;
        Class<?> mapCls;
        Class<?> innerMapContextCls;
        Class<?> genericCounterCls;
        try {
            if (v21) {
                jobContextCls = Class.forName("org.apache.hadoop.mapreduce.task.JobContextImpl");
                taskContextCls = Class.forName("org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl");
                taskIOContextCls = Class.forName("org.apache.hadoop.mapreduce.task.TaskInputOutputContextImpl");
                mapContextCls = Class.forName("org.apache.hadoop.mapreduce.task.MapContextImpl");
                mapCls = Class.forName("org.apache.hadoop.mapreduce.lib.map.WrappedMapper");
                innerMapContextCls = Class.forName("org.apache.hadoop.mapreduce.lib.map.WrappedMapper$Context");
                genericCounterCls = Class.forName("org.apache.hadoop.mapreduce.counters.GenericCounter");
            }
            else {
                jobContextCls = Class.forName("org.apache.hadoop.mapreduce.JobContext");
                taskContextCls = Class.forName("org.apache.hadoop.mapreduce.TaskAttemptContext");
                taskIOContextCls = Class.forName("org.apache.hadoop.mapreduce.TaskInputOutputContext");
                mapContextCls = Class.forName("org.apache.hadoop.mapreduce.MapContext");
                mapCls = Class.forName("org.apache.hadoop.mapreduce.Mapper");
                innerMapContextCls = Class.forName("org.apache.hadoop.mapreduce.Mapper$Context");
                genericCounterCls = Class.forName("org.apache.hadoop.mapred.Counters$Counter");
            }
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Can't find class", e);
        }
        try {
            (JOB_CONTEXT_CONSTRUCTOR = jobContextCls.getConstructor(Configuration.class, JobID.class)).setAccessible(true);
            (TASK_CONTEXT_CONSTRUCTOR = taskContextCls.getConstructor(Configuration.class, TaskAttemptID.class)).setAccessible(true);
            (GENERIC_COUNTER_CONSTRUCTOR = genericCounterCls.getDeclaredConstructor(String.class, String.class, Long.TYPE)).setAccessible(true);
            if (ContextUtil.useV21) {
                MAP_CONTEXT_CONSTRUCTOR = innerMapContextCls.getConstructor(mapCls, MapContext.class);
                (MAP_CONTEXT_IMPL_CONSTRUCTOR = mapContextCls.getDeclaredConstructor(Configuration.class, TaskAttemptID.class, RecordReader.class, RecordWriter.class, OutputCommitter.class, StatusReporter.class, InputSplit.class)).setAccessible(true);
                (WRAPPED_CONTEXT_FIELD = innerMapContextCls.getDeclaredField("mapContext")).setAccessible(true);
                Method get_counter_method;
                try {
                    get_counter_method = Class.forName("org.apache.hadoop.mapreduce.TaskAttemptContext").getMethod("getCounter", String.class, String.class);
                }
                catch (Exception e5) {
                    get_counter_method = Class.forName("org.apache.hadoop.mapreduce.TaskInputOutputContext").getMethod("getCounter", String.class, String.class);
                }
                GET_COUNTER_METHOD = get_counter_method;
            }
            else {
                MAP_CONTEXT_CONSTRUCTOR = innerMapContextCls.getConstructor(mapCls, Configuration.class, TaskAttemptID.class, RecordReader.class, RecordWriter.class, OutputCommitter.class, StatusReporter.class, InputSplit.class);
                MAP_CONTEXT_IMPL_CONSTRUCTOR = null;
                WRAPPED_CONTEXT_FIELD = null;
                GET_COUNTER_METHOD = taskIOContextCls.getMethod("getCounter", String.class, String.class);
            }
            ContextUtil.MAP_CONTEXT_CONSTRUCTOR.setAccessible(true);
            (READER_FIELD = mapContextCls.getDeclaredField("reader")).setAccessible(true);
            (WRITER_FIELD = taskIOContextCls.getDeclaredField("output")).setAccessible(true);
            (OUTER_MAP_FIELD = innerMapContextCls.getDeclaredField("this$0")).setAccessible(true);
            GET_CONFIGURATION_METHOD = Class.forName("org.apache.hadoop.mapreduce.JobContext").getMethod("getConfiguration", (Class<?>[])new Class[0]);
            INCREMENT_COUNTER_METHOD = Class.forName("org.apache.hadoop.mapreduce.Counter").getMethod("increment", Long.TYPE);
        }
        catch (SecurityException e2) {
            throw new IllegalArgumentException("Can't run constructor ", e2);
        }
        catch (NoSuchMethodException e3) {
            throw new IllegalArgumentException("Can't find constructor ", e3);
        }
        catch (NoSuchFieldException e4) {
            throw new IllegalArgumentException("Can't find field ", e4);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Can't find class", e);
        }
    }
}
