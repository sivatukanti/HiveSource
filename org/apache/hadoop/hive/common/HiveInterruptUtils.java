// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class HiveInterruptUtils
{
    private static List<HiveInterruptCallback> interruptCallbacks;
    
    public static HiveInterruptCallback add(final HiveInterruptCallback command) {
        synchronized (HiveInterruptUtils.interruptCallbacks) {
            HiveInterruptUtils.interruptCallbacks.add(command);
        }
        return command;
    }
    
    public static HiveInterruptCallback remove(final HiveInterruptCallback command) {
        synchronized (HiveInterruptUtils.interruptCallbacks) {
            HiveInterruptUtils.interruptCallbacks.remove(command);
        }
        return command;
    }
    
    public static void interrupt() {
        synchronized (HiveInterruptUtils.interruptCallbacks) {
            for (final HiveInterruptCallback resource : new ArrayList<HiveInterruptCallback>(HiveInterruptUtils.interruptCallbacks)) {
                resource.interrupt();
            }
        }
    }
    
    public static void checkInterrupted() {
        if (Thread.currentThread().isInterrupted()) {
            InterruptedException interrupt = null;
            try {
                Thread.sleep(0L);
            }
            catch (InterruptedException e) {
                interrupt = e;
            }
            throw new RuntimeException("Interuppted", interrupt);
        }
    }
    
    static {
        HiveInterruptUtils.interruptCallbacks = new ArrayList<HiveInterruptCallback>();
    }
}
