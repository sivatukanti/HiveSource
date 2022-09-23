// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public interface ClassLoaderResolver
{
    Class classForName(final String p0, final ClassLoader p1);
    
    Class classForName(final String p0, final ClassLoader p1, final boolean p2);
    
    Class classForName(final String p0);
    
    Class classForName(final String p0, final boolean p1);
    
    boolean isAssignableFrom(final String p0, final Class p1);
    
    boolean isAssignableFrom(final Class p0, final String p1);
    
    boolean isAssignableFrom(final String p0, final String p1);
    
    void setRuntimeClassLoader(final ClassLoader p0);
    
    void registerUserClassLoader(final ClassLoader p0);
    
    Enumeration<URL> getResources(final String p0, final ClassLoader p1) throws IOException;
    
    URL getResource(final String p0, final ClassLoader p1);
    
    void setPrimary(final ClassLoader p0);
    
    void unsetPrimary();
}
