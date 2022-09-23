// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

public class ActivityState
{
    public static final ActivityState NONE;
    public static final ActivityState INSERTING;
    public static final ActivityState INSERTING_CALLBACKS;
    public static final ActivityState DELETING;
    private final int typeId;
    
    private ActivityState(final int i) {
        this.typeId = i;
    }
    
    @Override
    public int hashCode() {
        return this.typeId;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ActivityState && ((ActivityState)o).typeId == this.typeId;
    }
    
    @Override
    public String toString() {
        switch (this.typeId) {
            case 0: {
                return "none";
            }
            case 1: {
                return "inserting";
            }
            case 2: {
                return "inserting-callback";
            }
            case 3: {
                return "deleting";
            }
            default: {
                return "";
            }
        }
    }
    
    public int getType() {
        return this.typeId;
    }
    
    public static ActivityState getActivityState(final String value) {
        if (value == null) {
            return ActivityState.NONE;
        }
        if (ActivityState.NONE.toString().equals(value)) {
            return ActivityState.NONE;
        }
        if (ActivityState.INSERTING.toString().equals(value)) {
            return ActivityState.INSERTING;
        }
        if (ActivityState.INSERTING_CALLBACKS.toString().equals(value)) {
            return ActivityState.INSERTING_CALLBACKS;
        }
        if (ActivityState.DELETING.toString().equals(value)) {
            return ActivityState.DELETING;
        }
        return ActivityState.NONE;
    }
    
    static {
        NONE = new ActivityState(0);
        INSERTING = new ActivityState(1);
        INSERTING_CALLBACKS = new ActivityState(2);
        DELETING = new ActivityState(3);
    }
}
