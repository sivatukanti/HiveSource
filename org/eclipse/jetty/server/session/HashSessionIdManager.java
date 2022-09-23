// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.session;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.Random;
import java.util.HashMap;
import javax.servlet.http.HttpSession;
import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.Map;

public class HashSessionIdManager extends AbstractSessionIdManager
{
    private final Map<String, Set<WeakReference<HttpSession>>> _sessions;
    
    public HashSessionIdManager() {
        this._sessions = new HashMap<String, Set<WeakReference<HttpSession>>>();
    }
    
    public HashSessionIdManager(final Random random) {
        super(random);
        this._sessions = new HashMap<String, Set<WeakReference<HttpSession>>>();
    }
    
    public Collection<String> getSessions() {
        return Collections.unmodifiableCollection((Collection<? extends String>)this._sessions.keySet());
    }
    
    public Collection<HttpSession> getSession(final String id) {
        final ArrayList<HttpSession> sessions = new ArrayList<HttpSession>();
        final Set<WeakReference<HttpSession>> refs = this._sessions.get(id);
        if (refs != null) {
            for (final WeakReference<HttpSession> ref : refs) {
                final HttpSession session = ref.get();
                if (session != null) {
                    sessions.add(session);
                }
            }
        }
        return sessions;
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        this._sessions.clear();
        super.doStop();
    }
    
    @Override
    public boolean idInUse(final String id) {
        synchronized (this) {
            return this._sessions.containsKey(id);
        }
    }
    
    @Override
    public void addSession(final HttpSession session) {
        final String id = this.getClusterId(session.getId());
        final WeakReference<HttpSession> ref = new WeakReference<HttpSession>(session);
        synchronized (this) {
            Set<WeakReference<HttpSession>> sessions = this._sessions.get(id);
            if (sessions == null) {
                sessions = new HashSet<WeakReference<HttpSession>>();
                this._sessions.put(id, sessions);
            }
            sessions.add(ref);
        }
    }
    
    @Override
    public void removeSession(final HttpSession session) {
        final String id = this.getClusterId(session.getId());
        synchronized (this) {
            final Collection<WeakReference<HttpSession>> sessions = this._sessions.get(id);
            if (sessions != null) {
                final Iterator<WeakReference<HttpSession>> iter = sessions.iterator();
                while (iter.hasNext()) {
                    final WeakReference<HttpSession> ref = iter.next();
                    final HttpSession s = ref.get();
                    if (s == null) {
                        iter.remove();
                    }
                    else {
                        if (s == session) {
                            iter.remove();
                            break;
                        }
                        continue;
                    }
                }
                if (sessions.isEmpty()) {
                    this._sessions.remove(id);
                }
            }
        }
    }
    
    @Override
    public void invalidateAll(final String id) {
        final Collection<WeakReference<HttpSession>> sessions;
        synchronized (this) {
            sessions = this._sessions.remove(id);
        }
        if (sessions != null) {
            for (final WeakReference<HttpSession> ref : sessions) {
                final AbstractSession session = ref.get();
                if (session != null && session.isValid()) {
                    session.invalidate();
                }
            }
            sessions.clear();
        }
    }
    
    @Override
    public void renewSessionId(final String oldClusterId, final String oldNodeId, final HttpServletRequest request) {
        final String newClusterId = this.newSessionId(request.hashCode());
        synchronized (this) {
            final Set<WeakReference<HttpSession>> sessions = this._sessions.remove(oldClusterId);
            if (sessions != null) {
                for (final WeakReference<HttpSession> ref : sessions) {
                    final HttpSession s = ref.get();
                    if (s == null) {
                        continue;
                    }
                    if (!(s instanceof AbstractSession)) {
                        continue;
                    }
                    final AbstractSession abstractSession = (AbstractSession)s;
                    abstractSession.getSessionManager().renewSessionId(oldClusterId, oldNodeId, newClusterId, this.getNodeId(newClusterId, request));
                }
                this._sessions.put(newClusterId, sessions);
            }
        }
    }
}
