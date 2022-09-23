// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.reloading;

import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.event.EventListenerList;
import org.apache.commons.configuration2.event.EventSource;

public class ReloadingController implements EventSource
{
    private final ReloadingDetector detector;
    private final EventListenerList listeners;
    private boolean reloadingState;
    
    public ReloadingController(final ReloadingDetector detect) {
        if (detect == null) {
            throw new IllegalArgumentException("ReloadingDetector must not be null!");
        }
        this.detector = detect;
        this.listeners = new EventListenerList();
    }
    
    public ReloadingDetector getDetector() {
        return this.detector;
    }
    
    @Override
    public <T extends Event> void addEventListener(final EventType<T> eventType, final EventListener<? super T> listener) {
        this.listeners.addEventListener(eventType, listener);
    }
    
    @Override
    public <T extends Event> boolean removeEventListener(final EventType<T> eventType, final EventListener<? super T> listener) {
        return this.listeners.removeEventListener(eventType, listener);
    }
    
    public synchronized boolean isInReloadingState() {
        return this.reloadingState;
    }
    
    public boolean checkForReloading(final Object data) {
        boolean sendEvent = false;
        synchronized (this) {
            if (this.isInReloadingState()) {
                return true;
            }
            if (this.getDetector().isReloadingRequired()) {
                sendEvent = true;
                this.reloadingState = true;
            }
        }
        if (sendEvent) {
            this.listeners.fire(new ReloadingEvent(this, data));
            return true;
        }
        return false;
    }
    
    public synchronized void resetReloadingState() {
        if (this.isInReloadingState()) {
            this.getDetector().reloadingPerformed();
            this.reloadingState = false;
        }
    }
}
