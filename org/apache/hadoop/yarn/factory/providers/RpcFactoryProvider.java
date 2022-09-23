// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.factory.providers;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.factories.RpcClientFactory;
import org.apache.hadoop.yarn.factories.RpcServerFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "MapReduce", "YARN" })
public class RpcFactoryProvider
{
    private RpcFactoryProvider() {
    }
    
    public static RpcServerFactory getServerFactory(Configuration conf) {
        if (conf == null) {
            conf = new Configuration();
        }
        final String serverFactoryClassName = conf.get("yarn.ipc.server.factory.class", "org.apache.hadoop.yarn.factories.impl.pb.RpcServerFactoryPBImpl");
        return (RpcServerFactory)getFactoryClassInstance(serverFactoryClassName);
    }
    
    public static RpcClientFactory getClientFactory(final Configuration conf) {
        final String clientFactoryClassName = conf.get("yarn.ipc.client.factory.class", "org.apache.hadoop.yarn.factories.impl.pb.RpcClientFactoryPBImpl");
        return (RpcClientFactory)getFactoryClassInstance(clientFactoryClassName);
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
}
