// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.listener;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import javax.jdo.spi.I18NHelper;
import java.util.EventObject;

public class InstanceLifecycleEvent extends EventObject
{
    private static final int FIRST_EVENT_TYPE = 0;
    public static final int CREATE = 0;
    public static final int LOAD = 1;
    public static final int STORE = 2;
    public static final int CLEAR = 3;
    public static final int DELETE = 4;
    public static final int DIRTY = 5;
    public static final int DETACH = 6;
    public static final int ATTACH = 7;
    private static final int LAST_EVENT_TYPE = 7;
    private static final I18NHelper msg;
    private final int eventType;
    private final Object target;
    
    public InstanceLifecycleEvent(final Object source, final int type) {
        this(source, type, null);
    }
    
    public InstanceLifecycleEvent(final Object source, final int type, final Object target) {
        super(source);
        if (type < 0 || type > 7) {
            throw new IllegalArgumentException(InstanceLifecycleEvent.msg.msg("EXC_IllegalEventType"));
        }
        this.eventType = type;
        this.target = target;
    }
    
    public int getEventType() {
        return this.eventType;
    }
    
    @Override
    public Object getSource() {
        return super.getSource();
    }
    
    public Object getTarget() {
        return this.target;
    }
    
    public Object getPersistentInstance() {
        switch (this.getEventType()) {
            case 6: {
                return (this.target == null) ? this.getSource() : this.getTarget();
            }
            case 7: {
                return (this.target == null) ? null : this.getSource();
            }
            default: {
                return this.getSource();
            }
        }
    }
    
    public Object getDetachedInstance() {
        switch (this.getEventType()) {
            case 6: {
                return (this.target == null) ? null : this.getSource();
            }
            case 7: {
                return (this.target == null) ? this.getSource() : this.getTarget();
            }
            default: {
                return null;
            }
        }
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        throw new NotSerializableException();
    }
    
    static {
        msg = I18NHelper.getInstance("javax.jdo.Bundle");
    }
}
