// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

import parquet.Log;
import java.util.Arrays;
import java.util.List;
import parquet.schema.Type;

public abstract class ColumnIO
{
    static final boolean DEBUG;
    private final GroupColumnIO parent;
    private final Type type;
    private final String name;
    private final int index;
    private int repetitionLevel;
    private int definitionLevel;
    private String[] fieldPath;
    private int[] indexFieldPath;
    
    ColumnIO(final Type type, final GroupColumnIO parent, final int index) {
        this.type = type;
        this.parent = parent;
        this.index = index;
        this.name = type.getName();
    }
    
    String[] getFieldPath() {
        return this.fieldPath;
    }
    
    public String getFieldPath(final int level) {
        return this.fieldPath[level];
    }
    
    public int[] getIndexFieldPath() {
        return this.indexFieldPath;
    }
    
    public int getIndexFieldPath(final int level) {
        return this.indexFieldPath[level];
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public String getName() {
        return this.name;
    }
    
    int getRepetitionLevel() {
        return this.repetitionLevel;
    }
    
    int getDefinitionLevel() {
        return this.definitionLevel;
    }
    
    void setRepetitionLevel(final int repetitionLevel) {
        this.repetitionLevel = repetitionLevel;
    }
    
    void setDefinitionLevel(final int definitionLevel) {
        this.definitionLevel = definitionLevel;
    }
    
    void setFieldPath(final String[] fieldPath, final int[] indexFieldPath) {
        this.fieldPath = fieldPath;
        this.indexFieldPath = indexFieldPath;
    }
    
    public Type getType() {
        return this.type;
    }
    
    void setLevels(final int r, final int d, final String[] fieldPath, final int[] indexFieldPath, final List<ColumnIO> repetition, final List<ColumnIO> path) {
        this.setRepetitionLevel(r);
        this.setDefinitionLevel(d);
        this.setFieldPath(fieldPath, indexFieldPath);
    }
    
    abstract List<String[]> getColumnNames();
    
    public GroupColumnIO getParent() {
        return this.parent;
    }
    
    abstract PrimitiveColumnIO getLast();
    
    abstract PrimitiveColumnIO getFirst();
    
    ColumnIO getParent(final int r) {
        if (this.getRepetitionLevel() == r && this.getType().isRepetition(Type.Repetition.REPEATED)) {
            return this;
        }
        if (this.getParent() != null && this.getParent().getDefinitionLevel() >= r) {
            return this.getParent().getParent(r);
        }
        throw new InvalidRecordException("no parent(" + r + ") for " + Arrays.toString(this.getFieldPath()));
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + this.type.getName() + " r:" + this.repetitionLevel + " d:" + this.definitionLevel + " " + Arrays.toString(this.fieldPath);
    }
    
    static {
        DEBUG = Log.DEBUG;
    }
}
