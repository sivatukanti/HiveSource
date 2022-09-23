// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.reflect;

import java.util.HashMap;
import org.apache.avro.io.BinaryData;
import org.apache.avro.AvroRemoteException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Collections;
import org.apache.avro.Protocol;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.NullNode;
import java.lang.annotation.Annotation;
import org.apache.avro.specific.FixedSize;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import org.apache.avro.AvroTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.GenericArrayType;
import org.apache.avro.util.ClassUtils;
import java.lang.reflect.Array;
import org.apache.avro.generic.GenericContainer;
import java.lang.reflect.Type;
import org.apache.avro.generic.GenericFixed;
import java.util.Collection;
import java.io.IOException;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DatumReader;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import java.lang.reflect.Field;
import org.apache.avro.Schema;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.avro.specific.SpecificData;

public class ReflectData extends SpecificData
{
    private static final ReflectData INSTANCE;
    static final ConcurrentHashMap<Class<?>, ClassAccessorData> ACCESSOR_CACHE;
    @Deprecated
    static final String CLASS_PROP = "java-class";
    @Deprecated
    static final String KEY_CLASS_PROP = "java-key-class";
    @Deprecated
    static final String ELEMENT_PROP = "java-element-class";
    private static final Map<String, Class> CLASS_CACHE;
    private static final Class BYTES_CLASS;
    private static final IdentityHashMap<Class, Class> ARRAY_CLASSES;
    private static final Schema THROWABLE_MESSAGE;
    private static final Map<Class<?>, Field[]> FIELDS_CACHE;
    private final Paranamer paranamer;
    
    public ReflectData() {
        this.paranamer = new CachingParanamer();
    }
    
    public ReflectData(final ClassLoader classLoader) {
        super(classLoader);
        this.paranamer = new CachingParanamer();
    }
    
    public static ReflectData get() {
        return ReflectData.INSTANCE;
    }
    
    public ReflectData addStringable(final Class c) {
        this.stringableClasses.add(c);
        return this;
    }
    
    @Override
    public DatumReader createDatumReader(final Schema schema) {
        return new ReflectDatumReader(schema, schema, this);
    }
    
    @Override
    public DatumReader createDatumReader(final Schema writer, final Schema reader) {
        return new ReflectDatumReader(writer, reader, this);
    }
    
    @Override
    public DatumWriter createDatumWriter(final Schema schema) {
        return new ReflectDatumWriter(schema, this);
    }
    
    @Override
    public void setField(final Object record, final String name, final int position, final Object o) {
        this.setField(record, name, position, o, null);
    }
    
    @Override
    protected void setField(final Object record, final String name, final int pos, final Object o, final Object state) {
        if (record instanceof IndexedRecord) {
            super.setField(record, name, pos, o);
            return;
        }
        try {
            this.getAccessorForField(record, name, pos, state).set(record, o);
        }
        catch (IllegalAccessException e) {
            throw new AvroRuntimeException(e);
        }
        catch (IOException e2) {
            throw new AvroRuntimeException(e2);
        }
    }
    
    @Override
    public Object getField(final Object record, final String name, final int position) {
        return this.getField(record, name, position, null);
    }
    
    @Override
    protected Object getField(final Object record, final String name, final int pos, final Object state) {
        if (record instanceof IndexedRecord) {
            return super.getField(record, name, pos);
        }
        try {
            return this.getAccessorForField(record, name, pos, state).get(record);
        }
        catch (IllegalAccessException e) {
            throw new AvroRuntimeException(e);
        }
    }
    
    private FieldAccessor getAccessorForField(final Object record, final String name, final int pos, final Object optionalState) {
        if (optionalState != null) {
            return ((FieldAccessor[])optionalState)[pos];
        }
        return this.getFieldAccessor(record.getClass(), name);
    }
    
    @Override
    protected boolean isRecord(final Object datum) {
        return datum != null && (super.isRecord(datum) || (!(datum instanceof Collection) && !(datum instanceof Map) && !(datum instanceof GenericFixed) && this.getSchema(datum.getClass()).getType() == Schema.Type.RECORD));
    }
    
    @Override
    protected boolean isArray(final Object datum) {
        return datum != null && (datum instanceof Collection || datum.getClass().isArray());
    }
    
    @Override
    protected boolean isBytes(final Object datum) {
        if (datum == null) {
            return false;
        }
        if (super.isBytes(datum)) {
            return true;
        }
        final Class c = datum.getClass();
        return c.isArray() && c.getComponentType() == Byte.TYPE;
    }
    
    @Override
    protected Schema getRecordSchema(final Object record) {
        if (record instanceof GenericContainer) {
            return super.getRecordSchema(record);
        }
        return this.getSchema(record.getClass());
    }
    
    @Override
    public boolean validate(final Schema schema, final Object datum) {
        switch (schema.getType()) {
            case ARRAY: {
                if (!datum.getClass().isArray()) {
                    return super.validate(schema, datum);
                }
                for (int length = java.lang.reflect.Array.getLength(datum), i = 0; i < length; ++i) {
                    if (!this.validate(schema.getElementType(), java.lang.reflect.Array.get(datum, i))) {
                        return false;
                    }
                }
                return true;
            }
            default: {
                return super.validate(schema, datum);
            }
        }
    }
    
    private ClassAccessorData getClassAccessorData(final Class<?> c) {
        ClassAccessorData data = ReflectData.ACCESSOR_CACHE.get(c);
        if (data == null && !IndexedRecord.class.isAssignableFrom(c)) {
            final ClassAccessorData newData = new ClassAccessorData((Class)c);
            data = ReflectData.ACCESSOR_CACHE.putIfAbsent(c, newData);
            if (null == data) {
                data = newData;
            }
        }
        return data;
    }
    
    private FieldAccessor[] getFieldAccessors(final Class<?> c, final Schema s) {
        final ClassAccessorData data = this.getClassAccessorData(c);
        if (data != null) {
            return data.getAccessorsFor(s);
        }
        return null;
    }
    
    private FieldAccessor getFieldAccessor(final Class<?> c, final String fieldName) {
        final ClassAccessorData data = this.getClassAccessorData(c);
        if (data != null) {
            return data.getAccessorFor(fieldName);
        }
        return null;
    }
    
    static Class getClassProp(final Schema schema, final String prop) {
        final String name = schema.getProp(prop);
        if (name == null) {
            return null;
        }
        Class c = ReflectData.CLASS_CACHE.get(name);
        if (c != null) {
            return c;
        }
        try {
            c = ClassUtils.forName(name);
            ReflectData.CLASS_CACHE.put(name, c);
        }
        catch (ClassNotFoundException e) {
            throw new AvroRuntimeException(e);
        }
        return c;
    }
    
    @Override
    public Class getClass(final Schema schema) {
        switch (schema.getType()) {
            case ARRAY: {
                final Class collectionClass = getClassProp(schema, "java-class");
                if (collectionClass != null) {
                    return collectionClass;
                }
                final Class elementClass = this.getClass(schema.getElementType());
                if (elementClass.isPrimitive()) {
                    return ReflectData.ARRAY_CLASSES.get(elementClass);
                }
                return java.lang.reflect.Array.newInstance(elementClass, 0).getClass();
            }
            case STRING: {
                final Class stringClass = getClassProp(schema, "java-class");
                if (stringClass != null) {
                    return stringClass;
                }
                return String.class;
            }
            case BYTES: {
                return ReflectData.BYTES_CLASS;
            }
            case INT: {
                final String intClass = schema.getProp("java-class");
                if (Byte.class.getName().equals(intClass)) {
                    return Byte.TYPE;
                }
                if (Short.class.getName().equals(intClass)) {
                    return Short.TYPE;
                }
                if (Character.class.getName().equals(intClass)) {
                    return Character.TYPE;
                }
                break;
            }
        }
        return super.getClass(schema);
    }
    
    @Override
    protected Schema createSchema(final Type type, final Map<String, Schema> names) {
        if (!(type instanceof GenericArrayType)) {
            if (type instanceof ParameterizedType) {
                final ParameterizedType ptype = (ParameterizedType)type;
                final Class raw = (Class)ptype.getRawType();
                final Type[] params = ptype.getActualTypeArguments();
                if (Map.class.isAssignableFrom(raw)) {
                    final Schema schema = Schema.createMap(this.createSchema(params[1], names));
                    final Class key = (Class)params[0];
                    if (this.isStringable(key)) {
                        schema.addProp("java-key-class", key.getName());
                    }
                    else if (key != String.class) {
                        throw new AvroTypeException("Map key class not String: " + key);
                    }
                    return schema;
                }
                if (Collection.class.isAssignableFrom(raw)) {
                    if (params.length != 1) {
                        throw new AvroTypeException("No array type specified.");
                    }
                    final Schema schema = Schema.createArray(this.createSchema(params[0], names));
                    schema.addProp("java-class", raw.getName());
                    return schema;
                }
            }
            else {
                if (type == Byte.class || type == Byte.TYPE) {
                    final Schema result = Schema.create(Schema.Type.INT);
                    result.addProp("java-class", Byte.class.getName());
                    return result;
                }
                if (type == Short.class || type == Short.TYPE) {
                    final Schema result = Schema.create(Schema.Type.INT);
                    result.addProp("java-class", Short.class.getName());
                    return result;
                }
                if (type == Character.class || type == Character.TYPE) {
                    final Schema result = Schema.create(Schema.Type.INT);
                    result.addProp("java-class", Character.class.getName());
                    return result;
                }
                if (type instanceof Class) {
                    final Class<?> c = (Class<?>)type;
                    if (c.isPrimitive() || c == Void.class || c == Boolean.class || c == Integer.class || c == Long.class || c == Float.class || c == Double.class || c == Byte.class || c == Short.class || c == Character.class) {
                        return super.createSchema(type, names);
                    }
                    if (c.isArray()) {
                        final Class component = c.getComponentType();
                        if (component == Byte.TYPE) {
                            final Schema result2 = Schema.create(Schema.Type.BYTES);
                            result2.addProp("java-class", c.getName());
                            return result2;
                        }
                        final Schema result2 = Schema.createArray(this.createSchema(component, names));
                        result2.addProp("java-class", c.getName());
                        this.setElement(result2, component);
                        return result2;
                    }
                    else {
                        final AvroSchema explicit = c.getAnnotation(AvroSchema.class);
                        if (explicit != null) {
                            return Schema.parse(explicit.value());
                        }
                        if (CharSequence.class.isAssignableFrom(c)) {
                            return Schema.create(Schema.Type.STRING);
                        }
                        if (ByteBuffer.class.isAssignableFrom(c)) {
                            return Schema.create(Schema.Type.BYTES);
                        }
                        if (Collection.class.isAssignableFrom(c)) {
                            throw new AvroRuntimeException("Can't find element type of Collection");
                        }
                        final String fullName = c.getName();
                        Schema schema = names.get(fullName);
                        if (schema == null) {
                            final String name = c.getSimpleName();
                            String space = (c.getPackage() == null) ? "" : c.getPackage().getName();
                            if (c.getEnclosingClass() != null) {
                                space = c.getEnclosingClass().getName() + "$";
                            }
                            final Union union = c.getAnnotation(Union.class);
                            if (union != null) {
                                return this.getAnnotatedUnion(union, names);
                            }
                            if (this.isStringable(c)) {
                                final Schema result3 = Schema.create(Schema.Type.STRING);
                                result3.addProp("java-class", c.getName());
                                return result3;
                            }
                            if (c.isEnum()) {
                                final List<String> symbols = new ArrayList<String>();
                                final Enum[] constants = (Enum[])c.getEnumConstants();
                                for (int i = 0; i < constants.length; ++i) {
                                    symbols.add(constants[i].name());
                                }
                                schema = Schema.createEnum(name, null, space, symbols);
                                this.consumeAvroAliasAnnotation(c, schema);
                            }
                            else if (GenericFixed.class.isAssignableFrom(c)) {
                                final int size = c.getAnnotation(FixedSize.class).value();
                                schema = Schema.createFixed(name, null, space, size);
                                this.consumeAvroAliasAnnotation(c, schema);
                            }
                            else {
                                if (IndexedRecord.class.isAssignableFrom(c)) {
                                    return super.createSchema(type, names);
                                }
                                final List<Schema.Field> fields = new ArrayList<Schema.Field>();
                                final boolean error = Throwable.class.isAssignableFrom(c);
                                schema = Schema.createRecord(name, null, space, error);
                                this.consumeAvroAliasAnnotation(c, schema);
                                names.put(c.getName(), schema);
                                for (final Field field : getCachedFields(c)) {
                                    if ((field.getModifiers() & 0x88) == 0x0 && !field.isAnnotationPresent(AvroIgnore.class)) {
                                        final Schema fieldSchema = this.createFieldSchema(field, names);
                                        final AvroDefault defaultAnnotation = field.getAnnotation(AvroDefault.class);
                                        JsonNode defaultValue = (defaultAnnotation == null) ? null : Schema.parseJson(defaultAnnotation.value());
                                        if (defaultValue == null && fieldSchema.getType() == Schema.Type.UNION) {
                                            final Schema defaultType = fieldSchema.getTypes().get(0);
                                            if (defaultType.getType() == Schema.Type.NULL) {
                                                defaultValue = NullNode.getInstance();
                                            }
                                        }
                                        final AvroName annotatedName = field.getAnnotation(AvroName.class);
                                        final String fieldName = (annotatedName != null) ? annotatedName.value() : field.getName();
                                        final Schema.Field recordField = new Schema.Field(fieldName, fieldSchema, null, defaultValue);
                                        final AvroMeta meta = field.getAnnotation(AvroMeta.class);
                                        if (meta != null) {
                                            recordField.addProp(meta.key(), meta.value());
                                        }
                                        for (final Schema.Field f : fields) {
                                            if (f.name().equals(fieldName)) {
                                                throw new AvroTypeException("double field entry: " + fieldName);
                                            }
                                        }
                                        fields.add(recordField);
                                    }
                                }
                                if (error) {
                                    fields.add(new Schema.Field("detailMessage", ReflectData.THROWABLE_MESSAGE, null, null));
                                }
                                schema.setFields(fields);
                                final AvroMeta meta2 = c.getAnnotation(AvroMeta.class);
                                if (meta2 != null) {
                                    schema.addProp(meta2.key(), meta2.value());
                                }
                            }
                            names.put(fullName, schema);
                        }
                        return schema;
                    }
                }
            }
            return super.createSchema(type, names);
        }
        final Type component2 = ((GenericArrayType)type).getGenericComponentType();
        if (component2 == Byte.TYPE) {
            return Schema.create(Schema.Type.BYTES);
        }
        final Schema result4 = Schema.createArray(this.createSchema(component2, names));
        this.setElement(result4, component2);
        return result4;
    }
    
    @Override
    protected boolean isStringable(final Class<?> c) {
        return c.isAnnotationPresent(Stringable.class) || super.isStringable(c);
    }
    
    private void setElement(final Schema schema, final Type element) {
        if (!(element instanceof Class)) {
            return;
        }
        final Class<?> c = (Class<?>)element;
        final Union union = c.getAnnotation(Union.class);
        if (union != null) {
            schema.addProp("java-element-class", c.getName());
        }
    }
    
    private Schema getAnnotatedUnion(final Union union, final Map<String, Schema> names) {
        final List<Schema> branches = new ArrayList<Schema>();
        for (final Class branch : union.value()) {
            branches.add(this.createSchema(branch, names));
        }
        return Schema.createUnion(branches);
    }
    
    public static Schema makeNullable(final Schema schema) {
        return Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.NULL), schema));
    }
    
    private static Field[] getCachedFields(final Class<?> recordClass) {
        Field[] fieldsList = ReflectData.FIELDS_CACHE.get(recordClass);
        if (fieldsList != null) {
            return fieldsList;
        }
        fieldsList = getFields(recordClass, true);
        ReflectData.FIELDS_CACHE.put(recordClass, fieldsList);
        return fieldsList;
    }
    
    private static Field[] getFields(final Class<?> recordClass, final boolean excludeJava) {
        final Map<String, Field> fields = new LinkedHashMap<String, Field>();
        Class<?> c = recordClass;
        while (!excludeJava || c.getPackage() == null || !c.getPackage().getName().startsWith("java.")) {
            for (final Field field : c.getDeclaredFields()) {
                if ((field.getModifiers() & 0x88) == 0x0 && fields.put(field.getName(), field) != null) {
                    throw new AvroTypeException(c + " contains two fields named: " + field);
                }
            }
            c = c.getSuperclass();
            if (c == null) {
                final Field[] fieldsList = fields.values().toArray(new Field[0]);
                return fieldsList;
            }
        }
        return fields.values().toArray(new Field[0]);
    }
    
    protected Schema createFieldSchema(final Field field, final Map<String, Schema> names) {
        final AvroEncode enc = field.getAnnotation(AvroEncode.class);
        if (enc != null) {
            try {
                return ((CustomEncoding)enc.using().newInstance()).getSchema();
            }
            catch (Exception e) {
                throw new AvroRuntimeException("Could not create schema from custom serializer for " + field.getName());
            }
        }
        final AvroSchema explicit = field.getAnnotation(AvroSchema.class);
        if (explicit != null) {
            return Schema.parse(explicit.value());
        }
        Schema schema = this.createSchema(field.getGenericType(), names);
        if (field.isAnnotationPresent(Stringable.class)) {
            schema = Schema.create(Schema.Type.STRING);
        }
        if (field.isAnnotationPresent(Nullable.class)) {
            schema = makeNullable(schema);
        }
        return schema;
    }
    
    @Override
    public Protocol getProtocol(final Class iface) {
        final Protocol protocol = new Protocol(iface.getSimpleName(), (iface.getPackage() == null) ? "" : iface.getPackage().getName());
        final Map<String, Schema> names = new LinkedHashMap<String, Schema>();
        final Map<String, Protocol.Message> messages = protocol.getMessages();
        for (final Method method : iface.getMethods()) {
            if ((method.getModifiers() & 0x8) == 0x0) {
                final String name = method.getName();
                if (messages.containsKey(name)) {
                    throw new AvroTypeException("Two methods with same name: " + name);
                }
                messages.put(name, this.getMessage(method, protocol, names));
            }
        }
        final List<Schema> types = new ArrayList<Schema>();
        types.addAll(names.values());
        Collections.reverse(types);
        protocol.setTypes(types);
        return protocol;
    }
    
    private Protocol.Message getMessage(final Method method, final Protocol protocol, final Map<String, Schema> names) {
        final List<Schema.Field> fields = new ArrayList<Schema.Field>();
        final String[] paramNames = this.paranamer.lookupParameterNames(method);
        final Type[] paramTypes = method.getGenericParameterTypes();
        final Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < paramTypes.length; ++i) {
            Schema paramSchema = this.getSchema(paramTypes[i], names);
            for (int j = 0; j < annotations[i].length; ++j) {
                final Annotation annotation = annotations[i][j];
                if (annotation instanceof AvroSchema) {
                    paramSchema = Schema.parse(((AvroSchema)annotation).value());
                }
                else if (annotation instanceof Union) {
                    paramSchema = this.getAnnotatedUnion((Union)annotation, names);
                }
                else if (annotation instanceof Nullable) {
                    paramSchema = makeNullable(paramSchema);
                }
            }
            final String paramName = (paramNames.length == paramTypes.length) ? paramNames[i] : (paramSchema.getName() + i);
            fields.add(new Schema.Field(paramName, paramSchema, null, null));
        }
        final Schema request = Schema.createRecord(fields);
        final Union union = method.getAnnotation(Union.class);
        Schema response = (union == null) ? this.getSchema(method.getGenericReturnType(), names) : this.getAnnotatedUnion(union, names);
        if (method.isAnnotationPresent(Nullable.class)) {
            response = makeNullable(response);
        }
        final AvroSchema explicit = method.getAnnotation(AvroSchema.class);
        if (explicit != null) {
            response = Schema.parse(explicit.value());
        }
        final List<Schema> errs = new ArrayList<Schema>();
        errs.add(Protocol.SYSTEM_ERROR);
        for (final Type err : method.getGenericExceptionTypes()) {
            if (err != AvroRemoteException.class) {
                errs.add(this.getSchema(err, names));
            }
        }
        final Schema errors = Schema.createUnion(errs);
        return protocol.createMessage(method.getName(), null, request, response, errors);
    }
    
    private Schema getSchema(final Type type, final Map<String, Schema> names) {
        try {
            return this.createSchema(type, names);
        }
        catch (AvroTypeException e) {
            throw new AvroTypeException("Error getting schema for " + type + ": " + e.getMessage(), e);
        }
    }
    
    @Override
    protected int compare(final Object o1, final Object o2, final Schema s, final boolean equals) {
        switch (s.getType()) {
            case ARRAY: {
                if (!o1.getClass().isArray()) {
                    break;
                }
                final Schema elementType = s.getElementType();
                final int l1 = java.lang.reflect.Array.getLength(o1);
                final int l2 = java.lang.reflect.Array.getLength(o2);
                for (int i = Math.min(l1, l2), j = 0; j < i; ++j) {
                    final int compare = this.compare(java.lang.reflect.Array.get(o1, j), java.lang.reflect.Array.get(o2, j), elementType, equals);
                    if (compare != 0) {
                        return compare;
                    }
                }
                return l1 - l2;
            }
            case BYTES: {
                if (!o1.getClass().isArray()) {
                    break;
                }
                final byte[] b1 = (byte[])o1;
                final byte[] b2 = (byte[])o2;
                return BinaryData.compareBytes(b1, 0, b1.length, b2, 0, b2.length);
            }
        }
        return super.compare(o1, o2, s, equals);
    }
    
    @Override
    protected Object getRecordState(final Object record, final Schema schema) {
        return this.getFieldAccessors(record.getClass(), schema);
    }
    
    private void consumeAvroAliasAnnotation(final Class<?> c, final Schema schema) {
        final AvroAlias alias = c.getAnnotation(AvroAlias.class);
        if (alias != null) {
            String space = alias.space();
            if ("NOT A VALID NAMESPACE".equals(space)) {
                space = null;
            }
            schema.addAlias(alias.alias(), space);
        }
    }
    
    static {
        INSTANCE = new ReflectData();
        ACCESSOR_CACHE = new ConcurrentHashMap<Class<?>, ClassAccessorData>();
        CLASS_CACHE = new ConcurrentHashMap<String, Class>();
        BYTES_CLASS = new byte[0].getClass();
        (ARRAY_CLASSES = new IdentityHashMap<Class, Class>()).put(Byte.TYPE, byte[].class);
        ReflectData.ARRAY_CLASSES.put(Character.TYPE, char[].class);
        ReflectData.ARRAY_CLASSES.put(Short.TYPE, short[].class);
        ReflectData.ARRAY_CLASSES.put(Integer.TYPE, int[].class);
        ReflectData.ARRAY_CLASSES.put(Long.TYPE, long[].class);
        ReflectData.ARRAY_CLASSES.put(Float.TYPE, float[].class);
        ReflectData.ARRAY_CLASSES.put(Double.TYPE, double[].class);
        ReflectData.ARRAY_CLASSES.put(Boolean.TYPE, boolean[].class);
        THROWABLE_MESSAGE = makeNullable(Schema.create(Schema.Type.STRING));
        FIELDS_CACHE = new ConcurrentHashMap<Class<?>, Field[]>();
    }
    
    public static class AllowNull extends ReflectData
    {
        private static final AllowNull INSTANCE;
        
        public static AllowNull get() {
            return AllowNull.INSTANCE;
        }
        
        @Override
        protected Schema createFieldSchema(final Field field, final Map<String, Schema> names) {
            final Schema schema = super.createFieldSchema(field, names);
            return ReflectData.makeNullable(schema);
        }
        
        static {
            INSTANCE = new AllowNull();
        }
    }
    
    private static class ClassAccessorData
    {
        private final Class<?> clazz;
        private final Map<String, FieldAccessor> byName;
        private final IdentityHashMap<Schema, FieldAccessor[]> bySchema;
        
        private ClassAccessorData(final Class<?> c) {
            this.byName = new HashMap<String, FieldAccessor>();
            this.bySchema = new IdentityHashMap<Schema, FieldAccessor[]>();
            this.clazz = c;
            for (final Field f : getFields(c, false)) {
                if (!f.isAnnotationPresent(AvroIgnore.class)) {
                    final FieldAccessor accessor = ReflectionUtil.getFieldAccess().getAccessor(f);
                    final AvroName avroname = f.getAnnotation(AvroName.class);
                    this.byName.put((avroname != null) ? avroname.value() : f.getName(), accessor);
                }
            }
        }
        
        private synchronized FieldAccessor[] getAccessorsFor(final Schema schema) {
            FieldAccessor[] result = this.bySchema.get(schema);
            if (result == null) {
                result = this.createAccessorsFor(schema);
                this.bySchema.put(schema, result);
            }
            return result;
        }
        
        private FieldAccessor[] createAccessorsFor(final Schema schema) {
            final List<Schema.Field> avroFields = schema.getFields();
            final FieldAccessor[] result = new FieldAccessor[avroFields.size()];
            for (final Schema.Field avroField : schema.getFields()) {
                result[avroField.pos()] = this.byName.get(avroField.name());
            }
            return result;
        }
        
        private FieldAccessor getAccessorFor(final String fieldName) {
            final FieldAccessor result = this.byName.get(fieldName);
            if (result == null) {
                throw new AvroRuntimeException("No field named " + fieldName + " in: " + this.clazz);
            }
            return result;
        }
    }
}
