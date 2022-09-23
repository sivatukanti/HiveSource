// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.context;

import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.error.ShutdownException;
import org.apache.derby.iapi.error.StandardException;

final class SystemContext extends ContextImpl
{
    SystemContext(final ContextManager contextManager) {
        super(contextManager, "SystemContext");
    }
    
    public void cleanupOnError(final Throwable t) {
        boolean b = false;
        if (t instanceof StandardException) {
            final int severity = ((StandardException)t).getSeverity();
            if (severity < 40000) {
                return;
            }
            this.popMe();
            if (severity >= 50000) {
                b = true;
            }
        }
        else if (!(t instanceof ShutdownException)) {
            if (t instanceof ThreadDeath) {}
        }
        if (!b) {
            this.getContextManager().owningCsf.removeContext(this.getContextManager());
            return;
        }
        try {
            System.err.println("Shutting down due to severe error.");
            Monitor.getStream().printlnWithHeader("Shutting down due to severe error." + t.getMessage());
        }
        finally {
            Monitor.getMonitor().shutdown();
        }
    }
}
