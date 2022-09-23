// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

public interface ImplementationCreator
{
    Object newInstance(final Class p0, final ClassLoaderResolver p1);
    
    ClassLoader getClassLoader();
}
