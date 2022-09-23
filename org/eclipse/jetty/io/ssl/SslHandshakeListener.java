// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io.ssl;

import javax.net.ssl.SSLEngine;
import java.util.EventObject;
import java.util.EventListener;

public interface SslHandshakeListener extends EventListener
{
    default void handshakeSucceeded(final Event event) {
    }
    
    default void handshakeFailed(final Event event, final Throwable failure) {
    }
    
    public static class Event extends EventObject
    {
        public Event(final Object source) {
            super(source);
        }
        
        public SSLEngine getSSLEngine() {
            return (SSLEngine)this.getSource();
        }
    }
}
