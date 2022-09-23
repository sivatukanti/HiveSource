// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.Transaction;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectOutput;
import org.apache.derby.iapi.store.raw.data.RawContainerHandle;
import org.apache.derby.iapi.store.raw.PageKey;
import org.apache.derby.iapi.store.raw.RePreparable;
import org.apache.derby.iapi.store.raw.Loggable;

public abstract class PageBasicOperation implements Loggable, RePreparable
{
    private PageKey pageId;
    private long pageVersion;
    protected transient BasePage page;
    protected transient RawContainerHandle containerHdl;
    protected transient boolean foundHere;
    
    protected PageBasicOperation(final BasePage page) {
        this.page = page;
        this.pageId = page.getPageId();
        this.pageVersion = page.getPageVersion();
    }
    
    public PageBasicOperation() {
    }
    
    public String toString() {
        return null;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        this.pageId.writeExternal(objectOutput);
        CompressedNumber.writeLong(objectOutput, this.pageVersion);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.pageId = PageKey.read(objectInput);
        this.pageVersion = CompressedNumber.readLong(objectInput);
    }
    
    public final boolean needsRedo(final Transaction transaction) throws StandardException {
        if (this.findpage(transaction) == null) {
            return false;
        }
        final long pageVersion = this.page.getPageVersion();
        if (pageVersion == this.pageVersion) {
            return true;
        }
        this.releaseResource(transaction);
        if (pageVersion > this.pageVersion) {
            return false;
        }
        throw StandardException.newException("XSDB4.D", this.pageId, new Long(pageVersion), new Long(this.pageVersion));
    }
    
    public void releaseResource(final Transaction transaction) {
        if (!this.foundHere) {
            return;
        }
        if (this.page != null) {
            this.page.unlatch();
            this.page = null;
        }
        if (this.containerHdl != null) {
            this.containerHdl.close();
            this.containerHdl = null;
        }
        this.foundHere = false;
    }
    
    public int group() {
        return 384;
    }
    
    public ByteArray getPreparedLog() throws StandardException {
        return null;
    }
    
    public void reclaimPrepareLocks(final Transaction transaction, final LockingPolicy lockingPolicy) throws StandardException {
    }
    
    protected final void resetPageNumber(final long n) {
        this.pageId = new PageKey(this.pageId.getContainerId(), n);
    }
    
    protected final PageKey getPageId() {
        return this.pageId;
    }
    
    public final BasePage findpage(final Transaction transaction) throws StandardException {
        this.releaseResource(transaction);
        final RawTransaction rawTransaction = (RawTransaction)transaction;
        this.containerHdl = rawTransaction.openDroppedContainer(this.pageId.getContainerId(), null);
        if (this.containerHdl == null) {
            throw StandardException.newException("40XD2", this.pageId.getContainerId());
        }
        this.foundHere = true;
        if (this.containerHdl.getContainerStatus() == 4) {
            this.releaseResource(transaction);
            return null;
        }
        StandardException ex = null;
        try {
            this.page = (BasePage)this.containerHdl.getAnyPage(this.pageId.getPageNumber());
        }
        catch (StandardException ex2) {
            ex = ex2;
        }
        if (this.page == null && ex != null && this.pageVersion == 0L && PropertyUtil.getSystemBoolean("derby.storage.patchInitPageRecoverError")) {
            this.page = this.getPageForRedoRecovery(transaction);
        }
        if (this.page == null && ex != null && rawTransaction.inRollForwardRecovery()) {
            this.page = this.getPageForRedoRecovery(transaction);
        }
        if (this.page != null) {
            return this.page;
        }
        if (ex != null) {
            throw ex;
        }
        throw StandardException.newException("XSDB5.D", this.pageId);
    }
    
    protected BasePage getPageForRedoRecovery(final Transaction transaction) throws StandardException {
        return null;
    }
    
    public final Page getPage() {
        return this.page;
    }
    
    public final long getPageVersion() {
        return this.pageVersion;
    }
    
    public abstract void restoreMe(final Transaction p0, final BasePage p1, final LogInstant p2, final LimitObjectInput p3) throws StandardException, IOException;
}
