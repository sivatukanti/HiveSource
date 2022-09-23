// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.concurrent.atomic.AtomicBoolean;
import java.security.PrivilegedExceptionAction;

public abstract class ExternalCall<T> extends Server.Call
{
    private final PrivilegedExceptionAction<T> action;
    private final AtomicBoolean done;
    private T result;
    private Throwable error;
    
    public ExternalCall(final PrivilegedExceptionAction<T> action) {
        this.done = new AtomicBoolean();
        this.action = action;
    }
    
    @Override
    public abstract UserGroupInformation getRemoteUser();
    
    public final T get() throws InterruptedException, ExecutionException {
        this.waitForCompletion();
        if (this.error != null) {
            throw new ExecutionException(this.error);
        }
        return this.result;
    }
    
    private void waitForCompletion() throws InterruptedException {
        synchronized (this.done) {
            while (!this.done.get()) {
                try {
                    this.done.wait();
                }
                catch (InterruptedException ie) {
                    if (Thread.interrupted()) {
                        throw ie;
                    }
                    continue;
                }
            }
        }
    }
    
    boolean isDone() {
        return this.done.get();
    }
    
    @Override
    public final Void run() throws IOException {
        try {
            this.result = this.action.run();
            this.sendResponse();
        }
        catch (Throwable t) {
            this.abortResponse(t);
        }
        return null;
    }
    
    @Override
    final void doResponse(final Throwable t) {
        synchronized (this.done) {
            this.error = t;
            this.done.set(true);
            this.done.notify();
        }
    }
}
