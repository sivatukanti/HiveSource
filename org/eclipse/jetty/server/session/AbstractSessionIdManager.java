// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.session;

import org.eclipse.jetty.util.log.Log;
import java.security.SecureRandom;
import javax.servlet.http.HttpServletRequest;
import java.util.Random;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public abstract class AbstractSessionIdManager extends AbstractLifeCycle implements SessionIdManager
{
    private static final Logger LOG;
    private static final String __NEW_SESSION_ID = "org.eclipse.jetty.server.newSessionId";
    protected Random _random;
    protected boolean _weakRandom;
    protected String _workerName;
    protected String _workerAttr;
    protected long _reseed;
    
    public AbstractSessionIdManager() {
        this._reseed = 100000L;
    }
    
    public AbstractSessionIdManager(final Random random) {
        this._reseed = 100000L;
        this._random = random;
    }
    
    @Override
    public String getWorkerName() {
        return this._workerName;
    }
    
    public void setWorkerName(final String workerName) {
        if (this.isRunning()) {
            throw new IllegalStateException(this.getState());
        }
        if (workerName.contains(".")) {
            throw new IllegalArgumentException("Name cannot contain '.'");
        }
        this._workerName = workerName;
    }
    
    public Random getRandom() {
        return this._random;
    }
    
    public synchronized void setRandom(final Random random) {
        this._random = random;
        this._weakRandom = false;
    }
    
    public long getReseed() {
        return this._reseed;
    }
    
    public void setReseed(final long reseed) {
        this._reseed = reseed;
    }
    
    @Override
    public String newSessionId(final HttpServletRequest request, final long created) {
        synchronized (this) {
            if (request == null) {
                return this.newSessionId(created);
            }
            final String requested_id = request.getRequestedSessionId();
            if (requested_id != null) {
                final String cluster_id = this.getClusterId(requested_id);
                if (this.idInUse(cluster_id)) {
                    return cluster_id;
                }
            }
            final String new_id = (String)request.getAttribute("org.eclipse.jetty.server.newSessionId");
            if (new_id != null && this.idInUse(new_id)) {
                return new_id;
            }
            final String id = this.newSessionId(request.hashCode());
            request.setAttribute("org.eclipse.jetty.server.newSessionId", id);
            return id;
        }
    }
    
    public String newSessionId(final long seedTerm) {
        String id;
        for (id = null; id == null || id.length() == 0 || this.idInUse(id); id = this._workerName + id) {
            long r0 = this._weakRandom ? ((long)this.hashCode() ^ Runtime.getRuntime().freeMemory() ^ (long)this._random.nextInt() ^ seedTerm << 32) : this._random.nextLong();
            if (r0 < 0L) {
                r0 = -r0;
            }
            if (this._reseed > 0L && r0 % this._reseed == 1L) {
                if (AbstractSessionIdManager.LOG.isDebugEnabled()) {
                    AbstractSessionIdManager.LOG.debug("Reseeding {}", this);
                }
                if (this._random instanceof SecureRandom) {
                    final SecureRandom secure = (SecureRandom)this._random;
                    secure.setSeed(secure.generateSeed(8));
                }
                else {
                    this._random.setSeed(this._random.nextLong() ^ System.currentTimeMillis() ^ seedTerm ^ Runtime.getRuntime().freeMemory());
                }
            }
            long r2 = this._weakRandom ? ((long)this.hashCode() ^ Runtime.getRuntime().freeMemory() ^ (long)this._random.nextInt() ^ seedTerm << 32) : this._random.nextLong();
            if (r2 < 0L) {
                r2 = -r2;
            }
            id = Long.toString(r0, 36) + Long.toString(r2, 36);
            if (this._workerName != null) {}
        }
        return id;
    }
    
    @Override
    public abstract void renewSessionId(final String p0, final String p1, final HttpServletRequest p2);
    
    @Override
    protected void doStart() throws Exception {
        this.initRandom();
        this._workerAttr = ((this._workerName != null && this._workerName.startsWith("$")) ? this._workerName.substring(1) : null);
    }
    
    @Override
    protected void doStop() throws Exception {
    }
    
    public void initRandom() {
        if (this._random == null) {
            try {
                this._random = new SecureRandom();
            }
            catch (Exception e) {
                AbstractSessionIdManager.LOG.warn("Could not generate SecureRandom for session-id randomness", e);
                this._random = new Random();
                this._weakRandom = true;
            }
        }
        else {
            this._random.setSeed(this._random.nextLong() ^ System.currentTimeMillis() ^ (long)this.hashCode() ^ Runtime.getRuntime().freeMemory());
        }
    }
    
    @Override
    public String getNodeId(final String clusterId, final HttpServletRequest request) {
        if (this._workerName != null) {
            if (this._workerAttr == null) {
                return clusterId + '.' + this._workerName;
            }
            final String worker = (String)request.getAttribute(this._workerAttr);
            if (worker != null) {
                return clusterId + '.' + worker;
            }
        }
        return clusterId;
    }
    
    @Override
    public String getClusterId(final String nodeId) {
        final int dot = nodeId.lastIndexOf(46);
        return (dot > 0) ? nodeId.substring(0, dot) : nodeId;
    }
    
    static {
        LOG = Log.getLogger(AbstractSessionIdManager.class);
    }
}
