// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.management.jmx;

import org.datanucleus.exceptions.NucleusException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import org.datanucleus.util.NucleusLogger;
import javax.management.MBeanServer;

public class PlatformManagementServer implements ManagementServer
{
    MBeanServer mbeanServer;
    
    @Override
    public void start() {
        if (NucleusLogger.GENERAL.isDebugEnabled()) {
            NucleusLogger.GENERAL.debug("Starting ManagementServer");
        }
        this.mbeanServer = ManagementFactory.getPlatformMBeanServer();
    }
    
    @Override
    public void stop() {
        if (NucleusLogger.GENERAL.isDebugEnabled()) {
            NucleusLogger.GENERAL.debug("Stopping ManagementServer");
        }
        this.mbeanServer = null;
    }
    
    @Override
    public void registerMBean(final Object mbean, final String name) {
        try {
            final ObjectName objName = new ObjectName(name);
            this.mbeanServer.registerMBean(mbean, objName);
        }
        catch (Exception e) {
            throw new NucleusException(e.getMessage(), e);
        }
    }
    
    @Override
    public void unregisterMBean(final String name) {
        try {
            final ObjectName objName = new ObjectName(name);
            this.mbeanServer.unregisterMBean(objName);
        }
        catch (Exception e) {
            throw new NucleusException(e.getMessage(), e);
        }
    }
}
