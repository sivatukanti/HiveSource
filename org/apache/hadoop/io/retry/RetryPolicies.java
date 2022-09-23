// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.retry;

import java.io.IOException;
import java.net.SocketException;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.net.ConnectTimeoutException;
import java.net.UnknownHostException;
import java.net.NoRouteToHostException;
import java.io.EOFException;
import java.net.ConnectException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.ipc.RetriableException;
import javax.security.sasl.SaslException;
import org.apache.hadoop.ipc.StandbyException;
import org.apache.hadoop.ipc.RemoteException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

public class RetryPolicies
{
    public static final Logger LOG;
    public static final RetryPolicy TRY_ONCE_THEN_FAIL;
    public static final RetryPolicy RETRY_FOREVER;
    
    public static final RetryPolicy retryForeverWithFixedSleep(final long sleepTime, final TimeUnit timeUnit) {
        return new RetryUpToMaximumCountWithFixedSleep(Integer.MAX_VALUE, sleepTime, timeUnit);
    }
    
    public static final RetryPolicy retryUpToMaximumCountWithFixedSleep(final int maxRetries, final long sleepTime, final TimeUnit timeUnit) {
        return new RetryUpToMaximumCountWithFixedSleep(maxRetries, sleepTime, timeUnit);
    }
    
    public static final RetryPolicy retryUpToMaximumTimeWithFixedSleep(final long maxTime, final long sleepTime, final TimeUnit timeUnit) {
        return new RetryUpToMaximumTimeWithFixedSleep(maxTime, sleepTime, timeUnit);
    }
    
    public static final RetryPolicy retryUpToMaximumCountWithProportionalSleep(final int maxRetries, final long sleepTime, final TimeUnit timeUnit) {
        return new RetryUpToMaximumCountWithProportionalSleep(maxRetries, sleepTime, timeUnit);
    }
    
    public static final RetryPolicy exponentialBackoffRetry(final int maxRetries, final long sleepTime, final TimeUnit timeUnit) {
        return new ExponentialBackoffRetry(maxRetries, sleepTime, timeUnit);
    }
    
    public static final RetryPolicy retryByException(final RetryPolicy defaultPolicy, final Map<Class<? extends Exception>, RetryPolicy> exceptionToPolicyMap) {
        return new ExceptionDependentRetry(defaultPolicy, exceptionToPolicyMap);
    }
    
    public static final RetryPolicy retryByRemoteException(final RetryPolicy defaultPolicy, final Map<Class<? extends Exception>, RetryPolicy> exceptionToPolicyMap) {
        return new RemoteExceptionDependentRetry(defaultPolicy, exceptionToPolicyMap);
    }
    
    public static final RetryPolicy retryOtherThanRemoteException(final RetryPolicy defaultPolicy, final Map<Class<? extends Exception>, RetryPolicy> exceptionToPolicyMap) {
        return new OtherThanRemoteExceptionDependentRetry(defaultPolicy, exceptionToPolicyMap);
    }
    
    public static final RetryPolicy failoverOnNetworkException(final int maxFailovers) {
        return failoverOnNetworkException(RetryPolicies.TRY_ONCE_THEN_FAIL, maxFailovers);
    }
    
    public static final RetryPolicy failoverOnNetworkException(final RetryPolicy fallbackPolicy, final int maxFailovers) {
        return failoverOnNetworkException(fallbackPolicy, maxFailovers, 0L, 0L);
    }
    
    public static final RetryPolicy failoverOnNetworkException(final RetryPolicy fallbackPolicy, final int maxFailovers, final long delayMillis, final long maxDelayBase) {
        return new FailoverOnNetworkExceptionRetry(fallbackPolicy, maxFailovers, delayMillis, maxDelayBase);
    }
    
    public static final RetryPolicy failoverOnNetworkException(final RetryPolicy fallbackPolicy, final int maxFailovers, final int maxRetries, final long delayMillis, final long maxDelayBase) {
        return new FailoverOnNetworkExceptionRetry(fallbackPolicy, maxFailovers, maxRetries, delayMillis, maxDelayBase);
    }
    
    private static long calculateExponentialTime(final long time, final int retries, final long cap) {
        final long baseTime = Math.min(time * (1L << retries), cap);
        return (long)(baseTime * (ThreadLocalRandom.current().nextDouble() + 0.5));
    }
    
    private static long calculateExponentialTime(final long time, final int retries) {
        return calculateExponentialTime(time, retries, Long.MAX_VALUE);
    }
    
    private static boolean isWrappedStandbyException(final Exception e) {
        if (!(e instanceof RemoteException)) {
            return false;
        }
        final Exception unwrapped = ((RemoteException)e).unwrapRemoteException(StandbyException.class);
        return unwrapped instanceof StandbyException;
    }
    
    private static boolean isSaslFailure(final Exception e) {
        Throwable current = e;
        while (!(current instanceof SaslException)) {
            current = current.getCause();
            if (current == null) {
                return false;
            }
        }
        return true;
    }
    
    static RetriableException getWrappedRetriableException(final Exception e) {
        if (!(e instanceof RemoteException)) {
            return null;
        }
        final Exception unwrapped = ((RemoteException)e).unwrapRemoteException(RetriableException.class);
        return (unwrapped instanceof RetriableException) ? ((RetriableException)unwrapped) : null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(RetryPolicies.class);
        TRY_ONCE_THEN_FAIL = new TryOnceThenFail();
        RETRY_FOREVER = new RetryForever();
    }
    
    static class TryOnceThenFail implements RetryPolicy
    {
        @Override
        public RetryAction shouldRetry(final Exception e, final int retries, final int failovers, final boolean isIdempotentOrAtMostOnce) throws Exception {
            return new RetryAction(RetryAction.RetryDecision.FAIL, 0L, "try once and fail.");
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj == this || (obj != null && obj.getClass() == this.getClass());
        }
        
        @Override
        public int hashCode() {
            return this.getClass().hashCode();
        }
    }
    
    static class RetryForever implements RetryPolicy
    {
        @Override
        public RetryAction shouldRetry(final Exception e, final int retries, final int failovers, final boolean isIdempotentOrAtMostOnce) throws Exception {
            return RetryAction.RETRY;
        }
    }
    
    abstract static class RetryLimited implements RetryPolicy
    {
        final int maxRetries;
        final long sleepTime;
        final TimeUnit timeUnit;
        private String myString;
        
        RetryLimited(final int maxRetries, final long sleepTime, final TimeUnit timeUnit) {
            if (maxRetries < 0) {
                throw new IllegalArgumentException("maxRetries = " + maxRetries + " < 0");
            }
            if (sleepTime < 0L) {
                throw new IllegalArgumentException("sleepTime = " + sleepTime + " < 0");
            }
            this.maxRetries = maxRetries;
            this.sleepTime = sleepTime;
            this.timeUnit = timeUnit;
        }
        
        @Override
        public RetryAction shouldRetry(final Exception e, final int retries, final int failovers, final boolean isIdempotentOrAtMostOnce) throws Exception {
            if (retries >= this.maxRetries) {
                return new RetryAction(RetryAction.RetryDecision.FAIL, 0L, this.getReason());
            }
            return new RetryAction(RetryAction.RetryDecision.RETRY, this.timeUnit.toMillis(this.calculateSleepTime(retries)), this.getReason());
        }
        
        protected String getReason() {
            return constructReasonString(this.maxRetries);
        }
        
        @VisibleForTesting
        public static String constructReasonString(final int retries) {
            return "retries get failed due to exceeded maximum allowed retries number: " + retries;
        }
        
        protected abstract long calculateSleepTime(final int p0);
        
        @Override
        public int hashCode() {
            return this.toString().hashCode();
        }
        
        @Override
        public boolean equals(final Object that) {
            return this == that || (that != null && this.getClass() == that.getClass() && this.toString().equals(that.toString()));
        }
        
        @Override
        public String toString() {
            if (this.myString == null) {
                this.myString = this.getClass().getSimpleName() + "(maxRetries=" + this.maxRetries + ", sleepTime=" + this.sleepTime + " " + this.timeUnit + ")";
            }
            return this.myString;
        }
    }
    
    static class RetryUpToMaximumCountWithFixedSleep extends RetryLimited
    {
        public RetryUpToMaximumCountWithFixedSleep(final int maxRetries, final long sleepTime, final TimeUnit timeUnit) {
            super(maxRetries, sleepTime, timeUnit);
        }
        
        @Override
        protected long calculateSleepTime(final int retries) {
            return this.sleepTime;
        }
    }
    
    static class RetryUpToMaximumTimeWithFixedSleep extends RetryUpToMaximumCountWithFixedSleep
    {
        private long maxTime;
        private TimeUnit timeUnit;
        
        public RetryUpToMaximumTimeWithFixedSleep(final long maxTime, final long sleepTime, final TimeUnit timeUnit) {
            super((int)(maxTime / sleepTime), sleepTime, timeUnit);
            this.maxTime = 0L;
            this.maxTime = maxTime;
            this.timeUnit = timeUnit;
        }
        
        @Override
        protected String getReason() {
            return constructReasonString(this.maxTime, this.timeUnit);
        }
        
        @VisibleForTesting
        public static String constructReasonString(final long maxTime, final TimeUnit timeUnit) {
            return "retries get failed due to exceeded maximum allowed time (in " + timeUnit.toString() + "): " + maxTime;
        }
    }
    
    static class RetryUpToMaximumCountWithProportionalSleep extends RetryLimited
    {
        public RetryUpToMaximumCountWithProportionalSleep(final int maxRetries, final long sleepTime, final TimeUnit timeUnit) {
            super(maxRetries, sleepTime, timeUnit);
        }
        
        @Override
        protected long calculateSleepTime(final int retries) {
            return this.sleepTime * (retries + 1);
        }
    }
    
    public static class MultipleLinearRandomRetry implements RetryPolicy
    {
        private final List<Pair> pairs;
        private String myString;
        
        public MultipleLinearRandomRetry(final List<Pair> pairs) {
            if (pairs == null || pairs.isEmpty()) {
                throw new IllegalArgumentException("pairs must be neither null nor empty.");
            }
            this.pairs = Collections.unmodifiableList((List<? extends Pair>)pairs);
        }
        
        @Override
        public RetryAction shouldRetry(final Exception e, final int curRetry, final int failovers, final boolean isIdempotentOrAtMostOnce) throws Exception {
            final Pair p = this.searchPair(curRetry);
            if (p == null) {
                return new RetryAction(RetryAction.RetryDecision.FAIL, 0L, "Retry all pairs in MultipleLinearRandomRetry: " + this.pairs);
            }
            final double ratio = ThreadLocalRandom.current().nextDouble() + 0.5;
            final long sleepTime = Math.round(p.sleepMillis * ratio);
            return new RetryAction(RetryAction.RetryDecision.RETRY, sleepTime);
        }
        
        private Pair searchPair(int curRetry) {
            int i;
            for (i = 0; i < this.pairs.size() && curRetry > this.pairs.get(i).numRetries; curRetry -= this.pairs.get(i).numRetries, ++i) {}
            return (i == this.pairs.size()) ? null : this.pairs.get(i);
        }
        
        @Override
        public int hashCode() {
            return this.toString().hashCode();
        }
        
        @Override
        public boolean equals(final Object that) {
            return this == that || (that != null && this.getClass() == that.getClass() && this.toString().equals(that.toString()));
        }
        
        @Override
        public String toString() {
            if (this.myString == null) {
                this.myString = this.getClass().getSimpleName() + this.pairs;
            }
            return this.myString;
        }
        
        public static MultipleLinearRandomRetry parseCommaSeparatedString(final String s) {
            final String[] elements = s.split(",");
            if (elements.length == 0) {
                RetryPolicies.LOG.warn("Illegal value: there is no element in \"" + s + "\".");
                return null;
            }
            if (elements.length % 2 != 0) {
                RetryPolicies.LOG.warn("Illegal value: the number of elements in \"" + s + "\" is " + elements.length + " but an even number of elements is expected.");
                return null;
            }
            final List<Pair> pairs = new ArrayList<Pair>();
            int i = 0;
            while (i < elements.length) {
                final int sleep = parsePositiveInt(elements, i++, s);
                if (sleep == -1) {
                    return null;
                }
                final int retries = parsePositiveInt(elements, i++, s);
                if (retries == -1) {
                    return null;
                }
                pairs.add(new Pair(retries, sleep));
            }
            return new MultipleLinearRandomRetry(pairs);
        }
        
        private static int parsePositiveInt(final String[] elements, final int i, final String originalString) {
            final String s = elements[i].trim();
            int n;
            try {
                n = Integer.parseInt(s);
            }
            catch (NumberFormatException nfe) {
                RetryPolicies.LOG.warn("Failed to parse \"" + s + "\", which is the index " + i + " element in \"" + originalString + "\"", nfe);
                return -1;
            }
            if (n <= 0) {
                RetryPolicies.LOG.warn("The value " + n + " <= 0: it is parsed from the string \"" + s + "\" which is the index " + i + " element in \"" + originalString + "\"");
                return -1;
            }
            return n;
        }
        
        public static class Pair
        {
            final int numRetries;
            final int sleepMillis;
            
            public Pair(final int numRetries, final int sleepMillis) {
                if (numRetries < 0) {
                    throw new IllegalArgumentException("numRetries = " + numRetries + " < 0");
                }
                if (sleepMillis < 0) {
                    throw new IllegalArgumentException("sleepMillis = " + sleepMillis + " < 0");
                }
                this.numRetries = numRetries;
                this.sleepMillis = sleepMillis;
            }
            
            @Override
            public String toString() {
                return this.numRetries + "x" + this.sleepMillis + "ms";
            }
        }
    }
    
    static class ExceptionDependentRetry implements RetryPolicy
    {
        RetryPolicy defaultPolicy;
        Map<Class<? extends Exception>, RetryPolicy> exceptionToPolicyMap;
        
        public ExceptionDependentRetry(final RetryPolicy defaultPolicy, final Map<Class<? extends Exception>, RetryPolicy> exceptionToPolicyMap) {
            this.defaultPolicy = defaultPolicy;
            this.exceptionToPolicyMap = exceptionToPolicyMap;
        }
        
        @Override
        public RetryAction shouldRetry(final Exception e, final int retries, final int failovers, final boolean isIdempotentOrAtMostOnce) throws Exception {
            RetryPolicy policy = this.exceptionToPolicyMap.get(e.getClass());
            if (policy == null) {
                policy = this.defaultPolicy;
            }
            return policy.shouldRetry(e, retries, failovers, isIdempotentOrAtMostOnce);
        }
    }
    
    static class RemoteExceptionDependentRetry implements RetryPolicy
    {
        RetryPolicy defaultPolicy;
        Map<String, RetryPolicy> exceptionNameToPolicyMap;
        
        public RemoteExceptionDependentRetry(final RetryPolicy defaultPolicy, final Map<Class<? extends Exception>, RetryPolicy> exceptionToPolicyMap) {
            this.defaultPolicy = defaultPolicy;
            this.exceptionNameToPolicyMap = new HashMap<String, RetryPolicy>();
            for (final Map.Entry<Class<? extends Exception>, RetryPolicy> e : exceptionToPolicyMap.entrySet()) {
                this.exceptionNameToPolicyMap.put(e.getKey().getName(), e.getValue());
            }
        }
        
        @Override
        public RetryAction shouldRetry(final Exception e, final int retries, final int failovers, final boolean isIdempotentOrAtMostOnce) throws Exception {
            RetryPolicy policy = null;
            if (e instanceof RemoteException) {
                policy = this.exceptionNameToPolicyMap.get(((RemoteException)e).getClassName());
            }
            if (policy == null) {
                policy = this.defaultPolicy;
            }
            return policy.shouldRetry(e, retries, failovers, isIdempotentOrAtMostOnce);
        }
    }
    
    static class OtherThanRemoteExceptionDependentRetry implements RetryPolicy
    {
        private RetryPolicy defaultPolicy;
        private Map<Class<? extends Exception>, RetryPolicy> exceptionToPolicyMap;
        
        public OtherThanRemoteExceptionDependentRetry(final RetryPolicy defaultPolicy, final Map<Class<? extends Exception>, RetryPolicy> exceptionToPolicyMap) {
            this.defaultPolicy = defaultPolicy;
            this.exceptionToPolicyMap = exceptionToPolicyMap;
        }
        
        @Override
        public RetryAction shouldRetry(final Exception e, final int retries, final int failovers, final boolean isIdempotentOrAtMostOnce) throws Exception {
            RetryPolicy policy = null;
            if (!(e instanceof RemoteException)) {
                policy = this.exceptionToPolicyMap.get(e.getClass());
            }
            if (policy == null) {
                policy = this.defaultPolicy;
            }
            return policy.shouldRetry(e, retries, failovers, isIdempotentOrAtMostOnce);
        }
    }
    
    static class ExponentialBackoffRetry extends RetryLimited
    {
        public ExponentialBackoffRetry(final int maxRetries, final long sleepTime, final TimeUnit timeUnit) {
            super(maxRetries, sleepTime, timeUnit);
            if (maxRetries < 0) {
                throw new IllegalArgumentException("maxRetries = " + maxRetries + " < 0");
            }
            if (maxRetries >= 63) {
                throw new IllegalArgumentException("maxRetries = " + maxRetries + " >= " + 63);
            }
        }
        
        @Override
        protected long calculateSleepTime(final int retries) {
            return calculateExponentialTime(this.sleepTime, retries + 1);
        }
    }
    
    static class FailoverOnNetworkExceptionRetry implements RetryPolicy
    {
        private RetryPolicy fallbackPolicy;
        private int maxFailovers;
        private int maxRetries;
        private long delayMillis;
        private long maxDelayBase;
        
        public FailoverOnNetworkExceptionRetry(final RetryPolicy fallbackPolicy, final int maxFailovers) {
            this(fallbackPolicy, maxFailovers, 0, 0L, 0L);
        }
        
        public FailoverOnNetworkExceptionRetry(final RetryPolicy fallbackPolicy, final int maxFailovers, final long delayMillis, final long maxDelayBase) {
            this(fallbackPolicy, maxFailovers, 0, delayMillis, maxDelayBase);
        }
        
        public FailoverOnNetworkExceptionRetry(final RetryPolicy fallbackPolicy, final int maxFailovers, final int maxRetries, final long delayMillis, final long maxDelayBase) {
            this.fallbackPolicy = fallbackPolicy;
            this.maxFailovers = maxFailovers;
            this.maxRetries = maxRetries;
            this.delayMillis = delayMillis;
            this.maxDelayBase = maxDelayBase;
        }
        
        private long getFailoverOrRetrySleepTime(final int times) {
            return (times == 0) ? 0L : calculateExponentialTime(this.delayMillis, times, this.maxDelayBase);
        }
        
        @Override
        public RetryAction shouldRetry(final Exception e, final int retries, final int failovers, final boolean isIdempotentOrAtMostOnce) throws Exception {
            if (failovers >= this.maxFailovers) {
                return new RetryAction(RetryAction.RetryDecision.FAIL, 0L, "failovers (" + failovers + ") exceeded maximum allowed (" + this.maxFailovers + ")");
            }
            if (retries - failovers > this.maxRetries) {
                return new RetryAction(RetryAction.RetryDecision.FAIL, 0L, "retries (" + retries + ") exceeded maximum allowed (" + this.maxRetries + ")");
            }
            if (isSaslFailure(e)) {
                return new RetryAction(RetryAction.RetryDecision.FAIL, 0L, "SASL failure");
            }
            if (e instanceof ConnectException || e instanceof EOFException || e instanceof NoRouteToHostException || e instanceof UnknownHostException || e instanceof StandbyException || e instanceof ConnectTimeoutException || isWrappedStandbyException(e)) {
                return new RetryAction(RetryAction.RetryDecision.FAILOVER_AND_RETRY, this.getFailoverOrRetrySleepTime(failovers));
            }
            if (e instanceof RetriableException || RetryPolicies.getWrappedRetriableException(e) != null) {
                return new RetryAction(RetryAction.RetryDecision.RETRY, this.getFailoverOrRetrySleepTime(retries));
            }
            if (e instanceof SecretManager.InvalidToken) {
                return new RetryAction(RetryAction.RetryDecision.FAIL, 0L, "Invalid or Cancelled Token");
            }
            if (!(e instanceof SocketException) && (!(e instanceof IOException) || e instanceof RemoteException)) {
                return this.fallbackPolicy.shouldRetry(e, retries, failovers, isIdempotentOrAtMostOnce);
            }
            if (isIdempotentOrAtMostOnce) {
                return new RetryAction(RetryAction.RetryDecision.FAILOVER_AND_RETRY, this.getFailoverOrRetrySleepTime(retries));
            }
            return new RetryAction(RetryAction.RetryDecision.FAIL, 0L, "the invoked method is not idempotent, and unable to determine whether it was invoked");
        }
    }
}
