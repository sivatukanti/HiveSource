// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import org.mortbay.log.Log;
import java.security.SecureRandom;
import javax.servlet.http.HttpServletRequest;
import org.mortbay.jetty.Server;
import java.util.Random;
import org.mortbay.jetty.SessionIdManager;
import org.mortbay.component.AbstractLifeCycle;

public abstract class AbstractSessionIdManager extends AbstractLifeCycle implements SessionIdManager
{
    private static final String __NEW_SESSION_ID = "org.mortbay.jetty.newSessionId";
    protected Random _random;
    protected boolean _weakRandom;
    protected String _workerName;
    protected Server _server;
    
    public AbstractSessionIdManager(final Server server) {
        this._server = server;
    }
    
    public AbstractSessionIdManager(final Server server, final Random random) {
        this._random = random;
        this._server = server;
    }
    
    public String getWorkerName() {
        return this._workerName;
    }
    
    public void setWorkerName(final String name) {
        this._workerName = name;
    }
    
    public Random getRandom() {
        return this._random;
    }
    
    public void setRandom(final Random random) {
        this._random = random;
        this._weakRandom = false;
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
            for (id = null; id == null || id.length() == 0 || this.idInUse(id); id = this._workerName + id) {
                long r0 = this._weakRandom ? ((long)this.hashCode() ^ Runtime.getRuntime().freeMemory() ^ (long)this._random.nextInt() ^ (long)request.hashCode() << 32) : this._random.nextLong();
                if (r0 < 0L) {
                    r0 = -r0;
                }
                long r2 = this._weakRandom ? ((long)this.hashCode() ^ Runtime.getRuntime().freeMemory() ^ (long)this._random.nextInt() ^ (long)request.hashCode() << 32) : this._random.nextLong();
                if (r2 < 0L) {
                    r2 = -r2;
                }
                id = Long.toString(r0, 36) + Long.toString(r2, 36);
                if (this._workerName != null) {}
            }
            request.setAttribute("org.mortbay.jetty.newSessionId", id);
            return id;
        }
    }
    
    public void doStart() {
        this.initRandom();
    }
    
    public void initRandom() {
        if (this._random == null) {
            try {
                this._random = new SecureRandom();
                this._weakRandom = false;
            }
            catch (Exception e) {
                Log.warn("Could not generate SecureRandom for session-id randomness", e);
                this._random = new Random();
                this._weakRandom = true;
            }
        }
        this._random.setSeed(this._random.nextLong() ^ System.currentTimeMillis() ^ (long)this.hashCode() ^ Runtime.getRuntime().freeMemory());
    }
}
