// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.log.LogInstant;
import java.io.IOException;
import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.FormatIdUtil;
import org.apache.derby.iapi.store.raw.PageKey;
import org.apache.derby.iapi.services.cache.CacheManager;
import org.apache.derby.iapi.services.cache.Cacheable;

public abstract class CachedPage extends BasePage implements Cacheable
{
    protected boolean alreadyReadPage;
    protected byte[] pageData;
    protected boolean isDirty;
    protected boolean preDirty;
    protected int initialRowCount;
    private long containerRowCount;
    protected CacheManager pageCache;
    protected CacheManager containerCache;
    protected BaseDataFileFactory dataFactory;
    protected static final int PAGE_FORMAT_ID_SIZE = 4;
    public static final int WRITE_SYNC = 1;
    public static final int WRITE_NO_SYNC = 2;
    
    public final void setFactory(final BaseDataFileFactory dataFactory) {
        this.dataFactory = dataFactory;
        this.pageCache = dataFactory.getPageCache();
        this.containerCache = dataFactory.getContainerCache();
    }
    
    protected void initialize() {
        super.initialize();
        this.isDirty = false;
        this.preDirty = false;
        this.initialRowCount = 0;
        this.containerRowCount = 0L;
    }
    
    public Cacheable setIdentity(final Object identity) throws StandardException {
        this.initialize();
        final PageKey pageKey = (PageKey)identity;
        final FileContainer fileContainer = (FileContainer)this.containerCache.find(pageKey.getContainerId());
        this.setContainerRowCount(fileContainer.getEstimatedRowCount(0));
        try {
            if (!this.alreadyReadPage) {
                this.readPage(fileContainer, pageKey);
            }
            else {
                this.alreadyReadPage = false;
            }
            final int typeFormatId = this.getTypeFormatId();
            final int formatIdInteger = FormatIdUtil.readFormatIdInteger(this.pageData);
            if (typeFormatId != formatIdInteger) {
                return this.changeInstanceTo(formatIdInteger, pageKey).setIdentity(identity);
            }
            this.initFromData(fileContainer, pageKey);
        }
        finally {
            this.containerCache.release(fileContainer);
        }
        this.fillInIdentity(pageKey);
        this.initialRowCount = 0;
        return this;
    }
    
    public Cacheable createIdentity(final Object o, final Object o2) throws StandardException {
        this.initialize();
        final PageKey pageKey = (PageKey)o;
        final PageCreationArgs pageCreationArgs = (PageCreationArgs)o2;
        final int formatId = pageCreationArgs.formatId;
        if (formatId == -1) {
            throw StandardException.newException("XSDBB.D", pageKey, StringUtil.hexDump(this.pageData));
        }
        if (formatId != this.getTypeFormatId()) {
            return this.changeInstanceTo(formatId, pageKey).createIdentity(o, o2);
        }
        this.initializeHeaders(5);
        this.createPage(pageKey, pageCreationArgs);
        this.fillInIdentity(pageKey);
        this.initialRowCount = 0;
        final int syncFlag = pageCreationArgs.syncFlag;
        if ((syncFlag & 0x1) != 0x0 || (syncFlag & 0x2) != 0x0) {
            this.writePage(pageKey, (syncFlag & 0x1) != 0x0);
        }
        return this;
    }
    
    private CachedPage changeInstanceTo(final int n, final PageKey pageKey) throws StandardException {
        CachedPage cachedPage;
        try {
            cachedPage = (CachedPage)Monitor.newInstanceFromIdentifier(n);
        }
        catch (StandardException ex) {
            if (ex.getSeverity() > 20000) {
                throw ex;
            }
            throw StandardException.newException("XSDBB.D", pageKey, StringUtil.hexDump(this.pageData));
        }
        cachedPage.setFactory(this.dataFactory);
        if (this.pageData != null) {
            cachedPage.alreadyReadPage = true;
            cachedPage.usePageBuffer(this.pageData);
        }
        return cachedPage;
    }
    
    public boolean isDirty() {
        synchronized (this) {
            return this.isDirty || this.preDirty;
        }
    }
    
    public boolean isActuallyDirty() {
        synchronized (this) {
            return this.isDirty;
        }
    }
    
    public void preDirty() {
        synchronized (this) {
            if (!this.isDirty) {
                this.preDirty = true;
            }
        }
    }
    
    protected void setDirty() {
        synchronized (this) {
            this.isDirty = true;
            this.preDirty = false;
        }
    }
    
    protected void releaseExclusive() {
        if (this.isDirty && !this.isOverflowPage() && this.containerRowCount / 8L < this.recordCount()) {
            final int internalNonDeletedRecordCount = this.internalNonDeletedRecordCount();
            final int n = internalNonDeletedRecordCount - this.initialRowCount;
            if (this.containerRowCount / 8L < ((n > 0) ? n : (-n))) {
                FileContainer fileContainer = null;
                try {
                    fileContainer = (FileContainer)this.containerCache.find(this.identity.getContainerId());
                    if (fileContainer != null) {
                        fileContainer.updateEstimatedRowCount(n);
                        this.setContainerRowCount(fileContainer.getEstimatedRowCount(0));
                        this.initialRowCount = internalNonDeletedRecordCount;
                        fileContainer.trackUnfilledPage(this.identity.getPageNumber(), this.unfilled());
                    }
                }
                catch (StandardException ex) {}
                finally {
                    if (fileContainer != null) {
                        this.containerCache.release(fileContainer);
                    }
                }
            }
        }
        super.releaseExclusive();
    }
    
    public void clean(final boolean b) throws StandardException {
        synchronized (this) {
            if (!this.isDirty()) {
                return;
            }
            while (this.inClean) {
                try {
                    this.wait();
                }
                catch (InterruptedException ex2) {
                    InterruptStatus.setInterrupted();
                }
            }
            if (!this.isDirty()) {
                return;
            }
            this.inClean = true;
            while (this.owner != null && !this.preLatch) {
                try {
                    this.wait();
                }
                catch (InterruptedException ex3) {
                    InterruptStatus.setInterrupted();
                }
            }
            if (!this.isActuallyDirty()) {
                this.preDirty = false;
                this.inClean = false;
                this.notifyAll();
                return;
            }
        }
        try {
            this.writePage(this.getPageId(), false);
        }
        catch (StandardException ex) {
            throw this.dataFactory.markCorrupt(ex);
        }
        finally {
            synchronized (this) {
                this.inClean = false;
                this.notifyAll();
            }
        }
    }
    
    public void clearIdentity() {
        this.alreadyReadPage = false;
        super.clearIdentity();
    }
    
    private void readPage(final FileContainer fileContainer, final PageKey pageKey) throws StandardException {
        final int pageSize = fileContainer.getPageSize();
        this.setPageArray(pageSize);
        int n = 0;
        while (true) {
            try {
                fileContainer.readPage(pageKey.getPageNumber(), this.pageData);
            }
            catch (IOException ex) {
                if (++n <= 4) {
                    continue;
                }
                final StandardException exception = StandardException.newException("XSDG0.D", ex, pageKey, new Integer(pageSize));
                if (this.dataFactory.getLogFactory().inRFR()) {
                    throw exception;
                }
                throw exception;
            }
            break;
        }
    }
    
    private void writePage(final PageKey pageKey, final boolean b) throws StandardException {
        this.writeFormatId(pageKey);
        this.writePage(pageKey);
        final LogInstant lastLogInstant = this.getLastLogInstant();
        this.dataFactory.flush(lastLogInstant);
        if (lastLogInstant != null) {
            this.clearLastLogInstant();
        }
        final FileContainer fileContainer = (FileContainer)this.containerCache.find(pageKey.getContainerId());
        if (fileContainer == null) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDG1.D", StandardException.newException("40XD2", pageKey.getContainerId()), pageKey));
        }
        try {
            fileContainer.writePage(pageKey.getPageNumber(), this.pageData, b);
            if (!this.isOverflowPage() && this.isDirty()) {
                fileContainer.trackUnfilledPage(pageKey.getPageNumber(), this.unfilled());
                final int internalNonDeletedRecordCount = this.internalNonDeletedRecordCount();
                if (internalNonDeletedRecordCount != this.initialRowCount) {
                    fileContainer.updateEstimatedRowCount(internalNonDeletedRecordCount - this.initialRowCount);
                    this.setContainerRowCount(fileContainer.getEstimatedRowCount(0));
                    this.initialRowCount = internalNonDeletedRecordCount;
                }
            }
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDG1.D", ex, pageKey);
        }
        finally {
            this.containerCache.release(fileContainer);
        }
        synchronized (this) {
            this.isDirty = false;
            this.preDirty = false;
        }
    }
    
    public void setContainerRowCount(final long containerRowCount) {
        this.containerRowCount = containerRowCount;
    }
    
    protected void setPageArray(final int n) {
        if (this.pageData == null || this.pageData.length != n) {
            this.pageData = null;
            this.pageData = new byte[n];
        }
        this.usePageBuffer(this.pageData);
    }
    
    protected byte[] getPageArray() throws StandardException {
        this.writeFormatId(this.identity);
        this.writePage(this.identity);
        return this.pageData;
    }
    
    protected abstract void usePageBuffer(final byte[] p0);
    
    protected abstract void initFromData(final FileContainer p0, final PageKey p1) throws StandardException;
    
    protected abstract void createPage(final PageKey p0, final PageCreationArgs p1) throws StandardException;
    
    protected abstract void writePage(final PageKey p0) throws StandardException;
    
    protected abstract void writeFormatId(final PageKey p0) throws StandardException;
}
