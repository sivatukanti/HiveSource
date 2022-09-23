// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.RowLocationRetRowSource;

public class CardinalityCounter implements RowLocationRetRowSource
{
    private RowLocationRetRowSource rowSource;
    private DataValueDescriptor[] prevKey;
    private long[] cardinality;
    private long numRows;
    
    public CardinalityCounter(final RowLocationRetRowSource rowSource) {
        this.rowSource = rowSource;
    }
    
    public boolean needsRowLocation() {
        return this.rowSource.needsRowLocation();
    }
    
    public void rowLocation(final RowLocation rowLocation) throws StandardException {
        this.rowSource.rowLocation(rowLocation);
    }
    
    public DataValueDescriptor[] getNextRowFromRowSource() throws StandardException {
        final DataValueDescriptor[] nextRowFromRowSource = this.rowSource.getNextRowFromRowSource();
        if (nextRowFromRowSource != null) {
            this.keepCount(nextRowFromRowSource);
        }
        return nextRowFromRowSource;
    }
    
    public boolean needsToClone() {
        return this.rowSource.needsToClone();
    }
    
    public FormatableBitSet getValidColumns() {
        return this.rowSource.getValidColumns();
    }
    
    public void closeRowSource() {
        this.rowSource.closeRowSource();
    }
    
    private DataValueDescriptor[] clone(final DataValueDescriptor[] array) {
        final DataValueDescriptor[] array2 = new DataValueDescriptor[array.length];
        for (int i = 0; i < array.length - 1; ++i) {
            array2[i] = array[i].cloneValue(false);
        }
        return array2;
    }
    
    public void keepCount(final DataValueDescriptor[] array) throws StandardException {
        final int n = array.length - 1;
        ++this.numRows;
        if (this.prevKey == null) {
            this.prevKey = this.clone(array);
            this.cardinality = new long[array.length - 1];
            for (int i = 0; i < n; ++i) {
                this.cardinality[i] = 1L;
            }
            return;
        }
        int j;
        for (j = 0; j < n; ++j) {
            if (this.prevKey[j].isNull()) {
                break;
            }
            if (this.prevKey[j].compare(array[j]) != 0) {
                this.prevKey = null;
                this.prevKey = this.clone(array);
                break;
            }
        }
        for (int k = j; k < n; ++k) {
            final long[] cardinality = this.cardinality;
            final int n2 = k;
            ++cardinality[n2];
        }
    }
    
    public long[] getCardinality() {
        return this.cardinality;
    }
    
    public long getRowCount() {
        return this.numRows;
    }
}
