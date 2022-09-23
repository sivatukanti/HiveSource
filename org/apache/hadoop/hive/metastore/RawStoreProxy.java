// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.ClassUtils;
import java.lang.reflect.Proxy;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.common.classification.InterfaceStability;
import org.apache.hadoop.hive.common.classification.InterfaceAudience;
import java.lang.reflect.InvocationHandler;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class RawStoreProxy implements InvocationHandler
{
    private final RawStore base;
    private final MetaStoreInit.MetaStoreInitData metaStoreInitData;
    private final int id;
    private final HiveConf hiveConf;
    private final Configuration conf;
    
    protected RawStoreProxy(final HiveConf hiveConf, final Configuration conf, final Class<? extends RawStore> rawStoreClass, final int id) throws MetaException {
        this.metaStoreInitData = new MetaStoreInit.MetaStoreInitData();
        this.conf = conf;
        this.hiveConf = hiveConf;
        this.id = id;
        this.init();
        this.base = ReflectionUtils.newInstance(rawStoreClass, conf);
    }
    
    public static RawStore getProxy(final HiveConf hiveConf, final Configuration conf, final String rawStoreClassName, final int id) throws MetaException {
        final Class<? extends RawStore> baseClass = (Class<? extends RawStore>)MetaStoreUtils.getClass(rawStoreClassName);
        final RawStoreProxy handler = new RawStoreProxy(hiveConf, conf, baseClass, id);
        return (RawStore)Proxy.newProxyInstance(RawStoreProxy.class.getClassLoader(), getAllInterfaces(baseClass), handler);
    }
    
    private static Class<?>[] getAllInterfaces(final Class<?> baseClass) {
        final List interfaces = ClassUtils.getAllInterfaces(baseClass);
        final Class<?>[] result = (Class<?>[])new Class[interfaces.size()];
        int i = 0;
        for (final Object o : interfaces) {
            result[i++] = (Class<?>)o;
        }
        return result;
    }
    
    private void init() throws MetaException {
        MetaStoreInit.updateConnectionURL(this.hiveConf, this.getConf(), null, this.metaStoreInitData);
    }
    
    private void initMS() {
        this.base.setConf(this.getConf());
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        Object ret = null;
        boolean isTimerStarted = false;
        try {
            try {
                if (!Deadline.isStarted()) {
                    Deadline.startTimer(method.getName());
                    isTimerStarted = true;
                }
            }
            catch (MetaException e3) {
                final long timeout = HiveConf.getTimeVar(this.hiveConf, HiveConf.ConfVars.METASTORE_CLIENT_SOCKET_TIMEOUT, TimeUnit.MILLISECONDS);
                Deadline.registerIfNot(timeout);
                Deadline.startTimer(method.getName());
                isTimerStarted = true;
            }
            ret = method.invoke(this.base, args);
            if (isTimerStarted) {
                Deadline.stopTimer();
            }
        }
        catch (UndeclaredThrowableException e) {
            throw e.getCause();
        }
        catch (InvocationTargetException e2) {
            throw e2.getCause();
        }
        return ret;
    }
    
    public Configuration getConf() {
        return this.conf;
    }
}
