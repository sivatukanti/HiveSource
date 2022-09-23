// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hive.service.cli.thrift.TColumnValue;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.hive.service.cli.thrift.TRowSet;
import org.apache.hive.service.cli.thrift.TRow;

public class RowBasedSet implements RowSet
{
    private long startOffset;
    private final Type[] types;
    private final RemovableList<TRow> rows;
    
    public RowBasedSet(final TableSchema schema) {
        this.types = schema.toTypes();
        this.rows = new RemovableList<TRow>();
    }
    
    public RowBasedSet(final TRowSet tRowSet) {
        this.types = null;
        this.rows = new RemovableList<TRow>(tRowSet.getRows());
        this.startOffset = tRowSet.getStartRowOffset();
    }
    
    private RowBasedSet(final Type[] types, final List<TRow> rows, final long startOffset) {
        this.types = types;
        this.rows = new RemovableList<TRow>(rows);
        this.startOffset = startOffset;
    }
    
    @Override
    public RowBasedSet addRow(final Object[] fields) {
        final TRow tRow = new TRow();
        for (int i = 0; i < fields.length; ++i) {
            tRow.addToColVals(ColumnValue.toTColumnValue(this.types[i], fields[i]));
        }
        this.rows.add(tRow);
        return this;
    }
    
    @Override
    public int numColumns() {
        return this.rows.isEmpty() ? 0 : this.rows.get(0).getColVals().size();
    }
    
    @Override
    public int numRows() {
        return this.rows.size();
    }
    
    @Override
    public RowBasedSet extractSubset(final int maxRows) {
        final int numRows = Math.min(this.numRows(), maxRows);
        final RowBasedSet result = new RowBasedSet(this.types, this.rows.subList(0, numRows), this.startOffset);
        this.rows.removeRange(0, numRows);
        this.startOffset += numRows;
        return result;
    }
    
    @Override
    public long getStartOffset() {
        return this.startOffset;
    }
    
    @Override
    public void setStartOffset(final long startOffset) {
        this.startOffset = startOffset;
    }
    
    public int getSize() {
        return this.rows.size();
    }
    
    @Override
    public TRowSet toTRowSet() {
        final TRowSet tRowSet = new TRowSet();
        tRowSet.setStartRowOffset(this.startOffset);
        tRowSet.setRows(new ArrayList<TRow>(this.rows));
        return tRowSet;
    }
    
    @Override
    public Iterator<Object[]> iterator() {
        return new Iterator<Object[]>() {
            final Iterator<TRow> iterator = RowBasedSet.this.rows.iterator();
            final Object[] convey = new Object[RowBasedSet.this.numColumns()];
            
            @Override
            public boolean hasNext() {
                return this.iterator.hasNext();
            }
            
            @Override
            public Object[] next() {
                final TRow row = this.iterator.next();
                final List<TColumnValue> values = row.getColVals();
                for (int i = 0; i < values.size(); ++i) {
                    this.convey[i] = ColumnValue.toColumnValue(values.get(i));
                }
                return this.convey;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
    }
    
    private static class RemovableList<E> extends ArrayList<E>
    {
        public RemovableList() {
        }
        
        public RemovableList(final List<E> rows) {
            super(rows);
        }
        
        public void removeRange(final int fromIndex, final int toIndex) {
            super.removeRange(fromIndex, toIndex);
        }
    }
}
