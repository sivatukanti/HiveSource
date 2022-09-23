// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import java.util.HashSet;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.KeeperException;
import java.io.Writer;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import org.apache.zookeeper.common.Time;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import org.slf4j.Logger;

public class SessionTrackerImpl extends ZooKeeperCriticalThread implements SessionTracker
{
    private static final Logger LOG;
    HashMap<Long, SessionImpl> sessionsById;
    HashMap<Long, SessionSet> sessionSets;
    ConcurrentHashMap<Long, Integer> sessionsWithTimeout;
    long nextSessionId;
    long nextExpirationTime;
    int expirationInterval;
    SessionExpirer expirer;
    volatile boolean running;
    volatile long currentTime;
    
    public static long initializeNextSession(final long id) {
        long nextSid = 0L;
        nextSid = Time.currentElapsedTime() << 24 >>> 8;
        nextSid |= id << 56;
        return nextSid;
    }
    
    private long roundToInterval(final long time) {
        return (time / this.expirationInterval + 1L) * this.expirationInterval;
    }
    
    public SessionTrackerImpl(final SessionExpirer expirer, final ConcurrentHashMap<Long, Integer> sessionsWithTimeout, final int tickTime, final long sid, final ZooKeeperServerListener listener) {
        super("SessionTracker", listener);
        this.sessionsById = new HashMap<Long, SessionImpl>();
        this.sessionSets = new HashMap<Long, SessionSet>();
        this.nextSessionId = 0L;
        this.running = true;
        this.expirer = expirer;
        this.expirationInterval = tickTime;
        this.sessionsWithTimeout = sessionsWithTimeout;
        this.nextExpirationTime = this.roundToInterval(Time.currentElapsedTime());
        this.nextSessionId = initializeNextSession(sid);
        for (final Map.Entry<Long, Integer> e : sessionsWithTimeout.entrySet()) {
            this.addSession(e.getKey(), e.getValue());
        }
    }
    
    @Override
    public synchronized void dumpSessions(final PrintWriter pwriter) {
        pwriter.print("Session Sets (");
        pwriter.print(this.sessionSets.size());
        pwriter.println("):");
        final ArrayList<Long> keys = new ArrayList<Long>(this.sessionSets.keySet());
        Collections.sort(keys);
        for (final long time : keys) {
            pwriter.print(this.sessionSets.get(time).sessions.size());
            pwriter.print(" expire at ");
            pwriter.print(new Date(time));
            pwriter.println(":");
            for (final SessionImpl s : this.sessionSets.get(time).sessions) {
                pwriter.print("\t0x");
                pwriter.println(Long.toHexString(s.sessionId));
            }
        }
    }
    
    @Override
    public synchronized String toString() {
        final StringWriter sw = new StringWriter();
        final PrintWriter pwriter = new PrintWriter(sw);
        this.dumpSessions(pwriter);
        pwriter.flush();
        pwriter.close();
        return sw.toString();
    }
    
    @Override
    public synchronized void run() {
        try {
            while (this.running) {
                this.currentTime = Time.currentElapsedTime();
                if (this.nextExpirationTime > this.currentTime) {
                    this.wait(this.nextExpirationTime - this.currentTime);
                }
                else {
                    final SessionSet set = this.sessionSets.remove(this.nextExpirationTime);
                    if (set != null) {
                        for (final SessionImpl s : set.sessions) {
                            this.setSessionClosing(s.sessionId);
                            this.expirer.expire(s);
                        }
                    }
                    this.nextExpirationTime += this.expirationInterval;
                }
            }
        }
        catch (InterruptedException e) {
            this.handleException(this.getName(), e);
        }
        SessionTrackerImpl.LOG.info("SessionTrackerImpl exited loop!");
    }
    
    @Override
    public synchronized boolean touchSession(final long sessionId, final int timeout) {
        if (SessionTrackerImpl.LOG.isTraceEnabled()) {
            ZooTrace.logTraceMessage(SessionTrackerImpl.LOG, 8L, "SessionTrackerImpl --- Touch session: 0x" + Long.toHexString(sessionId) + " with timeout " + timeout);
        }
        final SessionImpl s = this.sessionsById.get(sessionId);
        if (s == null || s.isClosing()) {
            return false;
        }
        final long expireTime = this.roundToInterval(Time.currentElapsedTime() + timeout);
        if (s.tickTime >= expireTime) {
            return true;
        }
        SessionSet set = this.sessionSets.get(s.tickTime);
        if (set != null) {
            set.sessions.remove(s);
        }
        s.tickTime = expireTime;
        set = this.sessionSets.get(s.tickTime);
        if (set == null) {
            set = new SessionSet();
            this.sessionSets.put(expireTime, set);
        }
        set.sessions.add(s);
        return true;
    }
    
    @Override
    public synchronized void setSessionClosing(final long sessionId) {
        if (SessionTrackerImpl.LOG.isTraceEnabled()) {
            SessionTrackerImpl.LOG.info("Session closing: 0x" + Long.toHexString(sessionId));
        }
        final SessionImpl s = this.sessionsById.get(sessionId);
        if (s == null) {
            return;
        }
        s.isClosing = true;
    }
    
    @Override
    public synchronized void removeSession(final long sessionId) {
        final SessionImpl s = this.sessionsById.remove(sessionId);
        this.sessionsWithTimeout.remove(sessionId);
        if (SessionTrackerImpl.LOG.isTraceEnabled()) {
            ZooTrace.logTraceMessage(SessionTrackerImpl.LOG, 32L, "SessionTrackerImpl --- Removing session 0x" + Long.toHexString(sessionId));
        }
        if (s != null) {
            final SessionSet set = this.sessionSets.get(s.tickTime);
            if (set != null) {
                set.sessions.remove(s);
            }
        }
    }
    
    @Override
    public void shutdown() {
        SessionTrackerImpl.LOG.info("Shutting down");
        this.running = false;
        if (SessionTrackerImpl.LOG.isTraceEnabled()) {
            ZooTrace.logTraceMessage(SessionTrackerImpl.LOG, ZooTrace.getTextTraceLevel(), "Shutdown SessionTrackerImpl!");
        }
    }
    
    @Override
    public synchronized long createSession(final int sessionTimeout) {
        this.addSession(this.nextSessionId, sessionTimeout);
        return this.nextSessionId++;
    }
    
    @Override
    public synchronized void addSession(final long id, final int sessionTimeout) {
        this.sessionsWithTimeout.put(id, sessionTimeout);
        if (this.sessionsById.get(id) == null) {
            final SessionImpl s = new SessionImpl(id, sessionTimeout, 0L);
            this.sessionsById.put(id, s);
            if (SessionTrackerImpl.LOG.isTraceEnabled()) {
                ZooTrace.logTraceMessage(SessionTrackerImpl.LOG, 32L, "SessionTrackerImpl --- Adding session 0x" + Long.toHexString(id) + " " + sessionTimeout);
            }
        }
        else if (SessionTrackerImpl.LOG.isTraceEnabled()) {
            ZooTrace.logTraceMessage(SessionTrackerImpl.LOG, 32L, "SessionTrackerImpl --- Existing session 0x" + Long.toHexString(id) + " " + sessionTimeout);
        }
        this.touchSession(id, sessionTimeout);
    }
    
    @Override
    public synchronized void checkSession(final long sessionId, final Object owner) throws KeeperException.SessionExpiredException, KeeperException.SessionMovedException {
        final SessionImpl session = this.sessionsById.get(sessionId);
        if (session == null || session.isClosing()) {
            throw new KeeperException.SessionExpiredException();
        }
        if (session.owner == null) {
            session.owner = owner;
        }
        else if (session.owner != owner) {
            throw new KeeperException.SessionMovedException();
        }
    }
    
    @Override
    public synchronized void setOwner(final long id, final Object owner) throws KeeperException.SessionExpiredException {
        final SessionImpl session = this.sessionsById.get(id);
        if (session == null || session.isClosing()) {
            throw new KeeperException.SessionExpiredException();
        }
        session.owner = owner;
    }
    
    static {
        LOG = LoggerFactory.getLogger(SessionTrackerImpl.class);
    }
    
    public static class SessionImpl implements Session
    {
        final long sessionId;
        final int timeout;
        long tickTime;
        boolean isClosing;
        Object owner;
        
        SessionImpl(final long sessionId, final int timeout, final long expireTime) {
            this.sessionId = sessionId;
            this.timeout = timeout;
            this.tickTime = expireTime;
            this.isClosing = false;
        }
        
        @Override
        public long getSessionId() {
            return this.sessionId;
        }
        
        @Override
        public int getTimeout() {
            return this.timeout;
        }
        
        @Override
        public boolean isClosing() {
            return this.isClosing;
        }
    }
    
    static class SessionSet
    {
        HashSet<SessionImpl> sessions;
        
        SessionSet() {
            this.sessions = new HashSet<SessionImpl>();
        }
    }
}
