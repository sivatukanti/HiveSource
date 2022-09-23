// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.util.Hashtable;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.store.access.SpaceInfo;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.raw.PageKey;
import org.apache.derby.iapi.store.raw.RecordHandle;
import java.util.Properties;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.services.locks.Latch;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.services.locks.Lockable;

abstract class BaseContainer implements Lockable
{
    protected ContainerKey identity;
    protected boolean isDropped;
    protected boolean isCommittedDrop;
    protected boolean isReusableRecordId;
    
    BaseContainer() {
        this.isReusableRecordId = false;
    }
    
    protected void fillInIdentity(final ContainerKey identity) {
        this.identity = identity;
    }
    
    public void clearIdentity() {
        this.identity = null;
    }
    
    public Object getIdentity() {
        return this.identity;
    }
    
    public void lockEvent(final Latch latch) {
    }
    
    public boolean requestCompatible(final Object o, final Object o2) {
        return false;
    }
    
    public boolean lockerAlwaysCompatible() {
        return false;
    }
    
    public void unlockEvent(final Latch latch) {
    }
    
    public void compressContainer(final BaseContainerHandle baseContainerHandle) throws StandardException {
        final RawTransaction startNestedTopTransaction = baseContainerHandle.getTransaction().startNestedTopTransaction();
        int mode = baseContainerHandle.getMode();
        if ((mode & 0x2) == 0x0 && (mode & 0x1) == 0x1) {
            mode &= 0xFFFFFFFE;
        }
        final BaseContainerHandle baseContainerHandle2 = (BaseContainerHandle)startNestedTopTransaction.openContainer(this.identity, null, mode);
        if (baseContainerHandle2 == null) {
            throw StandardException.newException("XSDAG.S", new Long(this.getSegmentId()), new Long(this.getContainerId()));
        }
        startNestedTopTransaction.getLockFactory().lockObject(startNestedTopTransaction.getCompatibilitySpace(), startNestedTopTransaction, this, null, -1);
        try {
            this.incrementReusableRecordIdSequenceNumber();
            this.compressContainer(startNestedTopTransaction, baseContainerHandle2);
        }
        finally {
            startNestedTopTransaction.commit();
            startNestedTopTransaction.close();
        }
    }
    
    public abstract long getReusableRecordIdSequenceNumber();
    
    protected abstract void incrementReusableRecordIdSequenceNumber();
    
    public Page addPage(final BaseContainerHandle baseContainerHandle, final boolean b) throws StandardException {
        final RawTransaction startNestedTopTransaction = baseContainerHandle.getTransaction().startNestedTopTransaction();
        int mode = baseContainerHandle.getMode();
        if ((mode & 0x2) == 0x0 && (mode & 0x1) == 0x1) {
            mode &= 0xFFFFFFFE;
        }
        final BaseContainerHandle baseContainerHandle2 = (BaseContainerHandle)startNestedTopTransaction.openContainer(this.identity, null, mode);
        if (baseContainerHandle2 == null) {
            throw StandardException.newException("XSDAG.S", new Long(this.getSegmentId()), new Long(this.getContainerId()));
        }
        startNestedTopTransaction.getLockFactory().lockObject(startNestedTopTransaction.getCompatibilitySpace(), startNestedTopTransaction, this, null, -1);
        BasePage page = null;
        try {
            page = this.newPage(baseContainerHandle, startNestedTopTransaction, baseContainerHandle2, b);
        }
        finally {
            if (page != null) {
                startNestedTopTransaction.commitNoSync(1);
            }
            else {
                startNestedTopTransaction.abort();
            }
            startNestedTopTransaction.close();
        }
        if (!this.identity.equals(page.getPageId().getContainerId())) {
            throw StandardException.newException("XSDAC.S", this.identity, page.getPageId().getContainerId());
        }
        return page;
    }
    
    public abstract void getContainerProperties(final Properties p0) throws StandardException;
    
    protected void removePage(final BaseContainerHandle baseContainerHandle, final BasePage basePage) throws StandardException {
        try {
            if (!this.getDeallocLock(baseContainerHandle, basePage.makeRecordHandle(2), false, false)) {
                throw StandardException.newException("XSDAI.S", basePage.getIdentity());
            }
            this.deallocatePage(baseContainerHandle, basePage);
        }
        finally {
            if (basePage != null) {
                basePage.unlatch();
            }
        }
    }
    
    protected boolean getDeallocLock(final BaseContainerHandle baseContainerHandle, final RecordHandle recordHandle, final boolean b, final boolean b2) throws StandardException {
        final RawTransaction transaction = baseContainerHandle.getTransaction();
        final LockingPolicy lockingPolicy = transaction.newLockingPolicy(1, 4, true);
        final PageKey pageKey = new PageKey(this.identity, recordHandle.getPageNumber());
        if (lockingPolicy == null) {
            throw StandardException.newException("XSDAI.S", pageKey);
        }
        if (b2) {
            return lockingPolicy.zeroDurationLockRecordForWrite(transaction, recordHandle, false, b);
        }
        return lockingPolicy.lockRecordForWrite(transaction, recordHandle, false, b);
    }
    
    protected Page getAllocPage(final BaseContainerHandle baseContainerHandle, final long n, final boolean b) throws StandardException {
        return this.latchPage(baseContainerHandle, this.getAllocPage(n), b);
    }
    
    protected Page getAnyPage(final BaseContainerHandle baseContainerHandle, final long n, final boolean b) throws StandardException {
        return this.latchPage(baseContainerHandle, this.getAnyPage(baseContainerHandle, n), b);
    }
    
    protected Page getFirstPage(final BaseContainerHandle baseContainerHandle) throws StandardException {
        return this.getFirstHeadPage(baseContainerHandle, true);
    }
    
    protected Page getNextPage(final BaseContainerHandle baseContainerHandle, final long n) throws StandardException {
        return this.getNextHeadPage(baseContainerHandle, n, true);
    }
    
    protected BasePage latchPage(final BaseContainerHandle baseContainerHandle, final BasePage basePage, final boolean b) throws StandardException {
        if (basePage != null) {
            if (b) {
                basePage.setExclusive(baseContainerHandle);
            }
            else if (!basePage.setExclusiveNoWait(baseContainerHandle)) {
                return null;
            }
        }
        return basePage;
    }
    
    protected boolean use(final BaseContainerHandle baseContainerHandle, final boolean b, final boolean b2) throws StandardException {
        if (b && !this.canUpdate()) {
            throw StandardException.newException("40XD1");
        }
        return b2 || (!this.getDroppedState() && !this.getCommittedDropState());
    }
    
    protected void letGo(final BaseContainerHandle baseContainerHandle) {
        baseContainerHandle.getLockingPolicy().unlockContainer(baseContainerHandle.getTransaction(), baseContainerHandle);
    }
    
    protected boolean getDroppedState() {
        return this.isDropped;
    }
    
    protected boolean getCommittedDropState() {
        return this.isCommittedDrop;
    }
    
    protected boolean isReusableRecordId() {
        return this.isReusableRecordId;
    }
    
    public int getContainerStatus() {
        if (this.getCommittedDropState()) {
            return 4;
        }
        if (this.getDroppedState()) {
            return 2;
        }
        return 1;
    }
    
    public long getContainerId() {
        return this.identity.getContainerId();
    }
    
    public long getSegmentId() {
        return this.identity.getSegmentId();
    }
    
    protected abstract SpaceInfo getSpaceInfo(final BaseContainerHandle p0) throws StandardException;
    
    protected abstract boolean canUpdate();
    
    protected abstract void preDirty(final boolean p0);
    
    protected abstract BasePage getPage(final BaseContainerHandle p0, final long p1, final boolean p2) throws StandardException;
    
    protected abstract BasePage getAllocPage(final long p0) throws StandardException;
    
    protected abstract BasePage getAnyPage(final BaseContainerHandle p0, final long p1) throws StandardException;
    
    protected abstract BasePage reCreatePageForRedoRecovery(final BaseContainerHandle p0, final int p1, final long p2, final long p3) throws StandardException;
    
    protected abstract ByteArray logCreateContainerInfo() throws StandardException;
    
    protected abstract BasePage getHeadPage(final BaseContainerHandle p0, final long p1, final boolean p2) throws StandardException;
    
    protected abstract BasePage getFirstHeadPage(final BaseContainerHandle p0, final boolean p1) throws StandardException;
    
    protected abstract BasePage getNextHeadPage(final BaseContainerHandle p0, final long p1, final boolean p2) throws StandardException;
    
    protected abstract BasePage getPageForInsert(final BaseContainerHandle p0, final int p1) throws StandardException;
    
    protected abstract BasePage getPageForCompress(final BaseContainerHandle p0, final int p1, final long p2) throws StandardException;
    
    protected abstract void truncatePages(final long p0) throws StandardException;
    
    protected abstract BasePage newPage(final BaseContainerHandle p0, final RawTransaction p1, final BaseContainerHandle p2, final boolean p3) throws StandardException;
    
    protected abstract void compressContainer(final RawTransaction p0, final BaseContainerHandle p1) throws StandardException;
    
    protected abstract void deallocatePage(final BaseContainerHandle p0, final BasePage p1) throws StandardException;
    
    protected void truncate(final BaseContainerHandle baseContainerHandle) throws StandardException {
    }
    
    protected abstract void dropContainer(final LogInstant p0, final boolean p1);
    
    protected abstract void removeContainer(final LogInstant p0, final boolean p1) throws StandardException;
    
    protected abstract long getContainerVersion() throws StandardException;
    
    protected abstract void flushAll() throws StandardException;
    
    protected abstract void prepareForBulkLoad(final BaseContainerHandle p0, final int p1);
    
    protected abstract void clearPreallocThreshold();
    
    public abstract long getEstimatedRowCount(final int p0) throws StandardException;
    
    public abstract void setEstimatedRowCount(final long p0, final int p1) throws StandardException;
    
    public abstract long getEstimatedPageCount(final BaseContainerHandle p0, final int p1) throws StandardException;
    
    protected abstract void backupContainer(final BaseContainerHandle p0, final String p1) throws StandardException;
    
    protected abstract void encryptOrDecryptContainer(final BaseContainerHandle p0, final String p1, final boolean p2) throws StandardException;
    
    protected void setDroppedState(final boolean isDropped) {
        this.isDropped = isDropped;
    }
    
    protected void setCommittedDropState(final boolean isCommittedDrop) {
        this.isCommittedDrop = isCommittedDrop;
    }
    
    protected void setReusableRecordIdState(final boolean isReusableRecordId) {
        this.isReusableRecordId = isReusableRecordId;
    }
    
    public boolean lockAttributes(final int n, final Hashtable hashtable) {
        return false;
    }
}
