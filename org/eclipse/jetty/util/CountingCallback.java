// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.concurrent.atomic.AtomicInteger;

public class CountingCallback extends Callback.Nested
{
    private final AtomicInteger count;
    
    public CountingCallback(final Callback callback, final int count) {
        super(callback);
        if (count < 1) {
            throw new IllegalArgumentException();
        }
        this.count = new AtomicInteger(count);
    }
    
    @Override
    public void succeeded() {
        while (true) {
            final int current = this.count.get();
            if (current == 0) {
                return;
            }
            if (this.count.compareAndSet(current, current - 1)) {
                if (current == 1) {
                    super.succeeded();
                }
            }
        }
    }
    
    @Override
    public void failed(final Throwable failure) {
        while (true) {
            final int current = this.count.get();
            if (current == 0) {
                return;
            }
            if (this.count.compareAndSet(current, 0)) {
                super.failed(failure);
            }
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x", this.getClass().getSimpleName(), this.hashCode());
    }
}
