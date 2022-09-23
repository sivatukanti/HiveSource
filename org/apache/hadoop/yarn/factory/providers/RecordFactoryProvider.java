// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.factory.providers;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "MapReduce", "YARN" })
@InterfaceStability.Unstable
public class RecordFactoryProvider
{
    private static Configuration defaultConf;
    
    private RecordFactoryProvider() {
    }
    
    public static RecordFactory getRecordFactory(Configuration conf) {
        if (conf == null) {
            conf = RecordFactoryProvider.defaultConf;
        }
        final String recordFactoryClassName = conf.get("yarn.ipc.record.factory.class", "org.apache.hadoop.yarn.factories.impl.pb.RecordFactoryPBImpl");
        return (RecordFactory)getFactoryClassInstance(recordFactoryClassName);
    }
    
    private static Object getFactoryClassInstance(final String factoryClassName) {
        try {
            final Class<?> clazz = Class.forName(factoryClassName);
            final Method method = clazz.getMethod("get", (Class<?>[])null);
            method.setAccessible(true);
            return method.invoke(null, (Object[])null);
        }
        catch (ClassNotFoundException e) {
            throw new YarnRuntimeException(e);
        }
        catch (NoSuchMethodException e2) {
            throw new YarnRuntimeException(e2);
        }
        catch (InvocationTargetException e3) {
            throw new YarnRuntimeException(e3);
        }
        catch (IllegalAccessException e4) {
            throw new YarnRuntimeException(e4);
        }
    }
    
    static {
        RecordFactoryProvider.defaultConf = new Configuration();
    }
}
