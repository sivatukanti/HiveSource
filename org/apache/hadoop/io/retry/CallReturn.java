// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.retry;

import com.google.common.base.Preconditions;

class CallReturn
{
    static final CallReturn ASYNC_CALL_IN_PROGRESS;
    static final CallReturn ASYNC_INVOKED;
    static final CallReturn RETRY;
    static final CallReturn WAIT_RETRY;
    private final Object returnValue;
    private final Throwable thrown;
    private final State state;
    
    CallReturn(final Object r) {
        this(r, null, State.RETURNED);
    }
    
    CallReturn(final Throwable t) {
        this(null, t, State.EXCEPTION);
        Preconditions.checkNotNull(t);
    }
    
    private CallReturn(final State s) {
        this(null, null, s);
    }
    
    private CallReturn(final Object r, final Throwable t, final State s) {
        Preconditions.checkArgument(r == null || t == null);
        this.returnValue = r;
        this.thrown = t;
        this.state = s;
    }
    
    State getState() {
        return this.state;
    }
    
    Object getReturnValue() throws Throwable {
        if (this.state == State.EXCEPTION) {
            throw this.thrown;
        }
        Preconditions.checkState(this.state == State.RETURNED, "state == %s", this.state);
        return this.returnValue;
    }
    
    static {
        ASYNC_CALL_IN_PROGRESS = new CallReturn(State.ASYNC_CALL_IN_PROGRESS);
        ASYNC_INVOKED = new CallReturn(State.ASYNC_INVOKED);
        RETRY = new CallReturn(State.RETRY);
        WAIT_RETRY = new CallReturn(State.WAIT_RETRY);
    }
    
    enum State
    {
        RETURNED, 
        EXCEPTION, 
        RETRY, 
        WAIT_RETRY, 
        ASYNC_CALL_IN_PROGRESS, 
        ASYNC_INVOKED;
    }
}
