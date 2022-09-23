// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.osgi;

import java.sql.SQLException;
import java.sql.DriverManager;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleActivator;

public final class EmbeddedActivator implements BundleActivator
{
    public void start(final BundleContext bundleContext) {
        new EmbeddedDriver();
    }
    
    public void stop(final BundleContext bundleContext) {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        }
        catch (SQLException ex) {}
    }
}
