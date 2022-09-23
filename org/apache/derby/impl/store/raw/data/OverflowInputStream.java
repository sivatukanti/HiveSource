// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.io.InputStream;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.error.StandardException;
import java.io.IOException;
import java.io.EOFException;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.services.io.CloneableStream;
import org.apache.derby.iapi.types.Resetable;

public class OverflowInputStream extends BufferedByteHolderInputStream implements Resetable, CloneableStream
{
    protected BaseContainerHandle owner;
    protected long overflowPage;
    protected int overflowId;
    protected long firstOverflowPage;
    protected int firstOverflowId;
    protected RecordHandle recordToLock;
    private boolean initialized;
    
    public OverflowInputStream(final ByteHolder byteHolder, final BaseContainerHandle owner, final long n, final int n2, final RecordHandle recordToLock) {
        super(byteHolder);
        this.initialized = false;
        this.owner = owner;
        this.overflowPage = n;
        this.overflowId = n2;
        this.firstOverflowPage = n;
        this.firstOverflowId = n2;
        this.recordToLock = recordToLock;
    }
    
    public void fillByteHolder() throws IOException {
        if (this.bh.available() == 0 && this.overflowPage != -1L) {
            this.bh.clear();
            try {
                final BasePage basePage = (BasePage)this.owner.getPage(this.overflowPage);
                if (basePage == null) {
                    throw new EOFException(MessageService.getTextMessage("D015"));
                }
                basePage.restorePortionLongColumn(this);
                basePage.unlatch();
            }
            catch (StandardException cause) {
                final IOException ex = new IOException(cause.toString());
                ex.initCause(cause);
                throw ex;
            }
            this.bh.startReading();
        }
    }
    
    public void setOverflowPage(final long overflowPage) {
        this.overflowPage = overflowPage;
    }
    
    public void setOverflowId(final int overflowId) {
        this.overflowId = overflowId;
    }
    
    public long getOverflowPage() {
        return this.overflowPage;
    }
    
    public int getOverflowId() {
        return this.overflowId;
    }
    
    public void initStream() throws StandardException {
        if (this.initialized) {
            return;
        }
        if (this.owner.getTransaction() == null) {
            throw StandardException.newException("40XD0");
        }
        this.owner = (BaseContainerHandle)this.owner.getTransaction().openContainer(this.owner.getId(), this.owner.getTransaction().newLockingPolicy(1, 2, true), this.owner.getMode());
        this.owner.getLockingPolicy().lockRecordForRead(this.owner.getTransaction(), this.owner, this.recordToLock, true, false);
        this.initialized = true;
    }
    
    public void resetStream() throws IOException, StandardException {
        this.owner.checkOpen();
        this.overflowPage = this.firstOverflowPage;
        this.overflowId = this.firstOverflowId;
        this.bh.clear();
        this.bh.startReading();
    }
    
    public void closeStream() {
        this.owner.close();
        this.initialized = false;
    }
    
    public InputStream cloneStream() {
        return new OverflowInputStream(this.bh.cloneEmpty(), this.owner, this.firstOverflowPage, this.firstOverflowId, this.recordToLock);
    }
}
