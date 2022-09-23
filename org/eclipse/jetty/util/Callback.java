// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

public interface Callback
{
    public static final Callback NOOP = new NonBlocking() {};
    
    default void succeeded() {
    }
    
    default void failed(final Throwable x) {
    }
    
    default boolean isNonBlocking() {
        return false;
    }
    
    public interface NonBlocking extends Callback
    {
        default boolean isNonBlocking() {
            return true;
        }
    }
    
    public static class Nested implements Callback
    {
        private final Callback callback;
        
        public Nested(final Callback callback) {
            this.callback = callback;
        }
        
        public Nested(final Nested nested) {
            this.callback = nested.callback;
        }
        
        @Override
        public void succeeded() {
            this.callback.succeeded();
        }
        
        @Override
        public void failed(final Throwable x) {
            this.callback.failed(x);
        }
        
        @Override
        public boolean isNonBlocking() {
            return this.callback.isNonBlocking();
        }
    }
    
    @Deprecated
    public static class Adapter implements Callback
    {
    }
}
