// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy.jmx;

import org.eclipse.jetty.deploy.AppProvider;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.deploy.App;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jetty.deploy.graph.Node;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.jmx.ObjectMBean;

public class DeploymentManagerMBean extends ObjectMBean
{
    private final DeploymentManager _manager;
    
    public DeploymentManagerMBean(final Object managedObject) {
        super(managedObject);
        this._manager = (DeploymentManager)managedObject;
    }
    
    public Collection<String> getNodes() {
        final List<String> nodes = new ArrayList<String>();
        for (final Node node : this._manager.getNodes()) {
            nodes.add(node.getName());
        }
        return nodes;
    }
    
    public Collection<String> getApps() {
        final List<String> apps = new ArrayList<String>();
        for (final App app : this._manager.getApps()) {
            apps.add(app.getOriginId());
        }
        return apps;
    }
    
    public Collection<String> getApps(final String nodeName) {
        final List<String> apps = new ArrayList<String>();
        for (final App app : this._manager.getApps(nodeName)) {
            apps.add(app.getOriginId());
        }
        return apps;
    }
    
    public Collection<ContextHandler> getContexts() throws Exception {
        final List<ContextHandler> apps = new ArrayList<ContextHandler>();
        for (final App app : this._manager.getApps()) {
            apps.add(app.getContextHandler());
        }
        return apps;
    }
    
    public Collection<AppProvider> getAppProviders() {
        return this._manager.getAppProviders();
    }
    
    public void requestAppGoal(final String appId, final String nodeName) {
        this._manager.requestAppGoal(appId, nodeName);
    }
}
