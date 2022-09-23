// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmnode;

public enum RMNodeEventType
{
    STARTED, 
    DECOMMISSION, 
    RESOURCE_UPDATE, 
    STATUS_UPDATE, 
    REBOOTING, 
    RECONNECTED, 
    CLEANUP_APP, 
    CONTAINER_ALLOCATED, 
    CLEANUP_CONTAINER, 
    FINISHED_CONTAINERS_PULLED_BY_AM, 
    EXPIRE;
}
