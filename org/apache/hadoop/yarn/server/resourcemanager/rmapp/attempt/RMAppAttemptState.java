// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt;

public enum RMAppAttemptState
{
    NEW, 
    SUBMITTED, 
    SCHEDULED, 
    ALLOCATED, 
    LAUNCHED, 
    FAILED, 
    RUNNING, 
    FINISHING, 
    FINISHED, 
    KILLED, 
    ALLOCATED_SAVING, 
    LAUNCHED_UNMANAGED_SAVING, 
    FINAL_SAVING;
}
