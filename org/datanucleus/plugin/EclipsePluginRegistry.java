// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.plugin;

import org.eclipse.osgi.service.resolver.BundleDescription;
import java.util.Map;
import org.eclipse.osgi.service.resolver.VersionRange;
import java.util.HashMap;
import org.eclipse.osgi.service.resolver.BundleSpecification;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.io.IOException;
import org.eclipse.core.runtime.FileLocator;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import java.net.URL;
import org.eclipse.core.runtime.RegistryFactory;
import org.datanucleus.ClassLoaderResolver;

public class EclipsePluginRegistry implements PluginRegistry
{
    public EclipsePluginRegistry(final ClassLoaderResolver clr) {
        if (RegistryFactory.getRegistry() == null || RegistryFactory.getRegistry().getExtensionPoint("org.datanucleus.store_manager") == null) {
            throw new RuntimeException("This plug-in registry class can only be used if datanucleus-core is managed by Eclipse");
        }
    }
    
    @Override
    public ExtensionPoint getExtensionPoint(final String id) {
        final IExtensionPoint eclipseExPoint = RegistryFactory.getRegistry().getExtensionPoint(id);
        final Bundle plugin = new Bundle(eclipseExPoint.getContributor().getName(), "", "", "", null);
        final org.osgi.framework.Bundle bundle = Platform.getBundle(eclipseExPoint.getContributor().getName());
        try {
            final ExtensionPoint exPoint = new ExtensionPoint(eclipseExPoint.getSimpleIdentifier(), eclipseExPoint.getLabel(), bundle.getResource(eclipseExPoint.getSchemaReference()), plugin);
            for (int e = 0; e < eclipseExPoint.getExtensions().length; ++e) {
                final Bundle pluginEx = new Bundle(eclipseExPoint.getExtensions()[e].getContributor().getName(), "", "", "", null);
                final Extension ex = new Extension(exPoint, pluginEx);
                this.configurationElement(ex, eclipseExPoint.getExtensions()[e].getConfigurationElements(), null);
                exPoint.addExtension(ex);
            }
            return exPoint;
        }
        catch (InvalidRegistryObjectException e2) {
            return null;
        }
    }
    
    private void configurationElement(final Extension ex, final IConfigurationElement[] elms, final ConfigurationElement parent) {
        for (int c = 0; c < elms.length; ++c) {
            final IConfigurationElement iconfElm = elms[c];
            final ConfigurationElement confElm = new ConfigurationElement(ex, iconfElm.getName(), null);
            for (int a = 0; a < iconfElm.getAttributeNames().length; ++a) {
                confElm.putAttribute(iconfElm.getAttributeNames()[a], iconfElm.getAttribute(iconfElm.getAttributeNames()[a]));
            }
            confElm.setText(iconfElm.getValue());
            if (parent == null) {
                ex.addConfigurationElement(confElm);
            }
            else {
                parent.addConfigurationElement(confElm);
            }
            this.configurationElement(ex, iconfElm.getChildren(), confElm);
        }
    }
    
    @Override
    public ExtensionPoint[] getExtensionPoints() {
        final IExtensionPoint[] eclipseExPoint = RegistryFactory.getRegistry().getExtensionPoints();
        final List elms = new ArrayList();
        for (int i = 0; i < eclipseExPoint.length; ++i) {
            final Bundle plugin = new Bundle(eclipseExPoint[i].getContributor().getName(), "", "", "", null);
            try {
                final org.osgi.framework.Bundle bundle = Platform.getBundle(eclipseExPoint[i].getContributor().getName());
                final ExtensionPoint exPoint = new ExtensionPoint(eclipseExPoint[i].getSimpleIdentifier(), eclipseExPoint[i].getLabel(), bundle.getResource(eclipseExPoint[i].getSchemaReference()), plugin);
                for (int e = 0; e < eclipseExPoint[i].getExtensions().length; ++e) {
                    final Extension ex = new Extension(exPoint, plugin);
                    this.configurationElement(ex, eclipseExPoint[i].getExtensions()[e].getConfigurationElements(), null);
                    exPoint.addExtension(ex);
                }
                elms.add(exPoint);
            }
            catch (InvalidRegistryObjectException ex2) {}
        }
        return elms.toArray(new ExtensionPoint[elms.size()]);
    }
    
    @Override
    public void registerExtensionPoints() {
    }
    
    @Override
    public void registerExtensions() {
    }
    
    @Override
    public URL resolveURLAsFileURL(final URL url) throws IOException {
        return FileLocator.toFileURL(url);
    }
    
    @Override
    public Object createExecutableExtension(final ConfigurationElement confElm, final String name, final Class[] argsClass, final Object[] args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Class cls = Platform.getBundle(confElm.getExtension().getPlugin().getSymbolicName()).loadClass(confElm.getAttribute(name));
        final Constructor constructor = cls.getConstructor((Class[])argsClass);
        return constructor.newInstance(args);
    }
    
    @Override
    public Class loadClass(final String pluginId, final String className) throws ClassNotFoundException {
        return Platform.getBundle(pluginId).loadClass(className);
    }
    
    @Override
    public void resolveConstraints() {
    }
    
    private Bundle.BundleDescription getBundleDescription(final BundleSpecification bs) {
        final Bundle.BundleDescription bd = new Bundle.BundleDescription();
        bd.setBundleSymbolicName(bs.getBundle().getSymbolicName());
        final Map parameters = new HashMap();
        if (bs.isOptional()) {
            parameters.put("resolution", "optional");
        }
        if (VersionRange.emptyRange != bs.getVersionRange()) {
            parameters.put("bundle-version", bs.getVersionRange().toString());
        }
        bd.setParameters(parameters);
        return bd;
    }
    
    @Override
    public Bundle[] getBundles() {
        final int size = Platform.getPlatformAdmin().getState().getBundles().length;
        final Bundle[] bundles = new Bundle[size];
        for (int i = 0; i < size; ++i) {
            final BundleDescription bd = Platform.getPlatformAdmin().getState().getBundles()[i];
            bundles[i] = new Bundle(bd.getSymbolicName(), bd.getSymbolicName(), bd.getSupplier().getName(), bd.getVersion().toString(), null);
            final BundleSpecification[] bs = bd.getRequiredBundles();
            final List requiredBundles = new ArrayList();
            for (int j = 0; j < bs.length; ++j) {
                requiredBundles.add(this.getBundleDescription(bs[j]));
            }
            bundles[i].setRequireBundle(requiredBundles);
        }
        return bundles;
    }
}
