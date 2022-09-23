// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy;

import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.AttributesMap;
import org.eclipse.jetty.server.handler.ContextHandler;

public class App
{
    private final DeploymentManager _manager;
    private final AppProvider _provider;
    private final String _originId;
    private ContextHandler _context;
    
    public App(final DeploymentManager manager, final AppProvider provider, final String originId) {
        this._manager = manager;
        this._provider = provider;
        this._originId = originId;
    }
    
    public App(final DeploymentManager manager, final AppProvider provider, final String originId, final ContextHandler context) {
        this(manager, provider, originId);
        this._context = context;
    }
    
    public DeploymentManager getDeploymentManager() {
        return this._manager;
    }
    
    public AppProvider getAppProvider() {
        return this._provider;
    }
    
    public ContextHandler getContextHandler() throws Exception {
        if (this._context == null) {
            this._context = this.getAppProvider().createContextHandler(this);
            AttributesMap attributes = this._manager.getContextAttributes();
            if (attributes != null && attributes.size() > 0) {
                attributes = new AttributesMap(attributes);
                attributes.addAll(this._context.getAttributes());
                this._context.setAttributes(attributes);
            }
        }
        return this._context;
    }
    
    @Deprecated
    public String getContextId() {
        return this.getContextPath();
    }
    
    public String getContextPath() {
        if (this._context == null) {
            return null;
        }
        return this._context.getContextPath();
    }
    
    public String getOriginId() {
        return this._originId;
    }
    
    @Override
    public String toString() {
        return "App[" + this._context + "," + this._originId + "]";
    }
}
