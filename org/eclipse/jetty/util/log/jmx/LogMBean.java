// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.log.jmx;

import java.util.Collection;
import java.util.ArrayList;
import org.eclipse.jetty.util.log.Log;
import java.util.List;
import org.eclipse.jetty.jmx.ObjectMBean;

public class LogMBean extends ObjectMBean
{
    public LogMBean(final Object managedObject) {
        super(managedObject);
    }
    
    public List<String> getLoggers() {
        final List<String> keySet = new ArrayList<String>(Log.getLoggers().keySet());
        return keySet;
    }
    
    public boolean isDebugEnabled(final String logger) {
        return Log.getLogger(logger).isDebugEnabled();
    }
    
    public void setDebugEnabled(final String logger, final Boolean enabled) {
        Log.getLogger(logger).setDebugEnabled(enabled);
    }
}
