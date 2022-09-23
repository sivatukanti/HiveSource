// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import sun.misc.Signal;
import sun.misc.SignalHandler;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public enum SignalLogger
{
    INSTANCE;
    
    private boolean registered;
    
    private SignalLogger() {
        this.registered = false;
    }
    
    public void register(final Log LOG) {
        this.register(LogAdapter.create(LOG));
    }
    
    void register(final LogAdapter LOG) {
        if (this.registered) {
            throw new IllegalStateException("Can't re-install the signal handlers.");
        }
        this.registered = true;
        final StringBuilder bld = new StringBuilder();
        bld.append("registered UNIX signal handlers for [");
        final String[] SIGNALS = { "TERM", "HUP", "INT" };
        String separator = "";
        for (final String signalName : SIGNALS) {
            try {
                new Handler(signalName, LOG);
                bld.append(separator);
                bld.append(signalName);
                separator = ", ";
            }
            catch (Exception e) {
                LOG.debug(e);
            }
        }
        bld.append("]");
        LOG.info(bld.toString());
    }
    
    private static class Handler implements SignalHandler
    {
        private final LogAdapter LOG;
        private final SignalHandler prevHandler;
        
        Handler(final String name, final LogAdapter LOG) {
            this.LOG = LOG;
            this.prevHandler = Signal.handle(new Signal(name), this);
        }
        
        @Override
        public void handle(final Signal signal) {
            this.LOG.error("RECEIVED SIGNAL " + signal.getNumber() + ": SIG" + signal.getName());
            this.prevHandler.handle(signal);
        }
    }
}
