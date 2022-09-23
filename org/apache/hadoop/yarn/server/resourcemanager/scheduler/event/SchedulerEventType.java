// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.event;

public enum SchedulerEventType
{
    NODE_ADDED, 
    NODE_REMOVED, 
    NODE_UPDATE, 
    NODE_RESOURCE_UPDATE, 
    APP_ADDED, 
    APP_REMOVED, 
    APP_ATTEMPT_ADDED, 
    APP_ATTEMPT_REMOVED, 
    CONTAINER_EXPIRED;
}
