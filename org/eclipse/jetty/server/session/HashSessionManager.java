// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.session;

import org.eclipse.jetty.util.ClassLoadingObjectInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContext;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.server.handler.ContextHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.io.File;
import org.eclipse.jetty.util.thread.Scheduler;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.jetty.util.log.Logger;

public class HashSessionManager extends AbstractSessionManager
{
    static final Logger LOG;
    protected final ConcurrentMap<String, HashedSession> _sessions;
    private Scheduler _timer;
    private Scheduler.Task _task;
    long _scavengePeriodMs;
    long _savePeriodMs;
    long _idleSavePeriodMs;
    private Scheduler.Task _saveTask;
    File _storeDir;
    private boolean _lazyLoad;
    private volatile boolean _sessionsLoaded;
    private boolean _deleteUnrestorableSessions;
    
    public HashSessionManager() {
        this._sessions = new ConcurrentHashMap<String, HashedSession>();
        this._scavengePeriodMs = 30000L;
        this._savePeriodMs = 0L;
        this._idleSavePeriodMs = 0L;
        this._lazyLoad = false;
        this._sessionsLoaded = false;
        this._deleteUnrestorableSessions = false;
    }
    
    @Override
    public void doStart() throws Exception {
        this._timer = this.getSessionHandler().getServer().getBean(Scheduler.class);
        if (this._timer == null) {
            final ServletContext context = ContextHandler.getCurrentContext();
            if (context != null) {
                this._timer = (Scheduler)context.getAttribute("org.eclipse.jetty.server.session.timer");
            }
        }
        if (this._timer == null) {
            this.addBean(this._timer = new ScheduledExecutorScheduler(this.toString() + "Timer", true), true);
        }
        else {
            this.addBean(this._timer, false);
        }
        super.doStart();
        this.setScavengePeriod(this.getScavengePeriod());
        if (this._storeDir != null) {
            if (!this._storeDir.exists()) {
                this._storeDir.mkdirs();
            }
            if (!this._lazyLoad) {
                this.restoreSessions();
            }
        }
        this.setSavePeriod(this.getSavePeriod());
    }
    
    @Override
    public void doStop() throws Exception {
        synchronized (this) {
            if (this._saveTask != null) {
                this._saveTask.cancel();
            }
            this._saveTask = null;
            if (this._task != null) {
                this._task.cancel();
            }
            this._task = null;
            if (this.isManaged(this._timer)) {
                this.removeBean(this._timer);
            }
            this._timer = null;
        }
        super.doStop();
        this._sessions.clear();
    }
    
    public int getScavengePeriod() {
        return (int)(this._scavengePeriodMs / 1000L);
    }
    
    @Override
    public int getSessions() {
        final int sessions = super.getSessions();
        if (HashSessionManager.LOG.isDebugEnabled() && this._sessions.size() != sessions) {
            HashSessionManager.LOG.warn("sessions: " + this._sessions.size() + "!=" + sessions, new Object[0]);
        }
        return sessions;
    }
    
    public int getIdleSavePeriod() {
        if (this._idleSavePeriodMs <= 0L) {
            return 0;
        }
        return (int)(this._idleSavePeriodMs / 1000L);
    }
    
    public void setIdleSavePeriod(final int seconds) {
        this._idleSavePeriodMs = seconds * 1000L;
    }
    
    @Override
    public void setMaxInactiveInterval(final int seconds) {
        super.setMaxInactiveInterval(seconds);
        if (this._dftMaxIdleSecs > 0 && this._scavengePeriodMs > this._dftMaxIdleSecs * 1000L) {
            this.setScavengePeriod((this._dftMaxIdleSecs + 9) / 10);
        }
    }
    
    public void setSavePeriod(final int seconds) {
        long period = seconds * 1000L;
        if (period < 0L) {
            period = 0L;
        }
        this._savePeriodMs = period;
        if (this._timer != null) {
            synchronized (this) {
                if (this._saveTask != null) {
                    this._saveTask.cancel();
                }
                this._saveTask = null;
                if (this._savePeriodMs > 0L && this._storeDir != null) {
                    this._saveTask = this._timer.schedule(new Saver(), this._savePeriodMs, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
    
    public int getSavePeriod() {
        if (this._savePeriodMs <= 0L) {
            return 0;
        }
        return (int)(this._savePeriodMs / 1000L);
    }
    
    public void setScavengePeriod(int seconds) {
        if (seconds == 0) {
            seconds = 60;
        }
        final long old_period = this._scavengePeriodMs;
        long period = seconds * 1000L;
        if (period > 60000L) {
            period = 60000L;
        }
        if (period < 1000L) {
            period = 1000L;
        }
        this._scavengePeriodMs = period;
        synchronized (this) {
            if (this._timer != null && (period != old_period || this._task == null)) {
                if (this._task != null) {
                    this._task.cancel();
                    this._task = null;
                }
                this._task = this._timer.schedule(new Scavenger(), this._scavengePeriodMs, TimeUnit.MILLISECONDS);
            }
        }
    }
    
    protected void scavenge() {
        if (this.isStopping() || this.isStopped()) {
            return;
        }
        final Thread thread = Thread.currentThread();
        final ClassLoader old_loader = thread.getContextClassLoader();
        try {
            if (this._loader != null) {
                thread.setContextClassLoader(this._loader);
            }
            final long now = System.currentTimeMillis();
            HashSessionManager.__log.debug("Scavenging sessions at {}", now);
            for (final HashedSession session : this._sessions.values()) {
                final long idleTime = session.getMaxInactiveInterval() * 1000L;
                if (idleTime > 0L && session.getAccessed() + idleTime < now) {
                    try {
                        session.timeout();
                    }
                    catch (Exception e) {
                        HashSessionManager.__log.warn("Problem scavenging sessions", e);
                    }
                }
                else {
                    if (this._idleSavePeriodMs <= 0L || session.getAccessed() + this._idleSavePeriodMs >= now) {
                        continue;
                    }
                    try {
                        session.idle();
                    }
                    catch (Exception e) {
                        HashSessionManager.__log.warn("Problem idling session " + session.getId(), e);
                    }
                }
            }
        }
        finally {
            thread.setContextClassLoader(old_loader);
        }
    }
    
    @Override
    protected void addSession(final AbstractSession session) {
        if (this.isRunning()) {
            this._sessions.put(session.getClusterId(), (HashedSession)session);
        }
    }
    
    @Override
    public AbstractSession getSession(final String idInCluster) {
        if (this._lazyLoad && !this._sessionsLoaded) {
            try {
                this.restoreSessions();
            }
            catch (Exception e) {
                HashSessionManager.LOG.warn(e);
            }
        }
        final Map<String, HashedSession> sessions = this._sessions;
        if (sessions == null) {
            return null;
        }
        HashedSession session = sessions.get(idInCluster);
        if (session == null && this._lazyLoad) {
            session = this.restoreSession(idInCluster);
        }
        if (session == null) {
            return null;
        }
        if (this._idleSavePeriodMs != 0L) {
            session.deIdle();
        }
        return session;
    }
    
    @Override
    protected void shutdownSessions() throws Exception {
        ArrayList<HashedSession> sessions = new ArrayList<HashedSession>(this._sessions.values());
        for (int loop = 100; sessions.size() > 0 && loop-- > 0; sessions = new ArrayList<HashedSession>(this._sessions.values())) {
            if (this.isStopping() && this._storeDir != null && this._storeDir.exists() && this._storeDir.canWrite()) {
                for (final HashedSession session : sessions) {
                    session.save(false);
                    this._sessions.remove(session.getClusterId());
                }
            }
            else {
                for (final HashedSession session : sessions) {
                    session.invalidate();
                }
            }
        }
    }
    
    @Override
    public void renewSessionId(final String oldClusterId, final String oldNodeId, final String newClusterId, final String newNodeId) {
        try {
            final Map<String, HashedSession> sessions = this._sessions;
            if (sessions == null) {
                return;
            }
            final HashedSession session = sessions.remove(oldClusterId);
            if (session == null) {
                return;
            }
            session.remove();
            session.setClusterId(newClusterId);
            session.setNodeId(newNodeId);
            session.save();
            sessions.put(newClusterId, session);
            super.renewSessionId(oldClusterId, oldNodeId, newClusterId, newNodeId);
        }
        catch (Exception e) {
            HashSessionManager.LOG.warn(e);
        }
    }
    
    @Override
    protected AbstractSession newSession(final HttpServletRequest request) {
        return new HashedSession(this, request);
    }
    
    protected AbstractSession newSession(final long created, final long accessed, final String clusterId) {
        return new HashedSession(this, created, accessed, clusterId);
    }
    
    @Override
    protected boolean removeSession(final String clusterId) {
        return this._sessions.remove(clusterId) != null;
    }
    
    public void setStoreDirectory(final File dir) throws IOException {
        this._storeDir = dir.getCanonicalFile();
    }
    
    public File getStoreDirectory() {
        return this._storeDir;
    }
    
    public void setLazyLoad(final boolean lazyLoad) {
        this._lazyLoad = lazyLoad;
    }
    
    public boolean isLazyLoad() {
        return this._lazyLoad;
    }
    
    public boolean isDeleteUnrestorableSessions() {
        return this._deleteUnrestorableSessions;
    }
    
    public void setDeleteUnrestorableSessions(final boolean deleteUnrestorableSessions) {
        this._deleteUnrestorableSessions = deleteUnrestorableSessions;
    }
    
    public void restoreSessions() throws Exception {
        this._sessionsLoaded = true;
        if (this._storeDir == null || !this._storeDir.exists()) {
            return;
        }
        if (!this._storeDir.canRead()) {
            HashSessionManager.LOG.warn("Unable to restore Sessions: Cannot read from Session storage directory " + this._storeDir.getAbsolutePath(), new Object[0]);
            return;
        }
        final String[] files = this._storeDir.list();
        for (int i = 0; files != null && i < files.length; ++i) {
            this.restoreSession(files[i]);
        }
    }
    
    protected synchronized HashedSession restoreSession(final String idInCuster) {
        final File file = new File(this._storeDir, idInCuster);
        Exception error = null;
        if (!file.exists()) {
            if (HashSessionManager.LOG.isDebugEnabled()) {
                HashSessionManager.LOG.debug("Not loading: {}", file);
            }
            return null;
        }
        try {
            final FileInputStream in = new FileInputStream(file);
            Throwable t = null;
            try {
                final HashedSession session = this.restoreSession(in, null);
                this.addSession(session, false);
                session.didActivate();
                return session;
            }
            catch (Throwable t2) {
                t = t2;
                throw t2;
            }
            finally {
                if (t != null) {
                    try {
                        in.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                else {
                    in.close();
                }
            }
        }
        catch (Exception e) {
            error = e;
        }
        finally {
            if (error != null) {
                if (this.isDeleteUnrestorableSessions() && file.exists() && file.getParentFile().equals(this._storeDir)) {
                    file.delete();
                    HashSessionManager.LOG.warn("Deleting file for unrestorable session {} {}", idInCuster, error);
                    HashSessionManager.__log.debug(error);
                }
                else {
                    HashSessionManager.__log.warn("Problem restoring session {} {}", idInCuster, error);
                    HashSessionManager.__log.debug(error);
                }
            }
            else if (this._savePeriodMs == 0L) {
                file.delete();
            }
        }
        return null;
    }
    
    public void saveSessions(final boolean reactivate) throws Exception {
        if (this._storeDir == null || !this._storeDir.exists()) {
            return;
        }
        if (!this._storeDir.canWrite()) {
            HashSessionManager.LOG.warn("Unable to save Sessions: Session persistence storage directory " + this._storeDir.getAbsolutePath() + " is not writeable", new Object[0]);
            return;
        }
        for (final HashedSession session : this._sessions.values()) {
            session.save(reactivate);
        }
    }
    
    public HashedSession restoreSession(final InputStream is, HashedSession session) throws Exception {
        final DataInputStream di = new DataInputStream(is);
        final String clusterId = di.readUTF();
        di.readUTF();
        final long created = di.readLong();
        final long accessed = di.readLong();
        final int requests = di.readInt();
        if (session == null) {
            session = (HashedSession)this.newSession(created, accessed, clusterId);
        }
        session.setRequests(requests);
        final int size = di.readInt();
        this.restoreSessionAttributes(di, size, session);
        try {
            final int maxIdle = di.readInt();
            session.setMaxInactiveInterval(maxIdle);
        }
        catch (IOException e) {
            HashSessionManager.LOG.debug("No maxInactiveInterval persisted for session " + clusterId, new Object[0]);
            HashSessionManager.LOG.ignore(e);
        }
        return session;
    }
    
    private void restoreSessionAttributes(final InputStream is, final int size, final HashedSession session) throws Exception {
        if (size > 0) {
            final ClassLoadingObjectInputStream ois = new ClassLoadingObjectInputStream(is);
            for (int i = 0; i < size; ++i) {
                final String key = ois.readUTF();
                final Object value = ois.readObject();
                session.setAttribute(key, value);
            }
        }
    }
    
    static {
        LOG = SessionHandler.LOG;
    }
    
    protected class Scavenger implements Runnable
    {
        @Override
        public void run() {
            try {
                HashSessionManager.this.scavenge();
            }
            finally {
                if (HashSessionManager.this._timer != null && HashSessionManager.this._timer.isRunning()) {
                    HashSessionManager.this._task = HashSessionManager.this._timer.schedule(this, HashSessionManager.this._scavengePeriodMs, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
    
    protected class Saver implements Runnable
    {
        @Override
        public void run() {
            try {
                HashSessionManager.this.saveSessions(true);
            }
            catch (Exception e) {
                HashSessionManager.LOG.warn(e);
            }
            finally {
                if (HashSessionManager.this._timer != null && HashSessionManager.this._timer.isRunning()) {
                    HashSessionManager.this._saveTask = HashSessionManager.this._timer.schedule(this, HashSessionManager.this._savePeriodMs, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
}
