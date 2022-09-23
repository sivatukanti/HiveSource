// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.ws.rs.ext;

import javax.ws.rs.core.Application;
import com.sun.jersey.core.spi.factory.AbstractRuntimeDelegate;

public class RuntimeDelegateImpl extends AbstractRuntimeDelegate
{
    @Override
    public <T> T createEndpoint(final Application application, final Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
