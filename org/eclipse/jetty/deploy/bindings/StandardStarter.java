// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy.bindings;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.deploy.App;
import org.eclipse.jetty.deploy.graph.Node;
import org.eclipse.jetty.deploy.AppLifeCycle;

public class StandardStarter implements AppLifeCycle.Binding
{
    public String[] getBindingTargets() {
        return new String[] { "starting" };
    }
    
    public void processBinding(final Node node, final App app) throws Exception {
        final ContextHandler handler = app.getContextHandler();
        if (!handler.isStarted()) {
            handler.start();
        }
    }
}
