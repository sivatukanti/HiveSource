// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.heap;

import org.apache.derby.impl.store.access.conglomerate.OpenConglomerate;
import org.apache.derby.iapi.store.access.conglomerate.Conglomerate;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.access.RowLocationRetRowSource;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.RowUtil;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.impl.store.access.conglomerate.RowPosition;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.impl.store.access.conglomerate.GenericConglomerateController;

public class HeapController extends GenericConglomerateController implements ConglomerateController
{
    protected final void getRowPositionFromRowLocation(final RowLocation rowLocation, final RowPosition rowPosition) throws StandardException {
        rowPosition.current_rh = ((HeapRowLocation)rowLocation).getRecordHandle(this.open_conglom.getContainer());
        rowPosition.current_rh_qualified = true;
    }
    
    protected void queueDeletePostCommitWork(final RowPosition rowPosition) throws StandardException {
        final TransactionManager xactMgr = this.open_conglom.getXactMgr();
        xactMgr.addPostCommitWork(new HeapPostCommit(xactMgr.getAccessManager(), (Heap)this.open_conglom.getConglomerate(), rowPosition.current_page.getPageNumber()));
    }
    
    protected final boolean purgeCommittedDeletes(final Page page) throws StandardException {
        boolean b = false;
        if (page.recordCount() - page.nonDeletedRecordCount() > 0) {
            for (int i = page.recordCount() - 1; i >= 0; --i) {
                if (page.isDeletedAtSlot(i) && this.lockRowAtSlotNoWaitExclusive(page.fetchFromSlot(null, i, RowUtil.EMPTY_ROW, RowUtil.EMPTY_ROW_FETCH_DESCRIPTOR, true))) {
                    b = true;
                    page.purgeAtSlot(i, 1, false);
                }
            }
        }
        if (page.recordCount() == 0) {
            this.removePage(page);
            b = true;
        }
        return b;
    }
    
    private RecordHandle doInsert(final DataValueDescriptor[] array) throws StandardException {
        final Page pageForInsert = this.open_conglom.getContainer().getPageForInsert(0);
        if (pageForInsert != null) {
            final RecordHandle insert = pageForInsert.insert(array, null, (byte)((pageForInsert.recordCount() == 0) ? 8 : 1), 100);
            pageForInsert.unlatch();
            if (insert != null) {
                return insert;
            }
        }
        final Page pageForInsert2 = this.open_conglom.getContainer().getPageForInsert(1);
        if (pageForInsert2 != null) {
            final RecordHandle insert2 = pageForInsert2.insert(array, null, (byte)((pageForInsert2.recordCount() == 0) ? 8 : 1), 100);
            pageForInsert2.unlatch();
            if (insert2 != null) {
                return insert2;
            }
        }
        final Page addPage = this.open_conglom.getContainer().addPage();
        final RecordHandle insert3 = addPage.insert(array, null, (byte)8, 100);
        addPage.unlatch();
        return insert3;
    }
    
    protected long load(final TransactionManager transactionManager, final Heap heap, final boolean b, final RowLocationRetRowSource rowLocationRetRowSource) throws StandardException {
        long n = 0L;
        int n2 = 5;
        if (b) {
            n2 |= 0x2;
        }
        final OpenHeap openHeap = new OpenHeap();
        if (openHeap.init(null, heap, heap.format_ids, heap.collation_ids, transactionManager, transactionManager.getRawStoreXact(), false, n2, 7, transactionManager.getRawStoreXact().newLockingPolicy(2, 5, true), null) == null) {
            throw StandardException.newException("XSCH1.S", new Long(heap.getId().getContainerId()));
        }
        this.init(openHeap);
        Page page = openHeap.getContainer().addPage();
        final boolean needsRowLocation = rowLocationRetRowSource.needsRowLocation();
        HeapRowLocation heapRowLocation;
        if (needsRowLocation) {
            heapRowLocation = new HeapRowLocation();
        }
        else {
            heapRowLocation = null;
        }
        final FormatableBitSet validColumns = rowLocationRetRowSource.getValidColumns();
        try {
            DataValueDescriptor[] nextRowFromRowSource;
            while ((nextRowFromRowSource = rowLocationRetRowSource.getNextRowFromRowSource()) != null) {
                ++n;
                RecordHandle from;
                if ((from = page.insert(nextRowFromRowSource, validColumns, (byte)1, 100)) == null) {
                    page.unlatch();
                    page = openHeap.getContainer().addPage();
                    from = page.insert(nextRowFromRowSource, validColumns, (byte)8, 100);
                }
                if (needsRowLocation) {
                    heapRowLocation.setFrom(from);
                    rowLocationRetRowSource.rowLocation(heapRowLocation);
                }
            }
            page.unlatch();
            if (!heap.isTemporary()) {
                openHeap.getContainer().flushContainer();
            }
        }
        finally {
            this.close();
        }
        return n;
    }
    
    protected boolean lockRow(final RecordHandle recordHandle, final int n, final boolean b, final int n2) throws StandardException {
        final boolean b2 = (0x1 & n) != 0x0;
        final boolean b3 = (0x8 & n) != 0x0;
        boolean b6;
        if (b2 && !b3) {
            final boolean b4 = (0x2 & n) != 0x0;
            final boolean b5 = (0x4 & n) != 0x0;
            if (n2 == 1) {
                b6 = this.open_conglom.getContainer().getLockingPolicy().zeroDurationLockRecordForWrite(this.open_conglom.getRawTran(), recordHandle, b5, b);
            }
            else {
                b6 = this.open_conglom.getContainer().getLockingPolicy().lockRecordForWrite(this.open_conglom.getRawTran(), recordHandle, b4, b);
            }
        }
        else {
            b6 = this.open_conglom.getContainer().getLockingPolicy().lockRecordForRead(this.open_conglom.getRawTran(), this.open_conglom.getContainer(), recordHandle, b, b2);
        }
        return b6;
    }
    
    protected Page getUserPageNoWait(final long n) throws StandardException {
        return this.open_conglom.getContainer().getUserPageNoWait(n);
    }
    
    protected Page getUserPageWait(final long n) throws StandardException {
        return this.open_conglom.getContainer().getUserPageWait(n);
    }
    
    protected boolean lockRowAtSlotNoWaitExclusive(final RecordHandle recordHandle) throws StandardException {
        return this.open_conglom.getContainer().getLockingPolicy().lockRecordForWrite(this.open_conglom.getRawTran(), recordHandle, false, false);
    }
    
    protected void removePage(final Page page) throws StandardException {
        this.open_conglom.getContainer().removePage(page);
    }
    
    public int insert(final DataValueDescriptor[] array) throws StandardException {
        if (this.open_conglom.isClosed()) {
            if (!this.open_conglom.getHold()) {
                throw StandardException.newException("XSCH6.S", this.open_conglom.getConglomerate().getId());
            }
            this.open_conglom.reopen();
        }
        this.doInsert(array);
        return 0;
    }
    
    public void insertAndFetchLocation(final DataValueDescriptor[] array, final RowLocation rowLocation) throws StandardException {
        if (this.open_conglom.isClosed()) {
            if (!this.open_conglom.getHold()) {
                throw StandardException.newException("XSCH6.S", this.open_conglom.getConglomerate().getId());
            }
            this.open_conglom.reopen();
        }
        ((HeapRowLocation)rowLocation).setFrom(this.doInsert(array));
    }
    
    public boolean lockRow(final RowLocation rowLocation, final int n, final boolean b, final int n2) throws StandardException {
        return this.lockRow(((HeapRowLocation)rowLocation).getRecordHandle(this.open_conglom.getContainer()), n, b, n2);
    }
    
    public void unlockRowAfterRead(final RowLocation rowLocation, final boolean b, final boolean b2) throws StandardException {
        this.open_conglom.getContainer().getLockingPolicy().unlockRecordAfterRead(this.open_conglom.getRawTran(), this.open_conglom.getContainer(), ((HeapRowLocation)rowLocation).getRecordHandle(this.open_conglom.getContainer()), this.open_conglom.isForUpdate(), b2);
    }
    
    public boolean lockRow(final long n, final int n2, final int n3, final boolean b, final int n4) throws StandardException {
        return this.lockRow(this.open_conglom.getContainer().makeRecordHandle(n, n2), n3, b, n4);
    }
    
    public RowLocation newRowLocationTemplate() throws StandardException {
        if (this.open_conglom.isClosed()) {
            if (!this.open_conglom.getHold()) {
                throw StandardException.newException("XSCH6.S", this.open_conglom.getConglomerate().getId());
            }
            this.open_conglom.reopen();
        }
        return new HeapRowLocation();
    }
}
