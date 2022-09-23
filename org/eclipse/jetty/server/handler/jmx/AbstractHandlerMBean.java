// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler.jmx;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.server.handler.AbstractHandlerContainer;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.jmx.ObjectMBean;

public class AbstractHandlerMBean extends ObjectMBean
{
    private static final Logger LOG;
    
    public AbstractHandlerMBean(final Object managedObject) {
        super(managedObject);
    }
    
    @Override
    public String getObjectContextBasis() {
        if (this._managed != null) {
            String basis = null;
            if (this._managed instanceof ContextHandler) {
                final ContextHandler handler = (ContextHandler)this._managed;
                String context = this.getContextName(handler);
                if (context == null) {
                    context = handler.getDisplayName();
                }
                if (context != null) {
                    return context;
                }
            }
            else if (this._managed instanceof AbstractHandler) {
                final AbstractHandler handler2 = (AbstractHandler)this._managed;
                final Server server = handler2.getServer();
                if (server != null) {
                    final ContextHandler context2 = AbstractHandlerContainer.findContainerOf(server, ContextHandler.class, handler2);
                    if (context2 != null) {
                        basis = this.getContextName(context2);
                    }
                }
            }
            if (basis != null) {
                return basis;
            }
        }
        return super.getObjectContextBasis();
    }
    
    protected String getContextName(final ContextHandler context) {
        String name = null;
        if (context.getContextPath() != null && context.getContextPath().length() > 0) {
            int idx = context.getContextPath().lastIndexOf(47);
            name = ((idx < 0) ? context.getContextPath() : context.getContextPath().substring(++idx));
            if (name == null || name.length() == 0) {
                name = "ROOT";
            }
        }
        if (name == null && context.getBaseResource() != null) {
            try {
                if (context.getBaseResource().getFile() != null) {
                    name = context.getBaseResource().getFile().getName();
                }
            }
            catch (IOException e) {
                AbstractHandlerMBean.LOG.ignore(e);
                name = context.getBaseResource().getName();
            }
        }
        if (context.getVirtualHosts() != null && context.getVirtualHosts().length > 0) {
            name = '\"' + name + "@" + context.getVirtualHosts()[0] + '\"';
        }
        return name;
    }
    
    static {
        LOG = Log.getLogger(AbstractHandlerMBean.class);
    }
}
