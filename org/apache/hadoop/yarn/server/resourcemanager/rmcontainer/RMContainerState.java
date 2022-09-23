// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmcontainer;

public enum RMContainerState
{
    NEW, 
    RESERVED, 
    ALLOCATED, 
    ACQUIRED, 
    RUNNING, 
    COMPLETED, 
    EXPIRED, 
    RELEASED, 
    KILLED;
}
