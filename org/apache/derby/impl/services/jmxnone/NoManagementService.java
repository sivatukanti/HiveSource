// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.jmxnone;

import org.apache.derby.iapi.services.jmx.ManagementService;

public final class NoManagementService implements ManagementService
{
    public Object registerMBean(final Object o, final Class clazz, final String s) {
        return null;
    }
    
    public void unregisterMBean(final Object o) {
    }
    
    public boolean isManagementActive() {
        return false;
    }
    
    public void startManagement() {
    }
    
    public void stopManagement() {
    }
    
    public String getSystemIdentifier() {
        return null;
    }
}
