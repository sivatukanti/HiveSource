// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.typeinfo;

import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public final class TypeInfoFactory
{
    public static final PrimitiveTypeInfo voidTypeInfo;
    public static final PrimitiveTypeInfo booleanTypeInfo;
    public static final PrimitiveTypeInfo intTypeInfo;
    public static final PrimitiveTypeInfo longTypeInfo;
    public static final PrimitiveTypeInfo stringTypeInfo;
    public static final PrimitiveTypeInfo charTypeInfo;
    public static final PrimitiveTypeInfo varcharTypeInfo;
    public static final PrimitiveTypeInfo floatTypeInfo;
    public static final PrimitiveTypeInfo doubleTypeInfo;
    public static final PrimitiveTypeInfo byteTypeInfo;
    public static final PrimitiveTypeInfo shortTypeInfo;
    public static final PrimitiveTypeInfo dateTypeInfo;
    public static final PrimitiveTypeInfo timestampTypeInfo;
    public static final PrimitiveTypeInfo intervalYearMonthTypeInfo;
    public static final PrimitiveTypeInfo intervalDayTimeTypeInfo;
    public static final PrimitiveTypeInfo binaryTypeInfo;
    public static final DecimalTypeInfo decimalTypeInfo;
    public static final PrimitiveTypeInfo unknownTypeInfo;
    private static ConcurrentHashMap<String, PrimitiveTypeInfo> cachedPrimitiveTypeInfo;
    static ConcurrentHashMap<ArrayList<List<?>>, TypeInfo> cachedStructTypeInfo;
    static ConcurrentHashMap<List<?>, TypeInfo> cachedUnionTypeInfo;
    static ConcurrentHashMap<TypeInfo, TypeInfo> cachedListTypeInfo;
    static ConcurrentHashMap<ArrayList<TypeInfo>, TypeInfo> cachedMapTypeInfo;
    
    private TypeInfoFactory() {
    }
    
    public static PrimitiveTypeInfo getPrimitiveTypeInfo(final String typeName) {
        PrimitiveTypeInfo result = TypeInfoFactory.cachedPrimitiveTypeInfo.get(typeName);
        if (result != null) {
            return result;
        }
        result = createPrimitiveTypeInfo(typeName);
        if (result == null) {
            throw new RuntimeException("Error creating PrimitiveTypeInfo instance for " + typeName);
        }
        final PrimitiveTypeInfo prev = TypeInfoFactory.cachedPrimitiveTypeInfo.putIfAbsent(typeName, result);
        if (prev != null) {
            result = prev;
        }
        return result;
    }
    
    private static PrimitiveTypeInfo createPrimitiveTypeInfo(final String fullName) {
        final String baseName = TypeInfoUtils.getBaseName(fullName);
        final PrimitiveObjectInspectorUtils.PrimitiveTypeEntry typeEntry = PrimitiveObjectInspectorUtils.getTypeEntryFromTypeName(baseName);
        if (null == typeEntry) {
            throw new RuntimeException("Unknown type " + fullName);
        }
        final TypeInfoUtils.PrimitiveParts parts = TypeInfoUtils.parsePrimitiveParts(fullName);
        if (parts.typeParams == null || parts.typeParams.length < 1) {
            return null;
        }
        switch (typeEntry.primitiveCategory) {
            case CHAR: {
                if (parts.typeParams.length != 1) {
                    return null;
                }
                return new CharTypeInfo(Integer.valueOf(parts.typeParams[0]));
            }
            case VARCHAR: {
                if (parts.typeParams.length != 1) {
                    return null;
                }
                return new VarcharTypeInfo(Integer.valueOf(parts.typeParams[0]));
            }
            case DECIMAL: {
                if (parts.typeParams.length != 2) {
                    return null;
                }
                return new DecimalTypeInfo(Integer.valueOf(parts.typeParams[0]), Integer.valueOf(parts.typeParams[1]));
            }
            default: {
                return null;
            }
        }
    }
    
    public static CharTypeInfo getCharTypeInfo(final int length) {
        final String fullName = BaseCharTypeInfo.getQualifiedName("char", length);
        return (CharTypeInfo)getPrimitiveTypeInfo(fullName);
    }
    
    public static VarcharTypeInfo getVarcharTypeInfo(final int length) {
        final String fullName = BaseCharTypeInfo.getQualifiedName("varchar", length);
        return (VarcharTypeInfo)getPrimitiveTypeInfo(fullName);
    }
    
    public static DecimalTypeInfo getDecimalTypeInfo(final int precision, final int scale) {
        final String fullName = DecimalTypeInfo.getQualifiedName(precision, scale);
        return (DecimalTypeInfo)getPrimitiveTypeInfo(fullName);
    }
    
    public static TypeInfo getPrimitiveTypeInfoFromPrimitiveWritable(final Class<?> clazz) {
        final String typeName = PrimitiveObjectInspectorUtils.getTypeNameFromPrimitiveWritable(clazz);
        if (typeName == null) {
            throw new RuntimeException("Internal error: Cannot get typeName for " + clazz);
        }
        return getPrimitiveTypeInfo(typeName);
    }
    
    public static TypeInfo getPrimitiveTypeInfoFromJavaPrimitive(final Class<?> clazz) {
        return getPrimitiveTypeInfo(PrimitiveObjectInspectorUtils.getTypeNameFromPrimitiveJava(clazz));
    }
    
    public static TypeInfo getStructTypeInfo(final List<String> names, final List<TypeInfo> typeInfos) {
        final ArrayList<List<?>> signature = new ArrayList<List<?>>(2);
        signature.add(names);
        signature.add(typeInfos);
        TypeInfo result = TypeInfoFactory.cachedStructTypeInfo.get(signature);
        if (result == null) {
            result = new StructTypeInfo(names, typeInfos);
            final TypeInfo prev = TypeInfoFactory.cachedStructTypeInfo.putIfAbsent(signature, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    public static TypeInfo getUnionTypeInfo(final List<TypeInfo> typeInfos) {
        TypeInfo result = TypeInfoFactory.cachedUnionTypeInfo.get(typeInfos);
        if (result == null) {
            result = new UnionTypeInfo(typeInfos);
            final TypeInfo prev = TypeInfoFactory.cachedUnionTypeInfo.putIfAbsent(typeInfos, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    public static TypeInfo getListTypeInfo(final TypeInfo elementTypeInfo) {
        TypeInfo result = TypeInfoFactory.cachedListTypeInfo.get(elementTypeInfo);
        if (result == null) {
            result = new ListTypeInfo(elementTypeInfo);
            final TypeInfo prev = TypeInfoFactory.cachedListTypeInfo.putIfAbsent(elementTypeInfo, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    public static TypeInfo getMapTypeInfo(final TypeInfo keyTypeInfo, final TypeInfo valueTypeInfo) {
        final ArrayList<TypeInfo> signature = new ArrayList<TypeInfo>(2);
        signature.add(keyTypeInfo);
        signature.add(valueTypeInfo);
        TypeInfo result = TypeInfoFactory.cachedMapTypeInfo.get(signature);
        if (result == null) {
            result = new MapTypeInfo(keyTypeInfo, valueTypeInfo);
            final TypeInfo prev = TypeInfoFactory.cachedMapTypeInfo.putIfAbsent(signature, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    static {
        voidTypeInfo = new PrimitiveTypeInfo("void");
        booleanTypeInfo = new PrimitiveTypeInfo("boolean");
        intTypeInfo = new PrimitiveTypeInfo("int");
        longTypeInfo = new PrimitiveTypeInfo("bigint");
        stringTypeInfo = new PrimitiveTypeInfo("string");
        charTypeInfo = new CharTypeInfo(255);
        varcharTypeInfo = new VarcharTypeInfo(65535);
        floatTypeInfo = new PrimitiveTypeInfo("float");
        doubleTypeInfo = new PrimitiveTypeInfo("double");
        byteTypeInfo = new PrimitiveTypeInfo("tinyint");
        shortTypeInfo = new PrimitiveTypeInfo("smallint");
        dateTypeInfo = new PrimitiveTypeInfo("date");
        timestampTypeInfo = new PrimitiveTypeInfo("timestamp");
        intervalYearMonthTypeInfo = new PrimitiveTypeInfo("interval_year_month");
        intervalDayTimeTypeInfo = new PrimitiveTypeInfo("interval_day_time");
        binaryTypeInfo = new PrimitiveTypeInfo("binary");
        decimalTypeInfo = new DecimalTypeInfo(38, 18);
        unknownTypeInfo = new PrimitiveTypeInfo("unknown");
        (TypeInfoFactory.cachedPrimitiveTypeInfo = new ConcurrentHashMap<String, PrimitiveTypeInfo>()).put("void", TypeInfoFactory.voidTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put("boolean", TypeInfoFactory.booleanTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put("int", TypeInfoFactory.intTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put("bigint", TypeInfoFactory.longTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put("string", TypeInfoFactory.stringTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put(TypeInfoFactory.charTypeInfo.getQualifiedName(), TypeInfoFactory.charTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put(TypeInfoFactory.varcharTypeInfo.getQualifiedName(), TypeInfoFactory.varcharTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put("float", TypeInfoFactory.floatTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put("double", TypeInfoFactory.doubleTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put("tinyint", TypeInfoFactory.byteTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put("smallint", TypeInfoFactory.shortTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put("date", TypeInfoFactory.dateTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put("timestamp", TypeInfoFactory.timestampTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put("interval_year_month", TypeInfoFactory.intervalYearMonthTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put("interval_day_time", TypeInfoFactory.intervalDayTimeTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put("binary", TypeInfoFactory.binaryTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put(TypeInfoFactory.decimalTypeInfo.getQualifiedName(), TypeInfoFactory.decimalTypeInfo);
        TypeInfoFactory.cachedPrimitiveTypeInfo.put("unknown", TypeInfoFactory.unknownTypeInfo);
        TypeInfoFactory.cachedStructTypeInfo = new ConcurrentHashMap<ArrayList<List<?>>, TypeInfo>();
        TypeInfoFactory.cachedUnionTypeInfo = new ConcurrentHashMap<List<?>, TypeInfo>();
        TypeInfoFactory.cachedListTypeInfo = new ConcurrentHashMap<TypeInfo, TypeInfo>();
        TypeInfoFactory.cachedMapTypeInfo = new ConcurrentHashMap<ArrayList<TypeInfo>, TypeInfo>();
    }
}
