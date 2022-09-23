// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet;

import java.util.EventListener;

public class ListenerHolder extends BaseHolder<EventListener>
{
    private EventListener _listener;
    
    public ListenerHolder(final Source source) {
        super(source);
    }
    
    public void setListener(final EventListener listener) {
        this._listener = listener;
        this.setClassName(listener.getClass().getName());
        this.setHeldClass(listener.getClass());
        this._extInstance = true;
    }
    
    public EventListener getListener() {
        return this._listener;
    }
    
    @Override
    public void doStart() throws Exception {
        if (this._listener == null) {
            throw new IllegalStateException("No listener instance");
        }
        super.doStart();
    }
}
