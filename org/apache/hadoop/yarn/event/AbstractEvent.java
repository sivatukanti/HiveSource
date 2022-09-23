// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.event;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class AbstractEvent<TYPE extends Enum<TYPE>> implements Event<TYPE>
{
    private final TYPE type;
    private final long timestamp;
    
    public AbstractEvent(final TYPE type) {
        this.type = type;
        this.timestamp = -1L;
    }
    
    public AbstractEvent(final TYPE type, final long timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }
    
    @Override
    public long getTimestamp() {
        return this.timestamp;
    }
    
    @Override
    public TYPE getType() {
        return this.type;
    }
    
    @Override
    public String toString() {
        return "EventType: " + this.getType();
    }
}
