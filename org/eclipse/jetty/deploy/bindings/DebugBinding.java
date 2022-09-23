// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy.bindings;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.deploy.App;
import org.eclipse.jetty.deploy.graph.Node;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.deploy.AppLifeCycle;

public class DebugBinding implements AppLifeCycle.Binding
{
    private static final Logger LOG;
    final String[] _targets;
    
    public DebugBinding(final String target) {
        this._targets = new String[] { target };
    }
    
    public DebugBinding(final String... targets) {
        this._targets = targets;
    }
    
    public String[] getBindingTargets() {
        return this._targets;
    }
    
    public void processBinding(final Node node, final App app) throws Exception {
        DebugBinding.LOG.info("processBinding {} {}", node, app.getContextHandler());
    }
    
    static {
        LOG = Log.getLogger(DebugBinding.class);
    }
}
