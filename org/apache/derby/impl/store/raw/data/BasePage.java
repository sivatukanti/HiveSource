// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.ContainerKey;
import java.io.ObjectInput;
import java.io.OutputStream;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.util.InterruptStatus;
import java.util.Observable;
import java.io.IOException;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.PageKey;
import org.apache.derby.iapi.store.raw.AuxObject;
import org.apache.derby.iapi.services.io.TypedFormat;
import java.util.Observer;
import org.apache.derby.iapi.store.raw.Page;

abstract class BasePage implements Page, Observer, TypedFormat
{
    private AuxObject auxObj;
    protected PageKey identity;
    private StoredRecordHeader[] headers;
    private int recordCount;
    protected BaseContainerHandle owner;
    private int nestedLatch;
    protected boolean inClean;
    protected boolean preLatch;
    private LogInstant lastLog;
    private long repositionNeededAfterVersion;
    private long pageVersion;
    private byte pageStatus;
    public static final byte VALID_PAGE = 1;
    public static final byte INVALID_PAGE = 2;
    public static final int INIT_PAGE_REUSE = 1;
    public static final int INIT_PAGE_OVERFLOW = 2;
    public static final int INIT_PAGE_REUSE_RECORDID = 4;
    public static final int LOG_RECORD_DEFAULT = 0;
    public static final int LOG_RECORD_FOR_UPDATE = 1;
    public static final int LOG_RECORD_FOR_PURGE = 2;
    private static final RecordHandle InvalidRecordHandle;
    
    protected BasePage() {
        this.pageVersion = 0L;
    }
    
    protected void initialize() {
        this.setAuxObject(null);
        this.identity = null;
        this.recordCount = 0;
        this.clearLastLogInstant();
        this.repositionNeededAfterVersion = 0L;
    }
    
    protected void initializeHeaders(final int n) {
        this.headers = new StoredRecordHeader[n];
    }
    
    protected void fillInIdentity(final PageKey identity) {
        this.identity = identity;
        this.repositionNeededAfterVersion = this.pageVersion;
    }
    
    public void clearIdentity() {
        this.identity = null;
        this.cleanPageForReuse();
    }
    
    protected void cleanPageForReuse() {
        this.setAuxObject(null);
        this.recordCount = 0;
        this.repositionNeededAfterVersion = 0L;
    }
    
    public Object getIdentity() {
        return this.identity;
    }
    
    public final RecordHandle getInvalidRecordHandle() {
        return BasePage.InvalidRecordHandle;
    }
    
    public static final RecordHandle MakeRecordHandle(final PageKey pageKey, final int n) throws StandardException {
        if (n >= 6) {
            throw StandardException.newException("XSDAE.S", new Long(n));
        }
        return new RecordId(pageKey, n);
    }
    
    public final RecordHandle makeRecordHandle(final int n) throws StandardException {
        return MakeRecordHandle(this.getPageId(), n);
    }
    
    public final long getPageNumber() {
        return this.identity.getPageNumber();
    }
    
    public final RecordHandle getRecordHandle(final int n) {
        final int recordById = this.findRecordById(n, 0);
        if (recordById < 0) {
            return null;
        }
        return this.getRecordHandleAtSlot(recordById);
    }
    
    public final RecordHandle getRecordHandleAtSlot(final int n) {
        return this.getHeaderAtSlot(n).getHandle(this.getPageId(), n);
    }
    
    public final boolean recordExists(final RecordHandle recordHandle, final boolean b) throws StandardException {
        if (recordHandle.getId() < 6) {
            throw StandardException.newException("XSDAF.S", recordHandle);
        }
        if (recordHandle.getPageNumber() != this.getPageNumber()) {
            return false;
        }
        final int recordById = this.findRecordById(recordHandle.getId(), recordHandle.getSlotNumberHint());
        return recordById >= 0 && (b || !this.isDeletedAtSlot(recordById));
    }
    
    public RecordHandle fetchFromSlot(RecordHandle handle, final int n, final Object[] array, final FetchDescriptor fetchDescriptor, final boolean b) throws StandardException {
        this.checkSlotOnPage(n);
        final StoredRecordHeader headerAtSlot = this.getHeaderAtSlot(n);
        if (handle == null) {
            handle = headerAtSlot.getHandle(this.getPageId(), n);
        }
        if (!b && headerAtSlot.isDeleted()) {
            return null;
        }
        return this.restoreRecordFromSlot(n, array, fetchDescriptor, handle, headerAtSlot, true) ? handle : null;
    }
    
    public final RecordHandle fetchFieldFromSlot(final int n, final int n2, final Object o) throws StandardException {
        final Object[] array = new Object[n2 + 1];
        array[n2] = o;
        return this.fetchFromSlot(null, n, array, new FetchDescriptor(n2 + 1, n2), true);
    }
    
    public final int getSlotNumber(final RecordHandle recordHandle) throws StandardException {
        final int recordById = this.findRecordById(recordHandle.getId(), recordHandle.getSlotNumberHint());
        if (recordById < 0) {
            throw StandardException.newException("XSRS9.S", recordHandle);
        }
        return recordById;
    }
    
    public final int getNextSlotNumber(final RecordHandle recordHandle) throws StandardException {
        return this.findNextRecordById(recordHandle.getId());
    }
    
    public RecordHandle insertAtSlot(final int n, final Object[] array, final FormatableBitSet set, final LogicalUndo logicalUndo, final byte b, final int n2) throws StandardException {
        if ((b & 0x1) == 0x1) {
            return this.insertNoOverflow(n, array, set, logicalUndo, b, n2);
        }
        return this.insertAllowOverflow(n, array, set, 0, b, n2, null);
    }
    
    protected RecordHandle insertNoOverflow(final int n, final Object[] array, final FormatableBitSet set, final LogicalUndo logicalUndo, final byte b, final int n2) throws StandardException {
        if (!this.owner.updateOK()) {
            throw StandardException.newException("40XD1");
        }
        if (n < 0 || n > this.recordCount) {
            throw StandardException.newException("XSDA1.S");
        }
        if (!this.allowInsert()) {
            return null;
        }
        final RawTransaction transaction = this.owner.getTransaction();
        if (logicalUndo != null) {
            transaction.checkLogicalOperationOk();
        }
        RecordId recordId;
        int recordIdAndBump;
        do {
            recordIdAndBump = this.newRecordIdAndBump();
            recordId = new RecordId(this.getPageId(), recordIdAndBump, n);
        } while (!this.owner.getLockingPolicy().lockRecordForWrite(transaction, recordId, true, false));
        this.owner.getActionSet().actionInsert(transaction, this, n, recordIdAndBump, array, set, logicalUndo, b, 0, false, -1, null, -1, n2);
        return recordId;
    }
    
    public final RecordHandle insert(final Object[] array, final FormatableBitSet set, final byte b, final int n) throws StandardException {
        if ((b & 0x1) == 0x1) {
            return this.insertAtSlot(this.recordCount, array, set, null, b, n);
        }
        return this.insertAllowOverflow(this.recordCount, array, set, 0, b, n, null);
    }
    
    public RecordHandle insertAllowOverflow(int recordCount, final Object[] array, final FormatableBitSet set, int actionInsert, final byte b, final int n, final RecordHandle recordHandle) throws StandardException {
        BasePage basePage = this;
        if (!basePage.owner.updateOK()) {
            throw StandardException.newException("40XD1");
        }
        RecordHandle recordHandle2 = null;
        RecordHandle recordHandle3 = null;
        final RawTransaction transaction = basePage.owner.getTransaction();
        while (basePage.allowInsert()) {
            if (basePage != this) {
                recordCount = basePage.recordCount;
            }
            int n2 = -1;
            int n3 = -1;
            DynamicByteArrayOutputStream dynamicByteArrayOutputStream = null;
            int n4 = basePage.newRecordIdAndBump();
            RecordId recordId = new RecordId(basePage.getPageId(), n4, recordCount);
            if (basePage == this) {
                if (recordHandle3 == null) {
                    while (!this.owner.getLockingPolicy().lockRecordForWrite(transaction, recordId, true, false)) {
                        n4 = basePage.newRecordIdAndBump();
                        recordId = new RecordId(basePage.getPageId(), n4, recordCount);
                    }
                }
                recordHandle2 = recordId;
            }
            boolean b2;
            do {
                try {
                    actionInsert = this.owner.getActionSet().actionInsert(transaction, basePage, recordCount, n4, array, set, null, b, actionInsert, false, n2, dynamicByteArrayOutputStream, n3, n);
                    b2 = false;
                }
                catch (LongColumnException ex) {
                    dynamicByteArrayOutputStream = new DynamicByteArrayOutputStream(ex.getLogBuffer());
                    final RecordHandle insertLongColumn = this.insertLongColumn(basePage, ex, b);
                    final int n5 = 0;
                    int n6;
                    try {
                        n6 = n5 + this.appendOverflowFieldHeader(dynamicByteArrayOutputStream, insertLongColumn);
                    }
                    catch (IOException ex2) {
                        return null;
                    }
                    n2 = ex.getNextColumn() + 1;
                    n3 = ex.getRealSpaceOnPage() - n6;
                    b2 = true;
                }
            } while (b2);
            if (recordHandle3 != null) {
                this.updateOverflowDetails(recordHandle3, recordId);
            }
            if (actionInsert == -1) {
                if (basePage != this) {
                    basePage.unlatch();
                }
                if (recordHandle != null) {
                    this.updateOverflowDetails(recordId, recordHandle);
                }
                return recordHandle2;
            }
            recordHandle3 = recordId;
            final BasePage overflowPageForInsert = basePage.getOverflowPageForInsert(recordCount, array, set, actionInsert);
            if (basePage != this) {
                basePage.unlatch();
            }
            basePage = overflowPageForInsert;
        }
        return null;
    }
    
    protected RecordHandle insertLongColumn(final BasePage basePage, final LongColumnException ex, final byte b) throws StandardException {
        final Object[] array = { ex.getColumn() };
        RecordHandle recordHandle = null;
        RecordHandle recordHandle2 = null;
        RecordHandle recordHandle3 = null;
        BasePage newOverflowPage = basePage;
        BasePage basePage2 = null;
        int n = 1;
        final byte b2 = (byte)(b | 0x2);
        int i = 0;
        final RawTransaction transaction = newOverflowPage.owner.getTransaction();
        do {
            if (n == 0) {
                basePage2 = newOverflowPage;
                recordHandle3 = recordHandle2;
            }
            newOverflowPage = this.getNewOverflowPage();
            final int recordCount = newOverflowPage.recordCount;
            final int recordId = newOverflowPage.newRecordId();
            recordHandle2 = new RecordId(newOverflowPage.getPageId(), recordId, recordCount);
            if (n != 0) {
                recordHandle = recordHandle2;
            }
            i = this.owner.getActionSet().actionInsert(transaction, newOverflowPage, recordCount, recordId, array, null, null, (n != 0) ? b : b2, i, true, -1, null, -1, 100);
            if (n == 0) {
                basePage2.updateFieldOverflowDetails(recordHandle3, recordHandle2);
                basePage2.unlatch();
                basePage2 = null;
            }
            else {
                n = 0;
            }
        } while (i != -1);
        if (newOverflowPage != null) {
            newOverflowPage.unlatch();
        }
        return recordHandle;
    }
    
    public abstract void preDirty();
    
    public abstract void updateOverflowDetails(final RecordHandle p0, final RecordHandle p1) throws StandardException;
    
    public abstract void updateFieldOverflowDetails(final RecordHandle p0, final RecordHandle p1) throws StandardException;
    
    public abstract int appendOverflowFieldHeader(final DynamicByteArrayOutputStream p0, final RecordHandle p1) throws StandardException, IOException;
    
    public abstract BasePage getOverflowPageForInsert(final int p0, final Object[] p1, final FormatableBitSet p2, final int p3) throws StandardException;
    
    protected abstract BasePage getNewOverflowPage() throws StandardException;
    
    public final RecordHandle updateAtSlot(final int n, final Object[] array, final FormatableBitSet set) throws StandardException {
        if (!this.owner.updateOK()) {
            throw StandardException.newException("40XD1");
        }
        if (this.isDeletedAtSlot(n)) {
            throw StandardException.newException("XSDA2.S");
        }
        final RecordHandle recordHandleAtSlot = this.getRecordHandleAtSlot(n);
        this.doUpdateAtSlot(this.owner.getTransaction(), n, recordHandleAtSlot.getId(), array, set);
        return recordHandleAtSlot;
    }
    
    public abstract void doUpdateAtSlot(final RawTransaction p0, final int p1, final int p2, final Object[] p3, final FormatableBitSet p4) throws StandardException;
    
    public RecordHandle updateFieldAtSlot(final int n, final int n2, final Object o, final LogicalUndo logicalUndo) throws StandardException {
        if (!this.owner.updateOK()) {
            throw StandardException.newException("40XD1");
        }
        if (this.isDeletedAtSlot(n)) {
            throw StandardException.newException("XSDA2.S");
        }
        final RawTransaction transaction = this.owner.getTransaction();
        final RecordHandle recordHandleAtSlot = this.getRecordHandleAtSlot(n);
        this.owner.getActionSet().actionUpdateField(transaction, this, n, recordHandleAtSlot.getId(), n2, o, logicalUndo);
        return recordHandleAtSlot;
    }
    
    public final int fetchNumFields(final RecordHandle recordHandle) throws StandardException {
        return this.fetchNumFieldsAtSlot(this.getSlotNumber(recordHandle));
    }
    
    public int fetchNumFieldsAtSlot(final int n) throws StandardException {
        return this.getHeaderAtSlot(n).getNumberFields();
    }
    
    public RecordHandle deleteAtSlot(final int n, final boolean b, final LogicalUndo logicalUndo) throws StandardException {
        if (!this.owner.updateOK()) {
            throw StandardException.newException("40XD1");
        }
        if (b) {
            if (this.isDeletedAtSlot(n)) {
                throw StandardException.newException("XSDA2.S");
            }
        }
        else if (!this.isDeletedAtSlot(n)) {
            throw StandardException.newException("XSDA5.S");
        }
        final RawTransaction transaction = this.owner.getTransaction();
        if (logicalUndo != null) {
            transaction.checkLogicalOperationOk();
        }
        final RecordHandle recordHandleAtSlot = this.getRecordHandleAtSlot(n);
        this.owner.getActionSet().actionDelete(transaction, this, n, recordHandleAtSlot.getId(), b, logicalUndo);
        return recordHandleAtSlot;
    }
    
    public void purgeAtSlot(final int n, final int n2, final boolean b) throws StandardException {
        if (n2 <= 0) {
            return;
        }
        if (!this.owner.updateOK()) {
            throw StandardException.newException("40XD1");
        }
        if (n < 0 || n + n2 > this.recordCount) {
            throw StandardException.newException("XSDA1.S");
        }
        final RawTransaction transaction = this.owner.getTransaction();
        final int[] array = new int[n2];
        final PageKey pageId = this.getPageId();
        for (int i = 0; i < n2; ++i) {
            array[i] = this.getHeaderAtSlot(n + i).getId();
            this.owner.getLockingPolicy().lockRecordForWrite(transaction, this.getRecordHandleAtSlot(n), false, true);
            if (!this.owner.isTemporaryContainer()) {
                if (!this.entireRecordOnPage(n + i)) {
                    this.purgeRowPieces(transaction, n + i, this.getHeaderAtSlot(n + i).getHandle(pageId, n + i), b);
                }
            }
        }
        this.owner.getActionSet().actionPurge(transaction, this, n, n2, array, b);
    }
    
    protected abstract void purgeRowPieces(final RawTransaction p0, final int p1, final RecordHandle p2, final boolean p3) throws StandardException;
    
    public void copyAndPurge(final Page page, final int n, final int n2, final int n3) throws StandardException {
        if (n2 <= 0) {
            throw StandardException.newException("XSDAD.S");
        }
        if (!this.owner.updateOK()) {
            throw StandardException.newException("40XD1");
        }
        if (n < 0 || n + n2 > this.recordCount) {
            throw StandardException.newException("XSDA1.S");
        }
        final BasePage basePage = (BasePage)page;
        final PageKey pageId = this.getPageId();
        if (!pageId.getContainerId().equals(basePage.getPageId().getContainerId())) {
            throw StandardException.newException("XSDAC.S", pageId.getContainerId(), basePage.getPageId().getContainerId());
        }
        final int[] array = new int[n2];
        final RawTransaction transaction = this.owner.getTransaction();
        for (int i = 0; i < n2; ++i) {
            this.owner.getLockingPolicy().lockRecordForWrite(transaction, this.getRecordHandleAtSlot(n + i), false, true);
            array[i] = this.getHeaderAtSlot(n + i).getId();
        }
        basePage.copyInto(this, n, n2, n3);
        this.owner.getActionSet().actionPurge(transaction, this, n, n2, array, true);
    }
    
    public void unlatch() {
        this.releaseExclusive();
    }
    
    public final synchronized boolean isLatched() {
        return this.owner != null;
    }
    
    public final int recordCount() {
        return this.recordCount;
    }
    
    protected abstract int internalDeletedRecordCount();
    
    protected int internalNonDeletedRecordCount() {
        if (this.pageStatus != 1) {
            return 0;
        }
        final int internalDeletedRecordCount = this.internalDeletedRecordCount();
        if (internalDeletedRecordCount == -1) {
            int n = 0;
            for (int recordCount = this.recordCount, i = 0; i < recordCount; ++i) {
                if (!this.isDeletedOnPage(i)) {
                    ++n;
                }
            }
            return n;
        }
        return this.recordCount - internalDeletedRecordCount;
    }
    
    public int nonDeletedRecordCount() {
        return this.internalNonDeletedRecordCount();
    }
    
    public boolean shouldReclaimSpace(final int n, final int n2) throws StandardException {
        boolean b = false;
        if (this.internalNonDeletedRecordCount() <= n) {
            b = true;
        }
        else if (!this.entireRecordOnPage(n2)) {
            b = true;
        }
        return b;
    }
    
    protected final boolean isDeletedOnPage(final int n) {
        return this.getHeaderAtSlot(n).isDeleted();
    }
    
    public boolean isDeletedAtSlot(final int n) throws StandardException {
        this.checkSlotOnPage(n);
        return this.isDeletedOnPage(n);
    }
    
    public void setAuxObject(final AuxObject auxObj) {
        if (this.auxObj != null) {
            this.auxObj.auxObjectInvalidated();
        }
        this.auxObj = auxObj;
    }
    
    public AuxObject getAuxObject() {
        return this.auxObj;
    }
    
    public void setRepositionNeeded() {
        this.repositionNeededAfterVersion = this.getPageVersion();
    }
    
    public boolean isRepositionNeeded(final long n) {
        return this.repositionNeededAfterVersion > n;
    }
    
    public void update(final Observable observable, final Object o) {
        this.releaseExclusive();
    }
    
    public final PageKey getPageId() {
        return this.identity;
    }
    
    void setExclusive(final BaseContainerHandle baseContainerHandle) throws StandardException {
        final RawTransaction transaction = baseContainerHandle.getTransaction();
        synchronized (this) {
            if (this.owner != null && transaction == this.owner.getTransaction()) {
                if (transaction.inAbort()) {
                    ++this.nestedLatch;
                    return;
                }
                throw StandardException.newException("XSDAO.S", this.identity);
            }
            else {
                while (this.owner != null) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException ex) {
                        InterruptStatus.setInterrupted();
                    }
                }
                this.preLatch(baseContainerHandle);
                while (this.inClean) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException ex2) {
                        InterruptStatus.setInterrupted();
                    }
                }
                this.preLatch = false;
            }
        }
    }
    
    boolean setExclusiveNoWait(final BaseContainerHandle baseContainerHandle) throws StandardException {
        final RawTransaction transaction = baseContainerHandle.getTransaction();
        synchronized (this) {
            if (this.owner != null && transaction == this.owner.getTransaction() && transaction.inAbort()) {
                ++this.nestedLatch;
                return true;
            }
            if (this.owner != null) {
                return false;
            }
            this.preLatch(baseContainerHandle);
            while (this.inClean) {
                try {
                    this.wait();
                }
                catch (InterruptedException ex) {
                    InterruptStatus.setInterrupted();
                }
            }
            this.preLatch = false;
        }
        return true;
    }
    
    protected synchronized void releaseExclusive() {
        if (this.nestedLatch > 0) {
            --this.nestedLatch;
            return;
        }
        this.owner.deleteObserver(this);
        this.owner = null;
        this.notifyAll();
    }
    
    private void preLatch(final BaseContainerHandle owner) {
        (this.owner = owner).addObserver(this);
        this.preLatch = true;
    }
    
    protected final void setHeaderAtSlot(final int n, final StoredRecordHeader storedRecordHeader) {
        if (n < this.headers.length) {
            if (storedRecordHeader != null) {
                this.headers[n] = storedRecordHeader;
            }
        }
        else {
            final StoredRecordHeader[] headers = new StoredRecordHeader[n + 1];
            System.arraycopy(this.headers, 0, headers, 0, this.headers.length);
            (this.headers = headers)[n] = storedRecordHeader;
        }
    }
    
    protected final void bumpRecordCount(final int n) {
        this.recordCount += n;
    }
    
    public final StoredRecordHeader getHeaderAtSlot(final int n) {
        if (n < this.headers.length) {
            final StoredRecordHeader storedRecordHeader = this.headers[n];
            return (storedRecordHeader != null) ? storedRecordHeader : this.recordHeaderOnDemand(n);
        }
        return this.recordHeaderOnDemand(n);
    }
    
    public abstract boolean entireRecordOnPage(final int p0) throws StandardException;
    
    public abstract StoredRecordHeader recordHeaderOnDemand(final int p0);
    
    private final void checkSlotOnPage(final int n) throws StandardException {
        if (n >= 0 && n < this.recordCount) {
            return;
        }
        throw StandardException.newException("XSDA1.S");
    }
    
    public int setDeleteStatus(final int n, final boolean deleted) throws StandardException, IOException {
        return this.getHeaderAtSlot(n).setDeleted(deleted);
    }
    
    public void deallocatePage() throws StandardException {
        if (!this.owner.updateOK()) {
            throw StandardException.newException("40XD1");
        }
        this.owner.getActionSet().actionInvalidatePage(this.owner.getTransaction(), this);
    }
    
    public void initPage(final int n, final long n2) throws StandardException {
        if (!this.owner.updateOK()) {
            throw StandardException.newException("40XD1");
        }
        this.owner.getActionSet().actionInitPage(this.owner.getTransaction(), this, n, this.getTypeFormatId(), n2);
    }
    
    public int findRecordById(final int n, int n2) {
        if (n2 == 0) {
            n2 = n - 6;
        }
        final int recordCount = this.recordCount();
        if (n2 > 0 && n2 < recordCount && n == this.getHeaderAtSlot(n2).getId()) {
            return n2;
        }
        for (int i = 0; i < recordCount; ++i) {
            if (n == this.getHeaderAtSlot(i).getId()) {
                return i;
            }
        }
        return -1;
    }
    
    private int findNextRecordById(final int n) {
        for (int recordCount = this.recordCount(), i = 0; i < recordCount; ++i) {
            if (this.getHeaderAtSlot(i).getId() > n) {
                return i;
            }
        }
        return -1;
    }
    
    private void copyInto(final BasePage basePage, final int n, final int n2, final int n3) throws StandardException {
        if (n3 < 0 || n3 > this.recordCount) {
            throw StandardException.newException("XSDA1.S");
        }
        final RawTransaction transaction = this.owner.getTransaction();
        final int[] array = new int[n2];
        final PageKey pageId = this.getPageId();
        for (int i = 0; i < n2; ++i) {
            if (i == 0) {
                array[i] = this.newRecordId();
            }
            else {
                array[i] = this.newRecordId(array[i - 1]);
            }
            this.owner.getLockingPolicy().lockRecordForWrite(transaction, new RecordId(pageId, array[i], i), false, true);
        }
        this.owner.getActionSet().actionCopyRows(transaction, this, basePage, n3, n2, n, array);
    }
    
    protected void removeAndShiftDown(final int n) {
        System.arraycopy(this.headers, n + 1, this.headers, n, this.headers.length - (n + 1));
        this.headers[this.headers.length - 1] = null;
        --this.recordCount;
    }
    
    protected StoredRecordHeader shiftUp(final int n) {
        if (n < this.headers.length) {
            System.arraycopy(this.headers, n, this.headers, n + 1, this.headers.length - (n + 1));
            this.headers[n] = null;
        }
        return null;
    }
    
    public void compactRecord(final RecordHandle recordHandle) throws StandardException {
        if (!this.owner.updateOK()) {
            throw StandardException.newException("40XD1");
        }
        if (recordHandle.getId() < 6) {
            throw StandardException.newException("XSDAF.S", recordHandle);
        }
        if (recordHandle.getPageNumber() != this.getPageNumber()) {
            throw StandardException.newException("XSDAK.S", recordHandle);
        }
        if (this.isOverflowPage()) {
            throw StandardException.newException("XSDAL.S", recordHandle);
        }
        final int recordById = this.findRecordById(recordHandle.getId(), recordHandle.getSlotNumberHint());
        if (recordById >= 0) {
            this.compactRecord(this.owner.getTransaction(), recordById, recordHandle.getId());
        }
    }
    
    public final LogInstant getLastLogInstant() {
        return this.lastLog;
    }
    
    protected final void clearLastLogInstant() {
        this.lastLog = null;
    }
    
    protected final void updateLastLogInstant(final LogInstant lastLog) {
        if (lastLog != null) {
            this.lastLog = lastLog;
        }
    }
    
    public final long getPageVersion() {
        return this.pageVersion;
    }
    
    protected final long bumpPageVersion() {
        return ++this.pageVersion;
    }
    
    public final void setPageVersion(final long pageVersion) {
        this.pageVersion = pageVersion;
    }
    
    protected void setPageStatus(final byte pageStatus) {
        this.pageStatus = pageStatus;
    }
    
    public byte getPageStatus() {
        return this.pageStatus;
    }
    
    protected abstract boolean restoreRecordFromSlot(final int p0, final Object[] p1, final FetchDescriptor p2, final RecordHandle p3, final StoredRecordHeader p4, final boolean p5) throws StandardException;
    
    protected abstract void restorePortionLongColumn(final OverflowInputStream p0) throws StandardException, IOException;
    
    public abstract int newRecordId() throws StandardException;
    
    public abstract int newRecordIdAndBump() throws StandardException;
    
    protected abstract int newRecordId(final int p0) throws StandardException;
    
    public abstract boolean spaceForCopy(final int p0, final int[] p1) throws StandardException;
    
    public abstract int getTotalSpace(final int p0) throws StandardException;
    
    public abstract int getReservedCount(final int p0) throws IOException;
    
    public abstract int getRecordLength(final int p0) throws IOException;
    
    public abstract void restoreRecordFromStream(final LimitObjectInput p0, final Object[] p1) throws StandardException, IOException;
    
    public abstract void logRecord(final int p0, final int p1, final int p2, final FormatableBitSet p3, final OutputStream p4, final RecordHandle p5) throws StandardException, IOException;
    
    public abstract int logRow(final int p0, final boolean p1, final int p2, final Object[] p3, final FormatableBitSet p4, final DynamicByteArrayOutputStream p5, final int p6, final byte p7, final int p8, final int p9, final int p10) throws StandardException, IOException;
    
    public abstract void logField(final int p0, final int p1, final OutputStream p2) throws StandardException, IOException;
    
    public abstract void logColumn(final int p0, final int p1, final Object p2, final DynamicByteArrayOutputStream p3, final int p4) throws StandardException, IOException;
    
    public abstract int logLongColumn(final int p0, final int p1, final Object p2, final DynamicByteArrayOutputStream p3) throws StandardException, IOException;
    
    public abstract void storeRecord(final LogInstant p0, final int p1, final boolean p2, final ObjectInput p3) throws StandardException, IOException;
    
    public abstract void storeField(final LogInstant p0, final int p1, final int p2, final ObjectInput p3) throws StandardException, IOException;
    
    public abstract void reserveSpaceForSlot(final LogInstant p0, final int p1, final int p2) throws StandardException, IOException;
    
    public abstract void skipField(final ObjectInput p0) throws StandardException, IOException;
    
    public abstract void skipRecord(final ObjectInput p0) throws StandardException, IOException;
    
    public abstract void setDeleteStatus(final LogInstant p0, final int p1, final boolean p2) throws StandardException, IOException;
    
    public abstract void purgeRecord(final LogInstant p0, final int p1, final int p2) throws StandardException, IOException;
    
    protected abstract void compactRecord(final RawTransaction p0, final int p1, final int p2) throws StandardException;
    
    public abstract void setPageStatus(final LogInstant p0, final byte p1) throws StandardException;
    
    public abstract void initPage(final LogInstant p0, final byte p1, final int p2, final boolean p3, final boolean p4) throws StandardException;
    
    public abstract void setReservedSpace(final LogInstant p0, final int p1, final int p2) throws StandardException, IOException;
    
    public abstract boolean isOverflowPage();
    
    public abstract boolean allowInsert();
    
    public abstract boolean unfilled();
    
    public abstract void setContainerRowCount(final long p0);
    
    protected abstract byte[] getPageArray() throws StandardException;
    
    protected String slotTableToString() {
        return null;
    }
    
    static {
        InvalidRecordHandle = new RecordId(new PageKey(new ContainerKey(0L, 0L), -1L), 0);
    }
}
