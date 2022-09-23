// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Atomics
{
    private Atomics() {
    }
    
    public static boolean updateMin(final AtomicLong currentMin, final long newValue) {
        for (long oldValue = currentMin.get(); newValue < oldValue; oldValue = currentMin.get()) {
            if (currentMin.compareAndSet(oldValue, newValue)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean updateMax(final AtomicLong currentMax, final long newValue) {
        for (long oldValue = currentMax.get(); newValue > oldValue; oldValue = currentMax.get()) {
            if (currentMax.compareAndSet(oldValue, newValue)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean updateMin(final AtomicInteger currentMin, final int newValue) {
        for (int oldValue = currentMin.get(); newValue < oldValue; oldValue = currentMin.get()) {
            if (currentMin.compareAndSet(oldValue, newValue)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean updateMax(final AtomicInteger currentMax, final int newValue) {
        for (int oldValue = currentMax.get(); newValue > oldValue; oldValue = currentMax.get()) {
            if (currentMax.compareAndSet(oldValue, newValue)) {
                return true;
            }
        }
        return false;
    }
}
