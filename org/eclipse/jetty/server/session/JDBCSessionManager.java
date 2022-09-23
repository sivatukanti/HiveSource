// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.session;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.server.handler.ContextHandler;
import java.sql.SQLException;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import org.eclipse.jetty.server.Request;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.Map;
import org.eclipse.jetty.util.ClassLoadingObjectInputStream;
import java.util.concurrent.atomic.AtomicReference;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.util.log.Logger;

public class JDBCSessionManager extends AbstractSessionManager
{
    private static final Logger LOG;
    private ConcurrentHashMap<String, Session> _sessions;
    protected JDBCSessionIdManager _jdbcSessionIdMgr;
    protected long _saveIntervalSec;
    protected JDBCSessionIdManager.SessionTableSchema _sessionTableSchema;
    
    public JDBCSessionManager() {
        this._jdbcSessionIdMgr = null;
        this._saveIntervalSec = 60L;
    }
    
    public void setSaveInterval(final long sec) {
        this._saveIntervalSec = sec;
    }
    
    public long getSaveInterval() {
        return this._saveIntervalSec;
    }
    
    public void cacheInvalidate(final Session session) {
    }
    
    @Override
    public Session getSession(final String idInCluster) {
        Session session = null;
        synchronized (this) {
            final Session memSession = this._sessions.get(idInCluster);
            final long now = System.currentTimeMillis();
            if (JDBCSessionManager.LOG.isDebugEnabled()) {
                if (memSession == null) {
                    JDBCSessionManager.LOG.debug("getSession(" + idInCluster + "): not in session map, now=" + now + " lastSaved=" + ((memSession == null) ? 0L : memSession._lastSaved) + " interval=" + this._saveIntervalSec * 1000L, new Object[0]);
                }
                else {
                    JDBCSessionManager.LOG.debug("getSession(" + idInCluster + "): in session map,  hashcode=" + memSession.hashCode() + " now=" + now + " lastSaved=" + ((memSession == null) ? 0L : memSession._lastSaved) + " interval=" + this._saveIntervalSec * 1000L + " lastNode=" + memSession._lastNode + " thisNode=" + this.getSessionIdManager().getWorkerName() + " difference=" + (now - memSession._lastSaved), new Object[0]);
                }
            }
            try {
                if (memSession == null) {
                    if (JDBCSessionManager.LOG.isDebugEnabled()) {
                        JDBCSessionManager.LOG.debug("getSession(" + idInCluster + "): no session in session map. Reloading session data from db.", new Object[0]);
                    }
                    session = this.loadSession(idInCluster, canonicalize(this._context.getContextPath()), getVirtualHost(this._context));
                }
                else if (now - memSession._lastSaved >= this._saveIntervalSec * 1000L) {
                    if (JDBCSessionManager.LOG.isDebugEnabled()) {
                        JDBCSessionManager.LOG.debug("getSession(" + idInCluster + "): stale session. Reloading session data from db.", new Object[0]);
                    }
                    session = this.loadSession(idInCluster, canonicalize(this._context.getContextPath()), getVirtualHost(this._context));
                }
                else {
                    if (JDBCSessionManager.LOG.isDebugEnabled()) {
                        JDBCSessionManager.LOG.debug("getSession(" + idInCluster + "): session in session map", new Object[0]);
                    }
                    session = memSession;
                }
            }
            catch (Exception e) {
                JDBCSessionManager.LOG.warn("Unable to load session " + idInCluster, e);
                return null;
            }
            if (session != null) {
                if (!session.getLastNode().equals(this.getSessionIdManager().getWorkerName()) || memSession == null) {
                    if (session._expiryTime <= 0L || session._expiryTime > now) {
                        if (JDBCSessionManager.LOG.isDebugEnabled()) {
                            JDBCSessionManager.LOG.debug("getSession(" + idInCluster + "): lastNode=" + session.getLastNode() + " thisNode=" + this.getSessionIdManager().getWorkerName(), new Object[0]);
                        }
                        session.setLastNode(this.getSessionIdManager().getWorkerName());
                        this._sessions.put(idInCluster, session);
                        try {
                            this.updateSessionNode(session);
                            session.didActivate();
                            return session;
                        }
                        catch (Exception e) {
                            JDBCSessionManager.LOG.warn("Unable to update freshly loaded session " + idInCluster, e);
                            return null;
                        }
                    }
                    if (JDBCSessionManager.LOG.isDebugEnabled()) {
                        JDBCSessionManager.LOG.debug("getSession ({}): Session has expired", idInCluster);
                    }
                    this._jdbcSessionIdMgr.removeSession(idInCluster);
                    session = null;
                }
                else {
                    session = memSession;
                    if (JDBCSessionManager.LOG.isDebugEnabled()) {
                        JDBCSessionManager.LOG.debug("getSession({}): Session not stale {}", idInCluster, session);
                    }
                }
            }
            else {
                if (memSession != null) {
                    this.removeSession(memSession, true);
                }
                JDBCSessionManager.LOG.debug("getSession({}): No session in database matching id={}", idInCluster, idInCluster);
            }
            return session;
        }
    }
    
    @Override
    public int getSessions() {
        return this._sessions.size();
    }
    
    @Override
    public void doStart() throws Exception {
        if (this._sessionIdManager == null) {
            throw new IllegalStateException("No session id manager defined");
        }
        this._jdbcSessionIdMgr = (JDBCSessionIdManager)this._sessionIdManager;
        this._sessionTableSchema = this._jdbcSessionIdMgr.getSessionTableSchema();
        this._sessions = new ConcurrentHashMap<String, Session>();
        super.doStart();
    }
    
    @Override
    public void doStop() throws Exception {
        super.doStop();
        this._sessions.clear();
        this._sessions = null;
    }
    
    @Override
    protected void shutdownSessions() {
        final long gracefulStopMs = this.getContextHandler().getServer().getStopTimeout();
        long stopTime = 0L;
        if (gracefulStopMs > 0L) {
            stopTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(gracefulStopMs, TimeUnit.MILLISECONDS);
        }
        for (ArrayList<Session> sessions = (this._sessions == null) ? new ArrayList<Session>() : new ArrayList<Session>(this._sessions.values()); sessions.size() > 0 && ((stopTime > 0L && System.nanoTime() < stopTime) || stopTime == 0L); sessions = new ArrayList<Session>(this._sessions.values())) {
            for (final Session session : sessions) {
                try {
                    session.save(false);
                }
                catch (Exception e) {
                    JDBCSessionManager.LOG.warn(e);
                }
                this._sessions.remove(session.getClusterId());
            }
            if (stopTime == 0L) {
                break;
            }
        }
    }
    
    @Override
    public void renewSessionId(final String oldClusterId, final String oldNodeId, final String newClusterId, final String newNodeId) {
        Session session = null;
        try {
            session = this._sessions.remove(oldClusterId);
            if (session != null) {
                synchronized (session) {
                    session.setClusterId(newClusterId);
                    session.setNodeId(newNodeId);
                    this._sessions.put(newClusterId, session);
                    this.updateSession(session);
                }
            }
        }
        catch (Exception e) {
            JDBCSessionManager.LOG.warn(e);
        }
        super.renewSessionId(oldClusterId, oldNodeId, newClusterId, newNodeId);
    }
    
    protected void invalidateSession(final String idInCluster) {
        final Session session = this._sessions.get(idInCluster);
        if (session != null) {
            session.invalidate();
        }
    }
    
    @Override
    protected boolean removeSession(final String idInCluster) {
        final Session session = this._sessions.remove(idInCluster);
        try {
            if (session != null) {
                this.deleteSession(session);
            }
        }
        catch (Exception e) {
            JDBCSessionManager.LOG.warn("Problem deleting session id=" + idInCluster, e);
        }
        return session != null;
    }
    
    @Override
    protected void addSession(final AbstractSession session) {
        if (session == null) {
            return;
        }
        this._sessions.put(session.getClusterId(), (Session)session);
        try {
            synchronized (session) {
                session.willPassivate();
                this.storeSession((Session)session);
                session.didActivate();
            }
        }
        catch (Exception e) {
            JDBCSessionManager.LOG.warn("Unable to store new session id=" + session.getId(), e);
        }
    }
    
    @Override
    protected AbstractSession newSession(final HttpServletRequest request) {
        return new Session(request);
    }
    
    protected AbstractSession newSession(final String sessionId, final String rowId, final long created, final long accessed, final long maxInterval) {
        return new Session(sessionId, rowId, created, accessed, maxInterval);
    }
    
    @Override
    public boolean removeSession(final AbstractSession session, final boolean invalidate) {
        final boolean removed = super.removeSession(session, invalidate);
        if (removed && !invalidate) {
            session.willPassivate();
        }
        return removed;
    }
    
    protected Set<String> expire(final Set<String> sessionIds) {
        if (this.isStopping() || this.isStopped()) {
            return null;
        }
        final Thread thread = Thread.currentThread();
        final ClassLoader old_loader = thread.getContextClassLoader();
        final Set<String> successfullyExpiredIds = new HashSet<String>();
        try {
            for (final String sessionId : sessionIds) {
                if (JDBCSessionManager.LOG.isDebugEnabled()) {
                    JDBCSessionManager.LOG.debug("Expiring session id " + sessionId, new Object[0]);
                }
                Session session = this._sessions.get(sessionId);
                if (session == null) {
                    if (JDBCSessionManager.LOG.isDebugEnabled()) {
                        JDBCSessionManager.LOG.debug("Force loading session id " + sessionId, new Object[0]);
                    }
                    session = this.loadSession(sessionId, canonicalize(this._context.getContextPath()), getVirtualHost(this._context));
                    if (session != null) {
                        this._sessions.put(session.getClusterId(), session);
                    }
                    else {
                        if (JDBCSessionManager.LOG.isDebugEnabled()) {
                            JDBCSessionManager.LOG.debug("Unrecognized session id=" + sessionId, new Object[0]);
                            continue;
                        }
                        continue;
                    }
                }
                if (session != null) {
                    session.timeout();
                    successfullyExpiredIds.add(session.getClusterId());
                }
            }
            return successfullyExpiredIds;
        }
        catch (Throwable t) {
            JDBCSessionManager.LOG.warn("Problem expiring sessions", t);
            return successfullyExpiredIds;
        }
        finally {
            thread.setContextClassLoader(old_loader);
        }
    }
    
    protected void expireCandidates(final Set<String> candidateIds) {
        final Iterator<String> itor = candidateIds.iterator();
        final long now = System.currentTimeMillis();
        while (itor.hasNext()) {
            final String id = itor.next();
            try {
                final Session memSession = this._sessions.get(id);
                if (memSession == null) {
                    continue;
                }
                final Session s = this.loadSession(id, canonicalize(this._context.getContextPath()), getVirtualHost(this._context));
                if (s != null) {
                    continue;
                }
                memSession.timeout();
            }
            catch (Exception e) {
                JDBCSessionManager.LOG.warn("Error checking db for expiry for session {}", id);
            }
        }
    }
    
    protected Set<String> getCandidateExpiredIds() {
        final HashSet<String> expiredIds = new HashSet<String>();
        for (final String id : this._sessions.keySet()) {
            final Session session = this._sessions.get(id);
            if (session._expiryTime > 0L && System.currentTimeMillis() > session._expiryTime) {
                expiredIds.add(id);
            }
        }
        return expiredIds;
    }
    
    protected Session loadSession(final String id, final String canonicalContextPath, final String vhost) throws Exception {
        final AtomicReference<Session> _reference = new AtomicReference<Session>();
        final AtomicReference<Exception> _exception = new AtomicReference<Exception>();
        final Runnable load = new Runnable() {
            @Override
            public void run() {
                try {
                    final Connection connection = JDBCSessionManager.this.getConnection();
                    Throwable x0 = null;
                    try {
                        final PreparedStatement statement = JDBCSessionManager.this._sessionTableSchema.getLoadStatement(connection, id, canonicalContextPath, vhost);
                        Throwable x2 = null;
                        try {
                            final ResultSet result = statement.executeQuery();
                            Throwable x3 = null;
                            try {
                                Session session = null;
                                if (result.next()) {
                                    long maxInterval = result.getLong(JDBCSessionManager.this._sessionTableSchema.getMaxIntervalColumn());
                                    if (maxInterval == -999L) {
                                        maxInterval = JDBCSessionManager.this.getMaxInactiveInterval();
                                    }
                                    session = (Session)JDBCSessionManager.this.newSession(id, result.getString(JDBCSessionManager.this._sessionTableSchema.getRowIdColumn()), result.getLong(JDBCSessionManager.this._sessionTableSchema.getCreateTimeColumn()), result.getLong(JDBCSessionManager.this._sessionTableSchema.getAccessTimeColumn()), maxInterval);
                                    session.setCookieSetTime(result.getLong(JDBCSessionManager.this._sessionTableSchema.getCookieTimeColumn()));
                                    session.setLastAccessedTime(result.getLong(JDBCSessionManager.this._sessionTableSchema.getLastAccessTimeColumn()));
                                    session.setLastNode(result.getString(JDBCSessionManager.this._sessionTableSchema.getLastNodeColumn()));
                                    session.setLastSaved(result.getLong(JDBCSessionManager.this._sessionTableSchema.getLastSavedTimeColumn()));
                                    session.setExpiryTime(result.getLong(JDBCSessionManager.this._sessionTableSchema.getExpiryTimeColumn()));
                                    session.setCanonicalContext(result.getString(JDBCSessionManager.this._sessionTableSchema.getContextPathColumn()));
                                    session.setVirtualHost(result.getString(JDBCSessionManager.this._sessionTableSchema.getVirtualHostColumn()));
                                    final InputStream is = ((JDBCSessionIdManager)JDBCSessionManager.this.getSessionIdManager())._dbAdaptor.getBlobInputStream(result, JDBCSessionManager.this._sessionTableSchema.getMapColumn());
                                    Throwable x4 = null;
                                    try {
                                        final ClassLoadingObjectInputStream ois = new ClassLoadingObjectInputStream(is);
                                        Throwable x5 = null;
                                        try {
                                            final Object o = ois.readObject();
                                            session.addAttributes((Map<String, Object>)o);
                                        }
                                        catch (Throwable t) {
                                            x5 = t;
                                            throw t;
                                        }
                                        finally {
                                            $closeResource(x5, ois);
                                        }
                                    }
                                    catch (Throwable t2) {
                                        x4 = t2;
                                        throw t2;
                                    }
                                    finally {
                                        if (is != null) {
                                            $closeResource(x4, is);
                                        }
                                    }
                                    if (JDBCSessionManager.LOG.isDebugEnabled()) {
                                        JDBCSessionManager.LOG.debug("LOADED session " + session, new Object[0]);
                                    }
                                }
                                else if (JDBCSessionManager.LOG.isDebugEnabled()) {
                                    JDBCSessionManager.LOG.debug("Failed to load session " + id, new Object[0]);
                                }
                                _reference.set(session);
                            }
                            catch (Throwable t3) {
                                x3 = t3;
                                throw t3;
                            }
                            finally {
                                if (result != null) {
                                    $closeResource(x3, result);
                                }
                            }
                        }
                        catch (Throwable t4) {
                            x2 = t4;
                            throw t4;
                        }
                        finally {
                            if (statement != null) {
                                $closeResource(x2, statement);
                            }
                        }
                    }
                    catch (Throwable t5) {
                        x0 = t5;
                        throw t5;
                    }
                    finally {
                        if (connection != null) {
                            $closeResource(x0, connection);
                        }
                    }
                }
                catch (Exception e) {
                    _exception.set(e);
                }
            }
            
            private static /* synthetic */ void $closeResource(final Throwable x0, final AutoCloseable x1) {
                if (x0 != null) {
                    try {
                        x1.close();
                    }
                    catch (Throwable exception) {
                        x0.addSuppressed(exception);
                    }
                }
                else {
                    x1.close();
                }
            }
        };
        if (this._context == null) {
            load.run();
        }
        else {
            this._context.getContextHandler().handle(null, load);
        }
        if (_exception.get() != null) {
            this._jdbcSessionIdMgr.removeSession(id);
            throw _exception.get();
        }
        return _reference.get();
    }
    
    protected void storeSession(final Session session) throws Exception {
        if (session == null) {
            return;
        }
        final Connection connection = this.getConnection();
        Throwable x0 = null;
        try {
            final PreparedStatement statement = connection.prepareStatement(this._jdbcSessionIdMgr._insertSession);
            Throwable x2 = null;
            try {
                final String rowId = this.calculateRowId(session);
                final long now = System.currentTimeMillis();
                connection.setAutoCommit(true);
                statement.setString(1, rowId);
                statement.setString(2, session.getClusterId());
                statement.setString(3, session.getCanonicalContext());
                statement.setString(4, session.getVirtualHost());
                statement.setString(5, this.getSessionIdManager().getWorkerName());
                statement.setLong(6, session.getAccessed());
                statement.setLong(7, session.getLastAccessedTime());
                statement.setLong(8, session.getCreationTime());
                statement.setLong(9, session.getCookieSetTime());
                statement.setLong(10, now);
                statement.setLong(11, session.getExpiryTime());
                statement.setLong(12, session.getMaxInactiveInterval());
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(session.getAttributeMap());
                oos.flush();
                final byte[] bytes = baos.toByteArray();
                final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                statement.setBinaryStream(13, bais, bytes.length);
                statement.executeUpdate();
                session.setRowId(rowId);
                session.setLastSaved(now);
            }
            catch (Throwable t) {
                x2 = t;
                throw t;
            }
            finally {
                if (statement != null) {
                    $closeResource(x2, statement);
                }
            }
        }
        catch (Throwable t2) {
            x0 = t2;
            throw t2;
        }
        finally {
            if (connection != null) {
                $closeResource(x0, connection);
            }
        }
        if (JDBCSessionManager.LOG.isDebugEnabled()) {
            JDBCSessionManager.LOG.debug("Stored session " + session, new Object[0]);
        }
    }
    
    protected void updateSession(final Session data) throws Exception {
        if (data == null) {
            return;
        }
        final Connection connection = this.getConnection();
        Throwable x0 = null;
        try {
            final PreparedStatement statement = connection.prepareStatement(this._jdbcSessionIdMgr._updateSession);
            Throwable x2 = null;
            try {
                final long now = System.currentTimeMillis();
                connection.setAutoCommit(true);
                statement.setString(1, data.getClusterId());
                statement.setString(2, this.getSessionIdManager().getWorkerName());
                statement.setLong(3, data.getAccessed());
                statement.setLong(4, data.getLastAccessedTime());
                statement.setLong(5, now);
                statement.setLong(6, data.getExpiryTime());
                statement.setLong(7, data.getMaxInactiveInterval());
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(data.getAttributeMap());
                oos.flush();
                final byte[] bytes = baos.toByteArray();
                final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                statement.setBinaryStream(8, bais, bytes.length);
                statement.setString(9, data.getRowId());
                statement.executeUpdate();
                data.setLastSaved(now);
            }
            catch (Throwable t) {
                x2 = t;
                throw t;
            }
            finally {
                if (statement != null) {
                    $closeResource(x2, statement);
                }
            }
        }
        catch (Throwable t2) {
            x0 = t2;
            throw t2;
        }
        finally {
            if (connection != null) {
                $closeResource(x0, connection);
            }
        }
        if (JDBCSessionManager.LOG.isDebugEnabled()) {
            JDBCSessionManager.LOG.debug("Updated session " + data, new Object[0]);
        }
    }
    
    protected void updateSessionNode(final Session data) throws Exception {
        final String nodeId = this.getSessionIdManager().getWorkerName();
        final Connection connection = this.getConnection();
        Throwable x0 = null;
        try {
            final PreparedStatement statement = connection.prepareStatement(this._jdbcSessionIdMgr._updateSessionNode);
            Throwable x2 = null;
            try {
                connection.setAutoCommit(true);
                statement.setString(1, nodeId);
                statement.setString(2, data.getRowId());
                statement.executeUpdate();
            }
            catch (Throwable t) {
                x2 = t;
                throw t;
            }
            finally {
                if (statement != null) {
                    $closeResource(x2, statement);
                }
            }
        }
        catch (Throwable t2) {
            x0 = t2;
            throw t2;
        }
        finally {
            if (connection != null) {
                $closeResource(x0, connection);
            }
        }
        if (JDBCSessionManager.LOG.isDebugEnabled()) {
            JDBCSessionManager.LOG.debug("Updated last node for session id=" + data.getId() + ", lastNode = " + nodeId, new Object[0]);
        }
    }
    
    private void updateSessionAccessTime(final Session data) throws Exception {
        final Connection connection = this.getConnection();
        Throwable x0 = null;
        try {
            final PreparedStatement statement = connection.prepareStatement(this._jdbcSessionIdMgr._updateSessionAccessTime);
            Throwable x2 = null;
            try {
                final long now = System.currentTimeMillis();
                connection.setAutoCommit(true);
                statement.setString(1, this.getSessionIdManager().getWorkerName());
                statement.setLong(2, data.getAccessed());
                statement.setLong(3, data.getLastAccessedTime());
                statement.setLong(4, now);
                statement.setLong(5, data.getExpiryTime());
                statement.setLong(6, data.getMaxInactiveInterval());
                statement.setString(7, data.getRowId());
                statement.executeUpdate();
                data.setLastSaved(now);
            }
            catch (Throwable t) {
                x2 = t;
                throw t;
            }
            finally {
                if (statement != null) {
                    $closeResource(x2, statement);
                }
            }
        }
        catch (Throwable t2) {
            x0 = t2;
            throw t2;
        }
        finally {
            if (connection != null) {
                $closeResource(x0, connection);
            }
        }
        if (JDBCSessionManager.LOG.isDebugEnabled()) {
            JDBCSessionManager.LOG.debug("Updated access time session id=" + data.getId() + " with lastsaved=" + data.getLastSaved(), new Object[0]);
        }
    }
    
    protected void deleteSession(final Session data) throws Exception {
        final Connection connection = this.getConnection();
        Throwable x0 = null;
        try {
            final PreparedStatement statement = connection.prepareStatement(this._jdbcSessionIdMgr._deleteSession);
            Throwable x2 = null;
            try {
                connection.setAutoCommit(true);
                statement.setString(1, data.getRowId());
                statement.executeUpdate();
                if (JDBCSessionManager.LOG.isDebugEnabled()) {
                    JDBCSessionManager.LOG.debug("Deleted Session " + data, new Object[0]);
                }
            }
            catch (Throwable t) {
                x2 = t;
                throw t;
            }
            finally {
                if (statement != null) {
                    $closeResource(x2, statement);
                }
            }
        }
        catch (Throwable t2) {
            x0 = t2;
            throw t2;
        }
        finally {
            if (connection != null) {
                $closeResource(x0, connection);
            }
        }
    }
    
    private Connection getConnection() throws SQLException {
        return ((JDBCSessionIdManager)this.getSessionIdManager()).getConnection();
    }
    
    private String calculateRowId(final Session data) {
        String rowId = canonicalize(this._context.getContextPath());
        rowId = rowId + "_" + getVirtualHost(this._context);
        rowId = rowId + "_" + data.getId();
        return rowId;
    }
    
    private static String getVirtualHost(final ContextHandler.Context context) {
        final String vhost = "0.0.0.0";
        if (context == null) {
            return vhost;
        }
        final String[] vhosts = context.getContextHandler().getVirtualHosts();
        if (vhosts == null || vhosts.length == 0 || vhosts[0] == null) {
            return vhost;
        }
        return vhosts[0];
    }
    
    private static String canonicalize(final String path) {
        if (path == null) {
            return "";
        }
        return path.replace('/', '_').replace('.', '_').replace('\\', '_');
    }
    
    private static /* synthetic */ void $closeResource(final Throwable x0, final AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            }
            catch (Throwable exception) {
                x0.addSuppressed(exception);
            }
        }
        else {
            x1.close();
        }
    }
    
    static {
        LOG = Log.getLogger(JDBCSessionManager.class);
    }
    
    public class Session extends MemSession
    {
        private static final long serialVersionUID = 5208464051134226143L;
        protected boolean _dirty;
        protected long _expiryTime;
        protected long _lastSaved;
        protected String _lastNode;
        protected String _virtualHost;
        protected String _rowId;
        protected String _canonicalContext;
        
        protected Session(final HttpServletRequest request) {
            super(JDBCSessionManager.this, request);
            this._dirty = false;
            final int maxInterval = this.getMaxInactiveInterval();
            this._expiryTime = ((maxInterval <= 0) ? 0L : (System.currentTimeMillis() + maxInterval * 1000L));
            this._virtualHost = getVirtualHost(JDBCSessionManager.this._context);
            this._canonicalContext = canonicalize(JDBCSessionManager.this._context.getContextPath());
            this._lastNode = JDBCSessionManager.this.getSessionIdManager().getWorkerName();
        }
        
        protected Session(final String sessionId, final String rowId, final long created, final long accessed, final long maxInterval) {
            super(JDBCSessionManager.this, created, accessed, sessionId);
            this._dirty = false;
            this._rowId = rowId;
            super.setMaxInactiveInterval((int)maxInterval);
            this._expiryTime = ((maxInterval <= 0L) ? 0L : (System.currentTimeMillis() + maxInterval * 1000L));
        }
        
        protected synchronized String getRowId() {
            return this._rowId;
        }
        
        protected synchronized void setRowId(final String rowId) {
            this._rowId = rowId;
        }
        
        public synchronized void setVirtualHost(final String vhost) {
            this._virtualHost = vhost;
        }
        
        public synchronized String getVirtualHost() {
            return this._virtualHost;
        }
        
        public synchronized long getLastSaved() {
            return this._lastSaved;
        }
        
        public synchronized void setLastSaved(final long time) {
            this._lastSaved = time;
        }
        
        public synchronized void setExpiryTime(final long time) {
            this._expiryTime = time;
        }
        
        public synchronized long getExpiryTime() {
            return this._expiryTime;
        }
        
        public synchronized void setCanonicalContext(final String str) {
            this._canonicalContext = str;
        }
        
        public synchronized String getCanonicalContext() {
            return this._canonicalContext;
        }
        
        public synchronized void setLastNode(final String node) {
            this._lastNode = node;
        }
        
        public synchronized String getLastNode() {
            return this._lastNode;
        }
        
        @Override
        public void setAttribute(final String name, final Object value) {
            final Object old = this.changeAttribute(name, value);
            if (value == null && old == null) {
                return;
            }
            this._dirty = true;
        }
        
        @Override
        public void removeAttribute(final String name) {
            final Object old = this.changeAttribute(name, null);
            if (old != null) {
                this._dirty = true;
            }
        }
        
        @Override
        protected boolean access(final long time) {
            synchronized (this) {
                if (super.access(time)) {
                    final int maxInterval = this.getMaxInactiveInterval();
                    this._expiryTime = ((maxInterval <= 0) ? 0L : (time + maxInterval * 1000L));
                    return true;
                }
                return false;
            }
        }
        
        @Override
        public void setMaxInactiveInterval(final int secs) {
            synchronized (this) {
                super.setMaxInactiveInterval(secs);
                final int maxInterval = this.getMaxInactiveInterval();
                this._expiryTime = ((maxInterval <= 0) ? 0L : (System.currentTimeMillis() + maxInterval * 1000L));
                try {
                    JDBCSessionManager.this.updateSessionAccessTime(this);
                }
                catch (Exception e) {
                    Session.LOG.warn("Problem saving changed max idle time for session " + this, e);
                }
            }
        }
        
        @Override
        protected void complete() {
            synchronized (this) {
                super.complete();
                try {
                    if (this.isValid()) {
                        if (this._dirty) {
                            this.save(true);
                        }
                        else if (this.getAccessed() - this._lastSaved >= JDBCSessionManager.this.getSaveInterval() * 1000L) {
                            JDBCSessionManager.this.updateSessionAccessTime(this);
                        }
                    }
                }
                catch (Exception e) {
                    Session.LOG.warn("Problem persisting changed session data id=" + this.getId(), e);
                }
                finally {
                    this._dirty = false;
                }
            }
        }
        
        protected void save() throws Exception {
            synchronized (this) {
                try {
                    JDBCSessionManager.this.updateSession(this);
                }
                finally {
                    this._dirty = false;
                }
            }
        }
        
        protected void save(final boolean reactivate) throws Exception {
            synchronized (this) {
                if (this._dirty) {
                    this.willPassivate();
                    JDBCSessionManager.this.updateSession(this);
                    if (reactivate) {
                        this.didActivate();
                    }
                }
            }
        }
        
        @Override
        protected void timeout() throws IllegalStateException {
            if (Session.LOG.isDebugEnabled()) {
                Session.LOG.debug("Timing out session id=" + this.getClusterId(), new Object[0]);
            }
            super.timeout();
        }
        
        @Override
        public String toString() {
            return "Session rowId=" + this._rowId + ",id=" + this.getId() + ",lastNode=" + this._lastNode + ",created=" + this.getCreationTime() + ",accessed=" + this.getAccessed() + ",lastAccessed=" + this.getLastAccessedTime() + ",cookieSet=" + this.getCookieSetTime() + ",maxInterval=" + this.getMaxInactiveInterval() + ",lastSaved=" + this._lastSaved + ",expiry=" + this._expiryTime;
        }
    }
}
