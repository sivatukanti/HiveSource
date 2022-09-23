// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmcontainer;

public enum RMContainerEventType
{
    START, 
    ACQUIRED, 
    KILL, 
    RESERVED, 
    LAUNCHED, 
    FINISHED, 
    RELEASED, 
    EXPIRE, 
    RECOVER;
}
