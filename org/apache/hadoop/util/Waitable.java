// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.concurrent.locks.Condition;

public class Waitable<T>
{
    private T val;
    private final Condition cond;
    
    public Waitable(final Condition cond) {
        this.val = null;
        this.cond = cond;
    }
    
    public T await() throws InterruptedException {
        while (this.val == null) {
            this.cond.await();
        }
        return this.val;
    }
    
    public void provide(final T val) {
        this.val = val;
        this.cond.signalAll();
    }
    
    public boolean hasVal() {
        return this.val != null;
    }
    
    public T getVal() {
        return this.val;
    }
}
