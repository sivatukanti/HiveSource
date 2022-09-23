// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.state;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface MultipleArcTransition<OPERAND, EVENT, STATE extends Enum<STATE>>
{
    STATE transition(final OPERAND p0, final EVENT p1);
}
