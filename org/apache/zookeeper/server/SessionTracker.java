// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import java.io.PrintWriter;
import org.apache.zookeeper.KeeperException;

public interface SessionTracker
{
    long createSession(final int p0);
    
    void addSession(final long p0, final int p1);
    
    boolean touchSession(final long p0, final int p1);
    
    void setSessionClosing(final long p0);
    
    void shutdown();
    
    void removeSession(final long p0);
    
    void checkSession(final long p0, final Object p1) throws KeeperException.SessionExpiredException, KeeperException.SessionMovedException;
    
    void setOwner(final long p0, final Object p1) throws KeeperException.SessionExpiredException;
    
    void dumpSessions(final PrintWriter p0);
    
    public interface SessionExpirer
    {
        void expire(final Session p0);
        
        long getServerId();
    }
    
    public interface Session
    {
        long getSessionId();
        
        int getTimeout();
        
        boolean isClosing();
    }
}
