// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.retry;

import org.apache.hadoop.ipc.RetriableException;
import java.lang.reflect.Method;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class LossyRetryInvocationHandler<T> extends RetryInvocationHandler<T>
{
    private final int numToDrop;
    private static final ThreadLocal<Integer> RetryCount;
    
    public LossyRetryInvocationHandler(final int numToDrop, final FailoverProxyProvider<T> proxyProvider, final RetryPolicy retryPolicy) {
        super(proxyProvider, retryPolicy);
        this.numToDrop = numToDrop;
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        LossyRetryInvocationHandler.RetryCount.set(0);
        return super.invoke(proxy, method, args);
    }
    
    @Override
    protected Object invokeMethod(final Method method, final Object[] args) throws Throwable {
        final Object result = super.invokeMethod(method, args);
        int retryCount = LossyRetryInvocationHandler.RetryCount.get();
        if (retryCount < this.numToDrop) {
            LossyRetryInvocationHandler.RetryCount.set(++retryCount);
            if (LossyRetryInvocationHandler.LOG.isDebugEnabled()) {
                LossyRetryInvocationHandler.LOG.debug("Drop the response. Current retryCount == " + retryCount);
            }
            throw new RetriableException("Fake Exception");
        }
        if (LossyRetryInvocationHandler.LOG.isDebugEnabled()) {
            LossyRetryInvocationHandler.LOG.debug("retryCount == " + retryCount + ". It's time to normally process the response");
        }
        return result;
    }
    
    static {
        RetryCount = new ThreadLocal<Integer>();
    }
}
