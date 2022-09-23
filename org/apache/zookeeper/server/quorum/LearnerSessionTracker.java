// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import java.io.PrintWriter;
import org.apache.zookeeper.server.SessionTrackerImpl;
import org.apache.zookeeper.server.ZooKeeperServerListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import org.apache.zookeeper.server.SessionTracker;

public class LearnerSessionTracker implements SessionTracker
{
    SessionExpirer expirer;
    HashMap<Long, Integer> touchTable;
    long serverId;
    long nextSessionId;
    private ConcurrentHashMap<Long, Integer> sessionsWithTimeouts;
    
    public LearnerSessionTracker(final SessionExpirer expirer, final ConcurrentHashMap<Long, Integer> sessionsWithTimeouts, final long id, final ZooKeeperServerListener listener) {
        this.touchTable = new HashMap<Long, Integer>();
        this.serverId = 1L;
        this.nextSessionId = 0L;
        this.expirer = expirer;
        this.sessionsWithTimeouts = sessionsWithTimeouts;
        this.serverId = id;
        this.nextSessionId = SessionTrackerImpl.initializeNextSession(this.serverId);
    }
    
    @Override
    public synchronized void removeSession(final long sessionId) {
        this.sessionsWithTimeouts.remove(sessionId);
        this.touchTable.remove(sessionId);
    }
    
    @Override
    public void shutdown() {
    }
    
    @Override
    public synchronized void addSession(final long sessionId, final int sessionTimeout) {
        this.sessionsWithTimeouts.put(sessionId, sessionTimeout);
        this.touchTable.put(sessionId, sessionTimeout);
    }
    
    @Override
    public synchronized boolean touchSession(final long sessionId, final int sessionTimeout) {
        this.touchTable.put(sessionId, sessionTimeout);
        return true;
    }
    
    synchronized HashMap<Long, Integer> snapshot() {
        final HashMap<Long, Integer> oldTouchTable = this.touchTable;
        this.touchTable = new HashMap<Long, Integer>();
        return oldTouchTable;
    }
    
    @Override
    public synchronized long createSession(final int sessionTimeout) {
        return this.nextSessionId++;
    }
    
    @Override
    public void checkSession(final long sessionId, final Object owner) {
    }
    
    @Override
    public void setOwner(final long sessionId, final Object owner) {
    }
    
    @Override
    public void dumpSessions(final PrintWriter pwriter) {
        pwriter.println(this.toString());
    }
    
    @Override
    public void setSessionClosing(final long sessionId) {
    }
}
