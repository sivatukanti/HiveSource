// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.event;

import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Collection;

public class BaseEventSource implements EventSource
{
    private EventListenerList eventListeners;
    private final Object lockDetailEventsCount;
    private int detailEvents;
    
    public BaseEventSource() {
        this.lockDetailEventsCount = new Object();
        this.initListeners();
    }
    
    public <T extends Event> Collection<EventListener<? super T>> getEventListeners(final EventType<T> eventType) {
        final List<EventListener<? super T>> result = new LinkedList<EventListener<? super T>>();
        for (final EventListener<? super T> l : this.eventListeners.getEventListeners(eventType)) {
            result.add(l);
        }
        return Collections.unmodifiableCollection((Collection<? extends EventListener<? super T>>)result);
    }
    
    public List<EventListenerRegistrationData<?>> getEventListenerRegistrations() {
        return this.eventListeners.getRegistrations();
    }
    
    public boolean isDetailEvents() {
        return this.checkDetailEvents(0);
    }
    
    public void setDetailEvents(final boolean enable) {
        synchronized (this.lockDetailEventsCount) {
            if (enable) {
                ++this.detailEvents;
            }
            else {
                --this.detailEvents;
            }
        }
    }
    
    @Override
    public <T extends Event> void addEventListener(final EventType<T> eventType, final EventListener<? super T> listener) {
        this.eventListeners.addEventListener(eventType, listener);
    }
    
    @Override
    public <T extends Event> boolean removeEventListener(final EventType<T> eventType, final EventListener<? super T> listener) {
        return this.eventListeners.removeEventListener(eventType, listener);
    }
    
    public void clearEventListeners() {
        this.eventListeners.clear();
    }
    
    public void clearErrorListeners() {
        for (final EventListenerRegistrationData<? extends ConfigurationErrorEvent> reg : this.eventListeners.getRegistrationsForSuperType(ConfigurationErrorEvent.ANY)) {
            this.eventListeners.removeEventListener(reg);
        }
    }
    
    public void copyEventListeners(final BaseEventSource source) {
        if (source == null) {
            throw new IllegalArgumentException("Target event source must not be null!");
        }
        source.eventListeners.addAll(this.eventListeners);
    }
    
    protected <T extends ConfigurationEvent> void fireEvent(final EventType<T> type, final String propName, final Object propValue, final boolean before) {
        if (this.checkDetailEvents(-1)) {
            final EventListenerList.EventListenerIterator<T> it = this.eventListeners.getEventListenerIterator(type);
            if (it.hasNext()) {
                final ConfigurationEvent event = this.createEvent(type, propName, propValue, before);
                while (it.hasNext()) {
                    it.invokeNext(event);
                }
            }
        }
    }
    
    protected <T extends ConfigurationEvent> ConfigurationEvent createEvent(final EventType<T> type, final String propName, final Object propValue, final boolean before) {
        return new ConfigurationEvent(this, type, propName, propValue, before);
    }
    
    public <T extends ConfigurationErrorEvent> void fireError(final EventType<T> eventType, final EventType<?> operationType, final String propertyName, final Object propertyValue, final Throwable cause) {
        final EventListenerList.EventListenerIterator<T> iterator = this.eventListeners.getEventListenerIterator(eventType);
        if (iterator.hasNext()) {
            final ConfigurationErrorEvent event = this.createErrorEvent(eventType, operationType, propertyName, propertyValue, cause);
            while (iterator.hasNext()) {
                iterator.invokeNext(event);
            }
        }
    }
    
    protected ConfigurationErrorEvent createErrorEvent(final EventType<? extends ConfigurationErrorEvent> type, final EventType<?> opType, final String propName, final Object propValue, final Throwable ex) {
        return new ConfigurationErrorEvent(this, type, opType, propName, propValue, ex);
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        final BaseEventSource copy = (BaseEventSource)super.clone();
        copy.initListeners();
        return copy;
    }
    
    private void initListeners() {
        this.eventListeners = new EventListenerList();
    }
    
    private boolean checkDetailEvents(final int limit) {
        synchronized (this.lockDetailEventsCount) {
            return this.detailEvents > limit;
        }
    }
}
