// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public enum ReservationRequestInterpreter
{
    R_ANY, 
    R_ALL, 
    R_ORDER, 
    R_ORDER_NO_GAP;
}
