// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.daemon;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.daemon.DaemonService;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.services.daemon.DaemonFactory;

public class SingleThreadDaemonFactory implements DaemonFactory
{
    private final ContextService contextService;
    
    public SingleThreadDaemonFactory() {
        this.contextService = ContextService.getFactory();
    }
    
    public DaemonService createNewDaemon(final String s) {
        final BasicDaemon basicDaemon = new BasicDaemon(this.contextService);
        final Thread daemonThread = Monitor.getMonitor().getDaemonThread(basicDaemon, s, false);
        try {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                public Object run() {
                    daemonThread.setContextClassLoader(null);
                    return null;
                }
            });
        }
        catch (SecurityException ex) {}
        daemonThread.start();
        return basicDaemon;
    }
}
