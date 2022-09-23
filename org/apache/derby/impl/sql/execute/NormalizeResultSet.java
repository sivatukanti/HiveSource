// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.RowChanger;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class NormalizeResultSet extends NoPutResultSetImpl implements CursorResultSet
{
    public NoPutResultSet source;
    private ExecRow normalizedRow;
    private int numCols;
    private int startCol;
    private final DataValueDescriptor[] cachedDestinations;
    private ResultDescription resultDescription;
    private DataTypeDescriptor[] desiredTypes;
    
    public NormalizeResultSet(final NoPutResultSet source, final Activation activation, final int n, final int n2, final double n3, final double n4, final boolean b) throws StandardException {
        super(activation, n, n3, n4);
        this.source = source;
        this.resultDescription = (ResultDescription)activation.getPreparedStatement().getSavedObject(n2);
        this.numCols = this.resultDescription.getColumnCount();
        this.startCol = computeStartColumn(b, this.resultDescription);
        this.normalizedRow = activation.getExecutionFactory().getValueRow(this.numCols);
        this.cachedDestinations = new DataValueDescriptor[this.numCols];
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.source.openCore();
        this.isOpen = true;
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void reopenCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.source.reopenCore();
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        ExecRow normalizeRow = null;
        this.beginTime = this.getCurrentTimeMillis();
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "next");
        }
        final ExecRow nextRowCore = this.source.getNextRowCore();
        if (nextRowCore != null) {
            normalizeRow = this.normalizeRow(nextRowCore);
            ++this.rowsSeen;
        }
        this.setCurrentRow(normalizeRow);
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return normalizeRow;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.currentRow = null;
            this.source.close();
            super.close();
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
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
    
    public ExecRow getCurrentRow() {
        return this.currentRow;
    }
    
    public static int computeStartColumn(final boolean b, final ResultDescription resultDescription) {
        final int columnCount = resultDescription.getColumnCount();
        return b ? ((columnCount - 1) / 2 + 1) : 1;
    }
    
    public static DataValueDescriptor normalizeColumn(final DataTypeDescriptor dataTypeDescriptor, final ExecRow execRow, final int n, final DataValueDescriptor dataValueDescriptor, final ResultDescription resultDescription) throws StandardException {
        final DataValueDescriptor column = execRow.getColumn(n);
        try {
            return dataTypeDescriptor.normalize(column, dataValueDescriptor);
        }
        catch (StandardException ex) {
            if (ex.getMessageId().startsWith("23502")) {
                throw StandardException.newException("23502", resultDescription.getColumnDescriptor(n).getName());
            }
            throw ex;
        }
    }
    
    private ExecRow normalizeRow(final ExecRow execRow) throws StandardException {
        for (int columnCount = this.resultDescription.getColumnCount(), i = 1; i <= columnCount; ++i) {
            final DataValueDescriptor column = execRow.getColumn(i);
            if (column != null) {
                DataValueDescriptor normalizeColumn;
                if (i < this.startCol) {
                    normalizeColumn = column;
                }
                else {
                    normalizeColumn = normalizeColumn(this.getDesiredType(i), execRow, i, this.getCachedDestination(i), this.resultDescription);
                }
                this.normalizedRow.setColumn(i, normalizeColumn);
            }
        }
        return this.normalizedRow;
    }
    
    private DataValueDescriptor getCachedDestination(final int n) throws StandardException {
        final int n2 = n - 1;
        if (this.cachedDestinations[n2] == null) {
            this.cachedDestinations[n2] = this.getDesiredType(n).getNull();
        }
        return this.cachedDestinations[n2];
    }
    
    private DataTypeDescriptor getDesiredType(final int n) {
        if (this.desiredTypes == null) {
            this.desiredTypes = this.fetchResultTypes(this.resultDescription);
        }
        return this.desiredTypes[n - 1];
    }
    
    private DataTypeDescriptor[] fetchResultTypes(final ResultDescription resultDescription) {
        final int columnCount = resultDescription.getColumnCount();
        final DataTypeDescriptor[] array = new DataTypeDescriptor[columnCount];
        for (int i = 1; i <= columnCount; ++i) {
            array[i - 1] = resultDescription.getColumnDescriptor(i).getType();
        }
        return array;
    }
    
    public void updateRow(final ExecRow execRow, final RowChanger rowChanger) throws StandardException {
        this.source.updateRow(execRow, rowChanger);
    }
    
    public void markRowAsDeleted() throws StandardException {
        this.source.markRowAsDeleted();
    }
}
