// 
// Decompiled by Procyon v0.5.36
// 

package parquet.schema;

import parquet.io.InvalidRecordException;
import java.util.Iterator;
import java.util.ArrayList;
import parquet.column.ColumnDescriptor;
import java.util.List;

public final class MessageType extends GroupType
{
    public MessageType(final String name, final Type... fields) {
        super(Repetition.REPEATED, name, fields);
    }
    
    public MessageType(final String name, final List<Type> fields) {
        super(Repetition.REPEATED, name, fields);
    }
    
    @Override
    public void accept(final TypeVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public void writeToStringBuilder(final StringBuilder sb, final String indent) {
        sb.append("message ").append(this.getName()).append((this.getOriginalType() == null) ? "" : (" (" + this.getOriginalType() + ")")).append(" {\n");
        this.membersDisplayString(sb, "  ");
        sb.append("}\n");
    }
    
    public int getMaxRepetitionLevel(final String... path) {
        return this.getMaxRepetitionLevel(path, 0) - 1;
    }
    
    public int getMaxDefinitionLevel(final String... path) {
        return this.getMaxDefinitionLevel(path, 0) - 1;
    }
    
    public Type getType(final String... path) {
        return this.getType(path, 0);
    }
    
    public ColumnDescriptor getColumnDescription(final String[] path) {
        final int maxRep = this.getMaxRepetitionLevel(path);
        final int maxDef = this.getMaxDefinitionLevel(path);
        final PrimitiveType type = this.getType(path).asPrimitiveType();
        return new ColumnDescriptor(path, type.getPrimitiveTypeName(), type.getTypeLength(), maxRep, maxDef);
    }
    
    public List<String[]> getPaths() {
        return this.getPaths(0);
    }
    
    public List<ColumnDescriptor> getColumns() {
        final List<String[]> paths = this.getPaths(0);
        final List<ColumnDescriptor> columns = new ArrayList<ColumnDescriptor>(paths.size());
        for (final String[] path : paths) {
            final PrimitiveType primitiveType = this.getType(path).asPrimitiveType();
            columns.add(new ColumnDescriptor(path, primitiveType.getPrimitiveTypeName(), primitiveType.getTypeLength(), this.getMaxRepetitionLevel(path), this.getMaxDefinitionLevel(path)));
        }
        return columns;
    }
    
    public void checkContains(final Type subType) {
        if (!(subType instanceof MessageType)) {
            throw new InvalidRecordException(subType + " found: expected " + this);
        }
        this.checkGroupContains(subType);
    }
    
    public <T> T convertWith(final TypeConverter<T> converter) {
        final ArrayList<GroupType> path = new ArrayList<GroupType>();
        path.add(this);
        return converter.convertMessageType(this, this.convertChildren(path, converter));
    }
    
    public boolean containsPath(final String[] path) {
        return this.containsPath(path, 0);
    }
    
    public MessageType union(final MessageType toMerge) {
        return this.union(toMerge, true);
    }
    
    public MessageType union(final MessageType toMerge, final boolean strict) {
        return new MessageType(this.getName(), this.mergeFields(toMerge, strict));
    }
}
