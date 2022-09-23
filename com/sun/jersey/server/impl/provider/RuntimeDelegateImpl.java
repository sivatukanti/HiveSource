// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.provider;

import com.sun.jersey.api.core.ApplicationAdapter;
import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.core.ResourceConfig;
import javax.ws.rs.core.Application;
import com.sun.jersey.core.spi.factory.AbstractRuntimeDelegate;

public class RuntimeDelegateImpl extends AbstractRuntimeDelegate
{
    @Override
    public <T> T createEndpoint(final Application application, final Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException {
        if (application instanceof ResourceConfig) {
            return ContainerFactory.createContainer(endpointType, (ResourceConfig)application);
        }
        return ContainerFactory.createContainer(endpointType, new ApplicationAdapter(application));
    }
}
