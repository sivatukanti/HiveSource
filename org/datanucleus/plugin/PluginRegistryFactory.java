// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.plugin;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.util.Localiser;

public class PluginRegistryFactory
{
    protected static final Localiser LOCALISER;
    
    public static PluginRegistry newPluginRegistry(final String registryClassName, final String registryBundleCheck, final boolean allowUserBundles, final ClassLoaderResolver clr) {
        PluginRegistry registry = null;
        if (registryClassName != null) {
            registry = newInstance(registryClassName, registryClassName, clr);
            if (registry != null) {
                if (NucleusLogger.GENERAL.isDebugEnabled()) {
                    NucleusLogger.GENERAL.debug("Using PluginRegistry " + registry.getClass().getName());
                }
                return registry;
            }
        }
        registry = newInstance("org.eclipse.core.runtime.RegistryFactory", "org.datanucleus.plugin.EclipsePluginRegistry", clr);
        if (registry != null) {
            if (NucleusLogger.GENERAL.isDebugEnabled()) {
                NucleusLogger.GENERAL.debug("Using PluginRegistry " + registry.getClass().getName());
            }
            return registry;
        }
        if (NucleusLogger.GENERAL.isDebugEnabled()) {
            NucleusLogger.GENERAL.debug("Using PluginRegistry " + NonManagedPluginRegistry.class.getName());
        }
        return new NonManagedPluginRegistry(clr, registryBundleCheck, allowUserBundles);
    }
    
    private static PluginRegistry newInstance(final String testClass, final String registryClassName, final ClassLoaderResolver clr) {
        try {
            if (clr.classForName(testClass, ClassConstants.NUCLEUS_CONTEXT_LOADER) == null && NucleusLogger.GENERAL.isDebugEnabled()) {
                NucleusLogger.GENERAL.debug(PluginRegistryFactory.LOCALISER.msg("024005", registryClassName));
            }
            return clr.classForName(registryClassName, ClassConstants.NUCLEUS_CONTEXT_LOADER).getConstructor(ClassConstants.CLASS_LOADER_RESOLVER).newInstance(clr);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
