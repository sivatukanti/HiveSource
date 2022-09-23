// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.event;

public final class EventListenerRegistrationData<T extends Event>
{
    private static final int HASH_FACTOR = 31;
    private final EventType<T> eventType;
    private final EventListener<? super T> listener;
    
    public EventListenerRegistrationData(final EventType<T> type, final EventListener<? super T> lstnr) {
        if (type == null) {
            throw new IllegalArgumentException("Event type must not be null!");
        }
        if (lstnr == null) {
            throw new IllegalArgumentException("Listener to be registered must not be null!");
        }
        this.eventType = type;
        this.listener = lstnr;
    }
    
    public EventType<T> getEventType() {
        return this.eventType;
    }
    
    public EventListener<? super T> getListener() {
        return this.listener;
    }
    
    @Override
    public int hashCode() {
        int result = this.eventType.hashCode();
        result = 31 * result + this.listener.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EventListenerRegistrationData)) {
            return false;
        }
        final EventListenerRegistrationData<?> c = (EventListenerRegistrationData<?>)obj;
        return this.getListener() == c.getListener() && this.getEventType().equals(c.getEventType());
    }
}
