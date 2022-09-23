// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.access.BackingStoreHashtable;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.Page;

public class BTreeMaxScan extends BTreeScan
{
    private boolean moveToLeftSibling() throws StandardException {
        try {
            this.positionAtPreviousPage();
            return true;
        }
        catch (WaitError waitError) {
            final long getleftSiblingPageNumber = this.scan_position.current_leaf.getleftSiblingPageNumber();
            if (BTreeScan.isEmpty(this.scan_position.current_leaf.getPage())) {
                this.scan_position.current_leaf.release();
                this.scan_position.init();
            }
            else {
                this.scan_position.current_slot = 1;
                this.savePositionAndReleasePage();
            }
            final Page page = this.container.getPage(getleftSiblingPageNumber);
            if (page != null) {
                page.unlatch();
            }
            return false;
        }
    }
    
    protected int fetchRows(final BTreeRowPosition bTreeRowPosition, final DataValueDescriptor[][] array, final RowLocation[] array2, final BackingStoreHashtable backingStoreHashtable, final long n, final int[] array3) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    protected void positionAtStartPosition(final BTreeRowPosition bTreeRowPosition) throws StandardException {
        while (true) {
            final ControlRow value = ControlRow.get(this, 1L);
            this.stat_numpages_visited += value.getLevel() + 1;
            if (this.init_startKeyValue != null) {
                throw StandardException.newException("XSCB3.S");
            }
            bTreeRowPosition.current_leaf = (LeafControlRow)value.searchRight(this);
            bTreeRowPosition.current_slot = bTreeRowPosition.current_leaf.page.recordCount();
            --bTreeRowPosition.current_slot;
            final boolean b = !this.getLockingPolicy().lockScanRow(this, bTreeRowPosition, this.init_lock_fetch_desc, bTreeRowPosition.current_lock_template, bTreeRowPosition.current_lock_row_loc, false, this.init_forUpdate, this.lock_operation);
            ++bTreeRowPosition.current_slot;
            if (!b) {
                this.scan_state = 2;
                return;
            }
            bTreeRowPosition.init();
        }
    }
    
    public boolean fetchMax(final DataValueDescriptor[] array) throws StandardException {
        final BTreeRowPosition scan_position = this.scan_position;
        int n = 0;
        if (this.scan_state == 2) {
            if (!this.reposition(this.scan_position, true)) {}
        }
        else {
            if (this.scan_state != 1) {
                return false;
            }
            this.positionAtStartPosition(this.scan_position);
        }
        boolean b = false;
        while (!b && scan_position.current_leaf != null) {
            if (scan_position.current_slot <= 1) {
                if (this.moveToLeftSibling()) {
                    continue;
                }
                if (scan_position.current_positionKey == null) {
                    this.scan_state = 1;
                    this.positionAtStartPosition(scan_position);
                }
                else {
                    if (this.reposition(scan_position, false)) {
                        continue;
                    }
                    if (!this.reposition(scan_position, true)) {}
                    final BTreeRowPosition bTreeRowPosition = scan_position;
                    ++bTreeRowPosition.current_slot;
                }
            }
            else {
                final BTreeRowPosition bTreeRowPosition2 = scan_position;
                --bTreeRowPosition2.current_slot;
                while (scan_position.current_slot > 0) {
                    ++this.stat_numrows_visited;
                    final RecordHandle fetchFromSlot = scan_position.current_leaf.page.fetchFromSlot(null, scan_position.current_slot, array, this.init_fetchDesc, true);
                    final boolean b2 = !this.getLockingPolicy().lockScanRow(this, scan_position, this.init_lock_fetch_desc, scan_position.current_lock_template, scan_position.current_lock_row_loc, false, this.init_forUpdate, this.lock_operation);
                    scan_position.current_rh = fetchFromSlot;
                    if (b2 && !this.reposition(scan_position, false)) {
                        if (!this.reposition(scan_position, true)) {}
                        final BTreeRowPosition bTreeRowPosition3 = scan_position;
                        ++bTreeRowPosition3.current_slot;
                        break;
                    }
                    if (scan_position.current_leaf.page.isDeletedAtSlot(scan_position.current_slot)) {
                        ++this.stat_numdeleted_rows_visited;
                        scan_position.current_rh_qualified = false;
                    }
                    else if (array[0].isNull()) {
                        scan_position.current_rh_qualified = false;
                    }
                    else {
                        scan_position.current_rh_qualified = true;
                    }
                    if (scan_position.current_rh_qualified) {
                        ++n;
                        ++this.stat_numrows_qualified;
                        scan_position.current_slot = -1;
                        b = true;
                        break;
                    }
                    final BTreeRowPosition bTreeRowPosition4 = scan_position;
                    --bTreeRowPosition4.current_slot;
                }
            }
        }
        if (scan_position.current_leaf != null) {
            scan_position.current_leaf.release();
            scan_position.current_leaf = null;
        }
        this.positionAtDoneScan(this.scan_position);
        return b;
    }
}
