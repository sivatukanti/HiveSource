// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class LoggingStateChangeListener implements ServiceStateChangeListener
{
    private static final Logger LOG;
    private final Logger log;
    
    public LoggingStateChangeListener(final Logger log) {
        log.isDebugEnabled();
        this.log = log;
    }
    
    public LoggingStateChangeListener() {
        this(LoggingStateChangeListener.LOG);
    }
    
    @Override
    public void stateChanged(final Service service) {
        this.log.info("Entry to state " + service.getServiceState() + " for " + service.getName());
    }
    
    static {
        LOG = LoggerFactory.getLogger(LoggingStateChangeListener.class);
    }
}
