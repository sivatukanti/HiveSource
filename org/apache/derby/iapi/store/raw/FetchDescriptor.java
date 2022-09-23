// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.io.FormatableBitSet;

public final class FetchDescriptor
{
    private int row_length;
    private FormatableBitSet validColumns;
    private Qualifier[][] qualifier_list;
    private int[] materialized_cols;
    private int maxFetchColumnId;
    private static final int ZERO_FILL_LENGTH = 100;
    private static final int[] zero_fill_array;
    private int[] validColumnsArray;
    
    FetchDescriptor() {
    }
    
    public FetchDescriptor(final int row_length) {
        this.row_length = row_length;
    }
    
    public FetchDescriptor(final int row_length, final int maxFetchColumnId) {
        this.row_length = row_length;
        this.maxFetchColumnId = maxFetchColumnId;
        (this.validColumnsArray = new int[this.maxFetchColumnId + 1])[maxFetchColumnId] = 1;
    }
    
    public FetchDescriptor(final int row_length, final FormatableBitSet validColumns, final Qualifier[][] qualifier_list) {
        this.row_length = row_length;
        this.qualifier_list = qualifier_list;
        if (this.qualifier_list != null) {
            this.materialized_cols = new int[this.row_length];
        }
        this.setValidColumns(validColumns);
    }
    
    public final FormatableBitSet getValidColumns() {
        return this.validColumns;
    }
    
    public final int[] getValidColumnsArray() {
        return this.validColumnsArray;
    }
    
    public final void setValidColumns(final FormatableBitSet validColumns) {
        this.validColumns = validColumns;
        this.setMaxFetchColumnId();
        if (this.validColumns != null) {
            this.validColumnsArray = new int[this.maxFetchColumnId + 1];
            for (int i = this.maxFetchColumnId; i >= 0; --i) {
                this.validColumnsArray[i] = (this.validColumns.isSet(i) ? 1 : 0);
            }
        }
    }
    
    public final Qualifier[][] getQualifierList() {
        return this.qualifier_list;
    }
    
    public final int[] getMaterializedColumns() {
        return this.materialized_cols;
    }
    
    public final int getMaxFetchColumnId() {
        return this.maxFetchColumnId;
    }
    
    private final void setMaxFetchColumnId() {
        this.maxFetchColumnId = this.row_length - 1;
        if (this.validColumns != null) {
            final int length = this.validColumns.getLength();
            if (length < this.maxFetchColumnId + 1) {
                this.maxFetchColumnId = length - 1;
            }
            while (this.maxFetchColumnId >= 0) {
                if (this.validColumns.isSet(this.maxFetchColumnId)) {
                    break;
                }
                --this.maxFetchColumnId;
            }
        }
    }
    
    public final void reset() {
        final int[] materialized_cols = this.materialized_cols;
        if (materialized_cols != null) {
            if (materialized_cols.length <= 100) {
                System.arraycopy(FetchDescriptor.zero_fill_array, 0, materialized_cols, 0, materialized_cols.length);
            }
            else {
                int n2;
                for (int n = 0, i = materialized_cols.length; i > 0; i -= n2, n += n2) {
                    n2 = ((i > FetchDescriptor.zero_fill_array.length) ? FetchDescriptor.zero_fill_array.length : i);
                    System.arraycopy(FetchDescriptor.zero_fill_array, 0, materialized_cols, n, n2);
                }
            }
        }
    }
    
    static {
        zero_fill_array = new int[100];
    }
}
