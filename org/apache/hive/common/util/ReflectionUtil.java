// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.common.util;

import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import com.google.common.cache.Cache;

public class ReflectionUtil
{
    private static final Cache<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE;
    private static final Class<?>[] EMPTY_ARRAY;
    private static final Class<?> jobConfClass;
    private static final Class<?> jobConfigurableClass;
    private static final Method configureMethod;
    
    public static <T> T newInstance(final Class<T> theClass, final Configuration conf) {
        T result;
        try {
            Constructor<?> ctor = ReflectionUtil.CONSTRUCTOR_CACHE.getIfPresent(theClass);
            if (ctor == null) {
                ctor = theClass.getDeclaredConstructor(ReflectionUtil.EMPTY_ARRAY);
                ctor.setAccessible(true);
                ReflectionUtil.CONSTRUCTOR_CACHE.put(theClass, ctor);
            }
            result = (T)ctor.newInstance(new Object[0]);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        setConf(result, conf);
        return result;
    }
    
    public static void setConf(final Object theObject, final Configuration conf) {
        if (conf != null) {
            if (theObject instanceof Configurable) {
                ((Configurable)theObject).setConf(conf);
            }
            setJobConf(theObject, conf);
        }
    }
    
    private static void setJobConf(final Object theObject, final Configuration conf) {
        if (ReflectionUtil.configureMethod == null) {
            return;
        }
        try {
            if (ReflectionUtil.jobConfClass.isAssignableFrom(conf.getClass()) && ReflectionUtil.jobConfigurableClass.isAssignableFrom(theObject.getClass())) {
                ReflectionUtil.configureMethod.invoke(theObject, conf);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error in configuring object", e);
        }
    }
    
    static {
        CONSTRUCTOR_CACHE = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.MINUTES).concurrencyLevel(64).weakKeys().weakValues().build();
        EMPTY_ARRAY = new Class[0];
        Class<?> jobConfClassLocal;
        Class<?> jobConfigurableClassLocal;
        Method configureMethodLocal;
        try {
            jobConfClassLocal = Class.forName("org.apache.hadoop.mapred.JobConf");
            jobConfigurableClassLocal = Class.forName("org.apache.hadoop.mapred.JobConfigurable");
            configureMethodLocal = jobConfigurableClassLocal.getMethod("configure", jobConfClassLocal);
        }
        catch (Throwable t) {
            jobConfigurableClassLocal = (jobConfClassLocal = null);
            configureMethodLocal = null;
        }
        jobConfClass = jobConfClassLocal;
        jobConfigurableClass = jobConfigurableClassLocal;
        configureMethod = configureMethodLocal;
    }
}
