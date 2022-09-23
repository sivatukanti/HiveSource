// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.event;

import java.util.EventObject;

public class Event extends EventObject
{
    public static final EventType<Event> ANY;
    private static final String FMT_PROPERTY = " %s=%s";
    private static final int BUF_SIZE = 256;
    private final EventType<? extends Event> eventType;
    
    public Event(final Object source, final EventType<? extends Event> evType) {
        super(source);
        if (evType == null) {
            throw new IllegalArgumentException("Event type must not be null!");
        }
        this.eventType = evType;
    }
    
    public EventType<? extends Event> getEventType() {
        return this.eventType;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(256);
        buf.append(this.getClass().getSimpleName());
        buf.append(" [");
        this.appendPropertyRepresentation(buf, "source", this.getSource());
        this.appendPropertyRepresentation(buf, "eventType", this.getEventType());
        buf.append(" ]");
        return buf.toString();
    }
    
    protected void appendPropertyRepresentation(final StringBuilder buf, final String property, final Object value) {
        buf.append(String.format(" %s=%s", property, String.valueOf(value)));
    }
    
    static {
        ANY = new EventType<Event>(null, "ANY");
    }
}
