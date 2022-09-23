// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.generic;

import java.util.Iterator;
import java.nio.ByteBuffer;
import java.lang.reflect.InvocationTargetException;
import org.apache.avro.util.Utf8;
import java.util.Collection;
import org.apache.avro.AvroRuntimeException;
import java.io.IOException;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.util.WeakIdentityHashMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.lang.reflect.Constructor;
import java.util.Map;
import org.apache.avro.io.ResolvingDecoder;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;

public class GenericDatumReader<D> implements DatumReader<D>
{
    private final GenericData data;
    private Schema actual;
    private Schema expected;
    private ResolvingDecoder creatorResolver;
    private final Thread creator;
    private static final ThreadLocal<Map<Schema, Map<Schema, ResolvingDecoder>>> RESOLVER_CACHE;
    private Map<Schema, Class> stringClassCache;
    private final Map<Class, Constructor> stringCtorCache;
    
    public GenericDatumReader() {
        this(null, null, GenericData.get());
    }
    
    public GenericDatumReader(final Schema schema) {
        this(schema, schema, GenericData.get());
    }
    
    public GenericDatumReader(final Schema writer, final Schema reader) {
        this(writer, reader, GenericData.get());
    }
    
    public GenericDatumReader(final Schema writer, final Schema reader, final GenericData data) {
        this(data);
        this.actual = writer;
        this.expected = reader;
    }
    
    protected GenericDatumReader(final GenericData data) {
        this.creatorResolver = null;
        this.stringClassCache = new IdentityHashMap<Schema, Class>();
        this.stringCtorCache = new HashMap<Class, Constructor>();
        this.data = data;
        this.creator = Thread.currentThread();
    }
    
    public GenericData getData() {
        return this.data;
    }
    
    public Schema getSchema() {
        return this.actual;
    }
    
    @Override
    public void setSchema(final Schema writer) {
        this.actual = writer;
        if (this.expected == null) {
            this.expected = this.actual;
        }
        this.creatorResolver = null;
    }
    
    public Schema getExpected() {
        return this.expected;
    }
    
    public void setExpected(final Schema reader) {
        this.expected = reader;
        this.creatorResolver = null;
    }
    
    protected final ResolvingDecoder getResolver(final Schema actual, final Schema expected) throws IOException {
        final Thread currThread = Thread.currentThread();
        if (currThread == this.creator && this.creatorResolver != null) {
            return this.creatorResolver;
        }
        Map<Schema, ResolvingDecoder> cache = GenericDatumReader.RESOLVER_CACHE.get().get(actual);
        if (cache == null) {
            cache = new WeakIdentityHashMap<Schema, ResolvingDecoder>();
            GenericDatumReader.RESOLVER_CACHE.get().put(actual, cache);
        }
        ResolvingDecoder resolver = cache.get(expected);
        if (resolver == null) {
            resolver = DecoderFactory.get().resolvingDecoder(Schema.applyAliases(actual, expected), expected, null);
            cache.put(expected, resolver);
        }
        if (currThread == this.creator) {
            this.creatorResolver = resolver;
        }
        return resolver;
    }
    
    @Override
    public D read(final D reuse, final Decoder in) throws IOException {
        final ResolvingDecoder resolver = this.getResolver(this.actual, this.expected);
        resolver.configure(in);
        final D result = (D)this.read(reuse, this.expected, resolver);
        resolver.drain();
        return result;
    }
    
    protected Object read(final Object old, final Schema expected, final ResolvingDecoder in) throws IOException {
        switch (expected.getType()) {
            case RECORD: {
                return this.readRecord(old, expected, in);
            }
            case ENUM: {
                return this.readEnum(expected, in);
            }
            case ARRAY: {
                return this.readArray(old, expected, in);
            }
            case MAP: {
                return this.readMap(old, expected, in);
            }
            case UNION: {
                return this.read(old, expected.getTypes().get(in.readIndex()), in);
            }
            case FIXED: {
                return this.readFixed(old, expected, in);
            }
            case STRING: {
                return this.readString(old, expected, in);
            }
            case BYTES: {
                return this.readBytes(old, expected, in);
            }
            case INT: {
                return this.readInt(old, expected, in);
            }
            case LONG: {
                return in.readLong();
            }
            case FLOAT: {
                return in.readFloat();
            }
            case DOUBLE: {
                return in.readDouble();
            }
            case BOOLEAN: {
                return in.readBoolean();
            }
            case NULL: {
                in.readNull();
                return null;
            }
            default: {
                throw new AvroRuntimeException("Unknown type: " + expected);
            }
        }
    }
    
    protected Object readRecord(final Object old, final Schema expected, final ResolvingDecoder in) throws IOException {
        final Object r = this.data.newRecord(old, expected);
        final Object state = this.data.getRecordState(r, expected);
        for (final Schema.Field f : in.readFieldOrder()) {
            final int pos = f.pos();
            final String name = f.name();
            Object oldDatum = null;
            if (old != null) {
                oldDatum = this.data.getField(r, name, pos, state);
            }
            this.readField(r, f, oldDatum, in, state);
        }
        return r;
    }
    
    protected void readField(final Object r, final Schema.Field f, final Object oldDatum, final ResolvingDecoder in, final Object state) throws IOException {
        this.data.setField(r, f.name(), f.pos(), this.read(oldDatum, f.schema(), in), state);
    }
    
    protected Object readEnum(final Schema expected, final Decoder in) throws IOException {
        return this.createEnum(expected.getEnumSymbols().get(in.readEnum()), expected);
    }
    
    protected Object createEnum(final String symbol, final Schema schema) {
        return this.data.createEnum(symbol, schema);
    }
    
    protected Object readArray(final Object old, final Schema expected, final ResolvingDecoder in) throws IOException {
        final Schema expectedType = expected.getElementType();
        long l = in.readArrayStart();
        long base = 0L;
        if (l > 0L) {
            final Object array = this.newArray(old, (int)l, expected);
            do {
                for (long i = 0L; i < l; ++i) {
                    this.addToArray(array, base + i, this.read(this.peekArray(array), expectedType, in));
                }
                base += l;
            } while ((l = in.arrayNext()) > 0L);
            return array;
        }
        return this.newArray(old, 0, expected);
    }
    
    protected Object peekArray(final Object array) {
        return (array instanceof GenericArray) ? ((GenericArray)array).peek() : null;
    }
    
    protected void addToArray(final Object array, final long pos, final Object e) {
        ((Collection)array).add(e);
    }
    
    protected Object readMap(final Object old, final Schema expected, final ResolvingDecoder in) throws IOException {
        final Schema eValue = expected.getValueType();
        long l = in.readMapStart();
        final Object map = this.newMap(old, (int)l);
        if (l > 0L) {
            do {
                for (int i = 0; i < l; ++i) {
                    this.addToMap(map, this.readMapKey(null, expected, in), this.read(null, eValue, in));
                }
            } while ((l = in.mapNext()) > 0L);
        }
        return map;
    }
    
    protected Object readMapKey(final Object old, final Schema expected, final Decoder in) throws IOException {
        return this.readString(old, expected, in);
    }
    
    protected void addToMap(final Object map, final Object key, final Object value) {
        ((Map)map).put(key, value);
    }
    
    protected Object readFixed(final Object old, final Schema expected, final Decoder in) throws IOException {
        final GenericFixed fixed = (GenericFixed)this.data.createFixed(old, expected);
        in.readFixed(fixed.bytes(), 0, expected.getFixedSize());
        return fixed;
    }
    
    @Deprecated
    protected Object createFixed(final Object old, final Schema schema) {
        return this.data.createFixed(old, schema);
    }
    
    @Deprecated
    protected Object createFixed(final Object old, final byte[] bytes, final Schema schema) {
        return this.data.createFixed(old, bytes, schema);
    }
    
    @Deprecated
    protected Object newRecord(final Object old, final Schema schema) {
        return this.data.newRecord(old, schema);
    }
    
    protected Object newArray(final Object old, final int size, final Schema schema) {
        if (old instanceof Collection) {
            ((Collection)old).clear();
            return old;
        }
        return new GenericData.Array(size, schema);
    }
    
    protected Object newMap(final Object old, final int size) {
        if (old instanceof Map) {
            ((Map)old).clear();
            return old;
        }
        return new HashMap(size);
    }
    
    protected Object readString(final Object old, final Schema expected, final Decoder in) throws IOException {
        final Class stringClass = this.getStringClass(expected);
        if (stringClass == String.class) {
            return in.readString();
        }
        if (stringClass == CharSequence.class) {
            return this.readString(old, in);
        }
        return this.newInstanceFromString(stringClass, in.readString());
    }
    
    protected Object readString(final Object old, final Decoder in) throws IOException {
        return in.readString((old instanceof Utf8) ? ((Utf8)old) : null);
    }
    
    protected Object createString(final String value) {
        return new Utf8(value);
    }
    
    protected Class findStringClass(final Schema schema) {
        final String name = schema.getProp("avro.java.string");
        if (name == null) {
            return CharSequence.class;
        }
        switch (GenericData.StringType.valueOf(name)) {
            case String: {
                return String.class;
            }
            default: {
                return CharSequence.class;
            }
        }
    }
    
    private Class getStringClass(final Schema s) {
        Class c = this.stringClassCache.get(s);
        if (c == null) {
            c = this.findStringClass(s);
            this.stringClassCache.put(s, c);
        }
        return c;
    }
    
    protected Object newInstanceFromString(final Class c, final String s) {
        try {
            Constructor ctor = this.stringCtorCache.get(c);
            if (ctor == null) {
                ctor = c.getDeclaredConstructor(String.class);
                ctor.setAccessible(true);
                this.stringCtorCache.put(c, ctor);
            }
            return ctor.newInstance(s);
        }
        catch (NoSuchMethodException e) {
            throw new AvroRuntimeException(e);
        }
        catch (InstantiationException e2) {
            throw new AvroRuntimeException(e2);
        }
        catch (IllegalAccessException e3) {
            throw new AvroRuntimeException(e3);
        }
        catch (InvocationTargetException e4) {
            throw new AvroRuntimeException(e4);
        }
    }
    
    protected Object readBytes(final Object old, final Schema s, final Decoder in) throws IOException {
        return this.readBytes(old, in);
    }
    
    protected Object readBytes(final Object old, final Decoder in) throws IOException {
        return in.readBytes((old instanceof ByteBuffer) ? ((ByteBuffer)old) : null);
    }
    
    protected Object readInt(final Object old, final Schema expected, final Decoder in) throws IOException {
        return in.readInt();
    }
    
    protected Object createBytes(final byte[] value) {
        return ByteBuffer.wrap(value);
    }
    
    public static void skip(final Schema schema, final Decoder in) throws IOException {
        switch (schema.getType()) {
            case RECORD: {
                for (final Schema.Field field : schema.getFields()) {
                    skip(field.schema(), in);
                }
                break;
            }
            case ENUM: {
                in.readInt();
                break;
            }
            case ARRAY: {
                final Schema elementType = schema.getElementType();
                for (long l = in.skipArray(); l > 0L; l = in.skipArray()) {
                    for (long i = 0L; i < l; ++i) {
                        skip(elementType, in);
                    }
                }
                break;
            }
            case MAP: {
                final Schema value = schema.getValueType();
                for (long j = in.skipMap(); j > 0L; j = in.skipMap()) {
                    for (long k = 0L; k < j; ++k) {
                        in.skipString();
                        skip(value, in);
                    }
                }
                break;
            }
            case UNION: {
                skip(schema.getTypes().get(in.readIndex()), in);
                break;
            }
            case FIXED: {
                in.skipFixed(schema.getFixedSize());
                break;
            }
            case STRING: {
                in.skipString();
                break;
            }
            case BYTES: {
                in.skipBytes();
                break;
            }
            case INT: {
                in.readInt();
                break;
            }
            case LONG: {
                in.readLong();
                break;
            }
            case FLOAT: {
                in.readFloat();
                break;
            }
            case DOUBLE: {
                in.readDouble();
                break;
            }
            case BOOLEAN: {
                in.readBoolean();
                break;
            }
            case NULL: {
                break;
            }
            default: {
                throw new RuntimeException("Unknown type: " + schema);
            }
        }
    }
    
    static {
        RESOLVER_CACHE = new ThreadLocal<Map<Schema, Map<Schema, ResolvingDecoder>>>() {
            @Override
            protected Map<Schema, Map<Schema, ResolvingDecoder>> initialValue() {
                return new WeakIdentityHashMap<Schema, Map<Schema, ResolvingDecoder>>();
            }
        };
    }
}
