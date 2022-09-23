// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.generic;

import org.apache.avro.AvroTypeException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.io.IOException;
import org.apache.avro.io.Encoder;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumWriter;

public class GenericDatumWriter<D> implements DatumWriter<D>
{
    private final GenericData data;
    private Schema root;
    
    public GenericDatumWriter() {
        this(GenericData.get());
    }
    
    protected GenericDatumWriter(final GenericData data) {
        this.data = data;
    }
    
    public GenericDatumWriter(final Schema root) {
        this();
        this.setSchema(root);
    }
    
    public GenericDatumWriter(final Schema root, final GenericData data) {
        this(data);
        this.setSchema(root);
    }
    
    public GenericData getData() {
        return this.data;
    }
    
    @Override
    public void setSchema(final Schema root) {
        this.root = root;
    }
    
    @Override
    public void write(final D datum, final Encoder out) throws IOException {
        this.write(this.root, datum, out);
    }
    
    protected void write(final Schema schema, final Object datum, final Encoder out) throws IOException {
        try {
            switch (schema.getType()) {
                case RECORD: {
                    this.writeRecord(schema, datum, out);
                    break;
                }
                case ENUM: {
                    this.writeEnum(schema, datum, out);
                    break;
                }
                case ARRAY: {
                    this.writeArray(schema, datum, out);
                    break;
                }
                case MAP: {
                    this.writeMap(schema, datum, out);
                    break;
                }
                case UNION: {
                    final int index = this.resolveUnion(schema, datum);
                    out.writeIndex(index);
                    this.write(schema.getTypes().get(index), datum, out);
                    break;
                }
                case FIXED: {
                    this.writeFixed(schema, datum, out);
                    break;
                }
                case STRING: {
                    this.writeString(schema, datum, out);
                    break;
                }
                case BYTES: {
                    this.writeBytes(datum, out);
                    break;
                }
                case INT: {
                    out.writeInt(((Number)datum).intValue());
                    break;
                }
                case LONG: {
                    out.writeLong((long)datum);
                    break;
                }
                case FLOAT: {
                    out.writeFloat((float)datum);
                    break;
                }
                case DOUBLE: {
                    out.writeDouble((double)datum);
                    break;
                }
                case BOOLEAN: {
                    out.writeBoolean((boolean)datum);
                    break;
                }
                case NULL: {
                    out.writeNull();
                    break;
                }
                default: {
                    this.error(schema, datum);
                    break;
                }
            }
        }
        catch (NullPointerException e) {
            throw this.npe(e, " of " + schema.getFullName());
        }
    }
    
    protected NullPointerException npe(final NullPointerException e, final String s) {
        final NullPointerException result = new NullPointerException(e.getMessage() + s);
        result.initCause((e.getCause() == null) ? e : e.getCause());
        return result;
    }
    
    protected void writeRecord(final Schema schema, final Object datum, final Encoder out) throws IOException {
        final Object state = this.data.getRecordState(datum, schema);
        for (final Schema.Field f : schema.getFields()) {
            this.writeField(datum, f, out, state);
        }
    }
    
    protected void writeField(final Object datum, final Schema.Field f, final Encoder out, final Object state) throws IOException {
        final Object value = this.data.getField(datum, f.name(), f.pos(), state);
        try {
            this.write(f.schema(), value, out);
        }
        catch (NullPointerException e) {
            throw this.npe(e, " in field " + f.name());
        }
    }
    
    protected void writeEnum(final Schema schema, final Object datum, final Encoder out) throws IOException {
        out.writeEnum(schema.getEnumOrdinal(datum.toString()));
    }
    
    protected void writeArray(final Schema schema, final Object datum, final Encoder out) throws IOException {
        final Schema element = schema.getElementType();
        final long size = this.getArraySize(datum);
        long actualSize = 0L;
        out.writeArrayStart();
        out.setItemCount(size);
        final Iterator<?> it = this.getArrayElements(datum);
        while (it.hasNext()) {
            out.startItem();
            this.write(element, it.next(), out);
            ++actualSize;
        }
        out.writeArrayEnd();
        if (actualSize != size) {
            throw new ConcurrentModificationException("Size of array written was " + size + ", but number of elements written was " + actualSize + ". ");
        }
    }
    
    protected int resolveUnion(final Schema union, final Object datum) {
        return this.data.resolveUnion(union, datum);
    }
    
    protected long getArraySize(final Object array) {
        return ((Collection)array).size();
    }
    
    protected Iterator<?> getArrayElements(final Object array) {
        return ((Collection)array).iterator();
    }
    
    protected void writeMap(final Schema schema, final Object datum, final Encoder out) throws IOException {
        final Schema value = schema.getValueType();
        final int size = this.getMapSize(datum);
        int actualSize = 0;
        out.writeMapStart();
        out.setItemCount(size);
        for (final Map.Entry<Object, Object> entry : this.getMapEntries(datum)) {
            out.startItem();
            this.writeString(entry.getKey().toString(), out);
            this.write(value, entry.getValue(), out);
            ++actualSize;
        }
        out.writeMapEnd();
        if (actualSize != size) {
            throw new ConcurrentModificationException("Size of map written was " + size + ", but number of entries written was " + actualSize + ". ");
        }
    }
    
    protected int getMapSize(final Object map) {
        return ((Map)map).size();
    }
    
    protected Iterable<Map.Entry<Object, Object>> getMapEntries(final Object map) {
        return (Iterable<Map.Entry<Object, Object>>)((Map)map).entrySet();
    }
    
    protected void writeString(final Schema schema, final Object datum, final Encoder out) throws IOException {
        this.writeString(datum, out);
    }
    
    protected void writeString(final Object datum, final Encoder out) throws IOException {
        out.writeString((CharSequence)datum);
    }
    
    protected void writeBytes(final Object datum, final Encoder out) throws IOException {
        out.writeBytes((ByteBuffer)datum);
    }
    
    protected void writeFixed(final Schema schema, final Object datum, final Encoder out) throws IOException {
        out.writeFixed(((GenericFixed)datum).bytes(), 0, schema.getFixedSize());
    }
    
    private void error(final Schema schema, final Object datum) {
        throw new AvroTypeException("Not a " + schema + ": " + datum);
    }
}
