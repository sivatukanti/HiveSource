// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.state;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class InvalidStateTransitonException extends YarnRuntimeException
{
    private static final long serialVersionUID = 8610511635996283691L;
    private Enum<?> currentState;
    private Enum<?> event;
    
    public InvalidStateTransitonException(final Enum<?> currentState, final Enum<?> event) {
        super("Invalid event: " + event + " at " + currentState);
        this.currentState = currentState;
        this.event = event;
    }
    
    public Enum<?> getCurrentState() {
        return this.currentState;
    }
    
    public Enum<?> getEvent() {
        return this.event;
    }
}
