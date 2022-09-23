// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

public class TriggerEvents
{
    public static final TriggerEvent BEFORE_INSERT;
    public static final TriggerEvent BEFORE_DELETE;
    public static final TriggerEvent BEFORE_UPDATE;
    public static final TriggerEvent AFTER_INSERT;
    public static final TriggerEvent AFTER_DELETE;
    public static final TriggerEvent AFTER_UPDATE;
    
    static {
        BEFORE_INSERT = new TriggerEvent(0);
        BEFORE_DELETE = new TriggerEvent(1);
        BEFORE_UPDATE = new TriggerEvent(2);
        AFTER_INSERT = new TriggerEvent(3);
        AFTER_DELETE = new TriggerEvent(4);
        AFTER_UPDATE = new TriggerEvent(5);
    }
}
