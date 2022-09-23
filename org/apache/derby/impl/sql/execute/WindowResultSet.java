// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
import org.apache.derby.iapi.sql.execute.ExecRowBuilder;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.services.loader.GeneratedMethod;

class WindowResultSet extends NoPutResultSetImpl
{
    private GeneratedMethod restriction;
    public NoPutResultSet source;
    public long restrictionTime;
    private FormatableBitSet referencedColumns;
    private ExecRow allocatedRow;
    private long rownumber;
    
    WindowResultSet(final Activation activation, final NoPutResultSet source, final int n, final int n2, final int n3, final GeneratedMethod restriction, final double n4, final double n5) throws StandardException {
        super(activation, n2, n4, n5);
        this.restriction = null;
        this.source = null;
        this.restriction = restriction;
        this.source = source;
        this.rownumber = 0L;
        final ExecPreparedStatement preparedStatement = activation.getPreparedStatement();
        this.allocatedRow = ((ExecRowBuilder)preparedStatement.getSavedObject(n)).build(activation.getExecutionFactory());
        if (n3 != -1) {
            this.referencedColumns = (FormatableBitSet)preparedStatement.getSavedObject(n3);
        }
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.source.openCore();
        this.isOpen = true;
        this.rownumber = 0L;
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void reopenCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.source.reopenCore();
        this.rownumber = 0L;
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        boolean b = false;
        final long n = 0L;
        this.beginTime = this.getCurrentTimeMillis();
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "next");
        }
        ExecRow nextRowCore;
        ExecRow currentRow;
        do {
            nextRowCore = this.source.getNextRowCore();
            if (nextRowCore != null) {
                ++this.rownumber;
                final ExecRow allocatedRow = this.getAllocatedRow();
                this.populateFromSourceRow(nextRowCore, allocatedRow);
                this.setCurrentRow(allocatedRow);
                final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)((this.restriction == null) ? null : this.restriction.invoke(this.activation));
                this.restrictionTime += this.getElapsedMillis(n);
                b = (dataValueDescriptor == null || (!dataValueDescriptor.isNull() && dataValueDescriptor.getBoolean()));
                if (!b) {
                    ++this.rowsFiltered;
                    this.clearCurrentRow();
                }
                ++this.rowsSeen;
                currentRow = this.currentRow;
            }
            else {
                this.clearCurrentRow();
                currentRow = null;
            }
        } while (nextRowCore != null && !b);
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return currentRow;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.clearCurrentRow();
            this.source.close();
            super.close();
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void populateFromSourceRow(final ExecRow execRow, final ExecRow execRow2) throws StandardException {
        int n = 1;
        try {
            final DataValueDescriptor[] rowArray = execRow2.getRowArray();
            for (int i = 0; i < rowArray.length; ++i) {
                if (this.referencedColumns != null && !this.referencedColumns.get(i)) {
                    rowArray[i].setValue(this.rownumber);
                }
                else {
                    execRow2.setColumn(i + 1, execRow.getColumn(n));
                    ++n;
                }
            }
        }
        catch (StandardException ex) {
            throw ex;
        }
        catch (Throwable t) {
            throw StandardException.unexpectedUserException(t);
        }
    }
    
    public long getTimeSpent(final int n) {
        final long n2 = this.constructorTime + this.openTime + this.nextTime + this.closeTime;
        if (n == 0) {
            return n2 - this.source.getTimeSpent(1);
        }
        return n2;
    }
    
    private ExecRow getAllocatedRow() throws StandardException {
        return this.allocatedRow;
    }
}
