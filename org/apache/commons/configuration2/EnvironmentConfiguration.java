// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.util.Map;
import java.util.HashMap;

public class EnvironmentConfiguration extends MapConfiguration
{
    public EnvironmentConfiguration() {
        super(new HashMap<String, Object>(System.getenv()));
    }
    
    @Override
    protected void addPropertyDirect(final String key, final Object value) {
        throw new UnsupportedOperationException("EnvironmentConfiguration is read-only!");
    }
    
    @Override
    protected void clearPropertyDirect(final String key) {
        throw new UnsupportedOperationException("EnvironmentConfiguration is read-only!");
    }
    
    @Override
    protected void clearInternal() {
        throw new UnsupportedOperationException("EnvironmentConfiguration is read-only!");
    }
}
