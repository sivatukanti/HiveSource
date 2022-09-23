// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.RowChanger;
import java.sql.SQLWarning;
import java.sql.Timestamp;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.sql.execute.TargetResultSet;
import org.apache.derby.iapi.types.SQLLongint;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.Qualifier;
import java.util.Properties;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;
import java.util.Arrays;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class TemporaryRowHolderResultSet implements CursorResultSet, NoPutResultSet, Cloneable
{
    private ExecRow[] rowArray;
    private int numRowsOut;
    private ScanController scan;
    private TransactionController tc;
    private boolean isOpen;
    private boolean finished;
    private ExecRow currentRow;
    private ResultDescription resultDescription;
    private boolean isAppendable;
    private long positionIndexConglomId;
    private boolean isVirtualMemHeap;
    private boolean currRowFromMem;
    private TemporaryRowHolderImpl holder;
    ConglomerateController heapCC;
    private RowLocation baseRowLocation;
    DataValueDescriptor[] indexRow;
    ScanController indexsc;
    
    public TemporaryRowHolderResultSet(final TransactionController transactionController, final ExecRow[] array, final ResultDescription resultDescription, final boolean b, final TemporaryRowHolderImpl temporaryRowHolderImpl) {
        this(transactionController, array, resultDescription, b, false, 0L, temporaryRowHolderImpl);
    }
    
    public TemporaryRowHolderResultSet(final TransactionController tc, final ExecRow[] rowArray, final ResultDescription resultDescription, final boolean isVirtualMemHeap, final boolean isAppendable, final long positionIndexConglomId, final TemporaryRowHolderImpl holder) {
        this.isAppendable = false;
        this.tc = tc;
        this.rowArray = rowArray;
        this.resultDescription = resultDescription;
        this.numRowsOut = 0;
        this.isOpen = false;
        this.finished = false;
        this.isVirtualMemHeap = isVirtualMemHeap;
        this.isAppendable = isAppendable;
        this.positionIndexConglomId = positionIndexConglomId;
        this.holder = holder;
    }
    
    public void reset(final ExecRow[] rowArray) {
        this.rowArray = rowArray;
        this.numRowsOut = 0;
        this.isOpen = false;
        this.finished = false;
    }
    
    public void reStartScan(final long n, final long positionIndexConglomId) throws StandardException {
        if (this.isAppendable) {
            this.positionIndexConglomId = positionIndexConglomId;
            this.setupPositionBasedScan(this.numRowsOut);
        }
        else {
            --this.numRowsOut;
        }
    }
    
    private static int[] supersetofAllColumns(final int[] array, final int[] array2) {
        final int n = array.length + array2.length;
        final int[] array3 = new int[n];
        for (int i = 0; i < n; ++i) {
            array3[i] = -1;
        }
        for (int j = 0; j < array.length; ++j) {
            array3[j] = array[j];
        }
        int length = array.length;
        for (int k = 0; k < array2.length; ++k) {
            boolean b = false;
            for (int l = 0; l < length; ++l) {
                if (array3[l] == array2[k]) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                array3[length] = array2[k];
                ++length;
            }
        }
        final int[] shrinkArray = shrinkArray(array3);
        Arrays.sort(shrinkArray);
        return shrinkArray;
    }
    
    private static int[] shrinkArray(final int[] array) {
        int n = 0;
        final int length = array.length;
        for (int i = 0; i < length; ++i) {
            if (array[i] != -1) {
                ++n;
            }
        }
        if (n > 0) {
            final int[] array2 = new int[n];
            int n2 = 0;
            for (int j = 0; j < length; ++j) {
                if (array[j] != -1) {
                    array2[n2++] = array[j];
                }
            }
            return array2;
        }
        return null;
    }
    
    private static int[] justTheRequiredColumnsPositions(final int[] array) {
        int n = 0;
        final int length = array.length;
        for (int i = 0; i < length; ++i) {
            if (array[i] != -1) {
                ++n;
            }
        }
        if (n > 0) {
            final int[] array2 = new int[n];
            int n2 = 0;
            for (int j = 0; j < length; ++j) {
                if (array[j] != -1) {
                    array2[n2++] = j + 1;
                }
            }
            return array2;
        }
        return null;
    }
    
    public static TemporaryRowHolderResultSet getNewRSOnCurrentRow(final TriggerDescriptor triggerDescriptor, final Activation activation, final CursorResultSet set, final int[] array) throws StandardException {
        if (!activation.getLanguageConnectionContext().getDataDictionary().checkVersion(210, null)) {
            final TemporaryRowHolderImpl temporaryRowHolderImpl = new TemporaryRowHolderImpl(activation, null, set.getResultDescription());
            temporaryRowHolderImpl.insert(set.getCurrentRow());
            return (TemporaryRowHolderResultSet)temporaryRowHolderImpl.getResultSet();
        }
        final int[] referencedColsInTriggerAction = triggerDescriptor.getReferencedColsInTriggerAction();
        final int[] referencedCols = triggerDescriptor.getReferencedCols();
        TemporaryRowHolderImpl temporaryRowHolderImpl2;
        if (referencedCols != null && triggerDescriptor.isRowTrigger() && referencedColsInTriggerAction != null && referencedColsInTriggerAction.length != 0) {
            final int[] supersetofAllColumns = supersetofAllColumns(referencedCols, referencedColsInTriggerAction);
            final int length = supersetofAllColumns.length;
            final int[] array2 = new int[length];
            int[] justTheRequiredColumnsPositions;
            if (array != null) {
                justTheRequiredColumnsPositions = justTheRequiredColumnsPositions(array);
            }
            else {
                final int numberOfColumns = triggerDescriptor.getTableDescriptor().getNumberOfColumns();
                justTheRequiredColumnsPositions = new int[numberOfColumns];
                for (int i = 1; i <= numberOfColumns; ++i) {
                    justTheRequiredColumnsPositions[i - 1] = i;
                }
            }
            int j = 0;
            for (int k = 0; k < length; ++k) {
                while (j < justTheRequiredColumnsPositions.length) {
                    if (justTheRequiredColumnsPositions[j] == supersetofAllColumns[k]) {
                        array2[k] = j + 1;
                        break;
                    }
                    ++j;
                }
            }
            temporaryRowHolderImpl2 = new TemporaryRowHolderImpl(activation, null, activation.getLanguageConnectionContext().getLanguageFactory().getResultDescription(set.getResultDescription(), array2));
            final ExecRow valueRow = activation.getExecutionFactory().getValueRow(length);
            for (int l = 0; l < length; ++l) {
                valueRow.setColumn(l + 1, set.getCurrentRow().getColumn(array2[l]));
            }
            temporaryRowHolderImpl2.insert(valueRow);
        }
        else {
            temporaryRowHolderImpl2 = new TemporaryRowHolderImpl(activation, null, set.getResultDescription());
            temporaryRowHolderImpl2.insert(set.getCurrentRow());
        }
        return (TemporaryRowHolderResultSet)temporaryRowHolderImpl2.getResultSet();
    }
    
    public void markAsTopResultSet() {
    }
    
    public void openCore() throws StandardException {
        this.numRowsOut = 0;
        this.isOpen = true;
        this.currentRow = null;
        if (this.isAppendable) {
            this.setupPositionBasedScan(this.numRowsOut);
        }
    }
    
    public void reopenCore() throws StandardException {
        this.numRowsOut = 0;
        this.isOpen = true;
        this.currentRow = null;
        if (this.isAppendable) {
            this.setupPositionBasedScan(this.numRowsOut);
            return;
        }
        if (this.scan != null) {
            this.scan.reopenScan(null, 0, null, null, 0);
        }
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (!this.isOpen) {
            return null;
        }
        if (this.isAppendable) {
            return this.getNextAppendedRow();
        }
        if (this.isVirtualMemHeap && this.holder.lastArraySlot >= 0) {
            ++this.numRowsOut;
            this.currentRow = this.rowArray[this.holder.lastArraySlot];
            this.currRowFromMem = true;
            return this.currentRow;
        }
        if (this.numRowsOut++ <= this.holder.lastArraySlot) {
            return this.currentRow = this.rowArray[this.numRowsOut - 1];
        }
        if (this.holder.getTemporaryConglomId() == 0L) {
            return null;
        }
        if (this.scan == null) {
            this.scan = this.tc.openScan(this.holder.getTemporaryConglomId(), false, 0, 7, 5, null, null, 0, null, null, 0);
        }
        else if (this.isVirtualMemHeap && this.holder.state == 1) {
            this.holder.state = 2;
            this.scan.reopenScan(null, 0, null, null, 0);
        }
        if (this.scan.next()) {
            this.currentRow = this.rowArray[0].getNewNullRow();
            this.scan.fetch(this.currentRow.getRowArray());
            this.currRowFromMem = false;
            return this.currentRow;
        }
        return null;
    }
    
    public void deleteCurrentRow() throws StandardException {
        if (this.currRowFromMem) {
            if (this.holder.lastArraySlot > 0) {
                this.rowArray[this.holder.lastArraySlot] = null;
            }
            final TemporaryRowHolderImpl holder = this.holder;
            --holder.lastArraySlot;
        }
        else {
            if (this.baseRowLocation == null) {
                this.baseRowLocation = this.scan.newRowLocationTemplate();
            }
            this.scan.fetchLocation(this.baseRowLocation);
            if (this.heapCC == null) {
                this.heapCC = this.tc.openConglomerate(this.holder.getTemporaryConglomId(), false, 4, 7, 5);
            }
            this.heapCC.delete(this.baseRowLocation);
        }
    }
    
    private void setupPositionBasedScan(final long n) throws StandardException {
        if (this.holder.getTemporaryConglomId() == 0L) {
            return;
        }
        if (this.heapCC == null) {
            this.heapCC = this.tc.openConglomerate(this.holder.getTemporaryConglomId(), false, 0, 7, 5);
        }
        this.currentRow = this.rowArray[0].getNewNullRow();
        (this.indexRow = new DataValueDescriptor[2])[0] = new SQLLongint(n);
        this.indexRow[1] = this.heapCC.newRowLocationTemplate();
        final DataValueDescriptor[] array = { new SQLLongint(n) };
        if (this.indexsc == null) {
            this.indexsc = this.tc.openScan(this.positionIndexConglomId, false, 0, 7, 5, null, array, 1, null, null, -1);
        }
        else {
            this.indexsc.reopenScan(array, 1, null, null, -1);
        }
    }
    
    private ExecRow getNextAppendedRow() throws StandardException {
        if (this.indexsc == null) {
            return null;
        }
        if (!this.indexsc.fetchNext(this.indexRow)) {
            return null;
        }
        this.heapCC.fetch((RowLocation)this.indexRow[1], this.currentRow.getRowArray(), null);
        ++this.numRowsOut;
        return this.currentRow;
    }
    
    public int getPointOfAttachment() {
        return -1;
    }
    
    public int getScanIsolationLevel() {
        return 5;
    }
    
    public void setTargetResultSet(final TargetResultSet set) {
    }
    
    public void setNeedsRowLocation(final boolean b) {
    }
    
    public double getEstimatedRowCount() {
        return 0.0;
    }
    
    public int resultSetNumber() {
        return 0;
    }
    
    public void setCurrentRow(final ExecRow currentRow) {
        this.currentRow = currentRow;
    }
    
    public void clearCurrentRow() {
        this.currentRow = null;
    }
    
    public ExecRow getCurrentRow() throws StandardException {
        return this.currentRow;
    }
    
    public RowLocation getRowLocation() {
        return null;
    }
    
    public void close() throws StandardException {
        this.isOpen = false;
        this.numRowsOut = 0;
        this.currentRow = null;
        if (this.scan != null) {
            this.scan.close();
            this.scan = null;
        }
    }
    
    public boolean returnsRows() {
        return true;
    }
    
    public long modifiedRowCount() {
        return 0L;
    }
    
    public ResultDescription getResultDescription() {
        return this.resultDescription;
    }
    
    public void open() throws StandardException {
        this.openCore();
    }
    
    public ExecRow getAbsoluteRow(final int n) throws StandardException {
        return null;
    }
    
    public ExecRow getRelativeRow(final int n) throws StandardException {
        return null;
    }
    
    public ExecRow setBeforeFirstRow() throws StandardException {
        return null;
    }
    
    public ExecRow getFirstRow() throws StandardException {
        return null;
    }
    
    public ExecRow getNextRow() throws StandardException {
        return this.getNextRowCore();
    }
    
    public ExecRow getPreviousRow() throws StandardException {
        return null;
    }
    
    public ExecRow getLastRow() throws StandardException {
        return null;
    }
    
    public ExecRow setAfterLastRow() throws StandardException {
        return null;
    }
    
    public boolean checkRowPosition(final int n) {
        return false;
    }
    
    public int getRowNumber() {
        return 0;
    }
    
    public void cleanUp() throws StandardException {
        this.close();
    }
    
    public boolean isClosed() {
        return !this.isOpen;
    }
    
    public void finish() throws StandardException {
        this.finished = true;
        this.close();
    }
    
    public long getExecuteTime() {
        return 0L;
    }
    
    public ResultSet getAutoGeneratedKeysResultset() {
        return null;
    }
    
    public Timestamp getBeginExecutionTimestamp() {
        return null;
    }
    
    public Timestamp getEndExecutionTimestamp() {
        return null;
    }
    
    public long getTimeSpent(final int n) {
        return 0L;
    }
    
    public NoPutResultSet[] getSubqueryTrackingArray(final int n) {
        return null;
    }
    
    public String getCursorName() {
        return null;
    }
    
    public boolean requiresRelocking() {
        return false;
    }
    
    public DataValueDescriptor[] getNextRowFromRowSource() throws StandardException {
        return null;
    }
    
    public boolean needsToClone() {
        return false;
    }
    
    public FormatableBitSet getValidColumns() {
        return null;
    }
    
    public void closeRowSource() {
    }
    
    public boolean needsRowLocation() {
        return false;
    }
    
    public void rowLocation(final RowLocation rowLocation) throws StandardException {
    }
    
    public void positionScanAtRowLocation(final RowLocation rowLocation) throws StandardException {
    }
    
    public boolean isForUpdate() {
        return false;
    }
    
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        }
        catch (CloneNotSupportedException ex) {}
        return clone;
    }
    
    public void addWarning(final SQLWarning sqlWarning) {
        this.getActivation().addWarning(sqlWarning);
    }
    
    public SQLWarning getWarnings() {
        return null;
    }
    
    public void updateRow(final ExecRow execRow, final RowChanger rowChanger) throws StandardException {
    }
    
    public void markRowAsDeleted() throws StandardException {
    }
    
    public final Activation getActivation() {
        return this.holder.activation;
    }
}
