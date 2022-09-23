// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import org.apache.thrift.TUnion;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.GenericArrayType;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

public final class ObjectInspectorFactory
{
    private static ConcurrentHashMap<Type, ObjectInspector> objectInspectorCache;
    static ConcurrentHashMap<ObjectInspector, StandardListObjectInspector> cachedStandardListObjectInspector;
    static ConcurrentHashMap<List<ObjectInspector>, StandardMapObjectInspector> cachedStandardMapObjectInspector;
    static ConcurrentHashMap<List<ObjectInspector>, StandardUnionObjectInspector> cachedStandardUnionObjectInspector;
    static ConcurrentHashMap<ArrayList<List<?>>, StandardStructObjectInspector> cachedStandardStructObjectInspector;
    static ConcurrentHashMap<List<StructObjectInspector>, UnionStructObjectInspector> cachedUnionStructObjectInspector;
    static ConcurrentHashMap<ArrayList<Object>, ColumnarStructObjectInspector> cachedColumnarStructObjectInspector;
    
    public static ObjectInspector getReflectionObjectInspector(final Type t, final ObjectInspectorOptions options) {
        ObjectInspector oi = ObjectInspectorFactory.objectInspectorCache.get(t);
        if (oi == null) {
            oi = getReflectionObjectInspectorNoCache(t, options);
            ObjectInspectorFactory.objectInspectorCache.put(t, oi);
        }
        verifyObjectInspector(options, oi, ObjectInspectorOptions.JAVA, new Class[] { ThriftStructObjectInspector.class, ProtocolBuffersStructObjectInspector.class });
        verifyObjectInspector(options, oi, ObjectInspectorOptions.THRIFT, new Class[] { ReflectionStructObjectInspector.class, ProtocolBuffersStructObjectInspector.class });
        verifyObjectInspector(options, oi, ObjectInspectorOptions.PROTOCOL_BUFFERS, new Class[] { ThriftStructObjectInspector.class, ReflectionStructObjectInspector.class });
        return oi;
    }
    
    private static void verifyObjectInspector(final ObjectInspectorOptions option, final ObjectInspector oi, final ObjectInspectorOptions checkOption, final Class[] classes) {
        if (option.equals(checkOption)) {
            for (final Class checkClass : classes) {
                if (oi.getClass().equals(checkClass)) {
                    throw new RuntimeException("Cannot call getObjectInspectorByReflection with more then one of " + Arrays.toString(ObjectInspectorOptions.values()) + "!");
                }
            }
        }
    }
    
    private static ObjectInspector getReflectionObjectInspectorNoCache(Type t, final ObjectInspectorOptions options) {
        if (t instanceof GenericArrayType) {
            final GenericArrayType at = (GenericArrayType)t;
            return getStandardListObjectInspector(getReflectionObjectInspector(at.getGenericComponentType(), options));
        }
        if (t instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType)t;
            if (List.class.isAssignableFrom((Class<?>)pt.getRawType()) || Set.class.isAssignableFrom((Class<?>)pt.getRawType())) {
                return getStandardListObjectInspector(getReflectionObjectInspector(pt.getActualTypeArguments()[0], options));
            }
            if (Map.class.isAssignableFrom((Class<?>)pt.getRawType())) {
                return getStandardMapObjectInspector(getReflectionObjectInspector(pt.getActualTypeArguments()[0], options), getReflectionObjectInspector(pt.getActualTypeArguments()[1], options));
            }
            t = pt.getRawType();
        }
        if (!(t instanceof Class)) {
            throw new RuntimeException(ObjectInspectorFactory.class.getName() + " internal error:" + t);
        }
        final Class<?> c = (Class<?>)t;
        if (PrimitiveObjectInspectorUtils.isPrimitiveJavaType(c)) {
            return PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(PrimitiveObjectInspectorUtils.getTypeEntryFromPrimitiveJavaType(c).primitiveCategory);
        }
        if (PrimitiveObjectInspectorUtils.isPrimitiveJavaClass(c)) {
            return PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(PrimitiveObjectInspectorUtils.getTypeEntryFromPrimitiveJavaClass(c).primitiveCategory);
        }
        if (PrimitiveObjectInspectorUtils.isPrimitiveWritableClass(c)) {
            return PrimitiveObjectInspectorFactory.getPrimitiveWritableObjectInspector(PrimitiveObjectInspectorUtils.getTypeEntryFromPrimitiveWritableClass(c).primitiveCategory);
        }
        if (Enum.class.isAssignableFrom(c)) {
            return PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(PrimitiveObjectInspector.PrimitiveCategory.STRING);
        }
        assert !List.class.isAssignableFrom(c);
        assert !Map.class.isAssignableFrom(c);
        ReflectionStructObjectInspector oi = null;
        switch (options) {
            case JAVA: {
                oi = new ReflectionStructObjectInspector();
                break;
            }
            case THRIFT: {
                oi = (TUnion.class.isAssignableFrom(c) ? new ThriftUnionObjectInspector() : new ThriftStructObjectInspector());
                break;
            }
            case PROTOCOL_BUFFERS: {
                oi = new ProtocolBuffersStructObjectInspector();
                break;
            }
            default: {
                throw new RuntimeException(ObjectInspectorFactory.class.getName() + ": internal error.");
            }
        }
        ObjectInspectorFactory.objectInspectorCache.put(t, oi);
        oi.init(c, options);
        return oi;
    }
    
    public static StandardListObjectInspector getStandardListObjectInspector(final ObjectInspector listElementObjectInspector) {
        StandardListObjectInspector result = ObjectInspectorFactory.cachedStandardListObjectInspector.get(listElementObjectInspector);
        if (result == null) {
            result = new StandardListObjectInspector(listElementObjectInspector);
            final StandardListObjectInspector prev = ObjectInspectorFactory.cachedStandardListObjectInspector.putIfAbsent(listElementObjectInspector, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    public static StandardConstantListObjectInspector getStandardConstantListObjectInspector(final ObjectInspector listElementObjectInspector, final List<?> constantValue) {
        return new StandardConstantListObjectInspector(listElementObjectInspector, constantValue);
    }
    
    public static StandardMapObjectInspector getStandardMapObjectInspector(final ObjectInspector mapKeyObjectInspector, final ObjectInspector mapValueObjectInspector) {
        final ArrayList<ObjectInspector> signature = new ArrayList<ObjectInspector>(2);
        signature.add(mapKeyObjectInspector);
        signature.add(mapValueObjectInspector);
        StandardMapObjectInspector result = ObjectInspectorFactory.cachedStandardMapObjectInspector.get(signature);
        if (result == null) {
            result = new StandardMapObjectInspector(mapKeyObjectInspector, mapValueObjectInspector);
            final StandardMapObjectInspector prev = ObjectInspectorFactory.cachedStandardMapObjectInspector.putIfAbsent(signature, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    public static StandardConstantMapObjectInspector getStandardConstantMapObjectInspector(final ObjectInspector mapKeyObjectInspector, final ObjectInspector mapValueObjectInspector, final Map<?, ?> constantValue) {
        return new StandardConstantMapObjectInspector(mapKeyObjectInspector, mapValueObjectInspector, constantValue);
    }
    
    public static StandardUnionObjectInspector getStandardUnionObjectInspector(final List<ObjectInspector> unionObjectInspectors) {
        StandardUnionObjectInspector result = ObjectInspectorFactory.cachedStandardUnionObjectInspector.get(unionObjectInspectors);
        if (result == null) {
            result = new StandardUnionObjectInspector(unionObjectInspectors);
            final StandardUnionObjectInspector prev = ObjectInspectorFactory.cachedStandardUnionObjectInspector.putIfAbsent(unionObjectInspectors, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    public static StandardStructObjectInspector getStandardStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors) {
        return getStandardStructObjectInspector(structFieldNames, structFieldObjectInspectors, null);
    }
    
    public static StandardStructObjectInspector getStandardStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structComments) {
        final ArrayList<List<?>> signature = new ArrayList<List<?>>(3);
        signature.add(structFieldNames);
        signature.add(structFieldObjectInspectors);
        if (structComments != null) {
            signature.add(structComments);
        }
        StandardStructObjectInspector result = ObjectInspectorFactory.cachedStandardStructObjectInspector.get(signature);
        if (result == null) {
            result = new StandardStructObjectInspector(structFieldNames, structFieldObjectInspectors, structComments);
            final StandardStructObjectInspector prev = ObjectInspectorFactory.cachedStandardStructObjectInspector.putIfAbsent(signature, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    public static UnionStructObjectInspector getUnionStructObjectInspector(final List<StructObjectInspector> structObjectInspectors) {
        UnionStructObjectInspector result = ObjectInspectorFactory.cachedUnionStructObjectInspector.get(structObjectInspectors);
        if (result == null) {
            result = new UnionStructObjectInspector(structObjectInspectors);
            final UnionStructObjectInspector prev = ObjectInspectorFactory.cachedUnionStructObjectInspector.putIfAbsent(structObjectInspectors, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    public static ColumnarStructObjectInspector getColumnarStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors) {
        return getColumnarStructObjectInspector(structFieldNames, structFieldObjectInspectors, null);
    }
    
    public static ColumnarStructObjectInspector getColumnarStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments) {
        final ArrayList<Object> signature = new ArrayList<Object>(3);
        signature.add(structFieldNames);
        signature.add(structFieldObjectInspectors);
        if (structFieldComments != null) {
            signature.add(structFieldComments);
        }
        ColumnarStructObjectInspector result = ObjectInspectorFactory.cachedColumnarStructObjectInspector.get(signature);
        if (result == null) {
            result = new ColumnarStructObjectInspector(structFieldNames, structFieldObjectInspectors, structFieldComments);
            final ColumnarStructObjectInspector prev = ObjectInspectorFactory.cachedColumnarStructObjectInspector.putIfAbsent(signature, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    private ObjectInspectorFactory() {
    }
    
    static {
        ObjectInspectorFactory.objectInspectorCache = new ConcurrentHashMap<Type, ObjectInspector>();
        ObjectInspectorFactory.cachedStandardListObjectInspector = new ConcurrentHashMap<ObjectInspector, StandardListObjectInspector>();
        ObjectInspectorFactory.cachedStandardMapObjectInspector = new ConcurrentHashMap<List<ObjectInspector>, StandardMapObjectInspector>();
        ObjectInspectorFactory.cachedStandardUnionObjectInspector = new ConcurrentHashMap<List<ObjectInspector>, StandardUnionObjectInspector>();
        ObjectInspectorFactory.cachedStandardStructObjectInspector = new ConcurrentHashMap<ArrayList<List<?>>, StandardStructObjectInspector>();
        ObjectInspectorFactory.cachedUnionStructObjectInspector = new ConcurrentHashMap<List<StructObjectInspector>, UnionStructObjectInspector>();
        ObjectInspectorFactory.cachedColumnarStructObjectInspector = new ConcurrentHashMap<ArrayList<Object>, ColumnarStructObjectInspector>();
    }
    
    public enum ObjectInspectorOptions
    {
        JAVA, 
        THRIFT, 
        PROTOCOL_BUFFERS, 
        AVRO;
    }
}
