// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.connection;

import org.datanucleus.util.StringUtils;
import javax.transaction.xa.XAResource;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractManagedConnection implements ManagedConnection
{
    protected Object conn;
    protected boolean closeOnRelease;
    protected boolean commitOnRelease;
    protected boolean locked;
    protected List<ManagedConnectionResourceListener> listeners;
    protected int useCount;
    
    public AbstractManagedConnection() {
        this.closeOnRelease = true;
        this.commitOnRelease = true;
        this.locked = false;
        this.listeners = new ArrayList<ManagedConnectionResourceListener>();
        this.useCount = 0;
    }
    
    protected void incrementUseCount() {
        ++this.useCount;
    }
    
    @Override
    public void release() {
        if (this.closeOnRelease) {
            --this.useCount;
            if (this.useCount == 0) {
                this.close();
            }
        }
    }
    
    @Override
    public void transactionFlushed() {
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).transactionFlushed();
        }
    }
    
    @Override
    public void transactionPreClose() {
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).transactionPreClose();
        }
    }
    
    @Override
    public void setCloseOnRelease(final boolean close) {
        this.closeOnRelease = close;
    }
    
    @Override
    public void setCommitOnRelease(final boolean commit) {
        this.commitOnRelease = commit;
    }
    
    @Override
    public boolean closeOnRelease() {
        return this.closeOnRelease;
    }
    
    @Override
    public boolean commitOnRelease() {
        return this.commitOnRelease;
    }
    
    @Override
    public void addListener(final ManagedConnectionResourceListener listener) {
        this.listeners.add(listener);
    }
    
    @Override
    public void removeListener(final ManagedConnectionResourceListener listener) {
        this.listeners.remove(listener);
    }
    
    @Override
    public boolean isLocked() {
        return this.locked;
    }
    
    @Override
    public synchronized void lock() {
        this.locked = true;
    }
    
    @Override
    public synchronized void unlock() {
        this.locked = false;
    }
    
    @Override
    public XAResource getXAResource() {
        return null;
    }
    
    @Override
    public boolean closeAfterTransactionEnd() {
        return true;
    }
    
    @Override
    public String toString() {
        return StringUtils.toJVMIDString(this) + " [conn=" + StringUtils.toJVMIDString(this.conn) + ", commitOnRelease=" + this.commitOnRelease + ", closeOnRelease=" + this.closeOnRelease + ", closeOnTxnEnd=" + this.closeAfterTransactionEnd() + "]";
    }
}
