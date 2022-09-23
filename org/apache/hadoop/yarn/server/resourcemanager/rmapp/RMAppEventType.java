// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp;

public enum RMAppEventType
{
    START, 
    RECOVER, 
    KILL, 
    MOVE, 
    APP_REJECTED, 
    APP_ACCEPTED, 
    ATTEMPT_REGISTERED, 
    ATTEMPT_UNREGISTERED, 
    ATTEMPT_FINISHED, 
    ATTEMPT_FAILED, 
    ATTEMPT_KILLED, 
    NODE_UPDATE, 
    APP_RUNNING_ON_NODE, 
    APP_NEW_SAVED, 
    APP_UPDATE_SAVED;
}
