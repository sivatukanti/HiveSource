// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class RowResultSet extends NoPutResultSetImpl implements CursorResultSet
{
    public int rowsReturned;
    private boolean canCacheRow;
    private boolean next;
    private GeneratedMethod row;
    private ExecRow cachedRow;
    
    RowResultSet(final Activation activation, final GeneratedMethod row, final boolean canCacheRow, final int n, final double n2, final double n3) {
        super(activation, n, n2, n3);
        this.row = row;
        this.canCacheRow = canCacheRow;
        this.recordConstructorTime();
    }
    
    RowResultSet(final Activation activation, final ExecRow cachedRow, final boolean canCacheRow, final int n, final double n2, final double n3) {
        super(activation, n, n2, n3);
        this.beginTime = this.getCurrentTimeMillis();
        this.cachedRow = cachedRow;
        this.canCacheRow = canCacheRow;
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        this.next = false;
        this.beginTime = this.getCurrentTimeMillis();
        this.isOpen = true;
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        this.currentRow = null;
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            if (!this.next) {
                this.next = true;
                if (this.cachedRow != null) {
                    this.currentRow = this.cachedRow;
                }
                else if (this.row != null) {
                    this.currentRow = (ExecRow)this.row.invoke(this.activation);
                    if (this.canCacheRow) {
                        this.cachedRow = this.currentRow;
                    }
                }
                ++this.rowsReturned;
            }
            this.setCurrentRow(this.currentRow);
            this.nextTime += this.getElapsedMillis(this.beginTime);
        }
        return this.currentRow;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.clearCurrentRow();
            this.next = false;
            super.close();
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
    }
    
    public long getTimeSpent(final int n) {
        return this.constructorTime + this.openTime + this.nextTime + this.closeTime;
    }
    
    public RowLocation getRowLocation() {
        return null;
    }
    
    public ExecRow getCurrentRow() {
        return null;
    }
}
