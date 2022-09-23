// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.client;

import org.apache.hadoop.io.retry.RetryProxy;
import java.security.PrivilegedAction;
import java.net.InetSocketAddress;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.hadoop.security.UserGroupInformation;
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
import com.google.common.base.Preconditions;
import org.apache.hadoop.io.retry.RetryPolicies;
import org.apache.hadoop.io.retry.RetryPolicy;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class ServerProxy
{
    protected static RetryPolicy createRetryPolicy(final Configuration conf, final String maxWaitTimeStr, final long defMaxWaitTime, final String connectRetryIntervalStr, final long defRetryInterval) {
        final long maxWaitTime = conf.getLong(maxWaitTimeStr, defMaxWaitTime);
        final long retryIntervalMS = conf.getLong(connectRetryIntervalStr, defRetryInterval);
        if (maxWaitTime == -1L) {
            return RetryPolicies.RETRY_FOREVER;
        }
        Preconditions.checkArgument(maxWaitTime > 0L, (Object)("Invalid Configuration. " + maxWaitTimeStr + " should be a positive value."));
        Preconditions.checkArgument(retryIntervalMS > 0L, (Object)("Invalid Configuration. " + connectRetryIntervalStr + "should be a positive value."));
        final RetryPolicy retryPolicy = RetryPolicies.retryUpToMaximumTimeWithFixedSleep(maxWaitTime, retryIntervalMS, TimeUnit.MILLISECONDS);
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
    
    protected static <T> T createRetriableProxy(final Configuration conf, final Class<T> protocol, final UserGroupInformation user, final YarnRPC rpc, final InetSocketAddress serverAddress, final RetryPolicy retryPolicy) {
        final T proxy = user.doAs((PrivilegedAction<T>)new PrivilegedAction<T>() {
            @Override
            public T run() {
                return (T)rpc.getProxy(protocol, serverAddress, conf);
            }
        });
        return (T)RetryProxy.create(protocol, proxy, retryPolicy);
    }
}
