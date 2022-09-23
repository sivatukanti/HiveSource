// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.data;

import java.util.Arrays;
import java.io.IOException;
import java.util.Iterator;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.generic.GenericData;
import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;

public abstract class RecordBuilderBase<T extends IndexedRecord> implements RecordBuilder<T>
{
    private static final Schema.Field[] EMPTY_FIELDS;
    private final Schema schema;
    private final Schema.Field[] fields;
    private final boolean[] fieldSetFlags;
    private final GenericData data;
    
    protected final Schema schema() {
        return this.schema;
    }
    
    protected final Schema.Field[] fields() {
        return this.fields;
    }
    
    protected final boolean[] fieldSetFlags() {
        return this.fieldSetFlags;
    }
    
    protected final GenericData data() {
        return this.data;
    }
    
    protected RecordBuilderBase(final Schema schema, final GenericData data) {
        this.schema = schema;
        this.data = data;
        this.fields = schema.getFields().toArray(RecordBuilderBase.EMPTY_FIELDS);
        this.fieldSetFlags = new boolean[this.fields.length];
    }
    
    protected RecordBuilderBase(final RecordBuilderBase<T> other, final GenericData data) {
        this.schema = other.schema;
        this.data = data;
        this.fields = this.schema.getFields().toArray(RecordBuilderBase.EMPTY_FIELDS);
        this.fieldSetFlags = new boolean[other.fieldSetFlags.length];
        System.arraycopy(other.fieldSetFlags, 0, this.fieldSetFlags, 0, this.fieldSetFlags.length);
    }
    
    protected void validate(final Schema.Field field, final Object value) {
        if (isValidValue(field, value)) {
            return;
        }
        if (field.defaultValue() != null) {
            return;
        }
        throw new AvroRuntimeException("Field " + field + " does not accept null values");
    }
    
    protected static boolean isValidValue(final Schema.Field f, final Object value) {
        if (value != null) {
            return true;
        }
        final Schema schema = f.schema();
        final Schema.Type type = schema.getType();
        if (type == Schema.Type.NULL) {
            return true;
        }
        if (type == Schema.Type.UNION) {
            for (final Schema s : schema.getTypes()) {
                if (s.getType() == Schema.Type.NULL) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected Object defaultValue(final Schema.Field field) throws IOException {
        return this.data.deepCopy(field.schema(), this.data.getDefaultValue(field));
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.fieldSetFlags);
        result = 31 * result + ((this.schema == null) ? 0 : this.schema.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final RecordBuilderBase other = (RecordBuilderBase)obj;
        if (!Arrays.equals(this.fieldSetFlags, other.fieldSetFlags)) {
            return false;
        }
        if (this.schema == null) {
            if (other.schema != null) {
                return false;
            }
        }
        else if (!this.schema.equals(other.schema)) {
            return false;
        }
        return true;
    }
    
    static {
        EMPTY_FIELDS = new Schema.Field[0];
    }
}
