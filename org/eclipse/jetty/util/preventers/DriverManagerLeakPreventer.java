// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.preventers;

import java.sql.DriverManager;

public class DriverManagerLeakPreventer extends AbstractLeakPreventer
{
    @Override
    public void prevent(final ClassLoader loader) {
        if (DriverManagerLeakPreventer.LOG.isDebugEnabled()) {
            DriverManagerLeakPreventer.LOG.debug("Pinning DriverManager classloader with " + loader, new Object[0]);
        }
        DriverManager.getDrivers();
    }
}
