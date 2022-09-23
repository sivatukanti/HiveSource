// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.jmx;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.mbeans.ManagementMBean;

public interface ManagementService extends ManagementMBean
{
    public static final String DERBY_JMX_DOMAIN = "org.apache.derby";
    
    Object registerMBean(final Object p0, final Class p1, final String p2) throws StandardException;
    
    void unregisterMBean(final Object p0);
}
