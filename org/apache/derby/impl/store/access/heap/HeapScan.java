// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.heap;

import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.store.access.ScanInfo;
import org.apache.derby.iapi.store.access.BackingStoreHashtable;
import org.apache.derby.iapi.store.access.RowUtil;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.impl.store.access.conglomerate.RowPosition;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.conglomerate.ScanManager;
import org.apache.derby.impl.store.access.conglomerate.GenericScanController;

class HeapScan extends GenericScanController implements ScanManager
{
    private DataValueDescriptor[][] fetchNext_one_slot_array;
    
    public HeapScan() {
        this.fetchNext_one_slot_array = new DataValueDescriptor[1][];
    }
    
    protected void queueDeletePostCommitWork(final RowPosition rowPosition) throws StandardException {
        final TransactionManager xactMgr = this.open_conglom.getXactMgr();
        xactMgr.addPostCommitWork(new HeapPostCommit(xactMgr.getAccessManager(), (Heap)this.open_conglom.getConglomerate(), rowPosition.current_page.getPageNumber()));
    }
    
    protected void setRowLocationArray(final RowLocation[] array, final int n, final RowPosition rowPosition) throws StandardException {
        if (array[n] == null) {
            array[n] = new HeapRowLocation(rowPosition.current_rh);
        }
        else {
            ((HeapRowLocation)array[n]).setFrom(rowPosition.current_rh);
        }
    }
    
    protected void setRowLocationArray(final RowLocation[] array, final int n, final RecordHandle from) throws StandardException {
        if (array[n] == null) {
            array[n] = new HeapRowLocation(from);
        }
        else {
            ((HeapRowLocation)array[n]).setFrom(from);
        }
    }
    
    private boolean reopenScanByRecordHandleAndSetLocks(final RecordHandle current_rh) throws StandardException {
        if (current_rh == null) {
            return false;
        }
        if (this.scan_position.current_rh != null) {
            this.open_conglom.unlockPositionAfterRead(this.scan_position);
        }
        this.scan_position.current_rh = current_rh;
        this.scan_position.current_rh_qualified = false;
        final boolean latchPageAndRepositionScan = this.open_conglom.latchPageAndRepositionScan(this.scan_position);
        if (!latchPageAndRepositionScan) {
            this.setScanState(2);
            this.open_conglom.lockPositionForRead(this.scan_position, null, true, true);
        }
        this.scan_position.unlatch();
        return !latchPageAndRepositionScan;
    }
    
    public boolean fetchNext(final DataValueDescriptor[] array) throws StandardException {
        if (array == null) {
            this.fetchNext_one_slot_array[0] = RowUtil.EMPTY_ROW;
        }
        else {
            this.fetchNext_one_slot_array[0] = array;
        }
        return this.fetchRows(this.fetchNext_one_slot_array, null, null, 1L, null) == 1;
    }
    
    public boolean next() throws StandardException {
        this.fetchNext_one_slot_array[0] = this.open_conglom.getRuntimeMem().get_scratch_row(this.open_conglom.getRawTran());
        return this.fetchRows(this.fetchNext_one_slot_array, null, null, 1L, null) == 1;
    }
    
    public boolean positionAtRowLocation(final RowLocation rowLocation) throws StandardException {
        if (this.open_conglom.isClosed() && !this.rowLocationsInvalidated) {
            this.reopenAfterEndTransaction();
        }
        return !this.rowLocationsInvalidated && this.reopenScanByRecordHandleAndSetLocks(((HeapRowLocation)rowLocation).getRecordHandle(this.open_conglom.getContainer()));
    }
    
    public void fetchLocation(final RowLocation rowLocation) throws StandardException {
        if (this.open_conglom.getContainer() == null || this.scan_position.current_rh == null) {
            throw StandardException.newException("XSCH7.S");
        }
        ((HeapRowLocation)rowLocation).setFrom(this.scan_position.current_rh);
    }
    
    public int fetchNextGroup(final DataValueDescriptor[][] array, final RowLocation[] array2) throws StandardException {
        return this.fetchRows(array, array2, null, array.length, null);
    }
    
    public int fetchNextGroup(final DataValueDescriptor[][] array, final RowLocation[] array2, final RowLocation[] array3) throws StandardException {
        throw StandardException.newException("XSCH8.S");
    }
    
    public ScanInfo getScanInfo() throws StandardException {
        return new HeapScanInfo(this);
    }
    
    public void reopenScanByRowLocation(final RowLocation rowLocation, final Qualifier[][] array) throws StandardException {
        this.reopenScanByRecordHandle(((HeapRowLocation)rowLocation).getRecordHandle(this.open_conglom.getContainer()), array);
    }
}
