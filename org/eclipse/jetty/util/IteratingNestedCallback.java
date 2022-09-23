// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

public abstract class IteratingNestedCallback extends IteratingCallback
{
    final Callback _callback;
    
    public IteratingNestedCallback(final Callback callback) {
        this._callback = callback;
    }
    
    @Override
    public boolean isNonBlocking() {
        return this._callback.isNonBlocking();
    }
    
    @Override
    protected void onCompleteSuccess() {
        this._callback.succeeded();
    }
    
    @Override
    protected void onCompleteFailure(final Throwable x) {
        this._callback.failed(x);
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x", this.getClass().getSimpleName(), this.hashCode());
    }
}
