// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.commons.logging.LogFactory;
import java.lang.reflect.InvocationTargetException;
import org.datanucleus.exceptions.NucleusException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import java.lang.reflect.UndeclaredThrowableException;
import org.apache.commons.lang.exception.ExceptionUtils;
import javax.jdo.JDOException;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.common.classification.InterfaceStability;
import org.apache.hadoop.hive.common.classification.InterfaceAudience;
import java.lang.reflect.InvocationHandler;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class RetryingHMSHandler implements InvocationHandler
{
    private static final Log LOG;
    private final IHMSHandler baseHandler;
    private final MetaStoreInit.MetaStoreInitData metaStoreInitData;
    private final HiveConf origConf;
    private final Configuration activeConf;
    
    private RetryingHMSHandler(final HiveConf hiveConf, final IHMSHandler baseHandler, final boolean local) throws MetaException {
        this.metaStoreInitData = new MetaStoreInit.MetaStoreInitData();
        this.origConf = hiveConf;
        this.baseHandler = baseHandler;
        if (local) {
            baseHandler.setConf(hiveConf);
        }
        this.activeConf = baseHandler.getConf();
        MetaStoreInit.updateConnectionURL(hiveConf, this.getActiveConf(), null, this.metaStoreInitData);
        baseHandler.init();
    }
    
    public static IHMSHandler getProxy(final HiveConf hiveConf, final IHMSHandler baseHandler, final boolean local) throws MetaException {
        final RetryingHMSHandler handler = new RetryingHMSHandler(hiveConf, baseHandler, local);
        return (IHMSHandler)Proxy.newProxyInstance(RetryingHMSHandler.class.getClassLoader(), new Class[] { IHMSHandler.class }, handler);
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        boolean gotNewConnectUrl = false;
        final boolean reloadConf = HiveConf.getBoolVar(this.origConf, HiveConf.ConfVars.HMSHANDLERFORCERELOADCONF);
        final long retryInterval = HiveConf.getTimeVar(this.origConf, HiveConf.ConfVars.HMSHANDLERINTERVAL, TimeUnit.MILLISECONDS);
        final int retryLimit = HiveConf.getIntVar(this.origConf, HiveConf.ConfVars.HMSHANDLERATTEMPTS);
        final long timeout = HiveConf.getTimeVar(this.origConf, HiveConf.ConfVars.METASTORE_CLIENT_SOCKET_TIMEOUT, TimeUnit.MILLISECONDS);
        Deadline.registerIfNot(timeout);
        if (reloadConf) {
            MetaStoreInit.updateConnectionURL(this.origConf, this.getActiveConf(), null, this.metaStoreInitData);
        }
        int retryCount = 0;
        Throwable caughtException = null;
        while (true) {
            try {
                if (reloadConf || gotNewConnectUrl) {
                    this.baseHandler.setConf(this.getActiveConf());
                }
                Deadline.startTimer(method.getName());
                final Object object = method.invoke(this.baseHandler, args);
                Deadline.stopTimer();
                return object;
            }
            catch (JDOException e) {
                caughtException = e;
            }
            catch (UndeclaredThrowableException e2) {
                if (e2.getCause() == null) {
                    RetryingHMSHandler.LOG.error(ExceptionUtils.getStackTrace(e2));
                    throw e2;
                }
                if (e2.getCause() instanceof JDOException) {
                    caughtException = e2.getCause();
                }
                else {
                    if (!(e2.getCause() instanceof MetaException) || e2.getCause().getCause() == null || !(e2.getCause().getCause() instanceof JDOException)) {
                        RetryingHMSHandler.LOG.error(ExceptionUtils.getStackTrace(e2.getCause()));
                        throw e2.getCause();
                    }
                    caughtException = e2.getCause().getCause();
                }
            }
            catch (InvocationTargetException e3) {
                if (e3.getCause() instanceof JDOException) {
                    caughtException = e3.getCause();
                }
                else {
                    if (e3.getCause() instanceof NoSuchObjectException || e3.getTargetException().getCause() instanceof NoSuchObjectException) {
                        final String methodName = method.getName();
                        if (!methodName.startsWith("get_database") && !methodName.startsWith("get_table") && !methodName.startsWith("get_partition") && !methodName.startsWith("get_function")) {
                            RetryingHMSHandler.LOG.error(ExceptionUtils.getStackTrace(e3.getCause()));
                        }
                        throw e3.getCause();
                    }
                    if (!(e3.getCause() instanceof MetaException) || e3.getCause().getCause() == null) {
                        RetryingHMSHandler.LOG.error(ExceptionUtils.getStackTrace(e3.getCause()));
                        throw e3.getCause();
                    }
                    if (e3.getCause().getCause() instanceof JDOException || e3.getCause().getCause() instanceof NucleusException) {
                        caughtException = e3.getCause().getCause();
                    }
                    else {
                        if (e3.getCause().getCause() instanceof DeadlineException) {
                            Deadline.clear();
                            RetryingHMSHandler.LOG.error("Error happens in method " + method.getName() + ": " + ExceptionUtils.getStackTrace(e3.getCause()));
                            throw e3.getCause();
                        }
                        RetryingHMSHandler.LOG.error(ExceptionUtils.getStackTrace(e3.getCause()));
                        throw e3.getCause();
                    }
                }
            }
            if (retryCount >= retryLimit) {
                RetryingHMSHandler.LOG.error("HMSHandler Fatal error: " + ExceptionUtils.getStackTrace(caughtException));
                throw new MetaException(ExceptionUtils.getStackTrace(caughtException));
            }
            assert retryInterval >= 0L;
            ++retryCount;
            RetryingHMSHandler.LOG.error(String.format("Retrying HMSHandler after %d ms (attempt %d of %d)", retryInterval, retryCount, retryLimit) + " with error: " + ExceptionUtils.getStackTrace(caughtException));
            Thread.sleep(retryInterval);
            final String lastUrl = MetaStoreInit.getConnectionURL(this.getActiveConf());
            gotNewConnectUrl = MetaStoreInit.updateConnectionURL(this.origConf, this.getActiveConf(), lastUrl, this.metaStoreInitData);
        }
    }
    
    public Configuration getActiveConf() {
        return this.activeConf;
    }
    
    static {
        LOG = LogFactory.getLog(RetryingHMSHandler.class);
    }
}
