// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.eclipse.jetty.util.component.LifeCycle;

public interface SessionIdManager extends LifeCycle
{
    boolean idInUse(final String p0);
    
    void addSession(final HttpSession p0);
    
    void removeSession(final HttpSession p0);
    
    void invalidateAll(final String p0);
    
    String newSessionId(final HttpServletRequest p0, final long p1);
    
    String getWorkerName();
    
    String getClusterId(final String p0);
    
    String getNodeId(final String p0, final HttpServletRequest p1);
    
    void renewSessionId(final String p0, final String p1, final HttpServletRequest p2);
}
