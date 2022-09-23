// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.plugin;

import java.io.Serializable;
import java.util.Comparator;
import org.datanucleus.ClassConstants;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import org.osgi.framework.BundleContext;
import java.util.List;
import org.datanucleus.util.NucleusLogger;
import java.util.Collection;
import org.osgi.framework.FrameworkUtil;
import java.util.ArrayList;
import java.util.HashMap;
import org.datanucleus.ClassLoaderResolver;
import java.util.Map;
import org.datanucleus.util.Localiser;

public class OSGiPluginRegistry implements PluginRegistry
{
    protected static final Localiser LOCALISER;
    private static final String DATANUCLEUS_PKG = "org.datanucleus";
    Map<String, ExtensionPoint> extensionPointsByUniqueId;
    Map<String, Bundle> registeredPluginByPluginId;
    ExtensionPoint[] extensionPoints;
    
    public OSGiPluginRegistry(final ClassLoaderResolver clr) {
        this.extensionPointsByUniqueId = new HashMap<String, ExtensionPoint>();
        this.registeredPluginByPluginId = new HashMap<String, Bundle>();
        this.extensionPoints = new ExtensionPoint[0];
    }
    
    @Override
    public ExtensionPoint getExtensionPoint(final String id) {
        return this.extensionPointsByUniqueId.get(id);
    }
    
    @Override
    public ExtensionPoint[] getExtensionPoints() {
        return this.extensionPoints;
    }
    
    @Override
    public void registerExtensionPoints() {
        this.registerExtensions();
    }
    
    @Override
    public void registerExtensions() {
        if (this.extensionPoints.length > 0) {
            return;
        }
        final List registeringExtensions = new ArrayList();
        final BundleContext ctx = FrameworkUtil.getBundle((Class)this.getClass()).getBundleContext();
        final DocumentBuilder docBuilder = OSGiBundleParser.getDocumentBuilder();
        final org.osgi.framework.Bundle[] arr$;
        final org.osgi.framework.Bundle[] osgiBundles = arr$ = ctx.getBundles();
        for (final org.osgi.framework.Bundle osgiBundle : arr$) {
            final URL pluginURL = osgiBundle.getEntry("plugin.xml");
            if (pluginURL != null) {
                final Bundle bundle = this.registerBundle(osgiBundle);
                if (bundle != null) {
                    final List[] elements = OSGiBundleParser.parsePluginElements(docBuilder, this, pluginURL, bundle, osgiBundle);
                    this.registerExtensionPointsForPluginInternal(elements[0], false);
                    registeringExtensions.addAll(elements[1]);
                }
            }
        }
        this.extensionPoints = this.extensionPointsByUniqueId.values().toArray(new ExtensionPoint[this.extensionPointsByUniqueId.values().size()]);
        for (int i = 0; i < registeringExtensions.size(); ++i) {
            final Extension extension = registeringExtensions.get(i);
            final ExtensionPoint exPoint = this.getExtensionPoint(extension.getExtensionPointId());
            if (exPoint == null) {
                if (extension.getPlugin() != null && extension.getPlugin().getSymbolicName() != null && extension.getPlugin().getSymbolicName().startsWith("org.datanucleus")) {
                    NucleusLogger.GENERAL.warn(OSGiPluginRegistry.LOCALISER.msg("024002", extension.getExtensionPointId(), extension.getPlugin().getSymbolicName(), extension.getPlugin().getManifestLocation()));
                }
            }
            else {
                extension.setExtensionPoint(exPoint);
                exPoint.addExtension(extension);
            }
        }
    }
    
    protected void registerExtensionPointsForPluginInternal(final List extPoints, final boolean updateExtensionPointsArray) {
        for (final ExtensionPoint exPoint : extPoints) {
            this.extensionPointsByUniqueId.put(exPoint.getUniqueId(), exPoint);
        }
        if (updateExtensionPointsArray) {
            this.extensionPoints = this.extensionPointsByUniqueId.values().toArray(new ExtensionPoint[this.extensionPointsByUniqueId.values().size()]);
        }
    }
    
    private Bundle registerBundle(final org.osgi.framework.Bundle osgiBundle) {
        final Bundle bundle = OSGiBundleParser.parseManifest(osgiBundle);
        if (bundle == null) {
            return null;
        }
        if (this.registeredPluginByPluginId.get(bundle.getSymbolicName()) == null) {
            if (NucleusLogger.GENERAL.isDebugEnabled()) {
                NucleusLogger.GENERAL.debug("Registering bundle " + bundle.getSymbolicName() + " version " + bundle.getVersion() + " at URL " + bundle.getManifestLocation() + ".");
            }
            this.registeredPluginByPluginId.put(bundle.getSymbolicName(), bundle);
        }
        return bundle;
    }
    
    @Override
    public Object createExecutableExtension(final ConfigurationElement confElm, final String name, final Class[] argsClass, final Object[] args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final String symbolicName = confElm.getExtension().getPlugin().getSymbolicName();
        final String attribute = confElm.getAttribute(name);
        final org.osgi.framework.Bundle osgiBundle = this.getOsgiBundle(symbolicName);
        final Class cls = osgiBundle.loadClass(attribute);
        final Constructor constructor = cls.getConstructor((Class[])argsClass);
        try {
            return constructor.newInstance(args);
        }
        catch (InstantiationException e1) {
            NucleusLogger.GENERAL.error(e1.getMessage(), e1);
            throw e1;
        }
        catch (IllegalAccessException e2) {
            NucleusLogger.GENERAL.error(e2.getMessage(), e2);
            throw e2;
        }
        catch (IllegalArgumentException e3) {
            NucleusLogger.GENERAL.error(e3.getMessage(), e3);
            throw e3;
        }
        catch (InvocationTargetException e4) {
            NucleusLogger.GENERAL.error(e4.getMessage(), e4);
            throw e4;
        }
    }
    
    @Override
    public Class loadClass(final String pluginId, final String className) throws ClassNotFoundException {
        return this.getOsgiBundle(pluginId).loadClass(className);
    }
    
    @Override
    public URL resolveURLAsFileURL(final URL url) throws IOException {
        return null;
    }
    
    @Override
    public void resolveConstraints() {
    }
    
    @Override
    public Bundle[] getBundles() {
        return this.registeredPluginByPluginId.values().toArray(new Bundle[this.registeredPluginByPluginId.values().size()]);
    }
    
    private org.osgi.framework.Bundle getOsgiBundle(final String symbolicName) {
        final BundleContext ctx = FrameworkUtil.getBundle((Class)this.getClass()).getBundleContext();
        final org.osgi.framework.Bundle[] arr$;
        final org.osgi.framework.Bundle[] osgiBundles = arr$ = ctx.getBundles();
        for (final org.osgi.framework.Bundle osgiBundle : arr$) {
            if (symbolicName.equals(osgiBundle.getSymbolicName())) {
                return osgiBundle;
            }
        }
        return null;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
    
    protected static class ExtensionSorter implements Comparator<Extension>, Serializable
    {
        @Override
        public int compare(final Extension o1, final Extension o2) {
            final String name1 = o1.getPlugin().getSymbolicName();
            final String name2 = o2.getPlugin().getSymbolicName();
            if (name1.startsWith("org.datanucleus") && !name2.startsWith("org.datanucleus")) {
                return -1;
            }
            if (!name1.startsWith("org.datanucleus") && name2.startsWith("org.datanucleus")) {
                return 1;
            }
            return name1.compareTo(name2);
        }
    }
}
