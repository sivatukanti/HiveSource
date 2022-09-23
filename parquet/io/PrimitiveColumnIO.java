// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

import java.util.Arrays;
import parquet.schema.PrimitiveType;
import java.util.List;
import parquet.schema.Type;
import parquet.column.ColumnDescriptor;

public class PrimitiveColumnIO extends ColumnIO
{
    private ColumnIO[] path;
    private ColumnDescriptor columnDescriptor;
    private final int id;
    
    PrimitiveColumnIO(final Type type, final GroupColumnIO parent, final int index, final int id) {
        super(type, parent, index);
        this.id = id;
    }
    
    @Override
    void setLevels(final int r, final int d, final String[] fieldPath, final int[] fieldIndexPath, final List<ColumnIO> repetition, final List<ColumnIO> path) {
        super.setLevels(r, d, fieldPath, fieldIndexPath, repetition, path);
        final PrimitiveType type = this.getType().asPrimitiveType();
        this.columnDescriptor = new ColumnDescriptor(fieldPath, type.getPrimitiveTypeName(), type.getTypeLength(), this.getRepetitionLevel(), this.getDefinitionLevel());
        this.path = path.toArray(new ColumnIO[path.size()]);
    }
    
    @Override
    List<String[]> getColumnNames() {
        return Arrays.asList(new String[][] { this.getFieldPath() });
    }
    
    public ColumnDescriptor getColumnDescriptor() {
        return this.columnDescriptor;
    }
    
    public ColumnIO[] getPath() {
        return this.path;
    }
    
    public boolean isLast(final int r) {
        return this.getLast(r) == this;
    }
    
    private PrimitiveColumnIO getLast(final int r) {
        final ColumnIO parent = this.getParent(r);
        final PrimitiveColumnIO last = parent.getLast();
        return last;
    }
    
    @Override
    PrimitiveColumnIO getLast() {
        return this;
    }
    
    @Override
    PrimitiveColumnIO getFirst() {
        return this;
    }
    
    public boolean isFirst(final int r) {
        return this.getFirst(r) == this;
    }
    
    private PrimitiveColumnIO getFirst(final int r) {
        final ColumnIO parent = this.getParent(r);
        return parent.getFirst();
    }
    
    public PrimitiveType.PrimitiveTypeName getPrimitive() {
        return this.getType().asPrimitiveType().getPrimitiveTypeName();
    }
    
    public int getId() {
        return this.id;
    }
}
