// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.generic;

import java.io.IOException;
import org.apache.avro.AvroRuntimeException;
import java.util.Iterator;
import org.apache.avro.Schema;
import org.apache.avro.data.RecordBuilderBase;

public class GenericRecordBuilder extends RecordBuilderBase<GenericData.Record>
{
    private final GenericData.Record record;
    
    public GenericRecordBuilder(final Schema schema) {
        super(schema, GenericData.get());
        this.record = new GenericData.Record(schema);
    }
    
    public GenericRecordBuilder(final GenericRecordBuilder other) {
        super(other, GenericData.get());
        this.record = new GenericData.Record(other.record, true);
    }
    
    public GenericRecordBuilder(final GenericData.Record other) {
        super(other.getSchema(), GenericData.get());
        this.record = new GenericData.Record(other, true);
        for (final Schema.Field f : this.schema().getFields()) {
            final Object value = other.get(f.pos());
            if (RecordBuilderBase.isValidValue(f, value)) {
                this.set(f, this.data().deepCopy(f.schema(), value));
            }
        }
    }
    
    public Object get(final String fieldName) {
        return this.get(this.schema().getField(fieldName));
    }
    
    public Object get(final Schema.Field field) {
        return this.get(field.pos());
    }
    
    protected Object get(final int pos) {
        return this.record.get(pos);
    }
    
    public GenericRecordBuilder set(final String fieldName, final Object value) {
        return this.set(this.schema().getField(fieldName), value);
    }
    
    public GenericRecordBuilder set(final Schema.Field field, final Object value) {
        return this.set(field, field.pos(), value);
    }
    
    protected GenericRecordBuilder set(final int pos, final Object value) {
        return this.set(this.fields()[pos], pos, value);
    }
    
    private GenericRecordBuilder set(final Schema.Field field, final int pos, final Object value) {
        this.validate(field, value);
        this.record.put(pos, value);
        this.fieldSetFlags()[pos] = true;
        return this;
    }
    
    public boolean has(final String fieldName) {
        return this.has(this.schema().getField(fieldName));
    }
    
    public boolean has(final Schema.Field field) {
        return this.has(field.pos());
    }
    
    protected boolean has(final int pos) {
        return this.fieldSetFlags()[pos];
    }
    
    public GenericRecordBuilder clear(final String fieldName) {
        return this.clear(this.schema().getField(fieldName));
    }
    
    public GenericRecordBuilder clear(final Schema.Field field) {
        return this.clear(field.pos());
    }
    
    protected GenericRecordBuilder clear(final int pos) {
        this.record.put(pos, null);
        this.fieldSetFlags()[pos] = false;
        return this;
    }
    
    @Override
    public GenericData.Record build() {
        GenericData.Record record;
        try {
            record = new GenericData.Record(this.schema());
        }
        catch (Exception e) {
            throw new AvroRuntimeException(e);
        }
        for (final Schema.Field field : this.fields()) {
            Object value;
            try {
                value = this.getWithDefault(field);
            }
            catch (IOException e2) {
                throw new AvroRuntimeException(e2);
            }
            if (value != null) {
                record.put(field.pos(), value);
            }
        }
        return record;
    }
    
    private Object getWithDefault(final Schema.Field field) throws IOException {
        return this.fieldSetFlags()[field.pos()] ? this.record.get(field.pos()) : this.defaultValue(field);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + ((this.record == null) ? 0 : this.record.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final GenericRecordBuilder other = (GenericRecordBuilder)obj;
        if (this.record == null) {
            if (other.record != null) {
                return false;
            }
        }
        else if (!this.record.equals(other.record)) {
            return false;
        }
        return true;
    }
}
