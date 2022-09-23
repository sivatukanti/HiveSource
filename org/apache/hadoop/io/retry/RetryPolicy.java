// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.retry;

import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Evolving
public interface RetryPolicy
{
    RetryAction shouldRetry(final Exception p0, final int p1, final int p2, final boolean p3) throws Exception;
    
    @InterfaceStability.Evolving
    public static class RetryAction
    {
        public static final RetryAction FAIL;
        public static final RetryAction RETRY;
        public static final RetryAction FAILOVER_AND_RETRY;
        public final RetryDecision action;
        public final long delayMillis;
        public final String reason;
        
        public RetryAction(final RetryDecision action) {
            this(action, 0L, null);
        }
        
        public RetryAction(final RetryDecision action, final long delayTime) {
            this(action, delayTime, null);
        }
        
        public RetryAction(final RetryDecision action, final long delayTime, final String reason) {
            this.action = action;
            this.delayMillis = delayTime;
            this.reason = reason;
        }
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(action=" + this.action + ", delayMillis=" + this.delayMillis + ", reason=" + this.reason + ")";
        }
        
        static {
            FAIL = new RetryAction(RetryDecision.FAIL);
            RETRY = new RetryAction(RetryDecision.RETRY);
            FAILOVER_AND_RETRY = new RetryAction(RetryDecision.FAILOVER_AND_RETRY);
        }
        
        public enum RetryDecision
        {
            FAIL, 
            RETRY, 
            FAILOVER_AND_RETRY;
        }
    }
}
