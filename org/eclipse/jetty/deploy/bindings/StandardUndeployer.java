// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy.bindings;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.deploy.App;
import org.eclipse.jetty.deploy.graph.Node;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.deploy.AppLifeCycle;

public class StandardUndeployer implements AppLifeCycle.Binding
{
    private static final Logger LOG;
    
    public String[] getBindingTargets() {
        return new String[] { "undeploying" };
    }
    
    public void processBinding(final Node node, final App app) throws Exception {
        final ContextHandler handler = app.getContextHandler();
        final ContextHandlerCollection chcoll = app.getDeploymentManager().getContexts();
        this.recursiveRemoveContext(chcoll, handler);
    }
    
    private void recursiveRemoveContext(final HandlerCollection coll, final ContextHandler context) {
        final Handler[] children = coll.getHandlers();
        final int originalCount = children.length;
        for (int i = 0, n = children.length; i < n; ++i) {
            final Handler child = children[i];
            StandardUndeployer.LOG.debug("Child handler {}", child);
            if (child.equals(context)) {
                StandardUndeployer.LOG.debug("Removing handler {}", child);
                coll.removeHandler(child);
                child.destroy();
                if (StandardUndeployer.LOG.isDebugEnabled()) {
                    StandardUndeployer.LOG.debug("After removal: {} (originally {})", coll.getHandlers().length, originalCount);
                }
            }
            else if (child instanceof HandlerCollection) {
                this.recursiveRemoveContext((HandlerCollection)child, context);
            }
        }
    }
    
    static {
        LOG = Log.getLogger(StandardUndeployer.class);
    }
}
