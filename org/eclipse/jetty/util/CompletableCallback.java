// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.concurrent.atomic.AtomicReference;

public abstract class CompletableCallback implements Callback
{
    private final AtomicReference<State> state;
    
    public CompletableCallback() {
        this.state = new AtomicReference<State>(State.IDLE);
    }
    
    @Override
    public void succeeded() {
        while (true) {
            final State current = this.state.get();
            switch (current) {
                case IDLE: {
                    if (this.state.compareAndSet(current, State.SUCCEEDED)) {
                        return;
                    }
                    continue;
                }
                case COMPLETED: {
                    if (this.state.compareAndSet(current, State.SUCCEEDED)) {
                        this.resume();
                        return;
                    }
                    continue;
                }
                case FAILED: {}
                default: {
                    throw new IllegalStateException(current.toString());
                }
            }
        }
    }
    
    @Override
    public void failed(final Throwable x) {
        while (true) {
            final State current = this.state.get();
            switch (current) {
                case IDLE:
                case COMPLETED: {
                    if (this.state.compareAndSet(current, State.FAILED)) {
                        this.abort(x);
                        return;
                    }
                    continue;
                }
                case FAILED: {}
                default: {
                    throw new IllegalStateException(current.toString());
                }
            }
        }
    }
    
    public abstract void resume();
    
    public abstract void abort(final Throwable p0);
    
    public boolean tryComplete() {
        while (true) {
            final State current = this.state.get();
            switch (current) {
                case IDLE: {
                    if (this.state.compareAndSet(current, State.COMPLETED)) {
                        return true;
                    }
                    continue;
                }
                case FAILED:
                case SUCCEEDED: {
                    return false;
                }
                default: {
                    throw new IllegalStateException(current.toString());
                }
            }
        }
    }
    
    private enum State
    {
        IDLE, 
        SUCCEEDED, 
        FAILED, 
        COMPLETED;
    }
}
