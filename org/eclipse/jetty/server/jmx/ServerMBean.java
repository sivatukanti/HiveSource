// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.jmx;

import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.jmx.ObjectMBean;

@ManagedObject("MBean Wrapper for Server")
public class ServerMBean extends ObjectMBean
{
    private final long startupTime;
    private final Server server;
    
    public ServerMBean(final Object managedObject) {
        super(managedObject);
        this.startupTime = System.currentTimeMillis();
        this.server = (Server)managedObject;
    }
    
    @ManagedAttribute("contexts on this server")
    public Handler[] getContexts() {
        return this.server.getChildHandlersByClass(ContextHandler.class);
    }
    
    @ManagedAttribute("the startup time since January 1st, 1970 (in ms)")
    public long getStartupTime() {
        return this.startupTime;
    }
}
