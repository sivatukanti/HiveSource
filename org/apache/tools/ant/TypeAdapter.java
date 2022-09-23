// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant;

public interface TypeAdapter
{
    void setProject(final Project p0);
    
    Project getProject();
    
    void setProxy(final Object p0);
    
    Object getProxy();
    
    void checkProxyClass(final Class<?> p0);
}
