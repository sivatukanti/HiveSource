// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.client;

import org.apache.commons.logging.LogFactory;
import com.google.common.annotations.VisibleForTesting;
import java.util.Map;
import java.net.SocketException;
import org.apache.hadoop.ipc.RetriableException;
import org.apache.hadoop.net.ConnectTimeoutException;
import java.net.UnknownHostException;
import java.net.NoRouteToHostException;
import java.net.ConnectException;
import java.io.EOFException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.io.retry.RetryPolicies;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import java.security.PrivilegedAction;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.io.retry.RetryPolicy;
import org.apache.hadoop.io.retry.FailoverProxyProvider;
import org.apache.hadoop.io.retry.RetryProxy;
import org.apache.hadoop.yarn.conf.HAUtil;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class RMProxy<T>
{
    private static final Log LOG;
    
    protected RMProxy() {
    }
    
    @InterfaceAudience.Private
    protected void checkAllowedProtocols(final Class<?> protocol) {
    }
    
    @InterfaceAudience.Private
    protected InetSocketAddress getRMAddress(final YarnConfiguration conf, final Class<?> protocol) throws IOException {
        throw new UnsupportedOperationException("This method should be invoked from an instance of ClientRMProxy or ServerRMProxy");
    }
    
    @InterfaceAudience.Private
    protected static <T> T createRMProxy(final Configuration configuration, final Class<T> protocol, final RMProxy instance) throws IOException {
        final YarnConfiguration conf = (YarnConfiguration)((configuration instanceof YarnConfiguration) ? configuration : new YarnConfiguration(configuration));
        final RetryPolicy retryPolicy = createRetryPolicy(conf);
        if (HAUtil.isHAEnabled(conf)) {
            final RMFailoverProxyProvider<T> provider = instance.createRMFailoverProxyProvider(conf, protocol);
            return (T)RetryProxy.create(protocol, provider, retryPolicy);
        }
        final InetSocketAddress rmAddress = instance.getRMAddress(conf, protocol);
        RMProxy.LOG.info("Connecting to ResourceManager at " + rmAddress);
        final T proxy = getProxy(conf, protocol, rmAddress);
        return (T)RetryProxy.create(protocol, proxy, retryPolicy);
    }
    
    @Deprecated
    public static <T> T createRMProxy(final Configuration conf, final Class<T> protocol, final InetSocketAddress rmAddress) throws IOException {
        final RetryPolicy retryPolicy = createRetryPolicy(conf);
        final T proxy = (T)getProxy(conf, (Class<Object>)protocol, rmAddress);
        RMProxy.LOG.info("Connecting to ResourceManager at " + rmAddress);
        return (T)RetryProxy.create(protocol, proxy, retryPolicy);
    }
    
    @InterfaceAudience.Private
    static <T> T getProxy(final Configuration conf, final Class<T> protocol, final InetSocketAddress rmAddress) throws IOException {
        return UserGroupInformation.getCurrentUser().doAs((PrivilegedAction<T>)new PrivilegedAction<T>() {
            @Override
            public T run() {
                return (T)YarnRPC.create(conf).getProxy(protocol, rmAddress, conf);
            }
        });
    }
    
    private <T> RMFailoverProxyProvider<T> createRMFailoverProxyProvider(final Configuration conf, final Class<T> protocol) {
        Class<? extends RMFailoverProxyProvider<T>> defaultProviderClass;
        try {
            defaultProviderClass = (Class<? extends RMFailoverProxyProvider<T>>)Class.forName("org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider");
        }
        catch (Exception e) {
            throw new YarnRuntimeException("Invalid default failover provider classorg.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider", e);
        }
        final RMFailoverProxyProvider<T> provider = ReflectionUtils.newInstance(conf.getClass("yarn.client.failover-proxy-provider", defaultProviderClass, RMFailoverProxyProvider.class), conf);
        provider.init(conf, (RMProxy<T>)this, protocol);
        return provider;
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public static RetryPolicy createRetryPolicy(final Configuration conf) {
        long rmConnectWaitMS = conf.getLong("yarn.resourcemanager.connect.max-wait.ms", 900000L);
        final long rmConnectionRetryIntervalMS = conf.getLong("yarn.resourcemanager.connect.retry-interval.ms", 30000L);
        final boolean waitForEver = rmConnectWaitMS == -1L;
        if (!waitForEver) {
            if (rmConnectWaitMS < 0L) {
                throw new YarnRuntimeException("Invalid Configuration. yarn.resourcemanager.connect.max-wait.ms can be -1, but can not be other negative numbers");
            }
            if (rmConnectWaitMS < rmConnectionRetryIntervalMS) {
                RMProxy.LOG.warn("yarn.resourcemanager.connect.max-wait.ms is smaller than yarn.resourcemanager.connect.retry-interval.ms. Only try connect once.");
                rmConnectWaitMS = 0L;
            }
        }
        if (HAUtil.isHAEnabled(conf)) {
            final long failoverSleepBaseMs = conf.getLong("yarn.client.failover-sleep-base-ms", rmConnectionRetryIntervalMS);
            final long failoverSleepMaxMs = conf.getLong("yarn.client.failover-sleep-max-ms", rmConnectionRetryIntervalMS);
            int maxFailoverAttempts = conf.getInt("yarn.client.failover-max-attempts", -1);
            if (maxFailoverAttempts == -1) {
                if (waitForEver) {
                    maxFailoverAttempts = Integer.MAX_VALUE;
                }
                else {
                    maxFailoverAttempts = (int)(rmConnectWaitMS / failoverSleepBaseMs);
                }
            }
            return RetryPolicies.failoverOnNetworkException(RetryPolicies.TRY_ONCE_THEN_FAIL, maxFailoverAttempts, failoverSleepBaseMs, failoverSleepMaxMs);
        }
        if (waitForEver) {
            return RetryPolicies.RETRY_FOREVER;
        }
        if (rmConnectionRetryIntervalMS < 0L) {
            throw new YarnRuntimeException("Invalid Configuration. yarn.resourcemanager.connect.retry-interval.ms should not be negative.");
        }
        final RetryPolicy retryPolicy = RetryPolicies.retryUpToMaximumTimeWithFixedSleep(rmConnectWaitMS, rmConnectionRetryIntervalMS, TimeUnit.MILLISECONDS);
        final Map<Class<? extends Exception>, RetryPolicy> exceptionToPolicyMap = new HashMap<Class<? extends Exception>, RetryPolicy>();
        exceptionToPolicyMap.put(EOFException.class, retryPolicy);
        exceptionToPolicyMap.put(ConnectException.class, retryPolicy);
        exceptionToPolicyMap.put(NoRouteToHostException.class, retryPolicy);
        exceptionToPolicyMap.put(UnknownHostException.class, retryPolicy);
        exceptionToPolicyMap.put(ConnectTimeoutException.class, retryPolicy);
        exceptionToPolicyMap.put(RetriableException.class, retryPolicy);
        exceptionToPolicyMap.put(SocketException.class, retryPolicy);
        return RetryPolicies.retryByException(RetryPolicies.TRY_ONCE_THEN_FAIL, exceptionToPolicyMap);
    }
    
    static {
        LOG = LogFactory.getLog(RMProxy.class);
    }
}
