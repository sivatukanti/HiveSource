// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.specific;

import org.apache.avro.Protocol;
import org.apache.avro.AvroTypeException;
import java.util.Collection;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import org.apache.avro.AvroRuntimeException;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.avro.util.ClassUtils;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DatumReader;
import java.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.net.URL;
import java.net.URI;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.HashSet;
import java.lang.reflect.Type;
import java.util.WeakHashMap;
import org.apache.avro.Schema;
import java.util.Set;
import java.lang.reflect.Constructor;
import java.util.Map;
import org.apache.avro.generic.GenericData;

public class SpecificData extends GenericData
{
    private static final SpecificData INSTANCE;
    private static final Class<?>[] NO_ARG;
    private static final Class<?>[] SCHEMA_ARG;
    private static final Map<Class, Constructor> CTOR_CACHE;
    public static final String CLASS_PROP = "java-class";
    public static final String KEY_CLASS_PROP = "java-key-class";
    public static final String ELEMENT_PROP = "java-element-class";
    protected Set<Class> stringableClasses;
    private Map<String, Class> classCache;
    private static final Class NO_CLASS;
    private static final Schema NULL_SCHEMA;
    private final WeakHashMap<Type, Schema> schemaCache;
    
    public SpecificData() {
        (this.stringableClasses = new HashSet<Class>()).add(BigDecimal.class);
        this.stringableClasses.add(BigInteger.class);
        this.stringableClasses.add(URI.class);
        this.stringableClasses.add(URL.class);
        this.stringableClasses.add(File.class);
        this.classCache = new ConcurrentHashMap<String, Class>();
        this.schemaCache = new WeakHashMap<Type, Schema>();
    }
    
    public SpecificData(final ClassLoader classLoader) {
        super(classLoader);
        (this.stringableClasses = new HashSet<Class>()).add(BigDecimal.class);
        this.stringableClasses.add(BigInteger.class);
        this.stringableClasses.add(URI.class);
        this.stringableClasses.add(URL.class);
        this.stringableClasses.add(File.class);
        this.classCache = new ConcurrentHashMap<String, Class>();
        this.schemaCache = new WeakHashMap<Type, Schema>();
    }
    
    @Override
    public DatumReader createDatumReader(final Schema schema) {
        return new SpecificDatumReader(schema, schema, this);
    }
    
    @Override
    public DatumReader createDatumReader(final Schema writer, final Schema reader) {
        return new SpecificDatumReader(writer, reader, this);
    }
    
    @Override
    public DatumWriter createDatumWriter(final Schema schema) {
        return new SpecificDatumWriter(schema, this);
    }
    
    public static SpecificData get() {
        return SpecificData.INSTANCE;
    }
    
    @Override
    protected boolean isEnum(final Object datum) {
        return datum instanceof Enum || super.isEnum(datum);
    }
    
    @Override
    public Object createEnum(final String symbol, final Schema schema) {
        final Class c = this.getClass(schema);
        if (c == null) {
            return super.createEnum(symbol, schema);
        }
        return Enum.valueOf((Class<Object>)c, symbol);
    }
    
    @Override
    protected Schema getEnumSchema(final Object datum) {
        return (datum instanceof Enum) ? this.getSchema(datum.getClass()) : super.getEnumSchema(datum);
    }
    
    public Class getClass(final Schema schema) {
        switch (schema.getType()) {
            case FIXED:
            case RECORD:
            case ENUM: {
                final String name = schema.getFullName();
                if (name == null) {
                    return null;
                }
                Class c = this.classCache.get(name);
                if (c == null) {
                    try {
                        c = ClassUtils.forName(this.getClassLoader(), getClassName(schema));
                    }
                    catch (ClassNotFoundException e) {
                        c = SpecificData.NO_CLASS;
                    }
                    this.classCache.put(name, c);
                }
                return (c == SpecificData.NO_CLASS) ? null : c;
            }
            case ARRAY: {
                return List.class;
            }
            case MAP: {
                return Map.class;
            }
            case UNION: {
                final List<Schema> types = schema.getTypes();
                if (types.size() == 2 && types.contains(SpecificData.NULL_SCHEMA)) {
                    return this.getWrapper(types.get((int)(types.get(0).equals(SpecificData.NULL_SCHEMA) ? 1 : 0)));
                }
                return Object.class;
            }
            case STRING: {
                if ("String".equals(schema.getProp("avro.java.string"))) {
                    return String.class;
                }
                return CharSequence.class;
            }
            case BYTES: {
                return ByteBuffer.class;
            }
            case INT: {
                return Integer.TYPE;
            }
            case LONG: {
                return Long.TYPE;
            }
            case FLOAT: {
                return Float.TYPE;
            }
            case DOUBLE: {
                return Double.TYPE;
            }
            case BOOLEAN: {
                return Boolean.TYPE;
            }
            case NULL: {
                return Void.TYPE;
            }
            default: {
                throw new AvroRuntimeException("Unknown type: " + schema);
            }
        }
    }
    
    private Class getWrapper(final Schema schema) {
        switch (schema.getType()) {
            case INT: {
                return Integer.class;
            }
            case LONG: {
                return Long.class;
            }
            case FLOAT: {
                return Float.class;
            }
            case DOUBLE: {
                return Double.class;
            }
            case BOOLEAN: {
                return Boolean.class;
            }
            default: {
                return this.getClass(schema);
            }
        }
    }
    
    public static String getClassName(final Schema schema) {
        final String namespace = schema.getNamespace();
        final String name = schema.getName();
        if (namespace == null || "".equals(namespace)) {
            return name;
        }
        final String dot = namespace.endsWith("$") ? "" : ".";
        return namespace + dot + name;
    }
    
    public Schema getSchema(final Type type) {
        Schema schema = this.schemaCache.get(type);
        if (schema == null) {
            schema = this.createSchema(type, new LinkedHashMap<String, Schema>());
            this.schemaCache.put(type, schema);
        }
        return schema;
    }
    
    protected Schema createSchema(final Type type, final Map<String, Schema> names) {
        if (type instanceof Class && CharSequence.class.isAssignableFrom((Class<?>)type)) {
            return Schema.create(Schema.Type.STRING);
        }
        if (type == ByteBuffer.class) {
            return Schema.create(Schema.Type.BYTES);
        }
        if (type == Integer.class || type == Integer.TYPE) {
            return Schema.create(Schema.Type.INT);
        }
        if (type == Long.class || type == Long.TYPE) {
            return Schema.create(Schema.Type.LONG);
        }
        if (type == Float.class || type == Float.TYPE) {
            return Schema.create(Schema.Type.FLOAT);
        }
        if (type == Double.class || type == Double.TYPE) {
            return Schema.create(Schema.Type.DOUBLE);
        }
        if (type == Boolean.class || type == Boolean.TYPE) {
            return Schema.create(Schema.Type.BOOLEAN);
        }
        if (type == Void.class || type == Void.TYPE) {
            return Schema.create(Schema.Type.NULL);
        }
        if (type instanceof ParameterizedType) {
            final ParameterizedType ptype = (ParameterizedType)type;
            final Class raw = (Class)ptype.getRawType();
            final Type[] params = ptype.getActualTypeArguments();
            if (Collection.class.isAssignableFrom(raw)) {
                if (params.length != 1) {
                    throw new AvroTypeException("No array type specified.");
                }
                return Schema.createArray(this.createSchema(params[0], names));
            }
            else {
                if (!Map.class.isAssignableFrom(raw)) {
                    return this.createSchema(raw, names);
                }
                final Type key = params[0];
                final Type value = params[1];
                if (!(key instanceof Class) || !CharSequence.class.isAssignableFrom((Class<?>)key)) {
                    throw new AvroTypeException("Map key class not CharSequence: " + key);
                }
                return Schema.createMap(this.createSchema(value, names));
            }
        }
        else {
            if (type instanceof Class) {
                final Class c = (Class)type;
                final String fullName = c.getName();
                Schema schema = names.get(fullName);
                if (schema == null) {
                    try {
                        schema = (Schema)c.getDeclaredField("SCHEMA$").get(null);
                        if (!fullName.equals(getClassName(schema))) {
                            schema = Schema.parse(schema.toString().replace(schema.getNamespace(), c.getPackage().getName()));
                        }
                    }
                    catch (NoSuchFieldException e2) {
                        throw new AvroRuntimeException("Not a Specific class: " + c);
                    }
                    catch (IllegalAccessException e) {
                        throw new AvroRuntimeException(e);
                    }
                }
                names.put(fullName, schema);
                return schema;
            }
            throw new AvroTypeException("Unknown type: " + type);
        }
    }
    
    @Override
    protected String getSchemaName(final Object datum) {
        if (datum != null) {
            final Class c = datum.getClass();
            if (this.isStringable(c)) {
                return Schema.Type.STRING.getName();
            }
        }
        return super.getSchemaName(datum);
    }
    
    protected boolean isStringable(final Class<?> c) {
        return this.stringableClasses.contains(c);
    }
    
    public Protocol getProtocol(final Class iface) {
        try {
            Protocol p = (Protocol)iface.getDeclaredField("PROTOCOL").get(null);
            if (!p.getNamespace().equals(iface.getPackage().getName())) {
                p = Protocol.parse(p.toString().replace(p.getNamespace(), iface.getPackage().getName()));
            }
            return p;
        }
        catch (NoSuchFieldException e2) {
            throw new AvroRuntimeException("Not a Specific protocol: " + iface);
        }
        catch (IllegalAccessException e) {
            throw new AvroRuntimeException(e);
        }
    }
    
    @Override
    protected int compare(final Object o1, final Object o2, final Schema s, final boolean eq) {
        switch (s.getType()) {
            case ENUM: {
                if (o1 instanceof Enum) {
                    return ((Enum)o1).ordinal() - ((Enum)o2).ordinal();
                }
                break;
            }
        }
        return super.compare(o1, o2, s, eq);
    }
    
    public static Object newInstance(final Class c, final Schema s) {
        final boolean useSchema = SchemaConstructable.class.isAssignableFrom(c);
        Object result;
        try {
            Constructor meth = SpecificData.CTOR_CACHE.get(c);
            if (meth == null) {
                meth = c.getDeclaredConstructor(useSchema ? SpecificData.SCHEMA_ARG : SpecificData.NO_ARG);
                meth.setAccessible(true);
                SpecificData.CTOR_CACHE.put(c, meth);
            }
            result = meth.newInstance(useSchema ? new Object[] { s } : ((Object[])null));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    
    @Override
    public Object createFixed(final Object old, final Schema schema) {
        final Class c = this.getClass(schema);
        if (c == null) {
            return super.createFixed(old, schema);
        }
        return c.isInstance(old) ? old : newInstance(c, schema);
    }
    
    @Override
    public Object newRecord(final Object old, final Schema schema) {
        final Class c = this.getClass(schema);
        if (c == null) {
            return super.newRecord(old, schema);
        }
        return c.isInstance(old) ? old : newInstance(c, schema);
    }
    
    static {
        INSTANCE = new SpecificData();
        NO_ARG = new Class[0];
        SCHEMA_ARG = new Class[] { Schema.class };
        CTOR_CACHE = new ConcurrentHashMap<Class, Constructor>();
        NO_CLASS = new Object() {}.getClass();
        NULL_SCHEMA = Schema.create(Schema.Type.NULL);
    }
    
    public interface SchemaConstructable
    {
    }
}
