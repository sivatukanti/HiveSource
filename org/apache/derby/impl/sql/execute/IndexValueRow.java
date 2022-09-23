// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;

class IndexValueRow implements ExecIndexRow
{
    private ExecRow valueRow;
    
    IndexValueRow(final ExecRow valueRow) {
        this.valueRow = valueRow;
    }
    
    public String toString() {
        return this.valueRow.toString();
    }
    
    public DataValueDescriptor[] getRowArray() {
        return this.valueRow.getRowArray();
    }
    
    public void setRowArray(final DataValueDescriptor[] rowArray) {
        this.valueRow.setRowArray(rowArray);
    }
    
    public DataValueDescriptor[] getRowArrayClone() {
        return this.valueRow.getRowArrayClone();
    }
    
    public int nColumns() {
        return this.valueRow.nColumns();
    }
    
    public DataValueDescriptor getColumn(final int n) throws StandardException {
        return this.valueRow.getColumn(n);
    }
    
    public void setColumn(final int n, final DataValueDescriptor dataValueDescriptor) {
        this.valueRow.setColumn(n, dataValueDescriptor);
    }
    
    public ExecRow getClone() {
        return new IndexValueRow(this.valueRow.getClone());
    }
    
    public ExecRow getClone(final FormatableBitSet set) {
        return new IndexValueRow(this.valueRow.getClone(set));
    }
    
    public ExecRow getNewNullRow() {
        return new IndexValueRow(this.valueRow.getNewNullRow());
    }
    
    public void resetRowArray() {
        this.valueRow.resetRowArray();
    }
    
    public DataValueDescriptor cloneColumn(final int n) {
        return this.valueRow.cloneColumn(n);
    }
    
    public void orderedNulls(final int n) {
    }
    
    public boolean areNullsOrdered(final int n) {
        return false;
    }
    
    public void execRowToExecIndexRow(final ExecRow valueRow) {
        this.valueRow = valueRow;
    }
    
    public void getNewObjectArray() {
        this.valueRow.getNewObjectArray();
    }
}
