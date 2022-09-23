// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.reflect;

import java.nio.ByteBuffer;
import org.apache.avro.io.Decoder;
import java.io.IOException;
import org.apache.avro.io.ResolvingDecoder;
import org.apache.avro.AvroRuntimeException;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.reflect.Array;
import org.apache.avro.specific.SpecificData;
import java.lang.reflect.Type;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificDatumReader;

public class ReflectDatumReader<T> extends SpecificDatumReader<T>
{
    public ReflectDatumReader() {
        this(null, null, ReflectData.get());
    }
    
    public ReflectDatumReader(final Class<T> c) {
        this(new ReflectData(c.getClassLoader()));
        this.setSchema(this.getSpecificData().getSchema(c));
    }
    
    public ReflectDatumReader(final Schema root) {
        this(root, root, ReflectData.get());
    }
    
    public ReflectDatumReader(final Schema writer, final Schema reader) {
        this(writer, reader, ReflectData.get());
    }
    
    public ReflectDatumReader(final Schema writer, final Schema reader, final ReflectData data) {
        super(writer, reader, data);
    }
    
    public ReflectDatumReader(final ReflectData data) {
        super(data);
    }
    
    @Override
    protected Object newArray(final Object old, final int size, final Schema schema) {
        final Class<?> collectionClass = (Class<?>)ReflectData.getClassProp(schema, "java-class");
        Class<?> elementClass = (Class<?>)ReflectData.getClassProp(schema, "java-element-class");
        if (collectionClass == null && elementClass == null) {
            return super.newArray(old, size, schema);
        }
        if (collectionClass == null || collectionClass.isArray()) {
            if (elementClass == null) {
                elementClass = collectionClass.getComponentType();
            }
            if (elementClass == null) {
                final ReflectData data = (ReflectData)this.getData();
                elementClass = (Class<?>)data.getClass(schema.getElementType());
            }
            return Array.newInstance(elementClass, size);
        }
        if (old instanceof Collection) {
            ((Collection)old).clear();
            return old;
        }
        if (collectionClass.isAssignableFrom(ArrayList.class)) {
            return new ArrayList();
        }
        return SpecificData.newInstance(collectionClass, schema);
    }
    
    @Override
    protected Object peekArray(final Object array) {
        return null;
    }
    
    @Override
    protected void addToArray(final Object array, final long pos, final Object e) {
        throw new AvroRuntimeException("reflectDatumReader does not use addToArray");
    }
    
    @Override
    protected Object readArray(final Object old, final Schema expected, final ResolvingDecoder in) throws IOException {
        final Schema expectedType = expected.getElementType();
        final long l = in.readArrayStart();
        if (l <= 0L) {
            return this.newArray(old, 0, expected);
        }
        final Object array = this.newArray(old, (int)l, expected);
        if (array instanceof Collection) {
            final Collection<Object> c = (Collection<Object>)array;
            return this.readCollection(c, expectedType, l, in);
        }
        return this.readJavaArray(array, expectedType, l, in);
    }
    
    private Object readJavaArray(final Object array, final Schema expectedType, final long l, final ResolvingDecoder in) throws IOException {
        final Class<?> elementType = array.getClass().getComponentType();
        if (elementType.isPrimitive()) {
            return this.readPrimitiveArray(array, elementType, l, in);
        }
        return this.readObjectArray((Object[])array, expectedType, l, in);
    }
    
    private Object readPrimitiveArray(final Object array, final Class<?> c, final long l, final ResolvingDecoder in) throws IOException {
        return ArrayAccessor.readArray(array, c, l, in);
    }
    
    private Object readObjectArray(final Object[] array, final Schema expectedType, long l, final ResolvingDecoder in) throws IOException {
        int index = 0;
        do {
            for (int limit = index + (int)l; index < limit; ++index) {
                final Object element = this.read(null, expectedType, in);
                array[index] = element;
            }
        } while ((l = in.arrayNext()) > 0L);
        return array;
    }
    
    private Object readCollection(final Collection<Object> c, final Schema expectedType, long l, final ResolvingDecoder in) throws IOException {
        do {
            for (int i = 0; i < l; ++i) {
                final Object element = this.read(null, expectedType, in);
                c.add(element);
            }
        } while ((l = in.arrayNext()) > 0L);
        return c;
    }
    
    @Override
    protected Object readString(final Object old, final Decoder in) throws IOException {
        return super.readString(null, in).toString();
    }
    
    @Override
    protected Object createString(final String value) {
        return value;
    }
    
    @Override
    protected Object readBytes(final Object old, final Schema s, final Decoder in) throws IOException {
        final ByteBuffer bytes = in.readBytes(null);
        final Class<?> c = (Class<?>)ReflectData.getClassProp(s, "java-class");
        if (c != null && c.isArray()) {
            final byte[] result = new byte[bytes.remaining()];
            bytes.get(result);
            return result;
        }
        return bytes;
    }
    
    @Override
    protected Object readInt(final Object old, final Schema expected, final Decoder in) throws IOException {
        Object value = in.readInt();
        final String intClass = expected.getProp("java-class");
        if (Byte.class.getName().equals(intClass)) {
            value = ((Integer)value).byteValue();
        }
        else if (Short.class.getName().equals(intClass)) {
            value = ((Integer)value).shortValue();
        }
        else if (Character.class.getName().equals(intClass)) {
            value = (char)(int)value;
        }
        return value;
    }
    
    @Override
    protected void readField(final Object record, final Schema.Field f, final Object oldDatum, final ResolvingDecoder in, final Object state) throws IOException {
        if (state != null) {
            final FieldAccessor accessor = ((FieldAccessor[])state)[f.pos()];
            if (accessor != null) {
                if (accessor.supportsIO() && (!Schema.Type.UNION.equals(f.schema().getType()) || accessor.isCustomEncoded())) {
                    accessor.read(record, in);
                    return;
                }
                if (accessor.isStringable()) {
                    try {
                        final String asString = (String)this.read(null, f.schema(), in);
                        accessor.set(record, (asString == null) ? null : this.newInstanceFromString(accessor.getField().getType(), asString));
                        return;
                    }
                    catch (Exception e) {
                        throw new AvroRuntimeException("Failed to read Stringable", e);
                    }
                }
            }
        }
        super.readField(record, f, oldDatum, in, state);
    }
}
