// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.heap;

import org.apache.derby.impl.store.access.conglomerate.RowPosition;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;

class HeapCompressScan extends HeapScan
{
    private long pagenum_to_start_moving_rows;
    
    public HeapCompressScan() {
        this.pagenum_to_start_moving_rows = -1L;
    }
    
    public int fetchNextGroup(final DataValueDescriptor[][] array, final RowLocation[] array2, final RowLocation[] array3) throws StandardException {
        return this.fetchRowsForCompress(array, array2, array3);
    }
    
    private int fetchRowsForCompress(final DataValueDescriptor[][] array, final RowLocation[] array2, final RowLocation[] array3) throws StandardException {
        int n = 0;
        Object[] array4 = null;
        final int length = array.length;
        if (this.getScanState() == 2) {
            this.positionAtResumeScan(this.scan_position);
        }
        else if (this.getScanState() == 1) {
            this.pagenum_to_start_moving_rows = this.open_conglom.getContainer().getSpaceInfo().getNumAllocatedPages();
            this.positionAtStartForForwardScan(this.scan_position);
        }
        else if (this.getScanState() == 5) {
            this.reopenAfterEndTransaction();
            this.open_conglom.latchPageAndRepositionScan(this.scan_position);
            this.setScanState(2);
        }
        else {
            if (this.getScanState() != 4) {
                return 0;
            }
            this.reopenAfterEndTransaction();
            this.positionAtStartForForwardScan(this.scan_position);
        }
        while (this.scan_position.current_page != null) {
            while (this.scan_position.current_slot + 1 < this.scan_position.current_page.recordCount()) {
                if (array4 == null) {
                    if (array[n] == null) {
                        array[n] = this.open_conglom.getRuntimeMem().get_row_for_export(this.open_conglom.getRawTran());
                    }
                    array4 = array[n];
                }
                this.scan_position.positionAtNextSlot();
                final int current_slot = this.scan_position.current_slot;
                ++this.stat_numrows_visited;
                if (this.scan_position.current_page.isDeletedAtSlot(this.scan_position.current_slot)) {
                    this.scan_position.current_page.purgeAtSlot(this.scan_position.current_slot, 1, false);
                    this.scan_position.positionAtPrevSlot();
                }
                else {
                    if (this.scan_position.current_page.getPageNumber() > this.pagenum_to_start_moving_rows) {
                        final RecordHandle[] array5 = { null };
                        final RecordHandle[] array6 = { null };
                        final long[] array7 = { 0L };
                        if (this.scan_position.current_page.moveRecordForCompressAtSlot(this.scan_position.current_slot, array4, array5, array6) == 1) {
                            this.scan_position.positionAtPrevSlot();
                            ++n;
                            ++this.stat_numrows_qualified;
                            this.setRowLocationArray(array2, n - 1, array5[0]);
                            this.setRowLocationArray(array3, n - 1, array6[0]);
                            array4 = null;
                        }
                    }
                    if (n >= length) {
                        this.scan_position.current_rh = this.scan_position.current_page.getRecordHandleAtSlot(current_slot);
                        this.scan_position.unlatch();
                        return n;
                    }
                    continue;
                }
            }
            ++this.stat_numpages_visited;
            if (this.scan_position.current_page.recordCount() == 0) {
                this.scan_position.current_pageno = this.scan_position.current_page.getPageNumber();
                this.open_conglom.getContainer().removePage(this.scan_position.current_page);
                this.scan_position.current_page = null;
            }
            else {
                this.positionAfterThisPage(this.scan_position);
                this.scan_position.unlatch();
            }
            if (n > 0) {
                return n;
            }
            this.positionAtResumeScan(this.scan_position);
        }
        this.positionAtDoneScan(this.scan_position);
        --this.stat_numpages_visited;
        return n;
    }
    
    protected void positionAtResumeScan(final RowPosition rowPosition) throws StandardException {
        this.open_conglom.latchPageAndRepositionScan(this.scan_position);
    }
    
    protected void positionAtStartForForwardScan(final RowPosition rowPosition) throws StandardException {
        if (rowPosition.current_rh == null) {
            rowPosition.current_page = this.open_conglom.getContainer().getNextPage(1L);
            rowPosition.current_slot = -1;
        }
        else {
            this.open_conglom.latchPageAndRepositionScan(rowPosition);
            --rowPosition.current_slot;
        }
        rowPosition.current_rh = null;
        this.stat_numpages_visited = 1;
        this.setScanState(2);
    }
    
    private void positionAfterThisPage(final RowPosition rowPosition) throws StandardException {
        rowPosition.current_rh = null;
        rowPosition.current_pageno = rowPosition.current_page.getPageNumber();
    }
}
