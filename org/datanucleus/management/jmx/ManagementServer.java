// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.management.jmx;

public interface ManagementServer
{
    void start();
    
    void stop();
    
    void registerMBean(final Object p0, final String p1);
    
    void unregisterMBean(final String p0);
}
