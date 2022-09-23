// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.memory;

public class LowMemory
{
    private long lowMemory;
    private long whenLowMemorySet;
    
    public void setLowMemory() {
        if (this.lowMemory == 0L) {
            boolean b = false;
            for (int i = 0; i < 5; ++i) {
                System.gc();
                System.runFinalization();
                try {
                    Thread.sleep(50L);
                }
                catch (InterruptedException ex) {
                    b = true;
                }
            }
            if (b) {
                Thread.currentThread().interrupt();
            }
        }
        synchronized (this) {
            if (this.lowMemory == 0L) {
                this.lowMemory = Runtime.getRuntime().freeMemory();
                this.whenLowMemorySet = System.currentTimeMillis();
            }
        }
    }
    
    public boolean isLowMemory() {
        synchronized (this) {
            final long lowMemory = this.lowMemory;
            if (lowMemory == 0L) {
                return false;
            }
            if (Runtime.getRuntime().freeMemory() > lowMemory) {
                return false;
            }
            if (System.currentTimeMillis() - this.whenLowMemorySet > 5000L) {
                this.lowMemory = 0L;
                this.whenLowMemorySet = 0L;
                return false;
            }
            return true;
        }
    }
}
