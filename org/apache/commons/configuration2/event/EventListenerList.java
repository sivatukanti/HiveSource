// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.event;

import java.util.NoSuchElementException;
import java.util.Map;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class EventListenerList
{
    private final List<EventListenerRegistrationData<?>> listeners;
    
    public EventListenerList() {
        this.listeners = new CopyOnWriteArrayList<EventListenerRegistrationData<?>>();
    }
    
    public <T extends Event> void addEventListener(final EventType<T> type, final EventListener<? super T> listener) {
        this.listeners.add(new EventListenerRegistrationData<Object>((EventType<Object>)type, (EventListener<? super Object>)listener));
    }
    
    public <T extends Event> void addEventListener(final EventListenerRegistrationData<T> regData) {
        if (regData == null) {
            throw new IllegalArgumentException("EventListenerRegistrationData must not be null!");
        }
        this.listeners.add(regData);
    }
    
    public <T extends Event> boolean removeEventListener(final EventType<T> eventType, final EventListener<? super T> listener) {
        return listener != null && eventType != null && this.removeEventListener(new EventListenerRegistrationData<T>(eventType, listener));
    }
    
    public <T extends Event> boolean removeEventListener(final EventListenerRegistrationData<T> regData) {
        return this.listeners.remove(regData);
    }
    
    public void fire(final Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event to be fired must not be null!");
        }
        final EventListenerIterator<? extends Event> iterator = this.getEventListenerIterator(event.getEventType());
        while (iterator.hasNext()) {
            ((EventListenerIterator<Event>)iterator).invokeNextListenerUnchecked(event);
        }
    }
    
    public <T extends Event> Iterable<EventListener<? super T>> getEventListeners(final EventType<T> eventType) {
        return new Iterable<EventListener<? super T>>() {
            @Override
            public Iterator<EventListener<? super T>> iterator() {
                return (Iterator<EventListener<? super T>>)EventListenerList.this.getEventListenerIterator((EventType<Event>)eventType);
            }
        };
    }
    
    public <T extends Event> EventListenerIterator<T> getEventListenerIterator(final EventType<T> eventType) {
        return new EventListenerIterator<T>((Iterator)this.listeners.iterator(), (EventType)eventType);
    }
    
    public List<EventListenerRegistrationData<?>> getRegistrations() {
        return Collections.unmodifiableList((List<? extends EventListenerRegistrationData<?>>)this.listeners);
    }
    
    public <T extends Event> List<EventListenerRegistrationData<? extends T>> getRegistrationsForSuperType(final EventType<T> eventType) {
        final Map<EventType<?>, Set<EventType<?>>> superTypes = new HashMap<EventType<?>, Set<EventType<?>>>();
        final List<EventListenerRegistrationData<? extends T>> results = new LinkedList<EventListenerRegistrationData<? extends T>>();
        for (final EventListenerRegistrationData<?> reg : this.listeners) {
            Set<EventType<?>> base = superTypes.get(reg.getEventType());
            if (base == null) {
                base = EventType.fetchSuperEventTypes(reg.getEventType());
                superTypes.put(reg.getEventType(), base);
            }
            if (base.contains(eventType)) {
                final EventListenerRegistrationData<? extends T> result = (EventListenerRegistrationData<? extends T>)reg;
                results.add(result);
            }
        }
        return results;
    }
    
    public void clear() {
        this.listeners.clear();
    }
    
    public void addAll(final EventListenerList c) {
        if (c == null) {
            throw new IllegalArgumentException("List to be copied must not be null!");
        }
        for (final EventListenerRegistrationData<?> regData : c.getRegistrations()) {
            this.addEventListener(regData);
        }
    }
    
    private static void callListener(final EventListener<?> listener, final Event event) {
        final EventListener rowListener = listener;
        rowListener.onEvent(event);
    }
    
    public static final class EventListenerIterator<T extends Event> implements Iterator<EventListener<? super T>>
    {
        private final Iterator<EventListenerRegistrationData<?>> underlyingIterator;
        private final EventType<T> baseEventType;
        private final Set<EventType<?>> acceptedTypes;
        private EventListener<? super T> nextElement;
        
        private EventListenerIterator(final Iterator<EventListenerRegistrationData<?>> it, final EventType<T> base) {
            this.underlyingIterator = it;
            this.baseEventType = base;
            this.acceptedTypes = EventType.fetchSuperEventTypes(base);
            this.initNextElement();
        }
        
        @Override
        public boolean hasNext() {
            return this.nextElement != null;
        }
        
        @Override
        public EventListener<? super T> next() {
            if (this.nextElement == null) {
                throw new NoSuchElementException("No more event listeners!");
            }
            final EventListener<? super T> result = this.nextElement;
            this.initNextElement();
            return result;
        }
        
        public void invokeNext(final Event event) {
            this.validateEvent(event);
            this.invokeNextListenerUnchecked(event);
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Removing elements is not supported!");
        }
        
        private void initNextElement() {
            this.nextElement = null;
            while (this.underlyingIterator.hasNext() && this.nextElement == null) {
                final EventListenerRegistrationData<?> regData = this.underlyingIterator.next();
                if (this.acceptedTypes.contains(regData.getEventType())) {
                    this.nextElement = this.castListener(regData);
                }
            }
        }
        
        private void validateEvent(final Event event) {
            if (event == null || !EventType.fetchSuperEventTypes(event.getEventType()).contains(this.baseEventType)) {
                throw new IllegalArgumentException("Event incompatible with listener iteration: " + event);
            }
        }
        
        private void invokeNextListenerUnchecked(final Event event) {
            final EventListener<? super T> listener = this.next();
            callListener(listener, event);
        }
        
        private EventListener<? super T> castListener(final EventListenerRegistrationData<?> regData) {
            final EventListener listener = regData.getListener();
            return (EventListener<? super T>)listener;
        }
    }
}
