// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column;

import java.util.Arrays;
import parquet.schema.PrimitiveType;

public class ColumnDescriptor implements Comparable<ColumnDescriptor>
{
    private final String[] path;
    private final PrimitiveType.PrimitiveTypeName type;
    private final int typeLength;
    private final int maxRep;
    private final int maxDef;
    
    public ColumnDescriptor(final String[] path, final PrimitiveType.PrimitiveTypeName type, final int maxRep, final int maxDef) {
        this(path, type, 0, maxRep, maxDef);
    }
    
    public ColumnDescriptor(final String[] path, final PrimitiveType.PrimitiveTypeName type, final int typeLength, final int maxRep, final int maxDef) {
        this.path = path;
        this.type = type;
        this.typeLength = typeLength;
        this.maxRep = maxRep;
        this.maxDef = maxDef;
    }
    
    public String[] getPath() {
        return this.path;
    }
    
    public int getMaxRepetitionLevel() {
        return this.maxRep;
    }
    
    public int getMaxDefinitionLevel() {
        return this.maxDef;
    }
    
    public PrimitiveType.PrimitiveTypeName getType() {
        return this.type;
    }
    
    public int getTypeLength() {
        return this.typeLength;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.path);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return Arrays.equals(this.path, ((ColumnDescriptor)obj).path);
    }
    
    @Override
    public int compareTo(final ColumnDescriptor o) {
        for (int i = 0; i < this.path.length; ++i) {
            final int compareTo = this.path[i].compareTo(o.path[i]);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return Arrays.toString(this.path) + " " + this.type;
    }
}
