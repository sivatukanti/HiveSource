// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.plugin;

import java.io.IOException;
import java.net.URL;
import java.lang.reflect.InvocationTargetException;

public interface PluginRegistry
{
    ExtensionPoint getExtensionPoint(final String p0);
    
    ExtensionPoint[] getExtensionPoints();
    
    void registerExtensionPoints();
    
    void registerExtensions();
    
    Object createExecutableExtension(final ConfigurationElement p0, final String p1, final Class[] p2, final Object[] p3) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException;
    
    Class loadClass(final String p0, final String p1) throws ClassNotFoundException;
    
    URL resolveURLAsFileURL(final URL p0) throws IOException;
    
    void resolveConstraints();
    
    Bundle[] getBundles();
}
