// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import java.io.ObjectStreamClass;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import org.mortbay.util.LazyList;
import org.mortbay.log.Log;
import java.util.Collections;
import java.util.HashMap;
import java.io.File;
import java.util.Map;
import java.util.TimerTask;
import java.util.Timer;

public class HashSessionManager extends AbstractSessionManager
{
    private Timer _timer;
    private TimerTask _task;
    private int _scavengePeriodMs;
    private int _savePeriodMs;
    private TimerTask _saveTask;
    protected Map _sessions;
    private File _storeDir;
    private boolean _lazyLoad;
    private boolean _sessionsLoaded;
    
    public HashSessionManager() {
        this._scavengePeriodMs = 30000;
        this._savePeriodMs = 0;
        this._lazyLoad = false;
        this._sessionsLoaded = false;
    }
    
    public void doStart() throws Exception {
        this._sessions = new HashMap();
        super.doStart();
        this._timer = new Timer(true);
        this.setScavengePeriod(this.getScavengePeriod());
        if (this._storeDir != null) {
            if (!this._storeDir.exists()) {
                this._storeDir.mkdir();
            }
            if (!this._lazyLoad) {
                this.restoreSessions();
            }
        }
        this.setSavePeriod(this.getSavePeriod());
    }
    
    public void doStop() throws Exception {
        if (this._storeDir != null) {
            this.saveSessions();
        }
        super.doStop();
        this._sessions.clear();
        this._sessions = null;
        synchronized (this) {
            if (this._saveTask != null) {
                this._saveTask.cancel();
            }
            if (this._task != null) {
                this._task.cancel();
            }
            if (this._timer != null) {
                this._timer.cancel();
            }
            this._timer = null;
        }
    }
    
    public int getScavengePeriod() {
        return this._scavengePeriodMs / 1000;
    }
    
    public Map getSessionMap() {
        return Collections.unmodifiableMap((Map<?, ?>)this._sessions);
    }
    
    public int getSessions() {
        return this._sessions.size();
    }
    
    public void setMaxInactiveInterval(final int seconds) {
        super.setMaxInactiveInterval(seconds);
        if (this._dftMaxIdleSecs > 0 && this._scavengePeriodMs > this._dftMaxIdleSecs * 1000) {
            this.setScavengePeriod((this._dftMaxIdleSecs + 9) / 10);
        }
    }
    
    public void setSavePeriod(final int seconds) {
        final int oldSavePeriod = this._savePeriodMs;
        int period = seconds * 1000;
        if (period < 0) {
            period = 0;
        }
        this._savePeriodMs = period;
        if (this._timer != null) {
            synchronized (this) {
                if (this._saveTask != null) {
                    this._saveTask.cancel();
                }
                if (this._savePeriodMs > 0 && this._storeDir != null) {
                    this._saveTask = new TimerTask() {
                        public void run() {
                            try {
                                HashSessionManager.this.saveSessions();
                            }
                            catch (Exception e) {
                                Log.warn(e);
                            }
                        }
                    };
                    this._timer.schedule(this._saveTask, this._savePeriodMs, this._savePeriodMs);
                }
            }
        }
    }
    
    public int getSavePeriod() {
        if (this._savePeriodMs <= 0) {
            return 0;
        }
        return this._savePeriodMs / 1000;
    }
    
    public void setScavengePeriod(int seconds) {
        if (seconds == 0) {
            seconds = 60;
        }
        final int old_period = this._scavengePeriodMs;
        int period = seconds * 1000;
        if (period > 60000) {
            period = 60000;
        }
        if (period < 1000) {
            period = 1000;
        }
        this._scavengePeriodMs = period;
        if (this._timer != null && (period != old_period || this._task == null)) {
            synchronized (this) {
                if (this._task != null) {
                    this._task.cancel();
                }
                this._task = new TimerTask() {
                    public void run() {
                        HashSessionManager.this.scavenge();
                    }
                };
                this._timer.schedule(this._task, this._scavengePeriodMs, this._scavengePeriodMs);
            }
        }
    }
    
    private void scavenge() {
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
            try {
                if (!this._sessionsLoaded && this._lazyLoad) {
                    this.restoreSessions();
                }
            }
            catch (Exception e) {
                Log.debug(e);
            }
            Object stale = null;
            synchronized (this) {
                for (final Session session : this._sessions.values()) {
                    final long idleTime = session._maxIdleMs;
                    if (idleTime > 0L && session._accessed + idleTime < now) {
                        stale = LazyList.add(stale, session);
                    }
                }
            }
            int j = LazyList.size(stale);
            while (j-- > 0) {
                final Session session2 = (Session)LazyList.get(stale, j);
                final long idleTime2 = session2._maxIdleMs;
                if (idleTime2 > 0L && session2._accessed + idleTime2 < System.currentTimeMillis()) {
                    session2.timeout();
                    final int nbsess = this._sessions.size();
                    if (nbsess >= this._minSessions) {
                        continue;
                    }
                    this._minSessions = nbsess;
                }
            }
        }
        catch (Throwable t) {
            if (t instanceof ThreadDeath) {
                throw (ThreadDeath)t;
            }
            Log.warn("Problem scavenging sessions", t);
        }
        finally {
            thread.setContextClassLoader(old_loader);
        }
    }
    
    protected void addSession(final AbstractSessionManager.Session session) {
        this._sessions.put(session.getClusterId(), session);
    }
    
    public AbstractSessionManager.Session getSession(final String idInCluster) {
        try {
            if (!this._sessionsLoaded && this._lazyLoad) {
                this.restoreSessions();
            }
        }
        catch (Exception e) {
            Log.warn(e);
        }
        if (this._sessions == null) {
            return null;
        }
        return this._sessions.get(idInCluster);
    }
    
    protected void invalidateSessions() {
        final ArrayList sessions = new ArrayList(this._sessions.values());
        for (final Session session : sessions) {
            session.invalidate();
        }
        this._sessions.clear();
    }
    
    protected AbstractSessionManager.Session newSession(final HttpServletRequest request) {
        return new Session(request);
    }
    
    protected void removeSession(final String clusterId) {
        this._sessions.remove(clusterId);
    }
    
    public void setStoreDirectory(final File dir) {
        this._storeDir = dir;
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
    
    public void restoreSessions() throws Exception {
        if (this._storeDir == null || !this._storeDir.exists()) {
            return;
        }
        if (!this._storeDir.canRead()) {
            Log.warn("Unable to restore Sessions: Cannot read from Session storage directory " + this._storeDir.getAbsolutePath());
            return;
        }
        final File[] files = this._storeDir.listFiles();
        for (int i = 0; files != null && i < files.length; ++i) {
            try {
                final FileInputStream in = new FileInputStream(files[i]);
                final Session session = this.restoreSession(in);
                in.close();
                this.addSession(session, false);
                files[i].delete();
            }
            catch (Exception e) {
                Log.warn("Problem restoring session " + files[i].getName(), e);
            }
        }
        this._sessionsLoaded = true;
    }
    
    public void saveSessions() throws Exception {
        if (this._storeDir == null || !this._storeDir.exists()) {
            return;
        }
        if (!this._storeDir.canWrite()) {
            Log.warn("Unable to save Sessions: Session persistence storage directory " + this._storeDir.getAbsolutePath() + " is not writeable");
            return;
        }
        synchronized (this) {
            for (final Map.Entry entry : this._sessions.entrySet()) {
                final String id = entry.getKey();
                final Session session = entry.getValue();
                try {
                    final File file = new File(this._storeDir, id);
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    final FileOutputStream fos = new FileOutputStream(file);
                    session.save(fos);
                    fos.close();
                }
                catch (Exception e) {
                    Log.warn("Problem persisting session " + id, e);
                }
            }
        }
    }
    
    public Session restoreSession(final FileInputStream fis) throws Exception {
        final DataInputStream in = new DataInputStream(fis);
        final String clusterId = in.readUTF();
        final String nodeId = in.readUTF();
        final boolean idChanged = in.readBoolean();
        final long created = in.readLong();
        final long cookieSet = in.readLong();
        final long accessed = in.readLong();
        final long lastAccessed = in.readLong();
        final int requests = in.readInt();
        final Session session = new Session(created, clusterId);
        session._cookieSet = cookieSet;
        session._lastAccessed = lastAccessed;
        final int size = in.readInt();
        if (size > 0) {
            final ArrayList keys = new ArrayList();
            for (int i = 0; i < size; ++i) {
                final String key = in.readUTF();
                keys.add(key);
            }
            final ClassLoadingObjectInputStream ois = new ClassLoadingObjectInputStream(in);
            for (int j = 0; j < size; ++j) {
                final Object value = ois.readObject();
                session.setAttribute(keys.get(j), value);
            }
            ois.close();
        }
        else {
            session.initValues();
        }
        in.close();
        return session;
    }
    
    protected class Session extends AbstractSessionManager.Session
    {
        private static final long serialVersionUID = -2134521374206116367L;
        
        protected Session(final HttpServletRequest request) {
            super(request);
        }
        
        protected Session(final long created, final String clusterId) {
            super(created, clusterId);
        }
        
        public void setMaxInactiveInterval(final int secs) {
            super.setMaxInactiveInterval(secs);
            if (this._maxIdleMs > 0L && this._maxIdleMs / 10L < HashSessionManager.this._scavengePeriodMs) {
                HashSessionManager.this.setScavengePeriod((secs + 9) / 10);
            }
        }
        
        protected Map newAttributeMap() {
            return new HashMap(3);
        }
        
        public void invalidate() throws IllegalStateException {
            super.invalidate();
            this.remove(this.getId());
        }
        
        public void remove(final String id) {
            if (id == null) {
                return;
            }
            if (HashSessionManager.this.isStopping() || HashSessionManager.this.isStopped()) {
                return;
            }
            if (HashSessionManager.this._storeDir == null || !HashSessionManager.this._storeDir.exists()) {
                return;
            }
            final File f = new File(HashSessionManager.this._storeDir, id);
            f.delete();
        }
        
        public void save(final FileOutputStream fos) throws IOException {
            final DataOutputStream out = new DataOutputStream(fos);
            out.writeUTF(this._clusterId);
            out.writeUTF(this._nodeId);
            out.writeBoolean(this._idChanged);
            out.writeLong(this._created);
            out.writeLong(this._cookieSet);
            out.writeLong(this._accessed);
            out.writeLong(this._lastAccessed);
            out.writeInt(this._requests);
            if (this._values != null) {
                out.writeInt(this._values.size());
                for (final String key : this._values.keySet()) {
                    out.writeUTF(key);
                }
                final Iterator itor = this._values.values().iterator();
                final ObjectOutputStream oos = new ObjectOutputStream(out);
                while (itor.hasNext()) {
                    oos.writeObject(itor.next());
                }
                oos.close();
            }
            else {
                out.writeInt(0);
            }
            out.close();
        }
    }
    
    protected class ClassLoadingObjectInputStream extends ObjectInputStream
    {
        public ClassLoadingObjectInputStream(final InputStream in) throws IOException {
            super(in);
        }
        
        public ClassLoadingObjectInputStream() throws IOException {
        }
        
        public Class resolveClass(final ObjectStreamClass cl) throws IOException, ClassNotFoundException {
            try {
                return Class.forName(cl.getName(), false, Thread.currentThread().getContextClassLoader());
            }
            catch (ClassNotFoundException e) {
                return super.resolveClass(cl);
            }
        }
    }
}
