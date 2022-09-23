// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.access.BackingStoreHashtable;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;

public class BTreeForwardScan extends BTreeScan
{
    protected void positionAtStartPosition(final BTreeRowPosition bTreeRowPosition) throws StandardException {
        this.positionAtStartForForwardScan(bTreeRowPosition);
    }
    
    protected int fetchRows(final BTreeRowPosition bTreeRowPosition, final DataValueDescriptor[][] array, final RowLocation[] array2, final BackingStoreHashtable backingStoreHashtable, long n, final int[] array3) throws StandardException {
        int n2 = 0;
        DataValueDescriptor[] get_row_for_export = null;
        if (n == -1L) {
            n = Long.MAX_VALUE;
        }
        if (this.scan_state == 2) {
            if (!this.reposition(bTreeRowPosition, true)) {}
        }
        else if (this.scan_state == 1) {
            this.positionAtStartPosition(bTreeRowPosition);
        }
        else if (this.scan_state == 5) {
            this.reopen();
            this.scan_state = 2;
            if (!this.reposition(bTreeRowPosition, true)) {}
        }
        else {
            if (this.scan_state != 4) {
                return 0;
            }
            this.reopen();
            this.positionAtStartForForwardScan(this.scan_position);
        }
    Label_0119:
        while (bTreeRowPosition.current_leaf != null) {
            while (bTreeRowPosition.current_slot + 1 < bTreeRowPosition.current_leaf.page.recordCount()) {
                if (bTreeRowPosition.current_rh != null) {
                    this.getLockingPolicy().unlockScanRecordAfterRead(bTreeRowPosition, this.init_forUpdate);
                    bTreeRowPosition.current_rh = null;
                }
                if (get_row_for_export == null) {
                    if (backingStoreHashtable == null) {
                        if (array[n2] == null) {
                            array[n2] = this.runtime_mem.get_row_for_export(this.getRawTran());
                        }
                        get_row_for_export = array[n2];
                    }
                    else {
                        get_row_for_export = this.runtime_mem.get_row_for_export(this.getRawTran());
                    }
                }
                ++bTreeRowPosition.current_slot;
                ++this.stat_numrows_visited;
                final RecordHandle fetchFromSlot = bTreeRowPosition.current_leaf.page.fetchFromSlot(null, bTreeRowPosition.current_slot, get_row_for_export, this.init_fetchDesc, true);
                bTreeRowPosition.current_rh_qualified = true;
                if (this.init_stopKeyValue != null) {
                    int compareIndexRowToKey = ControlRow.compareIndexRowToKey(get_row_for_export, this.init_stopKeyValue, get_row_for_export.length, 0, this.getConglomerate().ascDescInfo);
                    if (compareIndexRowToKey == 0 && this.init_stopSearchOperator == 1) {
                        compareIndexRowToKey = 1;
                    }
                    if (compareIndexRowToKey > 0) {
                        bTreeRowPosition.current_leaf.release();
                        bTreeRowPosition.current_leaf = null;
                        this.positionAtDoneScan(bTreeRowPosition);
                        return n2;
                    }
                }
                int i = this.getLockingPolicy().lockScanRow(this, bTreeRowPosition, this.init_lock_fetch_desc, bTreeRowPosition.current_lock_template, bTreeRowPosition.current_lock_row_loc, false, this.init_forUpdate, this.lock_operation) ? 0 : 1;
                bTreeRowPosition.current_rh = fetchFromSlot;
                while (i != 0) {
                    if (!this.reposition(bTreeRowPosition, false)) {
                        if (!this.reposition(bTreeRowPosition, true)) {
                            continue Label_0119;
                        }
                        continue Label_0119;
                    }
                    else {
                        i = 0;
                        if (!this.getConglomerate().isUnique()) {
                            continue;
                        }
                        bTreeRowPosition.current_leaf.page.fetchFromSlot(null, bTreeRowPosition.current_slot, get_row_for_export, this.init_fetchDesc, true);
                        i = (this.getLockingPolicy().lockScanRow(this, bTreeRowPosition, this.init_lock_fetch_desc, bTreeRowPosition.current_lock_template, bTreeRowPosition.current_lock_row_loc, false, this.init_forUpdate, this.lock_operation) ? 0 : 1);
                    }
                }
                if (bTreeRowPosition.current_leaf.page.isDeletedAtSlot(bTreeRowPosition.current_slot)) {
                    ++this.stat_numdeleted_rows_visited;
                    bTreeRowPosition.current_rh_qualified = false;
                }
                else if (this.init_qualifier != null) {
                    bTreeRowPosition.current_rh_qualified = this.process_qualifier(get_row_for_export);
                }
                if (bTreeRowPosition.current_rh_qualified) {
                    ++n2;
                    ++this.stat_numrows_qualified;
                    final boolean b = n <= n2;
                    if (b) {
                        this.savePositionAndReleasePage(get_row_for_export, this.init_fetchDesc.getValidColumnsArray());
                    }
                    if (backingStoreHashtable != null) {
                        if (backingStoreHashtable.putRow(false, get_row_for_export)) {
                            get_row_for_export = null;
                        }
                    }
                    else {
                        get_row_for_export = null;
                    }
                    if (b) {
                        return n2;
                    }
                    continue;
                }
            }
            this.positionAtNextPage(bTreeRowPosition);
            ++this.stat_numpages_visited;
        }
        this.positionAtDoneScan(bTreeRowPosition);
        --this.stat_numpages_visited;
        return n2;
    }
}
