// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public enum YarnApplicationState
{
    NEW, 
    NEW_SAVING, 
    SUBMITTED, 
    ACCEPTED, 
    RUNNING, 
    FINISHED, 
    FAILED, 
    KILLED;
}
