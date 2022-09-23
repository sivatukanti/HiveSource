// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.common;

public class QueueEntitlement
{
    private float capacity;
    private float maxCapacity;
    
    public QueueEntitlement(final float capacity, final float maxCapacity) {
        this.setCapacity(capacity);
        this.maxCapacity = maxCapacity;
    }
    
    public float getMaxCapacity() {
        return this.maxCapacity;
    }
    
    public void setMaxCapacity(final float maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    
    public float getCapacity() {
        return this.capacity;
    }
    
    public void setCapacity(final float capacity) {
        this.capacity = capacity;
    }
}
