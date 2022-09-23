// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.session;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.IO;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.io.ObjectOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.util.log.Logger;

public class HashedSession extends MemSession
{
    private static final Logger LOG;
    private final HashSessionManager _hashSessionManager;
    private transient boolean _idled;
    private transient boolean _saveFailed;
    private transient long _lastSaved;
    private transient boolean _deIdleFailed;
    
    protected HashedSession(final HashSessionManager hashSessionManager, final HttpServletRequest request) {
        super(hashSessionManager, request);
        this._idled = false;
        this._saveFailed = false;
        this._lastSaved = 0L;
        this._deIdleFailed = false;
        this._hashSessionManager = hashSessionManager;
    }
    
    protected HashedSession(final HashSessionManager hashSessionManager, final long created, final long accessed, final String clusterId) {
        super(hashSessionManager, created, accessed, clusterId);
        this._idled = false;
        this._saveFailed = false;
        this._lastSaved = 0L;
        this._deIdleFailed = false;
        this._hashSessionManager = hashSessionManager;
    }
    
    @Override
    protected void checkValid() {
        if (!this._deIdleFailed && this._hashSessionManager._idleSavePeriodMs != 0L) {
            this.deIdle();
        }
        super.checkValid();
    }
    
    @Override
    public void setMaxInactiveInterval(final int secs) {
        super.setMaxInactiveInterval(secs);
        if (this.getMaxInactiveInterval() > 0 && this.getMaxInactiveInterval() * 1000L / 10L < this._hashSessionManager._scavengePeriodMs) {
            this._hashSessionManager.setScavengePeriod((secs + 9) / 10);
        }
    }
    
    @Override
    protected void doInvalidate() throws IllegalStateException {
        super.doInvalidate();
        this.remove();
    }
    
    synchronized void remove() {
        if (this._hashSessionManager._storeDir != null && this.getId() != null) {
            final String id = this.getId();
            final File f = new File(this._hashSessionManager._storeDir, id);
            f.delete();
        }
    }
    
    synchronized void save(final boolean reactivate) throws Exception {
        if (!this.isIdled() && !this._saveFailed) {
            if (HashedSession.LOG.isDebugEnabled()) {
                HashedSession.LOG.debug("Saving {} {}", super.getId(), reactivate);
            }
            try {
                this.willPassivate();
                this.save();
                if (reactivate) {
                    this.didActivate();
                }
                else {
                    this.clearAttributes();
                }
            }
            catch (Exception e) {
                HashedSession.LOG.warn("Problem saving session " + super.getId(), e);
                this._idled = false;
            }
        }
    }
    
    synchronized void save() throws Exception {
        File file = null;
        if (!this._saveFailed && this._hashSessionManager._storeDir != null && this._lastSaved < this.getAccessed()) {
            file = new File(this._hashSessionManager._storeDir, super.getId());
            if (file.exists()) {
                file.delete();
            }
            try {
                final FileOutputStream fos = new FileOutputStream(file, false);
                Throwable t = null;
                try {
                    this._lastSaved = System.currentTimeMillis();
                    this.save(fos);
                }
                catch (Throwable t2) {
                    t = t2;
                    throw t2;
                }
                finally {
                    if (t != null) {
                        try {
                            fos.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    else {
                        fos.close();
                    }
                }
            }
            catch (Exception e) {
                this.saveFailed();
                if (file != null) {
                    file.delete();
                }
                throw e;
            }
        }
    }
    
    public synchronized void save(final OutputStream os) throws IOException {
        final DataOutputStream out = new DataOutputStream(os);
        out.writeUTF(this.getClusterId());
        out.writeUTF(this.getNodeId());
        out.writeLong(this.getCreationTime());
        out.writeLong(this.getAccessed());
        out.writeInt(this.getRequests());
        out.writeInt(this.getAttributes());
        final ObjectOutputStream oos = new ObjectOutputStream(out);
        final Enumeration<String> e = this.getAttributeNames();
        while (e.hasMoreElements()) {
            final String key = e.nextElement();
            oos.writeUTF(key);
            oos.writeObject(this.doGet(key));
        }
        out.writeInt(this.getMaxInactiveInterval());
    }
    
    public synchronized void deIdle() {
        if (this.isIdled() && !this._deIdleFailed) {
            this.access(System.currentTimeMillis());
            if (HashedSession.LOG.isDebugEnabled()) {
                HashedSession.LOG.debug("De-idling " + super.getId(), new Object[0]);
            }
            FileInputStream fis = null;
            try {
                final File file = new File(this._hashSessionManager._storeDir, super.getId());
                if (!file.exists() || !file.canRead()) {
                    throw new FileNotFoundException(file.getName());
                }
                fis = new FileInputStream(file);
                this._idled = false;
                this._hashSessionManager.restoreSession(fis, this);
                IO.close(fis);
                this.didActivate();
                if (this._hashSessionManager._savePeriodMs == 0L) {
                    file.delete();
                }
            }
            catch (Exception e) {
                this.deIdleFailed();
                HashedSession.LOG.warn("Problem de-idling session " + super.getId(), e);
                if (fis != null) {
                    IO.close(fis);
                }
                this.invalidate();
            }
        }
    }
    
    public synchronized void idle() throws Exception {
        this.save(false);
        this._idled = true;
    }
    
    public synchronized boolean isIdled() {
        return this._idled;
    }
    
    public synchronized boolean isSaveFailed() {
        return this._saveFailed;
    }
    
    public synchronized void saveFailed() {
        this._saveFailed = true;
    }
    
    public synchronized void deIdleFailed() {
        this._deIdleFailed = true;
    }
    
    public synchronized boolean isDeIdleFailed() {
        return this._deIdleFailed;
    }
    
    static {
        LOG = Log.getLogger(HashedSession.class);
    }
}
