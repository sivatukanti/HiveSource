// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import org.datanucleus.plugin.PluginManager;
import org.datanucleus.ClassConstants;
import javax.transaction.TransactionManager;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.NucleusContext;

public class TransactionManagerFinder
{
    NucleusContext nucleusContext;
    
    public TransactionManagerFinder(final NucleusContext ctx) {
        this.nucleusContext = ctx;
    }
    
    public TransactionManager getTransactionManager(final ClassLoaderResolver clr) {
        final String jtaLocatorName = this.nucleusContext.getPersistenceConfiguration().getStringProperty("datanucleus.jtaLocator");
        final PluginManager pluginMgr = this.nucleusContext.getPluginManager();
        if (jtaLocatorName != null) {
            try {
                final TransactionManagerLocator locator = (TransactionManagerLocator)pluginMgr.createExecutableExtension("org.datanucleus.jta_locator", "name", jtaLocatorName, "class-name", new Class[] { ClassConstants.NUCLEUS_CONTEXT }, new Object[] { this.nucleusContext });
                return locator.getTransactionManager(clr);
            }
            catch (Exception e) {
                return null;
            }
        }
        final String[] locatorNames = pluginMgr.getAttributeValuesForExtension("org.datanucleus.jta_locator", null, null, "name");
        if (locatorNames != null) {
            for (int i = 0; i < locatorNames.length; ++i) {
                try {
                    final TransactionManagerLocator locator2 = (TransactionManagerLocator)pluginMgr.createExecutableExtension("org.datanucleus.jta_locator", "name", locatorNames[i], "class-name", new Class[] { ClassConstants.NUCLEUS_CONTEXT }, new Object[] { this.nucleusContext });
                    if (locator2 != null) {
                        final TransactionManager tm = locator2.getTransactionManager(clr);
                        if (tm != null) {
                            return tm;
                        }
                    }
                }
                catch (Exception ex) {}
            }
        }
        return null;
    }
}
