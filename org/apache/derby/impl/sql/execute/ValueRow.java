// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;

public class ValueRow implements ExecRow
{
    private DataValueDescriptor[] column;
    private int ncols;
    
    public ValueRow(final int ncols) {
        this.column = new DataValueDescriptor[ncols];
        this.ncols = ncols;
    }
    
    public int nColumns() {
        return this.ncols;
    }
    
    public void getNewObjectArray() {
        this.column = new DataValueDescriptor[this.ncols];
    }
    
    public DataValueDescriptor getColumn(final int n) {
        if (n <= this.column.length) {
            return this.column[n - 1];
        }
        return null;
    }
    
    public void setColumn(final int n, final DataValueDescriptor dataValueDescriptor) {
        if (n > this.column.length) {
            this.realloc(n);
        }
        this.column[n - 1] = dataValueDescriptor;
    }
    
    public ExecRow getClone() {
        return this.getClone(null);
    }
    
    public ExecRow getClone(final FormatableBitSet set) {
        final int length = this.column.length;
        final ExecRow cloneMe = this.cloneMe();
        for (int i = 0; i < length; ++i) {
            if (set != null && !set.get(i + 1)) {
                cloneMe.setColumn(i + 1, this.column[i]);
            }
            else if (this.column[i] != null) {
                cloneMe.setColumn(i + 1, this.column[i].cloneValue(false));
            }
        }
        return cloneMe;
    }
    
    public ExecRow getNewNullRow() {
        final int length = this.column.length;
        final ExecRow cloneMe = this.cloneMe();
        for (int i = 0; i < length; ++i) {
            if (this.column[i] != null) {
                cloneMe.setColumn(i + 1, this.column[i].getNewNull());
            }
        }
        return cloneMe;
    }
    
    ExecRow cloneMe() {
        return new ValueRow(this.ncols);
    }
    
    public void resetRowArray() {
        for (int i = 0; i < this.column.length; ++i) {
            if (this.column[i] != null) {
                this.column[i] = this.column[i].recycle();
            }
        }
    }
    
    public final DataValueDescriptor cloneColumn(final int n) {
        return this.column[n - 1].cloneValue(false);
    }
    
    public String toString() {
        String s = "{ ";
        for (int i = 0; i < this.column.length; ++i) {
            if (this.column[i] == null) {
                s += "null";
            }
            else {
                s += this.column[i].toString();
            }
            if (i < this.column.length - 1) {
                s += ", ";
            }
        }
        return s + " }";
    }
    
    public DataValueDescriptor[] getRowArray() {
        return this.column;
    }
    
    public DataValueDescriptor[] getRowArrayClone() {
        final int length = this.column.length;
        final DataValueDescriptor[] array = new DataValueDescriptor[length];
        for (int i = 0; i < length; ++i) {
            if (this.column[i] != null) {
                array[i] = this.column[i].cloneValue(false);
            }
        }
        return array;
    }
    
    public void setRowArray(final DataValueDescriptor[] column) {
        this.column = column;
    }
    
    protected void realloc(final int n) {
        final DataValueDescriptor[] column = new DataValueDescriptor[n];
        System.arraycopy(this.column, 0, column, 0, this.column.length);
        this.column = column;
    }
}
