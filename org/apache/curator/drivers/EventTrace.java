// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.drivers;

public class EventTrace
{
    private final String name;
    private final TracerDriver driver;
    private final long sessionId;
    
    public EventTrace(final String name, final TracerDriver driver) {
        this(name, driver, -1L);
    }
    
    public EventTrace(final String name, final TracerDriver driver, final long sessionId) {
        this.name = name;
        this.driver = driver;
        this.sessionId = sessionId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public long getSessionId() {
        return this.sessionId;
    }
    
    public void commit() {
        if (this.driver instanceof AdvancedTracerDriver) {
            ((AdvancedTracerDriver)this.driver).addEvent(this);
        }
        else {
            this.driver.addCount(this.name, 1);
        }
    }
}
