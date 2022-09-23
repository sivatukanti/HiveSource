// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.session.jmx;

import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.server.handler.AbstractHandlerContainer;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.AbstractSessionManager;
import org.eclipse.jetty.server.handler.jmx.AbstractHandlerMBean;

public class AbstractSessionManagerMBean extends AbstractHandlerMBean
{
    public AbstractSessionManagerMBean(final Object managedObject) {
        super(managedObject);
    }
    
    @Override
    public String getObjectContextBasis() {
        if (this._managed != null && this._managed instanceof AbstractSessionManager) {
            final AbstractSessionManager manager = (AbstractSessionManager)this._managed;
            String basis = null;
            final SessionHandler handler = manager.getSessionHandler();
            if (handler != null) {
                final ContextHandler context = AbstractHandlerContainer.findContainerOf(handler.getServer(), ContextHandler.class, handler);
                if (context != null) {
                    basis = this.getContextName(context);
                }
            }
            if (basis != null) {
                return basis;
            }
        }
        return super.getObjectContextBasis();
    }
}
