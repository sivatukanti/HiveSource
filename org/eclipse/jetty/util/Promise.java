// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Objects;
import org.eclipse.jetty.util.log.Log;

public interface Promise<C>
{
    void succeeded(final C p0);
    
    void failed(final Throwable p0);
    
    public static class Adapter<C> implements Promise<C>
    {
        @Override
        public void succeeded(final C result) {
        }
        
        @Override
        public void failed(final Throwable x) {
            Log.getLogger(this.getClass()).warn(x);
        }
    }
    
    public static class Wrapper<W> implements Promise<W>
    {
        private final Promise<W> promise;
        
        public Wrapper(final Promise<W> promise) {
            this.promise = Objects.requireNonNull(promise);
        }
        
        @Override
        public void succeeded(final W result) {
            this.promise.succeeded(result);
        }
        
        @Override
        public void failed(final Throwable x) {
            this.promise.failed(x);
        }
        
        public Promise<W> getPromise() {
            return this.promise;
        }
        
        public Promise<W> unwrap() {
            Promise<W> result;
            for (result = this.promise; result instanceof Wrapper; result = (Promise<W>)((Wrapper)result).unwrap()) {}
            return result;
        }
    }
}
