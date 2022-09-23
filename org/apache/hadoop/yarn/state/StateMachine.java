// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.state;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface StateMachine<STATE extends Enum<STATE>, EVENTTYPE extends Enum<EVENTTYPE>, EVENT>
{
    STATE getCurrentState();
    
    STATE doTransition(final EVENTTYPE p0, final EVENT p1) throws InvalidStateTransitonException;
}
