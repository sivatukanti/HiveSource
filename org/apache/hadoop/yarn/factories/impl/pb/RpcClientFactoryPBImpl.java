// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.factories.impl.pb;

import org.apache.commons.logging.LogFactory;
import java.lang.reflect.InvocationHandler;
import org.apache.hadoop.HadoopIllegalArgumentException;
import java.lang.reflect.Proxy;
import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.factories.RpcClientFactory;

@InterfaceAudience.Private
public class RpcClientFactoryPBImpl implements RpcClientFactory
{
    private static final Log LOG;
    private static final String PB_IMPL_PACKAGE_SUFFIX = "impl.pb.client";
    private static final String PB_IMPL_CLASS_SUFFIX = "PBClientImpl";
    private static final RpcClientFactoryPBImpl self;
    private Configuration localConf;
    private ConcurrentMap<Class<?>, Constructor<?>> cache;
    
    public static RpcClientFactoryPBImpl get() {
        return RpcClientFactoryPBImpl.self;
    }
    
    private RpcClientFactoryPBImpl() {
        this.localConf = new Configuration();
        this.cache = new ConcurrentHashMap<Class<?>, Constructor<?>>();
    }
    
    @Override
    public Object getClient(final Class<?> protocol, final long clientVersion, final InetSocketAddress addr, final Configuration conf) {
        Constructor<?> constructor = this.cache.get(protocol);
        if (constructor == null) {
            Class<?> pbClazz = null;
            try {
                pbClazz = this.localConf.getClassByName(this.getPBImplClassName(protocol));
            }
            catch (ClassNotFoundException e) {
                throw new YarnRuntimeException("Failed to load class: [" + this.getPBImplClassName(protocol) + "]", e);
            }
            try {
                constructor = pbClazz.getConstructor(Long.TYPE, InetSocketAddress.class, Configuration.class);
                constructor.setAccessible(true);
                this.cache.putIfAbsent(protocol, constructor);
            }
            catch (NoSuchMethodException e2) {
                throw new YarnRuntimeException("Could not find constructor with params: " + Long.TYPE + ", " + InetSocketAddress.class + ", " + Configuration.class, e2);
            }
        }
        try {
            final Object retObject = constructor.newInstance(clientVersion, addr, conf);
            return retObject;
        }
        catch (InvocationTargetException e3) {
            throw new YarnRuntimeException(e3);
        }
        catch (IllegalAccessException e4) {
            throw new YarnRuntimeException(e4);
        }
        catch (InstantiationException e5) {
            throw new YarnRuntimeException(e5);
        }
    }
    
    @Override
    public void stopClient(final Object proxy) {
        try {
            if (proxy instanceof Closeable) {
                ((Closeable)proxy).close();
                return;
            }
            final InvocationHandler handler = Proxy.getInvocationHandler(proxy);
            if (handler instanceof Closeable) {
                ((Closeable)handler).close();
                return;
            }
        }
        catch (Exception e) {
            RpcClientFactoryPBImpl.LOG.error("Cannot call close method due to Exception. Ignoring.", e);
            throw new YarnRuntimeException(e);
        }
        throw new HadoopIllegalArgumentException("Cannot close proxy - is not Closeable or does not provide closeable invocation handler " + proxy.getClass());
    }
    
    private String getPBImplClassName(final Class<?> clazz) {
        final String srcPackagePart = this.getPackageName(clazz);
        final String srcClassName = this.getClassName(clazz);
        final String destPackagePart = srcPackagePart + "." + "impl.pb.client";
        final String destClassPart = srcClassName + "PBClientImpl";
        return destPackagePart + "." + destClassPart;
    }
    
    private String getClassName(final Class<?> clazz) {
        final String fqName = clazz.getName();
        return fqName.substring(fqName.lastIndexOf(".") + 1, fqName.length());
    }
    
    private String getPackageName(final Class<?> clazz) {
        return clazz.getPackage().getName();
    }
    
    static {
        LOG = LogFactory.getLog(RpcClientFactoryPBImpl.class);
        self = new RpcClientFactoryPBImpl();
    }
}
