// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.RowChanger;
import org.apache.derby.iapi.types.SQLBoolean;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.access.RowSource;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.types.SQLInteger;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.store.access.BackingStoreHashtable;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

public class ScrollInsensitiveResultSet extends NoPutResultSetImpl implements CursorResultSet
{
    public NoPutResultSet source;
    private int sourceRowWidth;
    private BackingStoreHashtable ht;
    private ExecRow resultRow;
    private int positionInSource;
    private int currentPosition;
    private int lastPosition;
    private boolean seenFirst;
    private boolean seenLast;
    private boolean beforeFirst;
    private boolean afterLast;
    public int numFromHashTable;
    public int numToHashTable;
    private long maxRows;
    private boolean keepAfterCommit;
    private int extraColumns;
    private SQLInteger positionInHashTable;
    private CursorResultSet target;
    private boolean needsRepositioning;
    private static final int POS_ROWLOCATION = 1;
    private static final int POS_ROWDELETED = 2;
    private static final int POS_ROWUPDATED = 3;
    private static final int LAST_EXTRA_COLUMN = 3;
    
    public ScrollInsensitiveResultSet(final NoPutResultSet source, final Activation activation, final int n, final int sourceRowWidth, final double n2, final double n3) throws StandardException {
        super(activation, n, n2, n3);
        this.beforeFirst = true;
        this.source = source;
        this.sourceRowWidth = sourceRowWidth;
        this.keepAfterCommit = activation.getResultSetHoldability();
        this.maxRows = activation.getMaxRows();
        this.positionInHashTable = new SQLInteger();
        this.needsRepositioning = false;
        if (this.isForUpdate()) {
            this.target = ((CursorActivation)activation).getTargetResultSet();
            this.extraColumns = 4;
        }
        else {
            this.target = null;
            this.extraColumns = 1;
        }
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.source.openCore();
        this.isOpen = true;
        ++this.numOpens;
        this.ht = new BackingStoreHashtable(this.getTransactionController(), null, new int[] { 0 }, false, -1L, -1L, -1, -1.0f, false, this.keepAfterCommit);
        this.lastPosition = 0;
        this.needsRepositioning = false;
        this.numFromHashTable = 0;
        this.numToHashTable = 0;
        this.positionInSource = 0;
        this.seenFirst = false;
        this.seenLast = false;
        this.maxRows = this.activation.getMaxRows();
        this.openTime += this.getElapsedMillis(this.beginTime);
        this.setBeforeFirstRow();
    }
    
    public void reopenCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.setBeforeFirstRow();
    }
    
    public ExecRow getAbsoluteRow(final int n) throws StandardException {
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "absolute");
        }
        this.attachStatementContext();
        if (n == 0) {
            this.setBeforeFirstRow();
            return null;
        }
        if (this.seenLast && n > this.lastPosition) {
            return this.setAfterLastRow();
        }
        if (n > 0) {
            if (n <= this.positionInSource) {
                return this.getRowFromHashTable(n);
            }
            int n2 = n - this.positionInSource;
            ExecRow currentRow = null;
            while (n2 > 0 && (currentRow = this.getNextRowFromSource()) != null) {
                --n2;
            }
            if (currentRow != null) {
                currentRow = this.getRowFromHashTable(n);
            }
            return this.currentRow = currentRow;
        }
        else {
            if (n >= 0) {
                return this.currentRow = null;
            }
            if (!this.seenLast) {
                this.getLastRow();
            }
            final int n3 = this.lastPosition + 1;
            if (n3 + n > 0) {
                return this.getRowFromHashTable(n3 + n);
            }
            return this.setBeforeFirstRow();
        }
    }
    
    public ExecRow getRelativeRow(final int n) throws StandardException {
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "relative");
        }
        this.attachStatementContext();
        if (n == 0) {
            if (this.beforeFirst || this.afterLast || this.currentPosition == 0) {
                return null;
            }
            return this.getRowFromHashTable(this.currentPosition);
        }
        else {
            if (n > 0) {
                return this.getAbsoluteRow(this.currentPosition + n);
            }
            if (this.currentPosition + n < 0) {
                return this.setBeforeFirstRow();
            }
            return this.getAbsoluteRow(this.currentPosition + n);
        }
    }
    
    public ExecRow setBeforeFirstRow() {
        this.currentPosition = 0;
        this.beforeFirst = true;
        this.afterLast = false;
        return this.currentRow = null;
    }
    
    public ExecRow getFirstRow() throws StandardException {
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "first");
        }
        if (this.seenFirst) {
            return this.getRowFromHashTable(1);
        }
        this.attachStatementContext();
        return this.getNextRowCore();
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        this.beginTime = this.getCurrentTimeMillis();
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "next");
        }
        if (this.seenLast && this.currentPosition == this.lastPosition) {
            return this.setAfterLastRow();
        }
        ExecRow currentRow;
        if (this.currentPosition == this.positionInSource) {
            currentRow = this.getNextRowFromSource();
            if (currentRow != null) {
                currentRow = this.getRowFromHashTable(this.currentPosition);
            }
        }
        else if (this.currentPosition < this.positionInSource) {
            currentRow = this.getRowFromHashTable(this.currentPosition + 1);
        }
        else {
            currentRow = null;
        }
        if (currentRow != null) {
            ++this.rowsSeen;
            this.afterLast = false;
        }
        this.setCurrentRow(currentRow);
        this.beforeFirst = false;
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return currentRow;
    }
    
    public ExecRow getPreviousRow() throws StandardException {
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "previous");
        }
        if (this.beforeFirst || this.currentPosition == 0) {
            return this.currentRow = null;
        }
        if (this.afterLast) {
            if (this.lastPosition == 0) {
                this.afterLast = false;
                this.beforeFirst = false;
                return this.currentRow = null;
            }
            return this.getRowFromHashTable(this.lastPosition);
        }
        else {
            --this.currentPosition;
            if (this.currentPosition == 0) {
                this.setBeforeFirstRow();
                return null;
            }
            return this.getRowFromHashTable(this.currentPosition);
        }
    }
    
    public ExecRow getLastRow() throws StandardException {
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "next");
        }
        if (!this.seenLast) {
            this.attachStatementContext();
            while (this.getNextRowFromSource() != null) {}
        }
        this.beforeFirst = false;
        this.afterLast = false;
        if (this.lastPosition == 0) {
            return this.currentRow = null;
        }
        return this.getRowFromHashTable(this.lastPosition);
    }
    
    public ExecRow setAfterLastRow() throws StandardException {
        if (!this.seenLast) {
            this.getLastRow();
        }
        if (this.lastPosition == 0) {
            this.currentPosition = 0;
            this.afterLast = false;
        }
        else {
            this.currentPosition = this.lastPosition + 1;
            this.afterLast = true;
        }
        this.beforeFirst = false;
        return this.currentRow = null;
    }
    
    public boolean checkRowPosition(final int n) throws StandardException {
        switch (n) {
            case 101: {
                if (!this.beforeFirst) {
                    return false;
                }
                if (this.seenFirst) {
                    return true;
                }
                if (this.getFirstRow() == null) {
                    return false;
                }
                this.getPreviousRow();
                return true;
            }
            case 102: {
                return this.currentPosition == 1;
            }
            case 103: {
                if (this.beforeFirst || this.afterLast || this.currentPosition == 0 || this.currentPosition < this.positionInSource) {
                    return false;
                }
                if (this.seenLast) {
                    return this.currentPosition == this.lastPosition;
                }
                final int currentPosition = this.currentPosition;
                final boolean b = this.getNextRowFromSource() == null;
                this.getRowFromHashTable(currentPosition);
                return b;
            }
            case 104: {
                return this.afterLast;
            }
            default: {
                return false;
            }
        }
    }
    
    public int getRowNumber() {
        return (this.currentRow == null) ? 0 : this.currentPosition;
    }
    
    private ExecRow getNextRowFromSource() throws StandardException {
        if (this.maxRows > 0L && this.maxRows == this.positionInSource) {
            this.seenLast = true;
            this.lastPosition = this.positionInSource;
            this.afterLast = true;
            return null;
        }
        if (this.needsRepositioning) {
            this.positionInLastFetchedRow();
            this.needsRepositioning = false;
        }
        final ExecRow nextRowCore = this.source.getNextRowCore();
        if (nextRowCore != null) {
            this.seenFirst = true;
            this.beforeFirst = false;
            this.getCurrentTimeMillis();
            if (this.resultRow == null) {
                this.resultRow = this.activation.getExecutionFactory().getValueRow(this.sourceRowWidth);
            }
            ++this.positionInSource;
            this.currentPosition = this.positionInSource;
            RowLocation rowLocation = null;
            if (this.source.isForUpdate()) {
                rowLocation = ((CursorResultSet)this.source).getRowLocation();
            }
            this.addRowToHashTable(nextRowCore, this.currentPosition, rowLocation, false);
        }
        else {
            if (!this.seenLast) {
                this.lastPosition = this.positionInSource;
            }
            this.seenLast = true;
            if (this.positionInSource == 0) {
                this.afterLast = false;
            }
            else {
                this.afterLast = true;
                this.currentPosition = this.positionInSource + 1;
            }
        }
        return nextRowCore;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.currentRow = null;
            this.source.close();
            if (this.ht != null) {
                this.ht.close();
                this.ht = null;
            }
            super.close();
        }
        this.setBeforeFirstRow();
        this.closeTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void finish() throws StandardException {
        this.source.finish();
        this.finishAndRTS();
    }
    
    public long getTimeSpent(final int n) {
        final long n2 = this.constructorTime + this.openTime + this.nextTime + this.closeTime;
        if (n == 0) {
            return n2 - this.source.getTimeSpent(1);
        }
        return n2;
    }
    
    public RowLocation getRowLocation() throws StandardException {
        return ((CursorResultSet)this.source).getRowLocation();
    }
    
    public ExecRow getCurrentRow() throws StandardException {
        if (this.isForUpdate() && this.isDeleted()) {
            return null;
        }
        return this.currentRow;
    }
    
    private void addRowToHashTable(final ExecRow execRow, final int n, final RowLocation rowLocation, final boolean b) throws StandardException {
        final DataValueDescriptor[] array = new DataValueDescriptor[this.sourceRowWidth + this.extraColumns];
        array[0] = new SQLInteger(n);
        if (this.isForUpdate()) {
            array[1] = rowLocation.cloneValue(false);
            array[2] = new SQLBoolean(false);
            array[3] = new SQLBoolean(b);
        }
        final DataValueDescriptor[] rowArray = execRow.getRowArray();
        System.arraycopy(rowArray, 0, array, this.extraColumns, rowArray.length);
        this.ht.putRow(true, array);
        ++this.numToHashTable;
    }
    
    private ExecRow getRowFromHashTable(final int n) throws StandardException {
        this.positionInHashTable.setValue(n);
        final DataValueDescriptor[] array = (DataValueDescriptor[])this.ht.get(this.positionInHashTable);
        final DataValueDescriptor[] rowArray = new DataValueDescriptor[array.length - this.extraColumns];
        System.arraycopy(array, this.extraColumns, rowArray, 0, rowArray.length);
        this.resultRow.setRowArray(rowArray);
        this.currentPosition = n;
        ++this.numFromHashTable;
        if (this.resultRow != null) {
            this.beforeFirst = false;
            this.afterLast = false;
        }
        if (this.isForUpdate()) {
            final RowLocation rowLocation = (RowLocation)array[1];
            ((NoPutResultSet)this.target).setCurrentRow(this.resultRow);
            ((NoPutResultSet)this.target).positionScanAtRowLocation(rowLocation);
            this.needsRepositioning = true;
        }
        this.setCurrentRow(this.resultRow);
        return this.resultRow;
    }
    
    private DataValueDescriptor[] getRowArrayFromHashTable(final int value) throws StandardException {
        this.positionInHashTable.setValue(value);
        final DataValueDescriptor[] array = (DataValueDescriptor[])this.ht.get(this.positionInHashTable);
        final DataValueDescriptor[] array2 = new DataValueDescriptor[array.length - this.extraColumns];
        System.arraycopy(array, this.extraColumns, array2, 0, array2.length);
        return array2;
    }
    
    private void positionInLastFetchedRow() throws StandardException {
        if (this.positionInSource > 0) {
            this.positionInHashTable.setValue(this.positionInSource);
            ((NoPutResultSet)this.target).positionScanAtRowLocation((RowLocation)((DataValueDescriptor[])this.ht.get(this.positionInHashTable))[1]);
            this.currentPosition = this.positionInSource;
        }
    }
    
    public void updateRow(final ExecRow execRow, final RowChanger rowChanger) throws StandardException {
        ProjectRestrictResultSet underlyingProjectRestrictRS = null;
        if (this.source instanceof ProjectRestrictResultSet) {
            underlyingProjectRestrictRS = (ProjectRestrictResultSet)this.source;
        }
        else if (this.source instanceof RowCountResultSet) {
            underlyingProjectRestrictRS = ((RowCountResultSet)this.source).getUnderlyingProjectRestrictRS();
        }
        this.positionInHashTable.setValue(this.currentPosition);
        final DataValueDescriptor[] array = (DataValueDescriptor[])this.ht.get(this.positionInHashTable);
        final RowLocation rowLocation = (RowLocation)array[1];
        int[] baseProjectMapping;
        if (underlyingProjectRestrictRS != null) {
            baseProjectMapping = underlyingProjectRestrictRS.getBaseProjectMapping();
        }
        else {
            final int n = array.length - 4;
            baseProjectMapping = new int[n];
            for (int i = 0; i < n; ++i) {
                baseProjectMapping[i] = i + 1;
            }
        }
        final ValueRow valueRow = new ValueRow(baseProjectMapping.length);
        for (int j = 0; j < baseProjectMapping.length; ++j) {
            final int selectedCol = rowChanger.findSelectedCol(baseProjectMapping[j]);
            if (selectedCol > 0) {
                valueRow.setColumn(j + 1, execRow.getColumn(selectedCol));
            }
            else {
                valueRow.setColumn(j + 1, array[4 + j]);
            }
        }
        this.ht.remove(new SQLInteger(this.currentPosition));
        this.addRowToHashTable(valueRow, this.currentPosition, rowLocation, true);
        final DataValueDescriptor[] rowArrayFromHashTable = this.getRowArrayFromHashTable(this.currentPosition);
        for (int k = 0; k < baseProjectMapping.length; ++k) {
            final int selectedCol2 = rowChanger.findSelectedCol(baseProjectMapping[k]);
            if (selectedCol2 > 0) {
                execRow.setColumn(selectedCol2, rowArrayFromHashTable[k]);
            }
        }
    }
    
    public void markRowAsDeleted() throws StandardException {
        this.positionInHashTable.setValue(this.currentPosition);
        final DataValueDescriptor[] array = (DataValueDescriptor[])this.ht.get(this.positionInHashTable);
        final RowLocation rowLocation = (RowLocation)array[1];
        this.ht.remove(new SQLInteger(this.currentPosition));
        ((SQLBoolean)array[2]).setValue(true);
        for (int i = this.extraColumns; i < array.length; ++i) {
            array[i].setToNull();
        }
        this.ht.putRow(true, array);
    }
    
    public boolean isDeleted() throws StandardException {
        if (this.currentPosition <= this.positionInSource && this.currentPosition > 0) {
            this.positionInHashTable.setValue(this.currentPosition);
            return ((DataValueDescriptor[])this.ht.get(this.positionInHashTable))[2].getBoolean();
        }
        return false;
    }
    
    public boolean isUpdated() throws StandardException {
        if (this.currentPosition <= this.positionInSource && this.currentPosition > 0) {
            this.positionInHashTable.setValue(this.currentPosition);
            return ((DataValueDescriptor[])this.ht.get(this.positionInHashTable))[3].getBoolean();
        }
        return false;
    }
    
    public boolean isForUpdate() {
        return this.source.isForUpdate();
    }
}
