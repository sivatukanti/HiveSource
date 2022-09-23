// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.PageKey;

public class AllocPage extends StoredPage
{
    public static final int FORMAT_NUMBER = 118;
    private long nextAllocPageNumber;
    private long nextAllocPageOffset;
    private long reserved1;
    private long reserved2;
    private long reserved3;
    private long reserved4;
    private AllocExtent extent;
    private int borrowedSpace;
    protected static final int ALLOC_PAGE_HEADER_OFFSET = 60;
    protected static final int ALLOC_PAGE_HEADER_SIZE = 48;
    protected static final int BORROWED_SPACE_OFFSET = 108;
    protected static final int BORROWED_SPACE_LEN = 1;
    protected static final int MAX_BORROWED_SPACE = 204;
    public static final String TEST_MULTIPLE_ALLOC_PAGE;
    
    public int getTypeFormatId() {
        return 118;
    }
    
    protected int getMaxFreeSpace() {
        return super.getMaxFreeSpace() - 48 - 1 - this.borrowedSpace;
    }
    
    protected void createPage(final PageKey pageKey, final PageCreationArgs pageCreationArgs) throws StandardException {
        this.borrowedSpace = pageCreationArgs.containerInfoSize;
        super.createPage(pageKey, pageCreationArgs);
        this.pageData[108] = (byte)this.borrowedSpace;
        if (this.borrowedSpace > 0) {
            this.clearSection(109, this.borrowedSpace);
        }
        this.nextAllocPageNumber = -1L;
        this.nextAllocPageOffset = 0L;
        final long n = 0L;
        this.reserved4 = n;
        this.reserved3 = n;
        this.reserved2 = n;
        this.reserved1 = n;
        this.extent = this.createExtent(pageKey.getPageNumber() + 1L, this.getPageSize(), 0, this.totalSpace);
    }
    
    private AllocExtent createExtent(final long n, final int n2, final int n3, final int n4) {
        return new AllocExtent(n * n2, n, n3, n2, AllocExtent.MAX_RANGE(n4));
    }
    
    protected void initFromData(final FileContainer fileContainer, final PageKey pageKey) throws StandardException {
        if (this.pageData.length < 109) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", pageKey));
        }
        final byte borrowedSpace = this.pageData[108];
        this.borrowedSpace = borrowedSpace;
        if (this.pageData.length < 109 + borrowedSpace) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", pageKey));
        }
        if (this.borrowedSpace > 0) {
            this.clearSection(109, this.borrowedSpace);
        }
        super.initFromData(fileContainer, pageKey);
        try {
            this.readAllocPageHeader();
            this.extent = this.readExtent(109 + this.borrowedSpace);
        }
        catch (IOException ex) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex, pageKey));
        }
        catch (ClassNotFoundException ex2) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex2, pageKey));
        }
    }
    
    protected void writePage(final PageKey pageKey) throws StandardException {
        try {
            this.updateAllocPageHeader();
            final byte b = this.pageData[108];
            if (b > 0) {
                this.clearSection(109, b);
            }
            this.writeExtent(109 + b);
        }
        catch (IOException ex) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex, pageKey));
        }
        super.writePage(pageKey);
    }
    
    private void readAllocPageHeader() throws IOException {
        final ArrayInputStream rawDataIn = this.rawDataIn;
        rawDataIn.setPosition(60);
        this.nextAllocPageNumber = rawDataIn.readLong();
        this.nextAllocPageOffset = rawDataIn.readLong();
        this.reserved1 = rawDataIn.readLong();
        this.reserved2 = rawDataIn.readLong();
        this.reserved3 = rawDataIn.readLong();
        this.reserved4 = rawDataIn.readLong();
    }
    
    private void updateAllocPageHeader() throws IOException {
        this.rawDataOut.setPosition(60);
        this.logicalDataOut.writeLong(this.nextAllocPageNumber);
        this.logicalDataOut.writeLong(this.nextAllocPageOffset);
        this.logicalDataOut.writeLong(0L);
        this.logicalDataOut.writeLong(0L);
        this.logicalDataOut.writeLong(0L);
        this.logicalDataOut.writeLong(0L);
    }
    
    private AllocExtent readExtent(final int position) throws IOException, ClassNotFoundException {
        final ArrayInputStream rawDataIn = this.rawDataIn;
        this.rawDataIn.setPosition(position);
        final AllocExtent allocExtent = new AllocExtent();
        allocExtent.readExternal(rawDataIn);
        return allocExtent;
    }
    
    private void writeExtent(final int position) throws IOException {
        this.rawDataOut.setPosition(position);
        this.extent.writeExternal(this.logicalDataOut);
    }
    
    public static void WriteContainerInfo(final byte[] array, final byte[] array2, final boolean b) throws StandardException {
        final int n = (array == null) ? 0 : array.length;
        if (n + 1 + 108 > array2.length) {}
        if (b) {
            array2[108] = (byte)n;
        }
        else {
            final byte b2 = array2[108];
            if (b2 != n) {
                throw StandardException.newException("XSDB3.D", new Long(b2), new Long(n));
            }
        }
        if (n != 0) {
            System.arraycopy(array, 0, array2, 109, n);
        }
    }
    
    public static void ReadContainerInfo(final byte[] array, final byte[] array2) {
        final byte b = array2[108];
        if (b != 0) {
            System.arraycopy(array2, 109, array, 0, b);
        }
    }
    
    public long nextFreePageNumber(final long n) {
        return this.extent.getFreePageNumber(n);
    }
    
    public void addPage(final FileContainer fileContainer, final long n, final RawTransaction rawTransaction, final BaseContainerHandle baseContainerHandle) throws StandardException {
        this.owner.getAllocationActionSet().actionAllocatePage(rawTransaction, this, n, 0, 2);
    }
    
    public void deallocatePage(final BaseContainerHandle baseContainerHandle, final long n) throws StandardException {
        this.owner.getAllocationActionSet().actionAllocatePage(baseContainerHandle.getTransaction(), this, n, 1, 0);
    }
    
    protected void updateUnfilledPageInfo(final AllocExtent allocExtent) {
        this.extent.updateUnfilledPageInfo(allocExtent);
    }
    
    public boolean canAddFreePage(final long n) {
        return !this.extent.isRetired() && (n == -1L || this.extent.getLastPagenum() > n || this.isLast()) && this.extent.canAddFreePage(n);
    }
    
    public long getNextAllocPageOffset() {
        return this.nextAllocPageOffset;
    }
    
    public void chainNewAllocPage(final BaseContainerHandle baseContainerHandle, final long n, final long n2) throws StandardException {
        this.owner.getAllocationActionSet().actionChainAllocPage(baseContainerHandle.getTransaction(), this, n, n2);
    }
    
    public long getNextAllocPageNumber() {
        return this.nextAllocPageNumber;
    }
    
    public boolean isLast() {
        return this.nextAllocPageNumber == -1L;
    }
    
    public long getLastPagenum() {
        return this.extent.getLastPagenum();
    }
    
    public long getMaxPagenum() {
        return this.extent.getExtentEnd();
    }
    
    protected long getLastPreallocPagenum() {
        return this.extent.getLastPreallocPagenum();
    }
    
    protected int getPageStatus(final long n) {
        return this.extent.getPageStatus(n);
    }
    
    protected void setPageStatus(final LogInstant logInstant, final long n, final int n2) throws StandardException {
        this.logAction(logInstant);
        switch (n2) {
            case 0: {
                this.extent.allocPage(n);
                break;
            }
            case 1: {
                this.extent.deallocPage(n);
                break;
            }
            case 2: {
                this.extent.deallocPage(n);
                break;
            }
        }
    }
    
    protected void chainNextAllocPage(final LogInstant logInstant, final long nextAllocPageNumber, final long nextAllocPageOffset) throws StandardException {
        this.logAction(logInstant);
        this.nextAllocPageNumber = nextAllocPageNumber;
        this.nextAllocPageOffset = nextAllocPageOffset;
    }
    
    protected void compressSpace(final LogInstant logInstant, final int n, final int n2) throws StandardException {
        this.logAction(logInstant);
        this.extent.compressPages(n, n2);
    }
    
    protected void undoCompressSpace(final LogInstant logInstant, final int n, final int n2) throws StandardException {
        this.logAction(logInstant);
        this.extent.undoCompressPages(n, n2);
    }
    
    public String toString() {
        return null;
    }
    
    protected AllocExtent getAllocExtent() {
        return this.extent;
    }
    
    protected void preAllocatePage(final FileContainer fileContainer, final int n, int n2) {
        final long lastPreallocPagenum = this.extent.getLastPreallocPagenum();
        if (lastPreallocPagenum < n) {
            return;
        }
        if (this.extent.getExtentEnd() < lastPreallocPagenum + n2) {
            n2 = (int)(this.extent.getExtentEnd() - lastPreallocPagenum);
        }
        if (n2 <= 0) {
            return;
        }
        final int preAllocate = fileContainer.preAllocate(lastPreallocPagenum, n2);
        if (preAllocate > 0) {
            this.extent.setLastPreallocPagenum(lastPreallocPagenum + preAllocate);
        }
    }
    
    protected boolean compress(final RawTransaction rawTransaction, final FileContainer fileContainer) throws StandardException {
        boolean b = false;
        final int compress = this.extent.compress(this.owner, rawTransaction, this);
        if (compress >= 0) {
            fileContainer.truncatePages(this.extent.getPagenum(compress));
            if (compress == 0) {
                b = true;
            }
        }
        return b;
    }
    
    static {
        TEST_MULTIPLE_ALLOC_PAGE = null;
    }
}
