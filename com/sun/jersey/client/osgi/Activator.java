// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.client.osgi;

import org.osgi.framework.Bundle;
import javax.ws.rs.ext.RuntimeDelegate;
import com.sun.ws.rs.ext.RuntimeDelegateImpl;
import org.osgi.framework.BundleContext;
import java.util.logging.Logger;
import org.osgi.framework.BundleActivator;

public class Activator implements BundleActivator
{
    private static final Logger LOGGER;
    
    public void start(final BundleContext bc) throws Exception {
        final Bundle jerseyServerBundle = this.getJerseyServerBundle(bc);
        if (jerseyServerBundle == null) {
            Activator.LOGGER.config("jersey-client bundle registers JAX-RS RuntimeDelegate");
            RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
        }
        else {
            jerseyServerBundle.start();
        }
    }
    
    private Bundle getJerseyServerBundle(final BundleContext bc) {
        for (final Bundle b : bc.getBundles()) {
            final String symbolicName = b.getSymbolicName();
            if (symbolicName != null && symbolicName.endsWith("jersey-server")) {
                return b;
            }
        }
        return null;
    }
    
    public void stop(final BundleContext bc) throws Exception {
    }
    
    static {
        LOGGER = Logger.getLogger(Activator.class.getName());
    }
}
