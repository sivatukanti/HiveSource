// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.conglomerate;

import org.apache.derby.iapi.store.access.ScanInfo;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.access.BackingStoreHashtable;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.access.RowUtil;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.conglomerate.ScanManager;

public abstract class GenericScanController extends GenericController implements ScanManager
{
    public static final int SCAN_INIT = 1;
    public static final int SCAN_INPROGRESS = 2;
    public static final int SCAN_DONE = 3;
    public static final int SCAN_HOLD_INIT = 4;
    public static final int SCAN_HOLD_INPROGRESS = 5;
    private FormatableBitSet init_scanColumnList;
    private DataValueDescriptor[] init_startKeyValue;
    private int init_startSearchOperator;
    private Qualifier[][] init_qualifier;
    private DataValueDescriptor[] init_stopKeyValue;
    private int init_stopSearchOperator;
    private FetchDescriptor init_fetchDesc;
    private int scan_state;
    protected boolean rowLocationsInvalidated;
    private long reusableRecordIdSequenceNumber;
    protected RowPosition scan_position;
    protected int stat_numpages_visited;
    protected int stat_numrows_visited;
    protected int stat_numrows_qualified;
    
    public GenericScanController() {
        this.rowLocationsInvalidated = false;
        this.reusableRecordIdSequenceNumber = 0L;
        this.stat_numpages_visited = 0;
        this.stat_numrows_visited = 0;
        this.stat_numrows_qualified = 0;
    }
    
    private final void repositionScanForUpateOper() throws StandardException {
        if (this.scan_state != 2) {
            throw StandardException.newException("XSAM5.S");
        }
        if (!this.open_conglom.latchPage(this.scan_position)) {
            throw StandardException.newException("XSAM6.S", this.open_conglom.getContainer().getId(), new Long(this.scan_position.current_rh.getPageNumber()), new Long(this.scan_position.current_rh.getId()));
        }
        if (this.open_conglom.isUseUpdateLocks()) {
            this.open_conglom.lockPositionForWrite(this.scan_position, true);
        }
    }
    
    protected void positionAtInitScan(final DataValueDescriptor[] init_startKeyValue, final int init_startSearchOperator, Qualifier[][] init_qualifier, final DataValueDescriptor[] init_stopKeyValue, final int init_stopSearchOperator, final RowPosition rowPosition) throws StandardException {
        this.init_startKeyValue = init_startKeyValue;
        if (RowUtil.isRowEmpty(this.init_startKeyValue)) {
            this.init_startKeyValue = null;
        }
        this.init_startSearchOperator = init_startSearchOperator;
        if (init_qualifier != null && init_qualifier.length == 0) {
            init_qualifier = null;
        }
        this.init_qualifier = init_qualifier;
        this.init_fetchDesc = new FetchDescriptor(this.open_conglom.getRuntimeMem().get_scratch_row(this.open_conglom.getRawTran()).length, this.init_scanColumnList, this.init_qualifier);
        this.init_stopKeyValue = init_stopKeyValue;
        if (RowUtil.isRowEmpty(this.init_stopKeyValue)) {
            this.init_stopKeyValue = null;
        }
        this.init_stopSearchOperator = init_stopSearchOperator;
        rowPosition.init();
        this.scan_state = 1;
    }
    
    protected void positionAtResumeScan(final RowPosition rowPosition) throws StandardException {
        this.open_conglom.latchPageAndRepositionScan(this.scan_position);
    }
    
    protected void positionAtStartForForwardScan(final RowPosition rowPosition) throws StandardException {
        if (rowPosition.current_rh == null) {
            rowPosition.current_page = this.open_conglom.getContainer().getFirstPage();
            rowPosition.current_slot = 0;
        }
        else {
            this.open_conglom.latchPageAndRepositionScan(rowPosition);
            --rowPosition.current_slot;
        }
        rowPosition.current_rh = null;
        this.stat_numpages_visited = 1;
        this.scan_state = 2;
    }
    
    protected void positionAtNextPage(final RowPosition rowPosition) throws StandardException {
        if (rowPosition.current_page != null) {
            final long pageNumber = rowPosition.current_page.getPageNumber();
            rowPosition.unlatch();
            rowPosition.current_page = this.open_conglom.getContainer().getNextPage(pageNumber);
            rowPosition.current_slot = -1;
        }
    }
    
    protected void positionAtDoneScan(final RowPosition rowPosition) throws StandardException {
        rowPosition.unlatch();
        if (this.scan_position.current_rh != null) {
            this.open_conglom.unlockPositionAfterRead(this.scan_position);
            this.scan_position.current_rh = null;
        }
        this.scan_state = 3;
    }
    
    public void reopenScanByRowLocation(final RowLocation rowLocation, final Qualifier[][] array) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    protected RowPosition allocateScanPosition() throws StandardException {
        return new RowPosition();
    }
    
    protected int fetchRows(final DataValueDescriptor[][] array, final RowLocation[] array2, final BackingStoreHashtable backingStoreHashtable, long n, final int[] array3) throws StandardException {
        int n2 = 0;
        DataValueDescriptor[] get_row_for_export = null;
        if (n == -1L) {
            n = Long.MAX_VALUE;
        }
        if (this.scan_state == 2) {
            this.positionAtResumeScan(this.scan_position);
        }
        else if (this.scan_state == 1) {
            this.positionAtStartForForwardScan(this.scan_position);
        }
        else if (this.scan_state == 5) {
            this.reopenAfterEndTransaction();
            this.open_conglom.latchPageAndRepositionScan(this.scan_position);
            this.scan_state = 2;
        }
        else {
            if (this.scan_state != 4) {
                return 0;
            }
            this.reopenAfterEndTransaction();
            this.positionAtStartForForwardScan(this.scan_position);
        }
        while (this.scan_position.current_page != null) {
            while (this.scan_position.current_slot + 1 < this.scan_position.current_page.recordCount()) {
                if (this.scan_position.current_rh != null) {
                    this.open_conglom.unlockPositionAfterRead(this.scan_position);
                }
                if (get_row_for_export == null) {
                    if (backingStoreHashtable == null) {
                        if (array[n2] == null) {
                            array[n2] = this.open_conglom.getRuntimeMem().get_row_for_export(this.open_conglom.getRawTran());
                        }
                        get_row_for_export = array[n2];
                    }
                    else {
                        get_row_for_export = this.open_conglom.getRuntimeMem().get_row_for_export(this.open_conglom.getRawTran());
                    }
                }
                this.scan_position.positionAtNextSlot();
                if (!this.open_conglom.lockPositionForRead(this.scan_position, null, true, true)) {
                    if (this.scan_position.current_page == null) {
                        break;
                    }
                    if (this.scan_position.current_slot == -1) {
                        continue;
                    }
                }
                ++this.stat_numrows_visited;
                this.scan_position.current_rh_qualified = (this.scan_position.current_page.fetchFromSlot(this.scan_position.current_rh, this.scan_position.current_slot, get_row_for_export, this.init_fetchDesc, false) != null);
                if (this.scan_position.current_rh_qualified) {
                    ++n2;
                    ++this.stat_numrows_qualified;
                    if (backingStoreHashtable == null) {
                        if (array2 != null) {
                            this.setRowLocationArray(array2, n2 - 1, this.scan_position);
                        }
                        get_row_for_export = null;
                    }
                    else if (backingStoreHashtable.putRow(false, get_row_for_export)) {
                        get_row_for_export = null;
                    }
                    if (n <= n2) {
                        this.scan_position.unlatch();
                        return n2;
                    }
                    continue;
                }
            }
            this.positionAtNextPage(this.scan_position);
            ++this.stat_numpages_visited;
        }
        this.positionAtDoneScan(this.scan_position);
        --this.stat_numpages_visited;
        return n2;
    }
    
    protected void reopenScanByRecordHandle(final RecordHandle current_rh, final Qualifier[][] array) throws StandardException {
        this.scan_state = (this.open_conglom.getHold() ? 4 : 1);
        this.scan_position.current_rh = current_rh;
    }
    
    protected abstract void setRowLocationArray(final RowLocation[] p0, final int p1, final RowPosition p2) throws StandardException;
    
    public void init(final OpenConglomerate openConglomerate, final FormatableBitSet init_scanColumnList, final DataValueDescriptor[] array, final int n, final Qualifier[][] array2, final DataValueDescriptor[] array3, final int n2) throws StandardException {
        super.init(openConglomerate);
        this.scan_position = openConglomerate.getRuntimeMem().get_scratch_row_position();
        this.init_scanColumnList = init_scanColumnList;
        this.positionAtInitScan(array, n, array2, array3, n2, this.scan_position);
        this.reusableRecordIdSequenceNumber = openConglomerate.getContainer().getReusableRecordIdSequenceNumber();
    }
    
    public final int getNumPagesVisited() {
        return this.stat_numpages_visited;
    }
    
    public final int getNumRowsVisited() {
        return this.stat_numrows_visited;
    }
    
    public final int getNumRowsQualified() {
        return this.stat_numrows_qualified;
    }
    
    public final FormatableBitSet getScanColumnList() {
        return this.init_scanColumnList;
    }
    
    public final DataValueDescriptor[] getStartKeyValue() {
        return this.init_startKeyValue;
    }
    
    public final int getStartSearchOperator() {
        return this.init_startSearchOperator;
    }
    
    public final DataValueDescriptor[] getStopKeyValue() {
        return this.init_stopKeyValue;
    }
    
    public final int getStopSearchOperator() {
        return this.init_stopSearchOperator;
    }
    
    public final Qualifier[][] getQualifier() {
        return this.init_qualifier;
    }
    
    public final int getScanState() {
        return this.scan_state;
    }
    
    public final void setScanState(final int scan_state) {
        this.scan_state = scan_state;
    }
    
    public final RowPosition getScanPosition() {
        return this.scan_position;
    }
    
    public final void setScanPosition(final RowPosition scan_position) {
        this.scan_position = scan_position;
    }
    
    private void closeScan() throws StandardException {
        super.close();
        if (this.open_conglom.getXactMgr() != null) {
            this.open_conglom.getXactMgr().closeMe(this);
        }
        this.init_qualifier = null;
        this.init_scanColumnList = null;
        this.init_startKeyValue = null;
        this.init_stopKeyValue = null;
    }
    
    public void close() throws StandardException {
        this.positionAtDoneScan(this.scan_position);
        this.closeScan();
    }
    
    protected final boolean reopenAfterEndTransaction() throws StandardException {
        if (!this.open_conglom.getHold()) {
            return false;
        }
        final ContainerHandle reopen = this.open_conglom.reopen();
        switch (this.scan_state) {
            case 2:
            case 3:
            case 5: {
                if (reopen.getReusableRecordIdSequenceNumber() != this.reusableRecordIdSequenceNumber) {
                    this.rowLocationsInvalidated = true;
                    break;
                }
                break;
            }
            case 1:
            case 4: {
                this.reusableRecordIdSequenceNumber = reopen.getReusableRecordIdSequenceNumber();
                break;
            }
        }
        return true;
    }
    
    public boolean closeForEndTransaction(final boolean b) throws StandardException {
        if (!this.open_conglom.getHold() || b) {
            this.scan_state = 3;
            this.closeScan();
            return true;
        }
        super.close();
        if (this.scan_state == 2) {
            this.scan_state = 5;
        }
        else if (this.scan_state == 1) {
            this.scan_state = 4;
        }
        return false;
    }
    
    public boolean delete() throws StandardException {
        this.repositionScanForUpateOper();
        boolean b = true;
        if (this.scan_position.current_page.isDeletedAtSlot(this.scan_position.current_slot)) {
            b = false;
        }
        else {
            this.scan_position.current_page.deleteAtSlot(this.scan_position.current_slot, true, null);
            if (this.scan_position.current_page.nonDeletedRecordCount() == 0) {
                this.queueDeletePostCommitWork(this.scan_position);
            }
        }
        this.scan_position.unlatch();
        return b;
    }
    
    public void didNotQualify() throws StandardException {
    }
    
    public void fetchSet(final long n, final int[] array, final BackingStoreHashtable backingStoreHashtable) throws StandardException {
        this.fetchRows(null, null, backingStoreHashtable, n, array);
    }
    
    public void reopenScan(final DataValueDescriptor[] array, final int n, final Qualifier[][] array2, final DataValueDescriptor[] array3, final int n2) throws StandardException {
        this.scan_state = (this.open_conglom.getHold() ? 4 : 1);
        this.scan_position.current_rh = null;
    }
    
    public boolean replace(final DataValueDescriptor[] array, final FormatableBitSet set) throws StandardException {
        this.repositionScanForUpateOper();
        final Page current_page = this.scan_position.current_page;
        final int current_slot = this.scan_position.current_slot;
        boolean b;
        if (current_page.isDeletedAtSlot(current_slot)) {
            b = false;
        }
        else {
            current_page.updateAtSlot(current_slot, array, set);
            b = true;
        }
        this.scan_position.unlatch();
        return b;
    }
    
    public boolean doesCurrentPositionQualify() throws StandardException {
        if (this.scan_state != 2) {
            throw StandardException.newException("XSAM5.S");
        }
        if (!this.open_conglom.latchPage(this.scan_position)) {
            return false;
        }
        final boolean b = this.scan_position.current_page.fetchFromSlot(this.scan_position.current_rh, this.scan_position.current_slot, this.open_conglom.getRuntimeMem().get_scratch_row(this.open_conglom.getRawTran()), this.init_fetchDesc, false) != null;
        this.scan_position.unlatch();
        return b;
    }
    
    public void fetchWithoutQualify(final DataValueDescriptor[] array) throws StandardException {
        this.fetch(array, false);
    }
    
    public boolean isHeldAfterCommit() throws StandardException {
        return this.scan_state == 4 || this.scan_state == 5;
    }
    
    public void fetch(final DataValueDescriptor[] array) throws StandardException {
        this.fetch(array, true);
    }
    
    private void fetch(final DataValueDescriptor[] array, final boolean b) throws StandardException {
        if (this.scan_state != 2) {
            throw StandardException.newException("XSAM5.S");
        }
        if (!this.open_conglom.latchPage(this.scan_position)) {
            throw StandardException.newException("XSAM6.S", this.open_conglom.getContainer().getId(), new Long(this.scan_position.current_rh.getPageNumber()), new Long(this.scan_position.current_rh.getId()));
        }
        final RecordHandle fetchFromSlot = this.scan_position.current_page.fetchFromSlot(this.scan_position.current_rh, this.scan_position.current_slot, array, b ? this.init_fetchDesc : null, false);
        this.scan_position.unlatch();
        if (fetchFromSlot == null) {
            throw StandardException.newException("XSAM6.S", this.open_conglom.getContainer().getId(), new Long(this.scan_position.current_rh.getPageNumber()), new Long(this.scan_position.current_rh.getId()));
        }
    }
    
    public void fetchLocation(final RowLocation rowLocation) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public ScanInfo getScanInfo() throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public boolean isCurrentPositionDeleted() throws StandardException {
        if (this.scan_state != 2) {
            throw StandardException.newException("XSAM5.S");
        }
        if (!this.open_conglom.latchPage(this.scan_position)) {
            return true;
        }
        final boolean deletedAtSlot = this.scan_position.current_page.isDeletedAtSlot(this.scan_position.current_slot);
        this.scan_position.unlatch();
        return deletedAtSlot;
    }
}
