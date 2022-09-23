// 
// Decompiled by Procyon v0.5.36
// 

package parquet.schema;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import parquet.io.InvalidRecordException;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Map;
import java.util.List;

public class GroupType extends Type
{
    private final List<Type> fields;
    private final Map<String, Integer> indexByName;
    
    public GroupType(final Repetition repetition, final String name, final List<Type> fields) {
        this(repetition, name, null, fields, null);
    }
    
    public GroupType(final Repetition repetition, final String name, final Type... fields) {
        this(repetition, name, Arrays.asList(fields));
    }
    
    @Deprecated
    public GroupType(final Repetition repetition, final String name, final OriginalType originalType, final Type... fields) {
        this(repetition, name, originalType, Arrays.asList(fields));
    }
    
    @Deprecated
    public GroupType(final Repetition repetition, final String name, final OriginalType originalType, final List<Type> fields) {
        this(repetition, name, originalType, fields, null);
    }
    
    GroupType(final Repetition repetition, final String name, final OriginalType originalType, final List<Type> fields, final ID id) {
        super(name, repetition, originalType, id);
        this.fields = fields;
        this.indexByName = new HashMap<String, Integer>();
        for (int i = 0; i < fields.size(); ++i) {
            this.indexByName.put(fields.get(i).getName(), i);
        }
    }
    
    @Override
    public GroupType withId(final int id) {
        return new GroupType(this.getRepetition(), this.getName(), this.getOriginalType(), this.fields, new ID(id));
    }
    
    public GroupType withNewFields(final List<Type> newFields) {
        return new GroupType(this.getRepetition(), this.getName(), this.getOriginalType(), newFields, this.getId());
    }
    
    public GroupType withNewFields(final Type... newFields) {
        return this.withNewFields(Arrays.asList(newFields));
    }
    
    public String getFieldName(final int index) {
        return this.fields.get(index).getName();
    }
    
    public boolean containsField(final String name) {
        return this.indexByName.containsKey(name);
    }
    
    public int getFieldIndex(final String name) {
        if (!this.indexByName.containsKey(name)) {
            throw new InvalidRecordException(name + " not found in " + this);
        }
        return this.indexByName.get(name);
    }
    
    public List<Type> getFields() {
        return this.fields;
    }
    
    public int getFieldCount() {
        return this.fields.size();
    }
    
    @Override
    public boolean isPrimitive() {
        return false;
    }
    
    public Type getType(final String fieldName) {
        return this.getType(this.getFieldIndex(fieldName));
    }
    
    public Type getType(final int index) {
        return this.fields.get(index);
    }
    
    void membersDisplayString(final StringBuilder sb, final String indent) {
        for (final Type field : this.fields) {
            field.writeToStringBuilder(sb, indent);
            if (field.isPrimitive()) {
                sb.append(";");
            }
            sb.append("\n");
        }
    }
    
    @Override
    public void writeToStringBuilder(final StringBuilder sb, final String indent) {
        sb.append(indent).append(this.getRepetition().name().toLowerCase()).append(" group ").append(this.getName()).append((this.getOriginalType() == null) ? "" : (" (" + this.getOriginalType() + ")")).append((this.getId() == null) ? "" : (" = " + this.getId())).append(" {\n");
        this.membersDisplayString(sb, indent + "  ");
        sb.append(indent).append("}");
    }
    
    @Override
    public void accept(final TypeVisitor visitor) {
        visitor.visit(this);
    }
    
    @Deprecated
    @Override
    protected int typeHashCode() {
        return this.hashCode();
    }
    
    @Deprecated
    @Override
    protected boolean typeEquals(final Type other) {
        return this.equals(other);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.getFields().hashCode();
    }
    
    @Override
    protected boolean equals(final Type otherType) {
        return !otherType.isPrimitive() && super.equals(otherType) && this.getFields().equals(otherType.asGroupType().getFields());
    }
    
    @Override
    protected int getMaxRepetitionLevel(final String[] path, final int depth) {
        final int myVal = this.isRepetition(Repetition.REPEATED) ? 1 : 0;
        if (depth == path.length) {
            return myVal;
        }
        return myVal + this.getType(path[depth]).getMaxRepetitionLevel(path, depth + 1);
    }
    
    @Override
    protected int getMaxDefinitionLevel(final String[] path, final int depth) {
        final int myVal = this.isRepetition(Repetition.REQUIRED) ? 0 : 1;
        if (depth == path.length) {
            return myVal;
        }
        return myVal + this.getType(path[depth]).getMaxDefinitionLevel(path, depth + 1);
    }
    
    @Override
    protected Type getType(final String[] path, final int depth) {
        if (depth == path.length) {
            return this;
        }
        return this.getType(path[depth]).getType(path, depth + 1);
    }
    
    @Override
    protected boolean containsPath(final String[] path, final int depth) {
        return depth != path.length && this.containsField(path[depth]) && this.getType(path[depth]).containsPath(path, depth + 1);
    }
    
    @Override
    protected List<String[]> getPaths(final int depth) {
        final List<String[]> result = new ArrayList<String[]>();
        for (final Type field : this.fields) {
            final List<String[]> paths = field.getPaths(depth + 1);
            for (final String[] path : paths) {
                path[depth] = field.getName();
                result.add(path);
            }
        }
        return result;
    }
    
    @Override
    void checkContains(final Type subType) {
        super.checkContains(subType);
        this.checkGroupContains(subType);
    }
    
    void checkGroupContains(final Type subType) {
        if (subType.isPrimitive()) {
            throw new InvalidRecordException(subType + " found: expected " + this);
        }
        final List<Type> fields = subType.asGroupType().getFields();
        for (final Type otherType : fields) {
            final Type thisType = this.getType(otherType.getName());
            thisType.checkContains(otherType);
        }
    }
    
    @Override
     <T> T convert(final List<GroupType> path, final TypeConverter<T> converter) {
        final List<GroupType> childrenPath = new ArrayList<GroupType>(path);
        childrenPath.add(this);
        final List<T> children = this.convertChildren(childrenPath, converter);
        return converter.convertGroupType(path, this, children);
    }
    
    protected <T> List<T> convertChildren(final List<GroupType> path, final TypeConverter<T> converter) {
        final List<T> children = new ArrayList<T>(this.fields.size());
        for (final Type field : this.fields) {
            children.add(field.convert(path, converter));
        }
        return children;
    }
    
    @Override
    protected Type union(final Type toMerge) {
        return this.union(toMerge, true);
    }
    
    @Override
    protected Type union(final Type toMerge, final boolean strict) {
        if (toMerge.isPrimitive()) {
            throw new IncompatibleSchemaModificationException("can not merge primitive type " + toMerge + " into group type " + this);
        }
        return new GroupType(toMerge.getRepetition(), this.getName(), this.mergeFields(toMerge.asGroupType()));
    }
    
    List<Type> mergeFields(final GroupType toMerge) {
        return this.mergeFields(toMerge, true);
    }
    
    List<Type> mergeFields(final GroupType toMerge, final boolean strict) {
        final List<Type> newFields = new ArrayList<Type>();
        for (final Type type : this.getFields()) {
            Type merged;
            if (toMerge.containsField(type.getName())) {
                final Type fieldToMerge = toMerge.getType(type.getName());
                if (fieldToMerge.getRepetition().isMoreRestrictiveThan(type.getRepetition())) {
                    throw new IncompatibleSchemaModificationException("repetition constraint is more restrictive: can not merge type " + fieldToMerge + " into " + type);
                }
                merged = type.union(fieldToMerge, strict);
            }
            else {
                merged = type;
            }
            newFields.add(merged);
        }
        for (final Type type : toMerge.getFields()) {
            if (!this.containsField(type.getName())) {
                newFields.add(type);
            }
        }
        return newFields;
    }
}
