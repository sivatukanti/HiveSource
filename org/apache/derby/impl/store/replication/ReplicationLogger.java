// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.replication;

import org.apache.derby.iapi.error.ErrorStringBuilder;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.util.Date;
import org.apache.derby.iapi.services.property.PropertyUtil;

public class ReplicationLogger
{
    private final boolean verbose;
    private final String dbname;
    
    public ReplicationLogger(final String dbname) {
        this.verbose = PropertyUtil.getSystemBoolean("derby.replication.verbose", true);
        this.dbname = dbname;
    }
    
    public void logError(final String s, final Throwable t) {
        if (this.verbose) {
            Monitor.logTextMessage("R001", new Date());
            if (s != null) {
                Monitor.logTextMessage(s, this.dbname);
            }
            if (t != null) {
                final ErrorStringBuilder errorStringBuilder = new ErrorStringBuilder(Monitor.getStream().getHeader());
                errorStringBuilder.stackTrace(t);
                Monitor.logMessage(errorStringBuilder.get().toString());
                errorStringBuilder.reset();
            }
            Monitor.logTextMessage("R002");
        }
    }
    
    public void logText(final String s, final boolean b) {
        if (this.verbose) {
            if (b) {
                Monitor.logTextMessage("R001", new Date());
                Monitor.logMessage(s);
                Monitor.logTextMessage("R002");
            }
            else {
                Monitor.logTextMessage("R013", new Date(), s);
            }
        }
    }
}
