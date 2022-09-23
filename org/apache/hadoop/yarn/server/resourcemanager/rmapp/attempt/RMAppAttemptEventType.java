// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt;

public enum RMAppAttemptEventType
{
    START, 
    KILL, 
    LAUNCHED, 
    LAUNCH_FAILED, 
    EXPIRE, 
    REGISTERED, 
    STATUS_UPDATE, 
    UNREGISTERED, 
    CONTAINER_ALLOCATED, 
    CONTAINER_FINISHED, 
    ATTEMPT_NEW_SAVED, 
    ATTEMPT_UPDATE_SAVED, 
    ATTEMPT_ADDED, 
    RECOVER;
}
