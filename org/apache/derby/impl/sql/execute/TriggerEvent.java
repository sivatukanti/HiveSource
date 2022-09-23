// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

public class TriggerEvent
{
    static final int BEFORE_INSERT = 0;
    static final int BEFORE_DELETE = 1;
    static final int BEFORE_UPDATE = 2;
    static final int LAST_BEFORE_EVENT = 2;
    static final int AFTER_INSERT = 3;
    static final int AFTER_DELETE = 4;
    static final int AFTER_UPDATE = 5;
    static final int MAX_EVENTS = 6;
    private static final String[] Names;
    private boolean before;
    private int type;
    
    TriggerEvent(final int type) {
        switch (this.type = type) {
            case 0:
            case 1:
            case 2: {
                this.before = true;
                break;
            }
            case 3:
            case 4:
            case 5: {
                this.before = false;
                break;
            }
        }
    }
    
    int getNumber() {
        return this.type;
    }
    
    String getName() {
        return TriggerEvent.Names[this.type];
    }
    
    boolean isBefore() {
        return this.before;
    }
    
    boolean isAfter() {
        return !this.before;
    }
    
    static {
        Names = new String[] { "BEFORE INSERT", "BEFORE DELETE", "BEFORE UPDATE", "AFTER INSERT", "AFTER DELETE", "AFTER UPDATE" };
    }
}
