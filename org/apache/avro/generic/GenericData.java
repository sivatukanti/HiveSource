// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.generic;

import org.apache.avro.io.BinaryData;
import java.util.Arrays;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import java.io.IOException;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.parsing.ResolvingGrammarGenerator;
import org.apache.avro.io.BinaryEncoder;
import java.io.OutputStream;
import org.apache.avro.io.EncoderFactory;
import java.io.ByteArrayOutputStream;
import org.apache.avro.util.Utf8;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.UnresolvedUnionException;
import org.apache.avro.AvroTypeException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Collection;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DatumReader;
import java.util.Collections;
import java.util.WeakHashMap;
import org.apache.avro.Schema;
import java.util.Map;

public class GenericData
{
    private static final GenericData INSTANCE;
    protected static final String STRING_PROP = "avro.java.string";
    protected static final String STRING_TYPE_STRING = "String";
    private final ClassLoader classLoader;
    private final Map<Schema.Field, Object> defaultValueCache;
    private static final Schema STRINGS;
    
    public static void setStringType(final Schema s, final StringType stringType) {
        if (stringType == StringType.String) {
            s.addProp("avro.java.string", "String");
        }
    }
    
    public static GenericData get() {
        return GenericData.INSTANCE;
    }
    
    public GenericData() {
        this(null);
    }
    
    public GenericData(final ClassLoader classLoader) {
        this.defaultValueCache = Collections.synchronizedMap(new WeakHashMap<Schema.Field, Object>());
        this.classLoader = ((classLoader != null) ? classLoader : this.getClass().getClassLoader());
    }
    
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
    
    public DatumReader createDatumReader(final Schema schema) {
        return new GenericDatumReader(schema, schema, this);
    }
    
    public DatumReader createDatumReader(final Schema writer, final Schema reader) {
        return new GenericDatumReader(writer, reader, this);
    }
    
    public DatumWriter createDatumWriter(final Schema schema) {
        return new GenericDatumWriter(schema, this);
    }
    
    public boolean validate(final Schema schema, final Object datum) {
        switch (schema.getType()) {
            case RECORD: {
                if (!this.isRecord(datum)) {
                    return false;
                }
                for (final Schema.Field f : schema.getFields()) {
                    if (!this.validate(f.schema(), this.getField(datum, f.name(), f.pos()))) {
                        return false;
                    }
                }
                return true;
            }
            case ENUM: {
                return schema.getEnumSymbols().contains(datum.toString());
            }
            case ARRAY: {
                if (!this.isArray(datum)) {
                    return false;
                }
                for (final Object element : (Collection)datum) {
                    if (!this.validate(schema.getElementType(), element)) {
                        return false;
                    }
                }
                return true;
            }
            case MAP: {
                if (!this.isMap(datum)) {
                    return false;
                }
                final Map<Object, Object> map = (Map<Object, Object>)datum;
                for (final Map.Entry<Object, Object> entry : map.entrySet()) {
                    if (!this.validate(schema.getValueType(), entry.getValue())) {
                        return false;
                    }
                }
                return true;
            }
            case UNION: {
                for (final Schema type : schema.getTypes()) {
                    if (this.validate(type, datum)) {
                        return true;
                    }
                }
                return false;
            }
            case FIXED: {
                return datum instanceof GenericFixed && ((GenericFixed)datum).bytes().length == schema.getFixedSize();
            }
            case STRING: {
                return this.isString(datum);
            }
            case BYTES: {
                return this.isBytes(datum);
            }
            case INT: {
                return this.isInteger(datum);
            }
            case LONG: {
                return this.isLong(datum);
            }
            case FLOAT: {
                return this.isFloat(datum);
            }
            case DOUBLE: {
                return this.isDouble(datum);
            }
            case BOOLEAN: {
                return this.isBoolean(datum);
            }
            case NULL: {
                return datum == null;
            }
            default: {
                return false;
            }
        }
    }
    
    public String toString(final Object datum) {
        final StringBuilder buffer = new StringBuilder();
        this.toString(datum, buffer);
        return buffer.toString();
    }
    
    protected void toString(final Object datum, final StringBuilder buffer) {
        if (this.isRecord(datum)) {
            buffer.append("{");
            int count = 0;
            final Schema schema = this.getRecordSchema(datum);
            for (final Schema.Field f : schema.getFields()) {
                this.toString(f.name(), buffer);
                buffer.append(": ");
                this.toString(this.getField(datum, f.name(), f.pos()), buffer);
                if (++count < schema.getFields().size()) {
                    buffer.append(", ");
                }
            }
            buffer.append("}");
        }
        else if (this.isArray(datum)) {
            final Collection<?> array = (Collection<?>)datum;
            buffer.append("[");
            final long last = array.size() - 1;
            int i = 0;
            for (final Object element : array) {
                this.toString(element, buffer);
                if (i++ < last) {
                    buffer.append(", ");
                }
            }
            buffer.append("]");
        }
        else if (this.isMap(datum)) {
            buffer.append("{");
            int count = 0;
            final Map<Object, Object> map = (Map<Object, Object>)datum;
            for (final Map.Entry<Object, Object> entry : map.entrySet()) {
                this.toString(entry.getKey(), buffer);
                buffer.append(": ");
                this.toString(entry.getValue(), buffer);
                if (++count < map.size()) {
                    buffer.append(", ");
                }
            }
            buffer.append("}");
        }
        else if (this.isString(datum) || this.isEnum(datum)) {
            buffer.append("\"");
            this.writeEscapedString(datum.toString(), buffer);
            buffer.append("\"");
        }
        else if (this.isBytes(datum)) {
            buffer.append("{\"bytes\": \"");
            final ByteBuffer bytes = (ByteBuffer)datum;
            for (int j = bytes.position(); j < bytes.limit(); ++j) {
                buffer.append((char)bytes.get(j));
            }
            buffer.append("\"}");
        }
        else if ((datum instanceof Float && (((Float)datum).isInfinite() || ((Float)datum).isNaN())) || (datum instanceof Double && (((Double)datum).isInfinite() || ((Double)datum).isNaN()))) {
            buffer.append("\"");
            buffer.append(datum);
            buffer.append("\"");
        }
        else {
            buffer.append(datum);
        }
    }
    
    private void writeEscapedString(final String string, final StringBuilder builder) {
        for (int i = 0; i < string.length(); ++i) {
            final char ch = string.charAt(i);
            switch (ch) {
                case '\"': {
                    builder.append("\\\"");
                    break;
                }
                case '\\': {
                    builder.append("\\\\");
                    break;
                }
                case '\b': {
                    builder.append("\\b");
                    break;
                }
                case '\f': {
                    builder.append("\\f");
                    break;
                }
                case '\n': {
                    builder.append("\\n");
                    break;
                }
                case '\r': {
                    builder.append("\\r");
                    break;
                }
                case '\t': {
                    builder.append("\\t");
                    break;
                }
                default: {
                    if ((ch >= '\0' && ch <= '\u001f') || (ch >= '\u007f' && ch <= '\u009f') || (ch >= '\u2000' && ch <= '\u20ff')) {
                        final String hex = Integer.toHexString(ch);
                        builder.append("\\u");
                        for (int j = 0; j < 4 - hex.length(); ++j) {
                            builder.append('0');
                        }
                        builder.append(hex.toUpperCase());
                        break;
                    }
                    builder.append(ch);
                    break;
                }
            }
        }
    }
    
    public Schema induce(final Object datum) {
        if (this.isRecord(datum)) {
            return this.getRecordSchema(datum);
        }
        if (this.isArray(datum)) {
            Schema elementType = null;
            for (final Object element : (Collection)datum) {
                if (elementType == null) {
                    elementType = this.induce(element);
                }
                else {
                    if (!elementType.equals(this.induce(element))) {
                        throw new AvroTypeException("No mixed type arrays.");
                    }
                    continue;
                }
            }
            if (elementType == null) {
                throw new AvroTypeException("Empty array: " + datum);
            }
            return Schema.createArray(elementType);
        }
        else if (this.isMap(datum)) {
            final Map<Object, Object> map = (Map<Object, Object>)datum;
            Schema value = null;
            for (final Map.Entry<Object, Object> entry : map.entrySet()) {
                if (value == null) {
                    value = this.induce(entry.getValue());
                }
                else {
                    if (!value.equals(this.induce(entry.getValue()))) {
                        throw new AvroTypeException("No mixed type map values.");
                    }
                    continue;
                }
            }
            if (value == null) {
                throw new AvroTypeException("Empty map: " + datum);
            }
            return Schema.createMap(value);
        }
        else {
            if (datum instanceof GenericFixed) {
                return Schema.createFixed(null, null, null, ((GenericFixed)datum).bytes().length);
            }
            if (this.isString(datum)) {
                return Schema.create(Schema.Type.STRING);
            }
            if (this.isBytes(datum)) {
                return Schema.create(Schema.Type.BYTES);
            }
            if (this.isInteger(datum)) {
                return Schema.create(Schema.Type.INT);
            }
            if (this.isLong(datum)) {
                return Schema.create(Schema.Type.LONG);
            }
            if (this.isFloat(datum)) {
                return Schema.create(Schema.Type.FLOAT);
            }
            if (this.isDouble(datum)) {
                return Schema.create(Schema.Type.DOUBLE);
            }
            if (this.isBoolean(datum)) {
                return Schema.create(Schema.Type.BOOLEAN);
            }
            if (datum == null) {
                return Schema.create(Schema.Type.NULL);
            }
            throw new AvroTypeException("Can't create schema for: " + datum);
        }
    }
    
    public void setField(final Object record, final String name, final int position, final Object o) {
        ((IndexedRecord)record).put(position, o);
    }
    
    public Object getField(final Object record, final String name, final int position) {
        return ((IndexedRecord)record).get(position);
    }
    
    protected Object getRecordState(final Object record, final Schema schema) {
        return null;
    }
    
    protected void setField(final Object r, final String n, final int p, final Object o, final Object state) {
        this.setField(r, n, p, o);
    }
    
    protected Object getField(final Object record, final String name, final int pos, final Object state) {
        return this.getField(record, name, pos);
    }
    
    public int resolveUnion(final Schema union, final Object datum) {
        final Integer i = union.getIndexNamed(this.getSchemaName(datum));
        if (i != null) {
            return i;
        }
        throw new UnresolvedUnionException(union, datum);
    }
    
    protected String getSchemaName(final Object datum) {
        if (datum == null) {
            return Schema.Type.NULL.getName();
        }
        if (this.isRecord(datum)) {
            return this.getRecordSchema(datum).getFullName();
        }
        if (this.isEnum(datum)) {
            return this.getEnumSchema(datum).getFullName();
        }
        if (this.isArray(datum)) {
            return Schema.Type.ARRAY.getName();
        }
        if (this.isMap(datum)) {
            return Schema.Type.MAP.getName();
        }
        if (this.isFixed(datum)) {
            return this.getFixedSchema(datum).getFullName();
        }
        if (this.isString(datum)) {
            return Schema.Type.STRING.getName();
        }
        if (this.isBytes(datum)) {
            return Schema.Type.BYTES.getName();
        }
        if (this.isInteger(datum)) {
            return Schema.Type.INT.getName();
        }
        if (this.isLong(datum)) {
            return Schema.Type.LONG.getName();
        }
        if (this.isFloat(datum)) {
            return Schema.Type.FLOAT.getName();
        }
        if (this.isDouble(datum)) {
            return Schema.Type.DOUBLE.getName();
        }
        if (this.isBoolean(datum)) {
            return Schema.Type.BOOLEAN.getName();
        }
        throw new AvroRuntimeException(String.format("Unknown datum type %s: %s", datum.getClass().getName(), datum));
    }
    
    protected boolean instanceOf(final Schema schema, final Object datum) {
        switch (schema.getType()) {
            case RECORD: {
                return this.isRecord(datum) && ((schema.getFullName() == null) ? (this.getRecordSchema(datum).getFullName() == null) : schema.getFullName().equals(this.getRecordSchema(datum).getFullName()));
            }
            case ENUM: {
                return this.isEnum(datum) && schema.getFullName().equals(this.getEnumSchema(datum).getFullName());
            }
            case ARRAY: {
                return this.isArray(datum);
            }
            case MAP: {
                return this.isMap(datum);
            }
            case FIXED: {
                return this.isFixed(datum) && schema.getFullName().equals(this.getFixedSchema(datum).getFullName());
            }
            case STRING: {
                return this.isString(datum);
            }
            case BYTES: {
                return this.isBytes(datum);
            }
            case INT: {
                return this.isInteger(datum);
            }
            case LONG: {
                return this.isLong(datum);
            }
            case FLOAT: {
                return this.isFloat(datum);
            }
            case DOUBLE: {
                return this.isDouble(datum);
            }
            case BOOLEAN: {
                return this.isBoolean(datum);
            }
            case NULL: {
                return datum == null;
            }
            default: {
                throw new AvroRuntimeException("Unexpected type: " + schema);
            }
        }
    }
    
    protected boolean isArray(final Object datum) {
        return datum instanceof Collection;
    }
    
    protected boolean isRecord(final Object datum) {
        return datum instanceof IndexedRecord;
    }
    
    protected Schema getRecordSchema(final Object record) {
        return ((GenericContainer)record).getSchema();
    }
    
    protected boolean isEnum(final Object datum) {
        return datum instanceof GenericEnumSymbol;
    }
    
    protected Schema getEnumSchema(final Object enu) {
        return ((GenericContainer)enu).getSchema();
    }
    
    protected boolean isMap(final Object datum) {
        return datum instanceof Map;
    }
    
    protected boolean isFixed(final Object datum) {
        return datum instanceof GenericFixed;
    }
    
    protected Schema getFixedSchema(final Object fixed) {
        return ((GenericContainer)fixed).getSchema();
    }
    
    protected boolean isString(final Object datum) {
        return datum instanceof CharSequence;
    }
    
    protected boolean isBytes(final Object datum) {
        return datum instanceof ByteBuffer;
    }
    
    protected boolean isInteger(final Object datum) {
        return datum instanceof Integer;
    }
    
    protected boolean isLong(final Object datum) {
        return datum instanceof Long;
    }
    
    protected boolean isFloat(final Object datum) {
        return datum instanceof Float;
    }
    
    protected boolean isDouble(final Object datum) {
        return datum instanceof Double;
    }
    
    protected boolean isBoolean(final Object datum) {
        return datum instanceof Boolean;
    }
    
    public int hashCode(final Object o, final Schema s) {
        if (o == null) {
            return 0;
        }
        int hashCode = 1;
        switch (s.getType()) {
            case RECORD: {
                for (final Schema.Field f : s.getFields()) {
                    if (f.order() == Schema.Field.Order.IGNORE) {
                        continue;
                    }
                    hashCode = this.hashCodeAdd(hashCode, this.getField(o, f.name(), f.pos()), f.schema());
                }
                return hashCode;
            }
            case ARRAY: {
                final Collection<?> a = (Collection<?>)o;
                final Schema elementType = s.getElementType();
                for (final Object e : a) {
                    hashCode = this.hashCodeAdd(hashCode, e, elementType);
                }
                return hashCode;
            }
            case UNION: {
                return this.hashCode(o, s.getTypes().get(this.resolveUnion(s, o)));
            }
            case ENUM: {
                return s.getEnumOrdinal(o.toString());
            }
            case NULL: {
                return 0;
            }
            case STRING: {
                return ((o instanceof Utf8) ? o : new Utf8(o.toString())).hashCode();
            }
            default: {
                return o.hashCode();
            }
        }
    }
    
    protected int hashCodeAdd(final int hashCode, final Object o, final Schema s) {
        return 31 * hashCode + this.hashCode(o, s);
    }
    
    public int compare(final Object o1, final Object o2, final Schema s) {
        return this.compare(o1, o2, s, false);
    }
    
    protected int compare(final Object o1, final Object o2, final Schema s, final boolean equals) {
        if (o1 == o2) {
            return 0;
        }
        switch (s.getType()) {
            case RECORD: {
                for (final Schema.Field f : s.getFields()) {
                    if (f.order() == Schema.Field.Order.IGNORE) {
                        continue;
                    }
                    final int pos = f.pos();
                    final String name = f.name();
                    final int compare = this.compare(this.getField(o1, name, pos), this.getField(o2, name, pos), f.schema(), equals);
                    if (compare != 0) {
                        return (f.order() == Schema.Field.Order.DESCENDING) ? (-compare) : compare;
                    }
                }
                return 0;
            }
            case ENUM: {
                return s.getEnumOrdinal(o1.toString()) - s.getEnumOrdinal(o2.toString());
            }
            case ARRAY: {
                final Collection a1 = (Collection)o1;
                final Collection a2 = (Collection)o2;
                final Iterator e1 = a1.iterator();
                final Iterator e2 = a2.iterator();
                final Schema elementType = s.getElementType();
                while (e1.hasNext() && e2.hasNext()) {
                    final int compare2 = this.compare(e1.next(), e2.next(), elementType, equals);
                    if (compare2 != 0) {
                        return compare2;
                    }
                }
                return e1.hasNext() ? 1 : (e2.hasNext() ? -1 : 0);
            }
            case MAP: {
                if (equals) {
                    return ((Map)o1).equals(o2) ? 0 : 1;
                }
                throw new AvroRuntimeException("Can't compare maps!");
            }
            case UNION: {
                final int i1 = this.resolveUnion(s, o1);
                final int i2 = this.resolveUnion(s, o2);
                return (i1 == i2) ? this.compare(o1, o2, s.getTypes().get(i1), equals) : (i1 - i2);
            }
            case NULL: {
                return 0;
            }
            case STRING: {
                final Utf8 u1 = (Utf8)((o1 instanceof Utf8) ? o1 : new Utf8(o1.toString()));
                final Utf8 u2 = (Utf8)((o2 instanceof Utf8) ? o2 : new Utf8(o2.toString()));
                return u1.compareTo(u2);
            }
            default: {
                return ((Comparable)o1).compareTo(o2);
            }
        }
    }
    
    public Object getDefaultValue(final Schema.Field field) {
        final JsonNode json = field.defaultValue();
        if (json == null) {
            throw new AvroRuntimeException("Field " + field + " not set and has no default value");
        }
        if (json.isNull() && (field.schema().getType() == Schema.Type.NULL || (field.schema().getType() == Schema.Type.UNION && field.schema().getTypes().get(0).getType() == Schema.Type.NULL))) {
            return null;
        }
        Object defaultValue = this.defaultValueCache.get(field);
        if (defaultValue == null) {
            try {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(baos, null);
                ResolvingGrammarGenerator.encode(encoder, field.schema(), json);
                encoder.flush();
                final BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(baos.toByteArray(), null);
                defaultValue = this.createDatumReader(field.schema()).read(null, decoder);
                this.defaultValueCache.put(field, defaultValue);
            }
            catch (IOException e) {
                throw new AvroRuntimeException(e);
            }
        }
        return defaultValue;
    }
    
    public <T> T deepCopy(final Schema schema, final T value) {
        if (value == null) {
            return null;
        }
        switch (schema.getType()) {
            case ARRAY: {
                final List<Object> arrayValue = (List<Object>)value;
                final List<Object> arrayCopy = new Array<Object>(arrayValue.size(), schema);
                for (final Object obj : arrayValue) {
                    arrayCopy.add(this.deepCopy(schema.getElementType(), obj));
                }
                return (T)arrayCopy;
            }
            case BOOLEAN: {
                return value;
            }
            case BYTES: {
                final ByteBuffer byteBufferValue = (ByteBuffer)value;
                final int start = byteBufferValue.position();
                final int length = byteBufferValue.limit() - start;
                final byte[] bytesCopy = new byte[length];
                byteBufferValue.get(bytesCopy, 0, length);
                byteBufferValue.position(start);
                return (T)ByteBuffer.wrap(bytesCopy, 0, length);
            }
            case DOUBLE: {
                return value;
            }
            case ENUM: {
                return value;
            }
            case FIXED: {
                return (T)this.createFixed(null, ((GenericFixed)value).bytes(), schema);
            }
            case FLOAT: {
                return value;
            }
            case INT: {
                return value;
            }
            case LONG: {
                return value;
            }
            case MAP: {
                final Map<CharSequence, Object> mapValue = (Map<CharSequence, Object>)value;
                final Map<CharSequence, Object> mapCopy = new HashMap<CharSequence, Object>(mapValue.size());
                for (final Map.Entry<CharSequence, Object> entry : mapValue.entrySet()) {
                    mapCopy.put(this.deepCopy(GenericData.STRINGS, entry.getKey()), this.deepCopy(schema.getValueType(), entry.getValue()));
                }
                return (T)mapCopy;
            }
            case NULL: {
                return null;
            }
            case RECORD: {
                final Object oldState = this.getRecordState(value, schema);
                final Object newRecord = this.newRecord(null, schema);
                final Object newState = this.getRecordState(newRecord, schema);
                for (final Schema.Field f : schema.getFields()) {
                    final int pos = f.pos();
                    final String name = f.name();
                    final Object newValue = this.deepCopy(f.schema(), this.getField(value, name, pos, oldState));
                    this.setField(newRecord, name, pos, newValue, newState);
                }
                return (T)newRecord;
            }
            case STRING: {
                if (value instanceof String) {
                    return value;
                }
                if (value instanceof Utf8) {
                    return (T)new Utf8((Utf8)value);
                }
                return (T)new Utf8(value.toString());
            }
            case UNION: {
                return (T)this.deepCopy(schema.getTypes().get(this.resolveUnion(schema, value)), (Object)value);
            }
            default: {
                throw new AvroRuntimeException("Deep copy failed for schema \"" + schema + "\" and value \"" + value + "\"");
            }
        }
    }
    
    public Object createFixed(final Object old, final Schema schema) {
        if (old instanceof GenericFixed && ((GenericFixed)old).bytes().length == schema.getFixedSize()) {
            return old;
        }
        return new Fixed(schema);
    }
    
    public Object createFixed(final Object old, final byte[] bytes, final Schema schema) {
        final GenericFixed fixed = (GenericFixed)this.createFixed(old, schema);
        System.arraycopy(bytes, 0, fixed.bytes(), 0, schema.getFixedSize());
        return fixed;
    }
    
    public Object createEnum(final String symbol, final Schema schema) {
        return new EnumSymbol(schema, symbol);
    }
    
    public Object newRecord(final Object old, final Schema schema) {
        if (old instanceof IndexedRecord) {
            final IndexedRecord record = (IndexedRecord)old;
            if (record.getSchema() == schema) {
                return record;
            }
        }
        return new Record(schema);
    }
    
    static {
        INSTANCE = new GenericData();
        STRINGS = Schema.create(Schema.Type.STRING);
    }
    
    public enum StringType
    {
        CharSequence, 
        String, 
        Utf8;
    }
    
    public static class Record implements GenericRecord, Comparable<Record>
    {
        private final Schema schema;
        private final Object[] values;
        
        public Record(final Schema schema) {
            if (schema == null || !Schema.Type.RECORD.equals(schema.getType())) {
                throw new AvroRuntimeException("Not a record schema: " + schema);
            }
            this.schema = schema;
            this.values = new Object[schema.getFields().size()];
        }
        
        public Record(final Record other, final boolean deepCopy) {
            this.schema = other.schema;
            this.values = new Object[this.schema.getFields().size()];
            if (deepCopy) {
                for (int ii = 0; ii < this.values.length; ++ii) {
                    this.values[ii] = GenericData.INSTANCE.deepCopy(this.schema.getFields().get(ii).schema(), other.values[ii]);
                }
            }
            else {
                System.arraycopy(other.values, 0, this.values, 0, other.values.length);
            }
        }
        
        @Override
        public Schema getSchema() {
            return this.schema;
        }
        
        @Override
        public void put(final String key, final Object value) {
            final Schema.Field field = this.schema.getField(key);
            if (field == null) {
                throw new AvroRuntimeException("Not a valid schema field: " + key);
            }
            this.values[field.pos()] = value;
        }
        
        @Override
        public void put(final int i, final Object v) {
            this.values[i] = v;
        }
        
        @Override
        public Object get(final String key) {
            final Schema.Field field = this.schema.getField(key);
            if (field == null) {
                return null;
            }
            return this.values[field.pos()];
        }
        
        @Override
        public Object get(final int i) {
            return this.values[i];
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Record)) {
                return false;
            }
            final Record that = (Record)o;
            return this.schema.equals(that.schema) && GenericData.get().compare(this, that, this.schema, true) == 0;
        }
        
        @Override
        public int hashCode() {
            return GenericData.get().hashCode(this, this.schema);
        }
        
        @Override
        public int compareTo(final Record that) {
            return GenericData.get().compare(this, that, this.schema);
        }
        
        @Override
        public String toString() {
            return GenericData.get().toString(this);
        }
    }
    
    public static class Array<T> extends AbstractList<T> implements GenericArray<T>, Comparable<GenericArray<T>>
    {
        private static final Object[] EMPTY;
        private final Schema schema;
        private int size;
        private Object[] elements;
        
        public Array(final int capacity, final Schema schema) {
            this.elements = Array.EMPTY;
            if (schema == null || !Schema.Type.ARRAY.equals(schema.getType())) {
                throw new AvroRuntimeException("Not an array schema: " + schema);
            }
            this.schema = schema;
            if (capacity != 0) {
                this.elements = new Object[capacity];
            }
        }
        
        public Array(final Schema schema, final Collection<T> c) {
            this.elements = Array.EMPTY;
            if (schema == null || !Schema.Type.ARRAY.equals(schema.getType())) {
                throw new AvroRuntimeException("Not an array schema: " + schema);
            }
            this.schema = schema;
            if (c != null) {
                this.elements = new Object[c.size()];
                this.addAll((Collection<? extends T>)c);
            }
        }
        
        @Override
        public Schema getSchema() {
            return this.schema;
        }
        
        @Override
        public int size() {
            return this.size;
        }
        
        @Override
        public void clear() {
            this.size = 0;
        }
        
        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private int position = 0;
                
                @Override
                public boolean hasNext() {
                    return this.position < Array.this.size;
                }
                
                @Override
                public T next() {
                    return (T)Array.this.elements[this.position++];
                }
                
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        
        @Override
        public T get(final int i) {
            if (i >= this.size) {
                throw new IndexOutOfBoundsException("Index " + i + " out of bounds.");
            }
            return (T)this.elements[i];
        }
        
        @Override
        public boolean add(final T o) {
            if (this.size == this.elements.length) {
                final Object[] newElements = new Object[this.size * 3 / 2 + 1];
                System.arraycopy(this.elements, 0, newElements, 0, this.size);
                this.elements = newElements;
            }
            this.elements[this.size++] = o;
            return true;
        }
        
        @Override
        public void add(final int location, final T o) {
            if (location > this.size || location < 0) {
                throw new IndexOutOfBoundsException("Index " + location + " out of bounds.");
            }
            if (this.size == this.elements.length) {
                final Object[] newElements = new Object[this.size * 3 / 2 + 1];
                System.arraycopy(this.elements, 0, newElements, 0, this.size);
                this.elements = newElements;
            }
            System.arraycopy(this.elements, location, this.elements, location + 1, this.size - location);
            this.elements[location] = o;
            ++this.size;
        }
        
        @Override
        public T set(final int i, final T o) {
            if (i >= this.size) {
                throw new IndexOutOfBoundsException("Index " + i + " out of bounds.");
            }
            final T response = (T)this.elements[i];
            this.elements[i] = o;
            return response;
        }
        
        @Override
        public T remove(final int i) {
            if (i >= this.size) {
                throw new IndexOutOfBoundsException("Index " + i + " out of bounds.");
            }
            final T result = (T)this.elements[i];
            --this.size;
            System.arraycopy(this.elements, i + 1, this.elements, i, this.size - i);
            this.elements[this.size] = null;
            return result;
        }
        
        @Override
        public T peek() {
            return (T)((this.size < this.elements.length) ? this.elements[this.size] : null);
        }
        
        @Override
        public int compareTo(final GenericArray<T> that) {
            return GenericData.get().compare(this, that, this.getSchema());
        }
        
        @Override
        public void reverse() {
            for (int left = 0, right = this.elements.length - 1; left < right; ++left, --right) {
                final Object tmp = this.elements[left];
                this.elements[left] = this.elements[right];
                this.elements[right] = tmp;
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder buffer = new StringBuilder();
            buffer.append("[");
            int count = 0;
            for (final T e : this) {
                buffer.append((e == null) ? "null" : e.toString());
                if (++count < this.size()) {
                    buffer.append(", ");
                }
            }
            buffer.append("]");
            return buffer.toString();
        }
        
        static {
            EMPTY = new Object[0];
        }
    }
    
    public static class Fixed implements GenericFixed, Comparable<Fixed>
    {
        private Schema schema;
        private byte[] bytes;
        
        public Fixed(final Schema schema) {
            this.setSchema(schema);
        }
        
        public Fixed(final Schema schema, final byte[] bytes) {
            this.schema = schema;
            this.bytes = bytes;
        }
        
        protected Fixed() {
        }
        
        protected void setSchema(final Schema schema) {
            this.schema = schema;
            this.bytes = new byte[schema.getFixedSize()];
        }
        
        @Override
        public Schema getSchema() {
            return this.schema;
        }
        
        public void bytes(final byte[] bytes) {
            this.bytes = bytes;
        }
        
        @Override
        public byte[] bytes() {
            return this.bytes;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o == this || (o instanceof GenericFixed && Arrays.equals(this.bytes, ((GenericFixed)o).bytes()));
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(this.bytes);
        }
        
        @Override
        public String toString() {
            return Arrays.toString(this.bytes);
        }
        
        @Override
        public int compareTo(final Fixed that) {
            return BinaryData.compareBytes(this.bytes, 0, this.bytes.length, that.bytes, 0, that.bytes.length);
        }
    }
    
    public static class EnumSymbol implements GenericEnumSymbol, Comparable<GenericEnumSymbol>
    {
        private Schema schema;
        private String symbol;
        
        public EnumSymbol(final Schema schema, final String symbol) {
            this.schema = schema;
            this.symbol = symbol;
        }
        
        @Override
        public Schema getSchema() {
            return this.schema;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o == this || (o instanceof GenericEnumSymbol && this.symbol.equals(o.toString()));
        }
        
        @Override
        public int hashCode() {
            return this.symbol.hashCode();
        }
        
        @Override
        public String toString() {
            return this.symbol;
        }
        
        @Override
        public int compareTo(final GenericEnumSymbol that) {
            return GenericData.get().compare(this, that, this.schema);
        }
    }
}
