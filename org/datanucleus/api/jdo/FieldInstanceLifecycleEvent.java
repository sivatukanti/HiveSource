// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import javax.jdo.listener.InstanceLifecycleEvent;

public class FieldInstanceLifecycleEvent extends InstanceLifecycleEvent
{
    private String[] fieldNames;
    
    public FieldInstanceLifecycleEvent(final Object obj, final int eventType, final Object otherObj, final String[] fieldNames) {
        super(obj, eventType, otherObj);
        this.fieldNames = fieldNames;
    }
    
    public String[] getFieldNames() {
        return this.fieldNames;
    }
}
