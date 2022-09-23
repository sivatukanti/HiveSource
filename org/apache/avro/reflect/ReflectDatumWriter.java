// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.reflect;

import java.io.IOException;
import org.apache.avro.AvroRuntimeException;
import java.util.Collection;
import org.apache.avro.io.Encoder;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.Schema;
import java.lang.reflect.Type;
import org.apache.avro.specific.SpecificDatumWriter;

public class ReflectDatumWriter<T> extends SpecificDatumWriter<T>
{
    public ReflectDatumWriter() {
        this(ReflectData.get());
    }
    
    public ReflectDatumWriter(final Class<T> c) {
        this(c, ReflectData.get());
    }
    
    public ReflectDatumWriter(final Class<T> c, final ReflectData data) {
        this(data.getSchema(c), data);
    }
    
    public ReflectDatumWriter(final Schema root) {
        this(root, ReflectData.get());
    }
    
    public ReflectDatumWriter(final Schema root, final ReflectData reflectData) {
        super(root, reflectData);
    }
    
    protected ReflectDatumWriter(final ReflectData reflectData) {
        super(reflectData);
    }
    
    @Override
    protected void writeArray(final Schema schema, final Object datum, final Encoder out) throws IOException {
        if (datum instanceof Collection) {
            super.writeArray(schema, datum, out);
            return;
        }
        final Class<?> elementClass = datum.getClass().getComponentType();
        if (null == elementClass) {
            throw new AvroRuntimeException("Array data must be a Collection or Array");
        }
        final Schema element = schema.getElementType();
        if (elementClass.isPrimitive()) {
            final Schema.Type type = element.getType();
            out.writeArrayStart();
            switch (type) {
                case BOOLEAN: {
                    if (elementClass.isPrimitive()) {
                        ArrayAccessor.writeArray((boolean[])datum, out);
                        break;
                    }
                    break;
                }
                case DOUBLE: {
                    ArrayAccessor.writeArray((double[])datum, out);
                    break;
                }
                case FLOAT: {
                    ArrayAccessor.writeArray((float[])datum, out);
                    break;
                }
                case INT: {
                    if (elementClass.equals(Integer.TYPE)) {
                        ArrayAccessor.writeArray((int[])datum, out);
                        break;
                    }
                    if (elementClass.equals(Character.TYPE)) {
                        ArrayAccessor.writeArray((char[])datum, out);
                        break;
                    }
                    if (elementClass.equals(Short.TYPE)) {
                        ArrayAccessor.writeArray((short[])datum, out);
                        break;
                    }
                    this.arrayError(elementClass, type);
                    break;
                }
                case LONG: {
                    ArrayAccessor.writeArray((long[])datum, out);
                    break;
                }
                default: {
                    this.arrayError(elementClass, type);
                    break;
                }
            }
            out.writeArrayEnd();
        }
        else {
            out.writeArrayStart();
            this.writeObjectArray(element, (Object[])datum, out);
            out.writeArrayEnd();
        }
    }
    
    private void writeObjectArray(final Schema element, final Object[] data, final Encoder out) throws IOException {
        final int size = data.length;
        out.setItemCount(size);
        for (int i = 0; i < size; ++i) {
            this.write(element, data[i], out);
        }
    }
    
    private void arrayError(final Class<?> cl, final Schema.Type type) {
        throw new AvroRuntimeException("Error writing array with inner type " + cl + " and avro type: " + type);
    }
    
    @Override
    protected void writeBytes(final Object datum, final Encoder out) throws IOException {
        if (datum instanceof byte[]) {
            out.writeBytes((byte[])datum);
        }
        else {
            super.writeBytes(datum, out);
        }
    }
    
    @Override
    protected void write(final Schema schema, Object datum, final Encoder out) throws IOException {
        if (datum instanceof Byte) {
            datum = datum;
        }
        else if (datum instanceof Short) {
            datum = datum;
        }
        else if (datum instanceof Character) {
            datum = datum;
        }
        try {
            super.write(schema, datum, out);
        }
        catch (NullPointerException e) {
            final NullPointerException result = new NullPointerException("in " + schema.getFullName() + " " + e.getMessage());
            result.initCause((e.getCause() == null) ? e : e.getCause());
            throw result;
        }
    }
    
    @Override
    protected void writeField(final Object record, final Schema.Field f, final Encoder out, final Object state) throws IOException {
        if (state != null) {
            final FieldAccessor accessor = ((FieldAccessor[])state)[f.pos()];
            if (accessor != null) {
                if (accessor.supportsIO() && (!Schema.Type.UNION.equals(f.schema().getType()) || accessor.isCustomEncoded())) {
                    accessor.write(record, out);
                    return;
                }
                if (accessor.isStringable()) {
                    try {
                        final Object object = accessor.get(record);
                        this.write(f.schema(), (object == null) ? null : object.toString(), out);
                    }
                    catch (IllegalAccessException e) {
                        throw new AvroRuntimeException("Failed to write Stringable", e);
                    }
                    return;
                }
            }
        }
        super.writeField(record, f, out, state);
    }
}
