// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import org.apache.hadoop.security.UserGroupInformation;
import java.lang.reflect.InvocationTargetException;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.TApplicationException;
import java.lang.reflect.UndeclaredThrowableException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.conf.HiveConf;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.common.classification.InterfaceAudience;
import java.lang.reflect.InvocationHandler;

@InterfaceAudience.Public
public class RetryingMetaStoreClient implements InvocationHandler
{
    private static final Log LOG;
    private final IMetaStoreClient base;
    private final int retryLimit;
    private final long retryDelaySeconds;
    private final Map<String, Long> metaCallTimeMap;
    private final long connectionLifeTimeInMillis;
    private long lastConnectionTime;
    private boolean localMetaStore;
    
    protected RetryingMetaStoreClient(final HiveConf hiveConf, final HiveMetaHookLoader hookLoader, final Map<String, Long> metaCallTimeMap, final Class<? extends IMetaStoreClient> msClientClass) throws MetaException {
        this(hiveConf, new Class[] { HiveConf.class, HiveMetaHookLoader.class }, new Object[] { hiveConf, hookLoader }, metaCallTimeMap, msClientClass);
    }
    
    protected RetryingMetaStoreClient(final HiveConf hiveConf, final Class<?>[] constructorArgTypes, final Object[] constructorArgs, final Map<String, Long> metaCallTimeMap, final Class<? extends IMetaStoreClient> msClientClass) throws MetaException {
        this.retryLimit = hiveConf.getIntVar(HiveConf.ConfVars.METASTORETHRIFTFAILURERETRIES);
        this.retryDelaySeconds = hiveConf.getTimeVar(HiveConf.ConfVars.METASTORE_CLIENT_CONNECT_RETRY_DELAY, TimeUnit.SECONDS);
        this.metaCallTimeMap = metaCallTimeMap;
        this.connectionLifeTimeInMillis = hiveConf.getTimeVar(HiveConf.ConfVars.METASTORE_CLIENT_SOCKET_LIFETIME, TimeUnit.SECONDS) * 1000L;
        this.lastConnectionTime = System.currentTimeMillis();
        final String msUri = hiveConf.getVar(HiveConf.ConfVars.METASTOREURIS);
        this.localMetaStore = (msUri == null || msUri.trim().isEmpty());
        this.reloginExpiringKeytabUser();
        this.base = MetaStoreUtils.newInstance(msClientClass, constructorArgTypes, constructorArgs);
    }
    
    public static IMetaStoreClient getProxy(final HiveConf hiveConf) throws MetaException {
        return getProxy(hiveConf, new Class[] { HiveConf.class }, new Object[] { hiveConf }, null, HiveMetaStoreClient.class.getName());
    }
    
    public static IMetaStoreClient getProxy(final HiveConf hiveConf, final HiveMetaHookLoader hookLoader, final String mscClassName) throws MetaException {
        return getProxy(hiveConf, hookLoader, null, mscClassName);
    }
    
    public static IMetaStoreClient getProxy(final HiveConf hiveConf, final HiveMetaHookLoader hookLoader, final Map<String, Long> metaCallTimeMap, final String mscClassName) throws MetaException {
        return getProxy(hiveConf, new Class[] { HiveConf.class, HiveMetaHookLoader.class }, new Object[] { hiveConf, hookLoader }, metaCallTimeMap, mscClassName);
    }
    
    public static IMetaStoreClient getProxy(final HiveConf hiveConf, final Class<?>[] constructorArgTypes, final Object[] constructorArgs, final String mscClassName) throws MetaException {
        return getProxy(hiveConf, constructorArgTypes, constructorArgs, null, mscClassName);
    }
    
    public static IMetaStoreClient getProxy(final HiveConf hiveConf, final Class<?>[] constructorArgTypes, final Object[] constructorArgs, final Map<String, Long> metaCallTimeMap, final String mscClassName) throws MetaException {
        final Class<? extends IMetaStoreClient> baseClass = (Class<? extends IMetaStoreClient>)MetaStoreUtils.getClass(mscClassName);
        final RetryingMetaStoreClient handler = new RetryingMetaStoreClient(hiveConf, constructorArgTypes, constructorArgs, metaCallTimeMap, baseClass);
        return (IMetaStoreClient)Proxy.newProxyInstance(RetryingMetaStoreClient.class.getClassLoader(), baseClass.getInterfaces(), handler);
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        Object ret = null;
        int retriesMade = 0;
        TException caughtException = null;
        while (true) {
            try {
                this.reloginExpiringKeytabUser();
                if (retriesMade > 0 || this.hasConnectionLifeTimeReached(method)) {
                    this.base.reconnect();
                    this.lastConnectionTime = System.currentTimeMillis();
                }
                if (this.metaCallTimeMap == null) {
                    ret = method.invoke(this.base, args);
                }
                else {
                    final long startTime = System.currentTimeMillis();
                    ret = method.invoke(this.base, args);
                    final long timeTaken = System.currentTimeMillis() - startTime;
                    this.addMethodTime(method, timeTaken);
                }
                return ret;
            }
            catch (UndeclaredThrowableException e) {
                throw e.getCause();
            }
            catch (InvocationTargetException e2) {
                if (e2.getCause() instanceof TApplicationException || e2.getCause() instanceof TProtocolException || e2.getCause() instanceof TTransportException) {
                    caughtException = (TException)e2.getCause();
                }
                else {
                    if (!(e2.getCause() instanceof MetaException) || !e2.getCause().getMessage().matches("(?s).*(JDO[a-zA-Z]*|TApplication|TProtocol|TTransport)Exception.*")) {
                        throw e2.getCause();
                    }
                    caughtException = (MetaException)e2.getCause();
                }
            }
            catch (MetaException e3) {
                if (e3.getMessage().matches("(?s).*(IO|TTransport)Exception.*")) {}
                caughtException = e3;
            }
            if (retriesMade >= this.retryLimit) {
                throw caughtException;
            }
            ++retriesMade;
            RetryingMetaStoreClient.LOG.warn("MetaStoreClient lost connection. Attempting to reconnect.", caughtException);
            Thread.sleep(this.retryDelaySeconds * 1000L);
        }
    }
    
    private void addMethodTime(final Method method, long timeTaken) {
        final String methodStr = this.getMethodString(method);
        final Long curTime = this.metaCallTimeMap.get(methodStr);
        if (curTime != null) {
            timeTaken += curTime;
        }
        this.metaCallTimeMap.put(methodStr, timeTaken);
    }
    
    private String getMethodString(final Method method) {
        final StringBuilder methodSb = new StringBuilder(method.getName());
        methodSb.append("_(");
        for (final Class<?> paramClass : method.getParameterTypes()) {
            methodSb.append(paramClass.getSimpleName());
            methodSb.append(", ");
        }
        methodSb.append(")");
        return methodSb.toString();
    }
    
    private boolean hasConnectionLifeTimeReached(final Method method) {
        if (this.connectionLifeTimeInMillis <= 0L || this.localMetaStore || method.getName().equalsIgnoreCase("close")) {
            return false;
        }
        final boolean shouldReconnect = System.currentTimeMillis() - this.lastConnectionTime >= this.connectionLifeTimeInMillis;
        if (RetryingMetaStoreClient.LOG.isDebugEnabled()) {
            RetryingMetaStoreClient.LOG.debug("Reconnection status for Method: " + method.getName() + " is " + shouldReconnect);
        }
        return shouldReconnect;
    }
    
    private void reloginExpiringKeytabUser() throws MetaException {
        if (!UserGroupInformation.isSecurityEnabled()) {
            return;
        }
        try {
            final UserGroupInformation ugi = UserGroupInformation.getLoginUser();
            if (ugi.isFromKeytab()) {
                ugi.checkTGTAndReloginFromKeytab();
            }
        }
        catch (IOException e) {
            final String msg = "Error doing relogin using keytab " + e.getMessage();
            RetryingMetaStoreClient.LOG.error(msg, e);
            throw new MetaException(msg);
        }
    }
    
    static {
        LOG = LogFactory.getLog(RetryingMetaStoreClient.class.getName());
    }
}
