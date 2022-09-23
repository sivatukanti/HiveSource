// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.net;

import java.util.EventListener;

public interface SocketNodeEventListener extends EventListener
{
    void socketOpened(final String p0);
    
    void socketClosedEvent(final Exception p0);
}
