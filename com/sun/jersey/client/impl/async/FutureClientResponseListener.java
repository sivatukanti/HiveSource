// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.client.impl.async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.async.FutureListener;
import java.util.concurrent.FutureTask;

public abstract class FutureClientResponseListener<T> extends FutureTask<T> implements FutureListener<ClientResponse>
{
    private static final Callable NO_OP_CALLABLE;
    private Future<ClientResponse> f;
    
    public FutureClientResponseListener() {
        super(FutureClientResponseListener.NO_OP_CALLABLE);
    }
    
    public void setCancelableFuture(final Future<ClientResponse> f) {
        this.f = f;
    }
    
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        if (this.f.isCancelled()) {
            if (!super.isCancelled()) {
                super.cancel(true);
            }
            return false;
        }
        final boolean cancelled = this.f.cancel(mayInterruptIfRunning);
        if (cancelled) {
            super.cancel(true);
        }
        return cancelled;
    }
    
    @Override
    public boolean isCancelled() {
        if (this.f.isCancelled()) {
            if (!super.isCancelled()) {
                super.cancel(true);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void onComplete(final Future<ClientResponse> response) {
        try {
            this.set(this.get(response.get()));
        }
        catch (CancellationException ex2) {
            super.cancel(true);
        }
        catch (ExecutionException ex) {
            this.setException(ex.getCause());
        }
        catch (Throwable t) {
            this.setException(t);
        }
    }
    
    protected abstract T get(final ClientResponse p0);
    
    static {
        NO_OP_CALLABLE = new Callable() {
            @Override
            public Object call() throws Exception {
                throw new IllegalStateException();
            }
        };
    }
}
