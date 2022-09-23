// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import org.apache.hadoop.io.Text;
import java.util.List;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public final class LazyPrimitiveObjectInspectorFactory
{
    public static final LazyBooleanObjectInspector LAZY_BOOLEAN_OBJECT_INSPECTOR;
    public static final LazyBooleanObjectInspector LAZY_EXT_BOOLEAN_OBJECT_INSPECTOR;
    public static final LazyByteObjectInspector LAZY_BYTE_OBJECT_INSPECTOR;
    public static final LazyShortObjectInspector LAZY_SHORT_OBJECT_INSPECTOR;
    public static final LazyIntObjectInspector LAZY_INT_OBJECT_INSPECTOR;
    public static final LazyLongObjectInspector LAZY_LONG_OBJECT_INSPECTOR;
    public static final LazyFloatObjectInspector LAZY_FLOAT_OBJECT_INSPECTOR;
    public static final LazyDoubleObjectInspector LAZY_DOUBLE_OBJECT_INSPECTOR;
    public static final LazyVoidObjectInspector LAZY_VOID_OBJECT_INSPECTOR;
    public static final LazyDateObjectInspector LAZY_DATE_OBJECT_INSPECTOR;
    public static final LazyTimestampObjectInspector LAZY_TIMESTAMP_OBJECT_INSPECTOR;
    public static final LazyHiveIntervalYearMonthObjectInspector LAZY_INTERVAL_YEAR_MONTH_OBJECT_INSPECTOR;
    public static final LazyHiveIntervalDayTimeObjectInspector LAZY_INTERVAL_DAY_TIME_OBJECT_INSPECTOR;
    public static final LazyBinaryObjectInspector LAZY_BINARY_OBJECT_INSPECTOR;
    private static ConcurrentHashMap<ArrayList<Object>, AbstractPrimitiveLazyObjectInspector<?>> cachedLazyStringTypeOIs;
    private static ConcurrentHashMap<PrimitiveTypeInfo, AbstractPrimitiveLazyObjectInspector<?>> cachedPrimitiveLazyObjectInspectors;
    
    private LazyPrimitiveObjectInspectorFactory() {
    }
    
    public static AbstractPrimitiveLazyObjectInspector<?> getLazyObjectInspector(final PrimitiveTypeInfo typeInfo, final boolean escaped, final byte escapeChar) {
        return getLazyObjectInspector(typeInfo, escaped, escapeChar, false);
    }
    
    public static AbstractPrimitiveLazyObjectInspector<?> getLazyObjectInspector(final PrimitiveTypeInfo typeInfo, final boolean escaped, final byte escapeChar, final boolean extBoolean) {
        final LazyObjectInspectorParameters lazyParams = new LazyObjectInspectorParametersImpl(escaped, escapeChar, extBoolean, null, null, null);
        return getLazyObjectInspector(typeInfo, lazyParams);
    }
    
    public static AbstractPrimitiveLazyObjectInspector<?> getLazyObjectInspector(final PrimitiveTypeInfo typeInfo, final LazyObjectInspectorParameters lazyParams) {
        final PrimitiveObjectInspector.PrimitiveCategory primitiveCategory = typeInfo.getPrimitiveCategory();
        switch (primitiveCategory) {
            case STRING: {
                return getLazyStringObjectInspector(lazyParams.isEscaped(), lazyParams.getEscapeChar());
            }
            case CHAR: {
                return getLazyHiveCharObjectInspector((CharTypeInfo)typeInfo, lazyParams.isEscaped(), lazyParams.getEscapeChar());
            }
            case VARCHAR: {
                return getLazyHiveVarcharObjectInspector((VarcharTypeInfo)typeInfo, lazyParams.isEscaped(), lazyParams.getEscapeChar());
            }
            case BOOLEAN: {
                return getLazyBooleanObjectInspector(lazyParams.isExtendedBooleanLiteral());
            }
            case TIMESTAMP: {
                return getLazyTimestampObjectInspector(lazyParams.getTimestampFormats());
            }
            default: {
                return getLazyObjectInspector(typeInfo);
            }
        }
    }
    
    public static AbstractPrimitiveLazyObjectInspector<?> getLazyObjectInspector(final PrimitiveTypeInfo typeInfo) {
        AbstractPrimitiveLazyObjectInspector<?> poi = LazyPrimitiveObjectInspectorFactory.cachedPrimitiveLazyObjectInspectors.get(typeInfo);
        if (poi != null) {
            return poi;
        }
        switch (typeInfo.getPrimitiveCategory()) {
            case CHAR: {
                poi = new LazyHiveCharObjectInspector((CharTypeInfo)typeInfo);
                break;
            }
            case VARCHAR: {
                poi = new LazyHiveVarcharObjectInspector((VarcharTypeInfo)typeInfo);
                break;
            }
            case DECIMAL: {
                poi = new LazyHiveDecimalObjectInspector((DecimalTypeInfo)typeInfo);
                break;
            }
            default: {
                throw new RuntimeException("Primitve type " + typeInfo.getPrimitiveCategory() + " should not take parameters");
            }
        }
        final AbstractPrimitiveLazyObjectInspector<?> prev = LazyPrimitiveObjectInspectorFactory.cachedPrimitiveLazyObjectInspectors.putIfAbsent(typeInfo, poi);
        if (prev != null) {
            poi = prev;
        }
        return poi;
    }
    
    public static LazyStringObjectInspector getLazyStringObjectInspector(final boolean escaped, final byte escapeChar) {
        final ArrayList<Object> signature = new ArrayList<Object>();
        signature.add(TypeInfoFactory.stringTypeInfo);
        signature.add(escaped);
        signature.add(escapeChar);
        LazyStringObjectInspector result = LazyPrimitiveObjectInspectorFactory.cachedLazyStringTypeOIs.get(signature);
        if (result == null) {
            result = new LazyStringObjectInspector(escaped, escapeChar);
            final AbstractPrimitiveLazyObjectInspector<?> prev = LazyPrimitiveObjectInspectorFactory.cachedLazyStringTypeOIs.putIfAbsent(signature, result);
            if (prev != null) {
                result = (LazyStringObjectInspector)prev;
            }
        }
        return result;
    }
    
    public static LazyHiveCharObjectInspector getLazyHiveCharObjectInspector(final CharTypeInfo typeInfo, final boolean escaped, final byte escapeChar) {
        final ArrayList<Object> signature = new ArrayList<Object>();
        signature.add(typeInfo);
        signature.add(escaped);
        signature.add(escapeChar);
        LazyHiveCharObjectInspector result = LazyPrimitiveObjectInspectorFactory.cachedLazyStringTypeOIs.get(signature);
        if (result == null) {
            result = new LazyHiveCharObjectInspector(typeInfo, escaped, escapeChar);
            final AbstractPrimitiveLazyObjectInspector<?> prev = LazyPrimitiveObjectInspectorFactory.cachedLazyStringTypeOIs.putIfAbsent(signature, result);
            if (prev != null) {
                result = (LazyHiveCharObjectInspector)prev;
            }
        }
        return result;
    }
    
    public static LazyHiveVarcharObjectInspector getLazyHiveVarcharObjectInspector(final VarcharTypeInfo typeInfo, final boolean escaped, final byte escapeChar) {
        final ArrayList<Object> signature = new ArrayList<Object>();
        signature.add(typeInfo);
        signature.add(escaped);
        signature.add(escapeChar);
        LazyHiveVarcharObjectInspector result = LazyPrimitiveObjectInspectorFactory.cachedLazyStringTypeOIs.get(signature);
        if (result == null) {
            result = new LazyHiveVarcharObjectInspector(typeInfo, escaped, escapeChar);
            final AbstractPrimitiveLazyObjectInspector<?> prev = LazyPrimitiveObjectInspectorFactory.cachedLazyStringTypeOIs.putIfAbsent(signature, result);
            if (prev != null) {
                result = (LazyHiveVarcharObjectInspector)prev;
            }
        }
        return result;
    }
    
    public static LazyTimestampObjectInspector getLazyTimestampObjectInspector(final List<String> tsFormats) {
        if (tsFormats == null) {
            return (LazyTimestampObjectInspector)getLazyObjectInspector(TypeInfoFactory.timestampTypeInfo);
        }
        final ArrayList<Object> signature = new ArrayList<Object>();
        signature.add(TypeInfoFactory.timestampTypeInfo);
        signature.add(tsFormats);
        LazyTimestampObjectInspector result = LazyPrimitiveObjectInspectorFactory.cachedLazyStringTypeOIs.get(signature);
        if (result == null) {
            result = new LazyTimestampObjectInspector(tsFormats);
            final AbstractPrimitiveLazyObjectInspector<?> prev = LazyPrimitiveObjectInspectorFactory.cachedLazyStringTypeOIs.putIfAbsent(signature, result);
            if (prev != null) {
                result = (LazyTimestampObjectInspector)prev;
            }
        }
        return result;
    }
    
    private static LazyBooleanObjectInspector getLazyBooleanObjectInspector(final boolean extLiteral) {
        return extLiteral ? LazyPrimitiveObjectInspectorFactory.LAZY_EXT_BOOLEAN_OBJECT_INSPECTOR : LazyPrimitiveObjectInspectorFactory.LAZY_BOOLEAN_OBJECT_INSPECTOR;
    }
    
    static {
        LAZY_BOOLEAN_OBJECT_INSPECTOR = new LazyBooleanObjectInspector();
        (LAZY_EXT_BOOLEAN_OBJECT_INSPECTOR = new LazyBooleanObjectInspector()).setExtendedLiteral(true);
        LAZY_BYTE_OBJECT_INSPECTOR = new LazyByteObjectInspector();
        LAZY_SHORT_OBJECT_INSPECTOR = new LazyShortObjectInspector();
        LAZY_INT_OBJECT_INSPECTOR = new LazyIntObjectInspector();
        LAZY_LONG_OBJECT_INSPECTOR = new LazyLongObjectInspector();
        LAZY_FLOAT_OBJECT_INSPECTOR = new LazyFloatObjectInspector();
        LAZY_DOUBLE_OBJECT_INSPECTOR = new LazyDoubleObjectInspector();
        LAZY_VOID_OBJECT_INSPECTOR = new LazyVoidObjectInspector();
        LAZY_DATE_OBJECT_INSPECTOR = new LazyDateObjectInspector();
        LAZY_TIMESTAMP_OBJECT_INSPECTOR = new LazyTimestampObjectInspector();
        LAZY_INTERVAL_YEAR_MONTH_OBJECT_INSPECTOR = new LazyHiveIntervalYearMonthObjectInspector();
        LAZY_INTERVAL_DAY_TIME_OBJECT_INSPECTOR = new LazyHiveIntervalDayTimeObjectInspector();
        LAZY_BINARY_OBJECT_INSPECTOR = new LazyBinaryObjectInspector();
        LazyPrimitiveObjectInspectorFactory.cachedLazyStringTypeOIs = new ConcurrentHashMap<ArrayList<Object>, AbstractPrimitiveLazyObjectInspector<?>>();
        (LazyPrimitiveObjectInspectorFactory.cachedPrimitiveLazyObjectInspectors = new ConcurrentHashMap<PrimitiveTypeInfo, AbstractPrimitiveLazyObjectInspector<?>>()).put(TypeInfoFactory.getPrimitiveTypeInfo("boolean"), LazyPrimitiveObjectInspectorFactory.LAZY_BOOLEAN_OBJECT_INSPECTOR);
        LazyPrimitiveObjectInspectorFactory.cachedPrimitiveLazyObjectInspectors.put(TypeInfoFactory.getPrimitiveTypeInfo("tinyint"), LazyPrimitiveObjectInspectorFactory.LAZY_BYTE_OBJECT_INSPECTOR);
        LazyPrimitiveObjectInspectorFactory.cachedPrimitiveLazyObjectInspectors.put(TypeInfoFactory.getPrimitiveTypeInfo("smallint"), LazyPrimitiveObjectInspectorFactory.LAZY_SHORT_OBJECT_INSPECTOR);
        LazyPrimitiveObjectInspectorFactory.cachedPrimitiveLazyObjectInspectors.put(TypeInfoFactory.getPrimitiveTypeInfo("int"), LazyPrimitiveObjectInspectorFactory.LAZY_INT_OBJECT_INSPECTOR);
        LazyPrimitiveObjectInspectorFactory.cachedPrimitiveLazyObjectInspectors.put(TypeInfoFactory.getPrimitiveTypeInfo("float"), LazyPrimitiveObjectInspectorFactory.LAZY_FLOAT_OBJECT_INSPECTOR);
        LazyPrimitiveObjectInspectorFactory.cachedPrimitiveLazyObjectInspectors.put(TypeInfoFactory.getPrimitiveTypeInfo("double"), LazyPrimitiveObjectInspectorFactory.LAZY_DOUBLE_OBJECT_INSPECTOR);
        LazyPrimitiveObjectInspectorFactory.cachedPrimitiveLazyObjectInspectors.put(TypeInfoFactory.getPrimitiveTypeInfo("bigint"), LazyPrimitiveObjectInspectorFactory.LAZY_LONG_OBJECT_INSPECTOR);
        LazyPrimitiveObjectInspectorFactory.cachedPrimitiveLazyObjectInspectors.put(TypeInfoFactory.getPrimitiveTypeInfo("void"), LazyPrimitiveObjectInspectorFactory.LAZY_VOID_OBJECT_INSPECTOR);
        LazyPrimitiveObjectInspectorFactory.cachedPrimitiveLazyObjectInspectors.put(TypeInfoFactory.getPrimitiveTypeInfo("date"), LazyPrimitiveObjectInspectorFactory.LAZY_DATE_OBJECT_INSPECTOR);
        LazyPrimitiveObjectInspectorFactory.cachedPrimitiveLazyObjectInspectors.put(TypeInfoFactory.getPrimitiveTypeInfo("timestamp"), LazyPrimitiveObjectInspectorFactory.LAZY_TIMESTAMP_OBJECT_INSPECTOR);
        LazyPrimitiveObjectInspectorFactory.cachedPrimitiveLazyObjectInspectors.put(TypeInfoFactory.getPrimitiveTypeInfo("interval_year_month"), LazyPrimitiveObjectInspectorFactory.LAZY_INTERVAL_YEAR_MONTH_OBJECT_INSPECTOR);
        LazyPrimitiveObjectInspectorFactory.cachedPrimitiveLazyObjectInspectors.put(TypeInfoFactory.getPrimitiveTypeInfo("interval_day_time"), LazyPrimitiveObjectInspectorFactory.LAZY_INTERVAL_DAY_TIME_OBJECT_INSPECTOR);
        LazyPrimitiveObjectInspectorFactory.cachedPrimitiveLazyObjectInspectors.put(TypeInfoFactory.getPrimitiveTypeInfo("binary"), LazyPrimitiveObjectInspectorFactory.LAZY_BINARY_OBJECT_INSPECTOR);
    }
}
