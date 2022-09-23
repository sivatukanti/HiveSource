// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp;

public enum RMAppState
{
    NEW, 
    NEW_SAVING, 
    SUBMITTED, 
    ACCEPTED, 
    RUNNING, 
    FINAL_SAVING, 
    FINISHING, 
    FINISHED, 
    FAILED, 
    KILLING, 
    KILLED;
}
