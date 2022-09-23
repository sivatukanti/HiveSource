// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.reloading;

import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.event.Event;

public class ReloadingEvent extends Event
{
    public static final EventType<ReloadingEvent> ANY;
    private static final long serialVersionUID = 20140701L;
    private final Object data;
    
    public ReloadingEvent(final ReloadingController source, final Object addData) {
        super(source, ReloadingEvent.ANY);
        this.data = addData;
    }
    
    public ReloadingController getController() {
        return (ReloadingController)this.getSource();
    }
    
    public Object getData() {
        return this.data;
    }
    
    static {
        ANY = new EventType<ReloadingEvent>(Event.ANY, "RELOAD");
    }
}
