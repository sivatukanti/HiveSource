// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.access.SpaceInfo;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.PageKey;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.Transaction;
import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.ContainerKey;
import java.util.Observer;
import org.apache.derby.iapi.store.raw.data.RawContainerHandle;
import java.util.Observable;

public class BaseContainerHandle extends Observable implements RawContainerHandle, Observer
{
    private ContainerKey identity;
    private boolean active;
    protected BaseContainer container;
    private LockingPolicy locking;
    private RawTransaction xact;
    private boolean forUpdate;
    private int mode;
    private PageActions actionsSet;
    private AllocationActions allocActionsSet;
    
    public BaseContainerHandle(final UUID uuid, final RawTransaction xact, final ContainerKey identity, final LockingPolicy locking, final int mode) {
        this.identity = identity;
        this.xact = xact;
        this.locking = locking;
        this.mode = mode;
        this.forUpdate = ((mode & 0x4) == 0x4);
    }
    
    public BaseContainerHandle(final UUID uuid, final RawTransaction rawTransaction, final PageActions actionsSet, final AllocationActions allocActionsSet, final LockingPolicy lockingPolicy, final BaseContainer container, final int n) {
        this(uuid, rawTransaction, (ContainerKey)container.getIdentity(), lockingPolicy, n);
        this.actionsSet = actionsSet;
        this.allocActionsSet = allocActionsSet;
        this.container = container;
    }
    
    public Page addPage() throws StandardException {
        this.checkUpdateOpen();
        return this.container.addPage(this, false);
    }
    
    public void compressContainer() throws StandardException {
        this.checkUpdateOpen();
        this.container.compressContainer(this);
    }
    
    public long getReusableRecordIdSequenceNumber() throws StandardException {
        this.checkOpen();
        return this.container.getReusableRecordIdSequenceNumber();
    }
    
    public Page addPage(final int n) throws StandardException {
        if ((n & 0x2) != 0x0 && this.active && this.forUpdate) {
            this.container.clearPreallocThreshold();
        }
        return this.addPage();
    }
    
    public void preAllocate(final int n) {
        if (n > 0 && this.active && this.forUpdate) {
            this.container.prepareForBulkLoad(this, n);
        }
    }
    
    public void getContainerProperties(final Properties properties) throws StandardException {
        this.checkOpen();
        this.container.getContainerProperties(properties);
    }
    
    public void removePage(final Page page) throws StandardException {
        if (!this.active) {
            if (page != null) {
                page.unlatch();
            }
            throw StandardException.newException("40XD0");
        }
        if (!this.forUpdate) {
            if (page != null) {
                page.unlatch();
            }
            throw StandardException.newException("40XD1");
        }
        this.container.removePage(this, (BasePage)page);
    }
    
    public Page getPage(final long n) throws StandardException {
        this.checkOpen();
        return this.container.getPage(this, n, true);
    }
    
    public Page getAllocPage(final long n) throws StandardException {
        this.checkOpen();
        return this.container.getAllocPage(this, n, true);
    }
    
    public Page getUserPageNoWait(final long n) throws StandardException {
        this.checkOpen();
        return this.container.getHeadPage(this, n, false);
    }
    
    public Page getUserPageWait(final long n) throws StandardException {
        this.checkOpen();
        return this.container.getHeadPage(this, n, true);
    }
    
    public Page getPageNoWait(final long n) throws StandardException {
        this.checkOpen();
        return this.container.getPage(this, n, false);
    }
    
    public Page getFirstPage() throws StandardException {
        this.checkOpen();
        return this.container.getFirstPage(this);
    }
    
    public Page getNextPage(final long n) throws StandardException {
        this.checkOpen();
        return this.container.getNextPage(this, n);
    }
    
    public Page getPageForInsert(final int n) throws StandardException {
        this.checkUpdateOpen();
        return this.container.getPageForInsert(this, n);
    }
    
    public Page getPageForCompress(final int n, final long n2) throws StandardException {
        this.checkUpdateOpen();
        return this.container.getPageForCompress(this, n, n2);
    }
    
    public final boolean isReadOnly() {
        return !this.forUpdate;
    }
    
    public synchronized void close() {
        if (this.xact == null) {
            return;
        }
        this.informObservers();
        this.active = false;
        this.getLockingPolicy().unlockContainer(this.xact, this);
        if (this.container != null) {
            this.container.letGo(this);
            this.container = null;
        }
        this.xact.deleteObserver(this);
        this.xact = null;
    }
    
    public long getEstimatedRowCount(final int n) throws StandardException {
        this.checkOpen();
        return this.container.getEstimatedRowCount(n);
    }
    
    public void setEstimatedRowCount(final long n, final int n2) throws StandardException {
        this.checkOpen();
        this.container.setEstimatedRowCount(n, n2);
    }
    
    public long getEstimatedPageCount(final int n) throws StandardException {
        this.checkOpen();
        return this.container.getEstimatedPageCount(this, n);
    }
    
    public void flushContainer() throws StandardException {
        this.checkUpdateOpen();
        this.container.flushAll();
    }
    
    public void compactRecord(final RecordHandle recordHandle) throws StandardException {
        if (!this.forUpdate) {
            throw StandardException.newException("40XD1");
        }
        final BasePage basePage = (BasePage)this.getPage(((PageKey)recordHandle.getPageId()).getPageNumber());
        if (basePage != null) {
            try {
                basePage.compactRecord(recordHandle);
            }
            finally {
                basePage.unlatch();
            }
        }
    }
    
    public int getContainerStatus() throws StandardException {
        this.checkOpen();
        return this.container.getContainerStatus();
    }
    
    public void removeContainer(final LogInstant logInstant) throws StandardException {
        this.checkUpdateOpen();
        this.container.removeContainer(logInstant, true);
    }
    
    public ContainerKey getId() {
        return this.identity;
    }
    
    public Object getUniqueId() {
        return this;
    }
    
    public void dropContainer(final LogInstant logInstant, final boolean b) throws StandardException {
        this.checkUpdateOpen();
        this.container.dropContainer(logInstant, b);
    }
    
    public long getContainerVersion() throws StandardException {
        this.checkOpen();
        return this.container.getContainerVersion();
    }
    
    public Page getAnyPage(final long n) throws StandardException {
        this.checkOpen();
        return this.container.getAnyPage(this, n, true);
    }
    
    public Page reCreatePageForRedoRecovery(final int n, final long n2, final long n3) throws StandardException {
        this.checkUpdateOpen();
        return this.container.reCreatePageForRedoRecovery(this, n, n2, n3);
    }
    
    public ByteArray logCreateContainerInfo() throws StandardException {
        this.checkUpdateOpen();
        return this.container.logCreateContainerInfo();
    }
    
    public RecordHandle makeRecordHandle(final long n, final int n2) throws StandardException {
        return new RecordId(this.identity, n, n2);
    }
    
    public void update(final Observable observable, final Object o) {
        if (this.xact == null) {
            return;
        }
        if (o.equals(RawTransaction.COMMIT) || o.equals(RawTransaction.ABORT) || o.equals(this.identity)) {
            this.close();
            return;
        }
        if (o.equals(RawTransaction.SAVEPOINT_ROLLBACK)) {
            this.informObservers();
            return;
        }
        if (o.equals(RawTransaction.LOCK_ESCALATE)) {
            if (this.getLockingPolicy().getMode() != 1) {
                return;
            }
            try {
                this.getLockingPolicy().lockContainer(this.getTransaction(), this, false, this.forUpdate);
            }
            catch (StandardException observerException) {
                this.xact.setObserverException(observerException);
            }
        }
    }
    
    public PageActions getActionSet() {
        return this.actionsSet;
    }
    
    public AllocationActions getAllocationActionSet() {
        return this.allocActionsSet;
    }
    
    public boolean useContainer(final boolean b, final boolean b2) throws StandardException {
        if (!this.getLockingPolicy().lockContainer(this.getTransaction(), this, b2, this.forUpdate)) {
            this.container = null;
            throw StandardException.newException("40XL1");
        }
        if ((this.mode & 0x40) == 0x0) {
            if (!this.container.use(this, this.forUpdate, b)) {
                this.getLockingPolicy().unlockContainer(this.xact, this);
                this.container = null;
                return false;
            }
            this.active = true;
        }
        else if (this.getLockingPolicy().getMode() != 1) {
            return true;
        }
        this.xact.addObserver(this);
        if ((this.mode & 0x408) == 0x0) {
            if ((this.mode & 0x10) == 0x10) {
                this.xact.addObserver(new TruncateOnCommit(this.identity, true));
            }
            else if ((this.mode & 0x100) == 0x100) {
                this.xact.addObserver(new TruncateOnCommit(this.identity, false));
            }
            if ((this.mode & 0x20) == 0x20) {
                this.xact.addObserver(new DropOnCommit(this.identity));
            }
            if ((this.mode & 0x200) == 0x200) {
                this.xact.addObserver(new SyncOnCommit(this.identity));
            }
        }
        return true;
    }
    
    public final RawTransaction getTransaction() {
        return this.xact;
    }
    
    public final LockingPolicy getLockingPolicy() {
        return this.locking;
    }
    
    public final void setLockingPolicy(final LockingPolicy locking) {
        this.locking = locking;
    }
    
    public final boolean updateOK() {
        return this.forUpdate;
    }
    
    public int getMode() {
        return this.mode;
    }
    
    public void preDirty(final boolean b) throws StandardException {
        this.checkUpdateOpen();
        this.container.preDirty(b);
    }
    
    public boolean isTemporaryContainer() throws StandardException {
        this.checkOpen();
        return this.identity != null && this.identity.getSegmentId() == -1L;
    }
    
    protected void checkOpen() throws StandardException {
        if (!this.active) {
            throw StandardException.newException("40XD0");
        }
    }
    
    private void checkUpdateOpen() throws StandardException {
        if (!this.active) {
            throw StandardException.newException("40XD0");
        }
        if (!this.forUpdate) {
            throw StandardException.newException("40XD1");
        }
    }
    
    protected void informObservers() {
        if (this.countObservers() != 0) {
            this.setChanged();
            this.notifyObservers();
        }
    }
    
    public SpaceInfo getSpaceInfo() throws StandardException {
        return this.container.getSpaceInfo(this);
    }
    
    public void backupContainer(final String s) throws StandardException {
        this.checkOpen();
        this.container.backupContainer(this, s);
    }
    
    public void encryptOrDecryptContainer(final String s, final boolean b) throws StandardException {
        this.checkOpen();
        this.container.encryptOrDecryptContainer(this, s, b);
    }
    
    public String toString() {
        return super.toString();
    }
}
