// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.retry;

import java.util.Iterator;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.io.InterruptedIOException;
import org.apache.hadoop.util.Time;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.ipc.RPC;
import java.io.IOException;
import com.google.common.annotations.VisibleForTesting;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.apache.hadoop.ipc.ProtocolTranslator;
import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.ipc.Client;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.ipc.RpcInvocationHandler;

@InterfaceAudience.Private
public class RetryInvocationHandler<T> implements RpcInvocationHandler
{
    public static final Logger LOG;
    private final ProxyDescriptor<T> proxyDescriptor;
    private volatile boolean hasSuccessfulCall;
    private final RetryPolicy defaultPolicy;
    private final Map<String, RetryPolicy> methodNameToPolicyMap;
    private final AsyncCallHandler asyncCallHandler;
    
    protected RetryInvocationHandler(final FailoverProxyProvider<T> proxyProvider, final RetryPolicy retryPolicy) {
        this(proxyProvider, retryPolicy, Collections.emptyMap());
    }
    
    protected RetryInvocationHandler(final FailoverProxyProvider<T> proxyProvider, final RetryPolicy defaultPolicy, final Map<String, RetryPolicy> methodNameToPolicyMap) {
        this.hasSuccessfulCall = false;
        this.asyncCallHandler = new AsyncCallHandler();
        this.proxyDescriptor = new ProxyDescriptor<T>(proxyProvider);
        this.defaultPolicy = defaultPolicy;
        this.methodNameToPolicyMap = methodNameToPolicyMap;
    }
    
    private RetryPolicy getRetryPolicy(final Method method) {
        final RetryPolicy policy = this.methodNameToPolicyMap.get(method.getName());
        return (policy != null) ? policy : this.defaultPolicy;
    }
    
    private long getFailoverCount() {
        return this.proxyDescriptor.getFailoverCount();
    }
    
    private Call newCall(final Method method, final Object[] args, final boolean isRpc, final int callId) {
        if (Client.isAsynchronousMode()) {
            return this.asyncCallHandler.newAsyncCall(method, args, isRpc, callId, this);
        }
        return new Call(method, args, isRpc, callId, this);
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final boolean isRpc = isRpcInvocation(this.proxyDescriptor.getProxy());
        final int callId = isRpc ? Client.nextCallId() : -2;
        final Call call = this.newCall(method, args, isRpc, callId);
        while (true) {
            final CallReturn c = call.invokeOnce();
            final CallReturn.State state = c.getState();
            if (state == CallReturn.State.ASYNC_INVOKED) {
                return null;
            }
            if (c.getState() != CallReturn.State.RETRY) {
                return c.getReturnValue();
            }
        }
    }
    
    private RetryInfo handleException(final Method method, final int callId, final RetryPolicy policy, final Counters counters, final long expectFailoverCount, final Exception e) throws Exception {
        final RetryInfo retryInfo = RetryInfo.newRetryInfo(policy, e, counters, this.proxyDescriptor.idempotentOrAtMostOnce(method), expectFailoverCount);
        if (retryInfo.isFail()) {
            if (retryInfo.action.reason != null && RetryInvocationHandler.LOG.isDebugEnabled()) {
                RetryInvocationHandler.LOG.debug("Exception while invoking call #" + callId + " " + this.proxyDescriptor.getProxyInfo().getString(method.getName()) + ". Not retrying because " + retryInfo.action.reason, e);
            }
            throw retryInfo.getFailException();
        }
        this.log(method, retryInfo.isFailover(), counters.failovers, retryInfo.delay, e);
        return retryInfo;
    }
    
    private void log(final Method method, final boolean isFailover, final int failovers, final long delay, final Exception ex) {
        final boolean info = this.hasSuccessfulCall || failovers != 0 || this.asyncCallHandler.hasSuccessfulCall();
        if (!info && !RetryInvocationHandler.LOG.isDebugEnabled()) {
            return;
        }
        final StringBuilder b = new StringBuilder().append(ex + ", while invoking ").append(this.proxyDescriptor.getProxyInfo().getString(method.getName()));
        if (failovers > 0) {
            b.append(" after ").append(failovers).append(" failover attempts");
        }
        b.append(isFailover ? ". Trying to failover " : ". Retrying ");
        b.append((delay > 0L) ? ("after sleeping for " + delay + "ms.") : "immediately.");
        if (info) {
            RetryInvocationHandler.LOG.info(b.toString());
        }
        else {
            RetryInvocationHandler.LOG.debug(b.toString(), ex);
        }
    }
    
    protected Object invokeMethod(final Method method, final Object[] args) throws Throwable {
        try {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            final Object r = method.invoke(this.proxyDescriptor.getProxy(), args);
            this.hasSuccessfulCall = true;
            return r;
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
    
    @VisibleForTesting
    static boolean isRpcInvocation(Object proxy) {
        if (proxy instanceof ProtocolTranslator) {
            proxy = ((ProtocolTranslator)proxy).getUnderlyingProxyObject();
        }
        if (!Proxy.isProxyClass(proxy.getClass())) {
            return false;
        }
        final InvocationHandler ih = Proxy.getInvocationHandler(proxy);
        return ih instanceof RpcInvocationHandler;
    }
    
    @Override
    public void close() throws IOException {
        this.proxyDescriptor.close();
    }
    
    @Override
    public Client.ConnectionId getConnectionId() {
        return RPC.getConnectionIdForProxy(this.proxyDescriptor.getProxy());
    }
    
    static {
        LOG = LoggerFactory.getLogger(RetryInvocationHandler.class);
    }
    
    static class Call
    {
        private final Method method;
        private final Object[] args;
        private final boolean isRpc;
        private final int callId;
        private final Counters counters;
        private final RetryPolicy retryPolicy;
        private final RetryInvocationHandler<?> retryInvocationHandler;
        private RetryInfo retryInfo;
        
        Call(final Method method, final Object[] args, final boolean isRpc, final int callId, final RetryInvocationHandler<?> retryInvocationHandler) {
            this.counters = new Counters();
            this.method = method;
            this.args = args;
            this.isRpc = isRpc;
            this.callId = callId;
            this.retryPolicy = ((RetryInvocationHandler<Object>)retryInvocationHandler).getRetryPolicy(method);
            this.retryInvocationHandler = retryInvocationHandler;
        }
        
        int getCallId() {
            return this.callId;
        }
        
        Counters getCounters() {
            return this.counters;
        }
        
        synchronized Long getWaitTime(final long now) {
            return (this.retryInfo == null) ? null : Long.valueOf(this.retryInfo.retryTime - now);
        }
        
        synchronized CallReturn invokeOnce() {
            try {
                if (this.retryInfo != null) {
                    return this.processWaitTimeAndRetryInfo();
                }
                final long failoverCount = ((RetryInvocationHandler<Object>)this.retryInvocationHandler).getFailoverCount();
                try {
                    return this.invoke();
                }
                catch (Exception e) {
                    if (RetryInvocationHandler.LOG.isTraceEnabled()) {
                        RetryInvocationHandler.LOG.trace(this.toString(), e);
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        throw e;
                    }
                    this.retryInfo = ((RetryInvocationHandler<Object>)this.retryInvocationHandler).handleException(this.method, this.callId, this.retryPolicy, this.counters, failoverCount, e);
                    return this.processWaitTimeAndRetryInfo();
                }
            }
            catch (Throwable t) {
                return new CallReturn(t);
            }
        }
        
        CallReturn processWaitTimeAndRetryInfo() throws InterruptedIOException {
            final Long waitTime = this.getWaitTime(Time.monotonicNow());
            RetryInvocationHandler.LOG.trace("#{} processRetryInfo: retryInfo={}, waitTime={}", this.callId, this.retryInfo, waitTime);
            if (waitTime != null && waitTime > 0L) {
                try {
                    Thread.sleep(this.retryInfo.delay);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    if (RetryInvocationHandler.LOG.isDebugEnabled()) {
                        RetryInvocationHandler.LOG.debug("Interrupted while waiting to retry", e);
                    }
                    final InterruptedIOException intIOE = new InterruptedIOException("Retry interrupted");
                    intIOE.initCause(e);
                    throw intIOE;
                }
            }
            this.processRetryInfo();
            return CallReturn.RETRY;
        }
        
        synchronized void processRetryInfo() {
            this.counters.retries++;
            if (this.retryInfo.isFailover()) {
                ((RetryInvocationHandler<Object>)this.retryInvocationHandler).proxyDescriptor.failover(this.retryInfo.expectedFailoverCount, this.method, this.callId);
                this.counters.failovers++;
            }
            this.retryInfo = null;
        }
        
        CallReturn invoke() throws Throwable {
            return new CallReturn(this.invokeMethod());
        }
        
        Object invokeMethod() throws Throwable {
            if (this.isRpc) {
                Client.setCallIdAndRetryCount(this.callId, this.counters.retries, ((RetryInvocationHandler<Object>)this.retryInvocationHandler).asyncCallHandler);
            }
            return this.retryInvocationHandler.invokeMethod(this.method, this.args);
        }
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "#" + this.callId + ": " + this.method.getDeclaringClass().getSimpleName() + "." + this.method.getName() + "(" + ((this.args == null || this.args.length == 0) ? "" : Arrays.toString(this.args)) + ")";
        }
    }
    
    static class Counters
    {
        private int retries;
        private int failovers;
        
        boolean isZeros() {
            return this.retries == 0 && this.failovers == 0;
        }
    }
    
    private static class ProxyDescriptor<T>
    {
        private final FailoverProxyProvider<T> fpp;
        private long failoverCount;
        private FailoverProxyProvider.ProxyInfo<T> proxyInfo;
        
        ProxyDescriptor(final FailoverProxyProvider<T> fpp) {
            this.failoverCount = 0L;
            this.fpp = fpp;
            this.proxyInfo = fpp.getProxy();
        }
        
        synchronized FailoverProxyProvider.ProxyInfo<T> getProxyInfo() {
            return this.proxyInfo;
        }
        
        synchronized T getProxy() {
            return this.proxyInfo.proxy;
        }
        
        synchronized long getFailoverCount() {
            return this.failoverCount;
        }
        
        synchronized void failover(final long expectedFailoverCount, final Method method, final int callId) {
            if (this.failoverCount == expectedFailoverCount) {
                this.fpp.performFailover(this.proxyInfo.proxy);
                ++this.failoverCount;
            }
            else {
                RetryInvocationHandler.LOG.warn("A failover has occurred since the start of call #" + callId + " " + this.proxyInfo.getString(method.getName()));
            }
            this.proxyInfo = this.fpp.getProxy();
        }
        
        boolean idempotentOrAtMostOnce(final Method method) throws NoSuchMethodException {
            final Method m = this.fpp.getInterface().getMethod(method.getName(), method.getParameterTypes());
            return m.isAnnotationPresent(Idempotent.class) || m.isAnnotationPresent(AtMostOnce.class);
        }
        
        void close() throws IOException {
            this.fpp.close();
        }
    }
    
    private static class RetryInfo
    {
        private final long retryTime;
        private final long delay;
        private final RetryPolicy.RetryAction action;
        private final long expectedFailoverCount;
        private final Exception failException;
        
        RetryInfo(final long delay, final RetryPolicy.RetryAction action, final long expectedFailoverCount, final Exception failException) {
            this.delay = delay;
            this.retryTime = Time.monotonicNow() + delay;
            this.action = action;
            this.expectedFailoverCount = expectedFailoverCount;
            this.failException = failException;
        }
        
        boolean isFailover() {
            return this.action != null && this.action.action == RetryPolicy.RetryAction.RetryDecision.FAILOVER_AND_RETRY;
        }
        
        boolean isFail() {
            return this.action != null && this.action.action == RetryPolicy.RetryAction.RetryDecision.FAIL;
        }
        
        Exception getFailException() {
            return this.failException;
        }
        
        static RetryInfo newRetryInfo(final RetryPolicy policy, final Exception e, final Counters counters, final boolean idempotentOrAtMostOnce, final long expectedFailoverCount) throws Exception {
            RetryPolicy.RetryAction max = null;
            long maxRetryDelay = 0L;
            Exception ex = null;
            final Iterable<Exception> exceptions = (e instanceof MultiException) ? ((MultiException)e).getExceptions().values() : Collections.singletonList(e);
            for (final Exception exception : exceptions) {
                final RetryPolicy.RetryAction a = policy.shouldRetry(exception, counters.retries, counters.failovers, idempotentOrAtMostOnce);
                if (a.action != RetryPolicy.RetryAction.RetryDecision.FAIL && a.delayMillis > maxRetryDelay) {
                    maxRetryDelay = a.delayMillis;
                }
                if (max == null || max.action.compareTo(a.action) < 0) {
                    max = a;
                    if (a.action != RetryPolicy.RetryAction.RetryDecision.FAIL) {
                        continue;
                    }
                    ex = exception;
                }
            }
            return new RetryInfo(maxRetryDelay, max, expectedFailoverCount, ex);
        }
        
        @Override
        public String toString() {
            return "RetryInfo{retryTime=" + this.retryTime + ", delay=" + this.delay + ", action=" + this.action + ", expectedFailoverCount=" + this.expectedFailoverCount + ", failException=" + this.failException + '}';
        }
    }
}
