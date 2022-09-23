// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hive.service.cli.thrift.TRow;
import org.apache.hive.service.cli.thrift.TColumn;
import org.apache.hive.service.cli.thrift.TRowSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class ColumnBasedSet implements RowSet
{
    private long startOffset;
    private final Type[] types;
    private final List<Column> columns;
    
    public ColumnBasedSet(final TableSchema schema) {
        this.types = schema.toTypes();
        this.columns = new ArrayList<Column>();
        for (final ColumnDescriptor colDesc : schema.getColumnDescriptors()) {
            this.columns.add(new Column(colDesc.getType()));
        }
    }
    
    public ColumnBasedSet(final TRowSet tRowSet) {
        this.types = null;
        this.columns = new ArrayList<Column>();
        for (final TColumn tvalue : tRowSet.getColumns()) {
            this.columns.add(new Column(tvalue));
        }
        this.startOffset = tRowSet.getStartRowOffset();
    }
    
    private ColumnBasedSet(final Type[] types, final List<Column> columns, final long startOffset) {
        this.types = types;
        this.columns = columns;
        this.startOffset = startOffset;
    }
    
    @Override
    public ColumnBasedSet addRow(final Object[] fields) {
        for (int i = 0; i < fields.length; ++i) {
            this.columns.get(i).addValue(this.types[i], fields[i]);
        }
        return this;
    }
    
    public List<Column> getColumns() {
        return this.columns;
    }
    
    @Override
    public int numColumns() {
        return this.columns.size();
    }
    
    @Override
    public int numRows() {
        return this.columns.isEmpty() ? 0 : this.columns.get(0).size();
    }
    
    @Override
    public ColumnBasedSet extractSubset(final int maxRows) {
        final int numRows = Math.min(this.numRows(), maxRows);
        final List<Column> subset = new ArrayList<Column>();
        for (int i = 0; i < this.columns.size(); ++i) {
            subset.add(this.columns.get(i).extractSubset(0, numRows));
        }
        final ColumnBasedSet result = new ColumnBasedSet(this.types, subset, this.startOffset);
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
    
    @Override
    public TRowSet toTRowSet() {
        final TRowSet tRowSet = new TRowSet(this.startOffset, new ArrayList<TRow>());
        for (int i = 0; i < this.columns.size(); ++i) {
            tRowSet.addToColumns(this.columns.get(i).toTColumn());
        }
        return tRowSet;
    }
    
    @Override
    public Iterator<Object[]> iterator() {
        return new Iterator<Object[]>() {
            private int index;
            private final Object[] convey = new Object[ColumnBasedSet.this.numColumns()];
            
            @Override
            public boolean hasNext() {
                return this.index < ColumnBasedSet.this.numRows();
            }
            
            @Override
            public Object[] next() {
                for (int i = 0; i < ColumnBasedSet.this.columns.size(); ++i) {
                    this.convey[i] = ColumnBasedSet.this.columns.get(i).get(this.index);
                }
                ++this.index;
                return this.convey;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
    }
    
    public Object[] fill(final int index, final Object[] convey) {
        for (int i = 0; i < this.columns.size(); ++i) {
            convey[i] = this.columns.get(i).get(index);
        }
        return convey;
    }
}
