// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

import java.util.EventObject;

public abstract class Event extends EventObject
{
    private int type;
    
    public Event(final Object source) {
        super(source);
    }
    
    public Event(final Object source, final int type) {
        super(source);
        this.setType(type);
    }
    
    public int getType() {
        return this.type;
    }
    
    void setType(final int type) {
        this.type = type;
    }
    
    void setValues(final int type) {
        this.setType(type);
    }
}
