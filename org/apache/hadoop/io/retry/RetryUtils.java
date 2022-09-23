// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.retry;

import java.io.IOException;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.ipc.RetriableException;
import com.google.protobuf.ServiceException;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;

public class RetryUtils
{
    public static final Logger LOG;
    
    public static RetryPolicy getDefaultRetryPolicy(final Configuration conf, final String retryPolicyEnabledKey, final boolean defaultRetryPolicyEnabled, final String retryPolicySpecKey, final String defaultRetryPolicySpec, final String remoteExceptionToRetry) {
        final RetryPolicy multipleLinearRandomRetry = getMultipleLinearRandomRetry(conf, retryPolicyEnabledKey, defaultRetryPolicyEnabled, retryPolicySpecKey, defaultRetryPolicySpec);
        RetryUtils.LOG.debug("multipleLinearRandomRetry = {}", multipleLinearRandomRetry);
        if (multipleLinearRandomRetry == null) {
            return RetryPolicies.TRY_ONCE_THEN_FAIL;
        }
        return new WrapperRetryPolicy((RetryPolicies.MultipleLinearRandomRetry)multipleLinearRandomRetry, remoteExceptionToRetry);
    }
    
    public static RetryPolicy getMultipleLinearRandomRetry(final Configuration conf, final String retryPolicyEnabledKey, final boolean defaultRetryPolicyEnabled, final String retryPolicySpecKey, final String defaultRetryPolicySpec) {
        final boolean enabled = conf.getBoolean(retryPolicyEnabledKey, defaultRetryPolicyEnabled);
        if (!enabled) {
            return null;
        }
        final String policy = conf.get(retryPolicySpecKey, defaultRetryPolicySpec);
        final RetryPolicy r = RetryPolicies.MultipleLinearRandomRetry.parseCommaSeparatedString(policy);
        return (r != null) ? r : RetryPolicies.MultipleLinearRandomRetry.parseCommaSeparatedString(defaultRetryPolicySpec);
    }
    
    static {
        LOG = LoggerFactory.getLogger(RetryUtils.class);
    }
    
    private static final class WrapperRetryPolicy implements RetryPolicy
    {
        private RetryPolicies.MultipleLinearRandomRetry multipleLinearRandomRetry;
        private String remoteExceptionToRetry;
        
        private WrapperRetryPolicy(final RetryPolicies.MultipleLinearRandomRetry multipleLinearRandomRetry, final String remoteExceptionToRetry) {
            this.multipleLinearRandomRetry = multipleLinearRandomRetry;
            this.remoteExceptionToRetry = remoteExceptionToRetry;
        }
        
        @Override
        public RetryAction shouldRetry(Exception e, final int retries, final int failovers, final boolean isMethodIdempotent) throws Exception {
            if (e instanceof ServiceException) {
                final Throwable cause = e.getCause();
                if (cause != null && cause instanceof Exception) {
                    e = (Exception)cause;
                }
            }
            RetryPolicy p;
            if (e instanceof RetriableException || RetryPolicies.getWrappedRetriableException(e) != null) {
                p = this.multipleLinearRandomRetry;
            }
            else if (e instanceof RemoteException) {
                final RemoteException re = (RemoteException)e;
                p = (re.getClassName().equals(this.remoteExceptionToRetry) ? this.multipleLinearRandomRetry : RetryPolicies.TRY_ONCE_THEN_FAIL);
            }
            else if (e instanceof IOException || e instanceof ServiceException) {
                p = this.multipleLinearRandomRetry;
            }
            else {
                p = RetryPolicies.TRY_ONCE_THEN_FAIL;
            }
            RetryUtils.LOG.debug("RETRY {}) policy={}", retries, p.getClass().getSimpleName(), e);
            return p.shouldRetry(e, retries, failovers, isMethodIdempotent);
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj == this || (obj instanceof WrapperRetryPolicy && this.multipleLinearRandomRetry.equals(((WrapperRetryPolicy)obj).multipleLinearRandomRetry));
        }
        
        @Override
        public int hashCode() {
            return this.multipleLinearRandomRetry.hashCode();
        }
        
        @Override
        public String toString() {
            return "RetryPolicy[" + this.multipleLinearRandomRetry + ", " + RetryPolicies.TRY_ONCE_THEN_FAIL.getClass().getSimpleName() + "]";
        }
    }
}
