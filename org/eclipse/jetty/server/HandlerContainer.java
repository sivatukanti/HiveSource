// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.LifeCycle;

@ManagedObject("Handler of Multiple Handlers")
public interface HandlerContainer extends LifeCycle
{
    @ManagedAttribute("handlers in this container")
    Handler[] getHandlers();
    
    @ManagedAttribute("all contained handlers")
    Handler[] getChildHandlers();
    
    Handler[] getChildHandlersByClass(final Class<?> p0);
    
     <T extends Handler> T getChildHandlerByClass(final Class<T> p0);
}
