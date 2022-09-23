// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.component.LifeCycle;

public interface AppProvider extends LifeCycle
{
    void setDeploymentManager(final DeploymentManager p0);
    
    ContextHandler createContextHandler(final App p0) throws Exception;
}
