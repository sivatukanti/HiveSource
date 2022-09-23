// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import org.mortbay.log.Log;
import javax.servlet.http.HttpServletRequest;
import java.util.Random;
import org.mortbay.util.MultiMap;
import org.mortbay.jetty.SessionIdManager;
import org.mortbay.component.AbstractLifeCycle;

public class HashSessionIdManager extends AbstractLifeCycle implements SessionIdManager
{
    private static final String __NEW_SESSION_ID = "org.mortbay.jetty.newSessionId";
    MultiMap _sessions;
    protected Random _random;
    private boolean _weakRandom;
    private String _workerName;
    
    public HashSessionIdManager() {
    }
    
    public HashSessionIdManager(final Random random) {
        this._random = random;
    }
    
    public String getWorkerName() {
        return this._workerName;
    }
    
    public void setWorkerName(final String workerName) {
        this._workerName = workerName;
    }
    
    public String getNodeId(final String clusterId, final HttpServletRequest request) {
        final String worker = (request == null) ? null : ((String)request.getAttribute("org.mortbay.http.ajp.JVMRoute"));
        if (worker != null) {
            return clusterId + '.' + worker;
        }
        if (this._workerName != null) {
            return clusterId + '.' + this._workerName;
        }
        return clusterId;
    }
    
    public String getClusterId(final String nodeId) {
        final int dot = nodeId.lastIndexOf(46);
        return (dot > 0) ? nodeId.substring(0, dot) : nodeId;
    }
    
    protected void doStart() {
        if (this._random == null) {
            try {
                Log.debug("Init SecureRandom.");
                this._random = new SecureRandom();
            }
            catch (Exception e) {
                Log.warn("Could not generate SecureRandom for session-id randomness", e);
                this._random = new Random();
                this._weakRandom = true;
            }
        }
        this._sessions = new MultiMap();
    }
    
    protected void doStop() {
        if (this._sessions != null) {
            this._sessions.clear();
        }
        this._sessions = null;
    }
    
    public boolean idInUse(final String id) {
        return this._sessions.containsKey(id);
    }
    
    public void addSession(final HttpSession session) {
        synchronized (this) {
            this._sessions.add(this.getClusterId(session.getId()), session);
        }
    }
    
    public void removeSession(final HttpSession session) {
        synchronized (this) {
            this._sessions.removeValue(this.getClusterId(session.getId()), session);
        }
    }
    
    public void invalidateAll(final String id) {
        while (true) {
            AbstractSessionManager.Session session = null;
            synchronized (this) {
                if (!this._sessions.containsKey(id)) {
                    return;
                }
                session = (AbstractSessionManager.Session)this._sessions.getValue(id, 0);
                this._sessions.removeValue(id, session);
            }
            if (session.isValid()) {
                session.invalidate();
            }
        }
    }
    
    public String newSessionId(final HttpServletRequest request, final long created) {
        synchronized (this) {
            final String requested_id = request.getRequestedSessionId();
            if (requested_id != null) {
                final String cluster_id = this.getClusterId(requested_id);
                if (this.idInUse(cluster_id)) {
                    return cluster_id;
                }
            }
            final String new_id = (String)request.getAttribute("org.mortbay.jetty.newSessionId");
            if (new_id != null && this.idInUse(new_id)) {
                return new_id;
            }
            String id;
            long r0;
            long r2;
            for (id = null; id == null || id.length() == 0 || this.idInUse(id); id = Long.toString(r0, 36) + Long.toString(r2, 36)) {
                r0 = (this._weakRandom ? ((long)this.hashCode() ^ Runtime.getRuntime().freeMemory() ^ (long)this._random.nextInt() ^ (long)request.hashCode() << 32) : this._random.nextLong());
                r2 = this._random.nextLong();
                if (r0 < 0L) {
                    r0 = -r0;
                }
                if (r2 < 0L) {
                    r2 = -r2;
                }
            }
            if (this._workerName != null) {
                id = this._workerName + id;
            }
            request.setAttribute("org.mortbay.jetty.newSessionId", id);
            return id;
        }
    }
    
    public Random getRandom() {
        return this._random;
    }
    
    public void setRandom(final Random random) {
        this._random = random;
        this._weakRandom = false;
    }
}
