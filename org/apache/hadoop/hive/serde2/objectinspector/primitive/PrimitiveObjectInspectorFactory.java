// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import java.util.EnumMap;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.io.ByteWritable;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import java.util.Map;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import java.util.concurrent.ConcurrentHashMap;

public final class PrimitiveObjectInspectorFactory
{
    public static final WritableBooleanObjectInspector writableBooleanObjectInspector;
    public static final WritableByteObjectInspector writableByteObjectInspector;
    public static final WritableShortObjectInspector writableShortObjectInspector;
    public static final WritableIntObjectInspector writableIntObjectInspector;
    public static final WritableLongObjectInspector writableLongObjectInspector;
    public static final WritableFloatObjectInspector writableFloatObjectInspector;
    public static final WritableDoubleObjectInspector writableDoubleObjectInspector;
    public static final WritableStringObjectInspector writableStringObjectInspector;
    public static final WritableHiveCharObjectInspector writableHiveCharObjectInspector;
    public static final WritableHiveVarcharObjectInspector writableHiveVarcharObjectInspector;
    public static final WritableVoidObjectInspector writableVoidObjectInspector;
    public static final WritableDateObjectInspector writableDateObjectInspector;
    public static final WritableTimestampObjectInspector writableTimestampObjectInspector;
    public static final WritableHiveIntervalYearMonthObjectInspector writableHiveIntervalYearMonthObjectInspector;
    public static final WritableHiveIntervalDayTimeObjectInspector writableHiveIntervalDayTimeObjectInspector;
    public static final WritableBinaryObjectInspector writableBinaryObjectInspector;
    public static final WritableHiveDecimalObjectInspector writableHiveDecimalObjectInspector;
    private static ConcurrentHashMap<PrimitiveTypeInfo, AbstractPrimitiveWritableObjectInspector> cachedPrimitiveWritableInspectorCache;
    private static Map<PrimitiveObjectInspector.PrimitiveCategory, AbstractPrimitiveWritableObjectInspector> primitiveCategoryToWritableOI;
    public static final JavaBooleanObjectInspector javaBooleanObjectInspector;
    public static final JavaByteObjectInspector javaByteObjectInspector;
    public static final JavaShortObjectInspector javaShortObjectInspector;
    public static final JavaIntObjectInspector javaIntObjectInspector;
    public static final JavaLongObjectInspector javaLongObjectInspector;
    public static final JavaFloatObjectInspector javaFloatObjectInspector;
    public static final JavaDoubleObjectInspector javaDoubleObjectInspector;
    public static final JavaStringObjectInspector javaStringObjectInspector;
    public static final JavaHiveCharObjectInspector javaHiveCharObjectInspector;
    public static final JavaHiveVarcharObjectInspector javaHiveVarcharObjectInspector;
    public static final JavaVoidObjectInspector javaVoidObjectInspector;
    public static final JavaDateObjectInspector javaDateObjectInspector;
    public static final JavaTimestampObjectInspector javaTimestampObjectInspector;
    public static final JavaHiveIntervalYearMonthObjectInspector javaHiveIntervalYearMonthObjectInspector;
    public static final JavaHiveIntervalDayTimeObjectInspector javaHiveIntervalDayTimeObjectInspector;
    public static final JavaBinaryObjectInspector javaByteArrayObjectInspector;
    public static final JavaHiveDecimalObjectInspector javaHiveDecimalObjectInspector;
    private static ConcurrentHashMap<PrimitiveTypeInfo, AbstractPrimitiveJavaObjectInspector> cachedPrimitiveJavaInspectorCache;
    private static Map<PrimitiveObjectInspector.PrimitiveCategory, AbstractPrimitiveJavaObjectInspector> primitiveCategoryToJavaOI;
    
    public static AbstractPrimitiveWritableObjectInspector getPrimitiveWritableObjectInspector(final PrimitiveObjectInspector.PrimitiveCategory primitiveCategory) {
        final AbstractPrimitiveWritableObjectInspector result = PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.get(primitiveCategory);
        if (result == null) {
            throw new RuntimeException("Internal error: Cannot find ObjectInspector  for " + primitiveCategory);
        }
        return result;
    }
    
    public static AbstractPrimitiveWritableObjectInspector getPrimitiveWritableObjectInspector(final PrimitiveTypeInfo typeInfo) {
        AbstractPrimitiveWritableObjectInspector result = PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.get(typeInfo);
        if (result != null) {
            return result;
        }
        switch (typeInfo.getPrimitiveCategory()) {
            case CHAR: {
                result = new WritableHiveCharObjectInspector((CharTypeInfo)typeInfo);
                break;
            }
            case VARCHAR: {
                result = new WritableHiveVarcharObjectInspector((VarcharTypeInfo)typeInfo);
                break;
            }
            case DECIMAL: {
                result = new WritableHiveDecimalObjectInspector((DecimalTypeInfo)typeInfo);
                break;
            }
            default: {
                throw new RuntimeException("Failed to create object inspector for " + typeInfo);
            }
        }
        final AbstractPrimitiveWritableObjectInspector prev = PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.putIfAbsent(typeInfo, result);
        if (prev != null) {
            result = prev;
        }
        return result;
    }
    
    public static ConstantObjectInspector getPrimitiveWritableConstantObjectInspector(final PrimitiveTypeInfo typeInfo, final Object value) {
        switch (typeInfo.getPrimitiveCategory()) {
            case BOOLEAN: {
                return new WritableConstantBooleanObjectInspector((BooleanWritable)value);
            }
            case BYTE: {
                return new WritableConstantByteObjectInspector((ByteWritable)value);
            }
            case SHORT: {
                return new WritableConstantShortObjectInspector((ShortWritable)value);
            }
            case INT: {
                return new WritableConstantIntObjectInspector((IntWritable)value);
            }
            case LONG: {
                return new WritableConstantLongObjectInspector((LongWritable)value);
            }
            case FLOAT: {
                return new WritableConstantFloatObjectInspector((FloatWritable)value);
            }
            case DOUBLE: {
                return new WritableConstantDoubleObjectInspector((DoubleWritable)value);
            }
            case STRING: {
                return new WritableConstantStringObjectInspector((Text)value);
            }
            case CHAR: {
                return new WritableConstantHiveCharObjectInspector((CharTypeInfo)typeInfo, (HiveCharWritable)value);
            }
            case VARCHAR: {
                return new WritableConstantHiveVarcharObjectInspector((VarcharTypeInfo)typeInfo, (HiveVarcharWritable)value);
            }
            case DATE: {
                return new WritableConstantDateObjectInspector((DateWritable)value);
            }
            case TIMESTAMP: {
                return new WritableConstantTimestampObjectInspector((TimestampWritable)value);
            }
            case INTERVAL_YEAR_MONTH: {
                return new WritableConstantHiveIntervalYearMonthObjectInspector((HiveIntervalYearMonthWritable)value);
            }
            case INTERVAL_DAY_TIME: {
                return new WritableConstantHiveIntervalDayTimeObjectInspector((HiveIntervalDayTimeWritable)value);
            }
            case DECIMAL: {
                return new WritableConstantHiveDecimalObjectInspector((DecimalTypeInfo)typeInfo, (HiveDecimalWritable)value);
            }
            case BINARY: {
                return new WritableConstantBinaryObjectInspector((BytesWritable)value);
            }
            case VOID: {
                return new WritableVoidObjectInspector();
            }
            default: {
                throw new RuntimeException("Internal error: Cannot find ConstantObjectInspector for " + typeInfo);
            }
        }
    }
    
    public static AbstractPrimitiveJavaObjectInspector getPrimitiveJavaObjectInspector(final PrimitiveObjectInspector.PrimitiveCategory primitiveCategory) {
        final AbstractPrimitiveJavaObjectInspector result = PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.get(primitiveCategory);
        if (result == null) {
            throw new RuntimeException("Internal error: Cannot find ObjectInspector  for " + primitiveCategory);
        }
        return result;
    }
    
    public static AbstractPrimitiveJavaObjectInspector getPrimitiveJavaObjectInspector(final PrimitiveTypeInfo typeInfo) {
        AbstractPrimitiveJavaObjectInspector result = PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.get(typeInfo);
        if (result != null) {
            return result;
        }
        switch (typeInfo.getPrimitiveCategory()) {
            case CHAR: {
                result = new JavaHiveCharObjectInspector((CharTypeInfo)typeInfo);
                break;
            }
            case VARCHAR: {
                result = new JavaHiveVarcharObjectInspector((VarcharTypeInfo)typeInfo);
                break;
            }
            case DECIMAL: {
                result = new JavaHiveDecimalObjectInspector((DecimalTypeInfo)typeInfo);
                break;
            }
            default: {
                throw new RuntimeException("Failed to create Java ObjectInspector for " + typeInfo);
            }
        }
        final AbstractPrimitiveJavaObjectInspector prev = PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.putIfAbsent(typeInfo, result);
        if (prev != null) {
            result = prev;
        }
        return result;
    }
    
    public static PrimitiveObjectInspector getPrimitiveObjectInspectorFromClass(final Class<?> c) {
        if (Writable.class.isAssignableFrom(c)) {
            final PrimitiveObjectInspectorUtils.PrimitiveTypeEntry te = PrimitiveObjectInspectorUtils.getTypeEntryFromPrimitiveWritableClass(c);
            if (te == null) {
                throw new RuntimeException("Internal error: Cannot recognize " + c);
            }
            return getPrimitiveWritableObjectInspector(te.primitiveCategory);
        }
        else {
            final PrimitiveObjectInspectorUtils.PrimitiveTypeEntry te = PrimitiveObjectInspectorUtils.getTypeEntryFromPrimitiveJavaClass(c);
            if (te == null) {
                throw new RuntimeException("Internal error: Cannot recognize " + c);
            }
            return getPrimitiveJavaObjectInspector(te.primitiveCategory);
        }
    }
    
    private PrimitiveObjectInspectorFactory() {
    }
    
    static {
        writableBooleanObjectInspector = new WritableBooleanObjectInspector();
        writableByteObjectInspector = new WritableByteObjectInspector();
        writableShortObjectInspector = new WritableShortObjectInspector();
        writableIntObjectInspector = new WritableIntObjectInspector();
        writableLongObjectInspector = new WritableLongObjectInspector();
        writableFloatObjectInspector = new WritableFloatObjectInspector();
        writableDoubleObjectInspector = new WritableDoubleObjectInspector();
        writableStringObjectInspector = new WritableStringObjectInspector();
        writableHiveCharObjectInspector = new WritableHiveCharObjectInspector((CharTypeInfo)TypeInfoFactory.charTypeInfo);
        writableHiveVarcharObjectInspector = new WritableHiveVarcharObjectInspector((VarcharTypeInfo)TypeInfoFactory.varcharTypeInfo);
        writableVoidObjectInspector = new WritableVoidObjectInspector();
        writableDateObjectInspector = new WritableDateObjectInspector();
        writableTimestampObjectInspector = new WritableTimestampObjectInspector();
        writableHiveIntervalYearMonthObjectInspector = new WritableHiveIntervalYearMonthObjectInspector();
        writableHiveIntervalDayTimeObjectInspector = new WritableHiveIntervalDayTimeObjectInspector();
        writableBinaryObjectInspector = new WritableBinaryObjectInspector();
        writableHiveDecimalObjectInspector = new WritableHiveDecimalObjectInspector(TypeInfoFactory.decimalTypeInfo);
        (PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache = new ConcurrentHashMap<PrimitiveTypeInfo, AbstractPrimitiveWritableObjectInspector>()).put(TypeInfoFactory.getPrimitiveTypeInfo("boolean"), PrimitiveObjectInspectorFactory.writableBooleanObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("tinyint"), PrimitiveObjectInspectorFactory.writableByteObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("smallint"), PrimitiveObjectInspectorFactory.writableShortObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("int"), PrimitiveObjectInspectorFactory.writableIntObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("bigint"), PrimitiveObjectInspectorFactory.writableLongObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("float"), PrimitiveObjectInspectorFactory.writableFloatObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("double"), PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("string"), PrimitiveObjectInspectorFactory.writableStringObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.charTypeInfo, PrimitiveObjectInspectorFactory.writableHiveCharObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.varcharTypeInfo, PrimitiveObjectInspectorFactory.writableHiveVarcharObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("void"), PrimitiveObjectInspectorFactory.writableVoidObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("date"), PrimitiveObjectInspectorFactory.writableDateObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("timestamp"), PrimitiveObjectInspectorFactory.writableTimestampObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("interval_year_month"), PrimitiveObjectInspectorFactory.writableHiveIntervalYearMonthObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("interval_day_time"), PrimitiveObjectInspectorFactory.writableHiveIntervalDayTimeObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("binary"), PrimitiveObjectInspectorFactory.writableBinaryObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveWritableInspectorCache.put(TypeInfoFactory.decimalTypeInfo, PrimitiveObjectInspectorFactory.writableHiveDecimalObjectInspector);
        (PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI = new EnumMap<PrimitiveObjectInspector.PrimitiveCategory, AbstractPrimitiveWritableObjectInspector>(PrimitiveObjectInspector.PrimitiveCategory.class)).put(PrimitiveObjectInspector.PrimitiveCategory.BOOLEAN, PrimitiveObjectInspectorFactory.writableBooleanObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.BYTE, PrimitiveObjectInspectorFactory.writableByteObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.SHORT, PrimitiveObjectInspectorFactory.writableShortObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.INT, PrimitiveObjectInspectorFactory.writableIntObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.LONG, PrimitiveObjectInspectorFactory.writableLongObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.FLOAT, PrimitiveObjectInspectorFactory.writableFloatObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.DOUBLE, PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.STRING, PrimitiveObjectInspectorFactory.writableStringObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.CHAR, PrimitiveObjectInspectorFactory.writableHiveCharObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.VARCHAR, PrimitiveObjectInspectorFactory.writableHiveVarcharObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.VOID, PrimitiveObjectInspectorFactory.writableVoidObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.DATE, PrimitiveObjectInspectorFactory.writableDateObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.TIMESTAMP, PrimitiveObjectInspectorFactory.writableTimestampObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.INTERVAL_YEAR_MONTH, PrimitiveObjectInspectorFactory.writableHiveIntervalYearMonthObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.INTERVAL_DAY_TIME, PrimitiveObjectInspectorFactory.writableHiveIntervalDayTimeObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.BINARY, PrimitiveObjectInspectorFactory.writableBinaryObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToWritableOI.put(PrimitiveObjectInspector.PrimitiveCategory.DECIMAL, PrimitiveObjectInspectorFactory.writableHiveDecimalObjectInspector);
        javaBooleanObjectInspector = new JavaBooleanObjectInspector();
        javaByteObjectInspector = new JavaByteObjectInspector();
        javaShortObjectInspector = new JavaShortObjectInspector();
        javaIntObjectInspector = new JavaIntObjectInspector();
        javaLongObjectInspector = new JavaLongObjectInspector();
        javaFloatObjectInspector = new JavaFloatObjectInspector();
        javaDoubleObjectInspector = new JavaDoubleObjectInspector();
        javaStringObjectInspector = new JavaStringObjectInspector();
        javaHiveCharObjectInspector = new JavaHiveCharObjectInspector((CharTypeInfo)TypeInfoFactory.charTypeInfo);
        javaHiveVarcharObjectInspector = new JavaHiveVarcharObjectInspector((VarcharTypeInfo)TypeInfoFactory.varcharTypeInfo);
        javaVoidObjectInspector = new JavaVoidObjectInspector();
        javaDateObjectInspector = new JavaDateObjectInspector();
        javaTimestampObjectInspector = new JavaTimestampObjectInspector();
        javaHiveIntervalYearMonthObjectInspector = new JavaHiveIntervalYearMonthObjectInspector();
        javaHiveIntervalDayTimeObjectInspector = new JavaHiveIntervalDayTimeObjectInspector();
        javaByteArrayObjectInspector = new JavaBinaryObjectInspector();
        javaHiveDecimalObjectInspector = new JavaHiveDecimalObjectInspector(TypeInfoFactory.decimalTypeInfo);
        (PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache = new ConcurrentHashMap<PrimitiveTypeInfo, AbstractPrimitiveJavaObjectInspector>()).put(TypeInfoFactory.getPrimitiveTypeInfo("boolean"), PrimitiveObjectInspectorFactory.javaBooleanObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("tinyint"), PrimitiveObjectInspectorFactory.javaByteObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("smallint"), PrimitiveObjectInspectorFactory.javaShortObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("int"), PrimitiveObjectInspectorFactory.javaIntObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("bigint"), PrimitiveObjectInspectorFactory.javaLongObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("float"), PrimitiveObjectInspectorFactory.javaFloatObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("double"), PrimitiveObjectInspectorFactory.javaDoubleObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("string"), PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.charTypeInfo, PrimitiveObjectInspectorFactory.javaHiveCharObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.varcharTypeInfo, PrimitiveObjectInspectorFactory.javaHiveVarcharObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("void"), PrimitiveObjectInspectorFactory.javaVoidObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("date"), PrimitiveObjectInspectorFactory.javaDateObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("timestamp"), PrimitiveObjectInspectorFactory.javaTimestampObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("interval_year_month"), PrimitiveObjectInspectorFactory.javaHiveIntervalYearMonthObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("interval_day_time"), PrimitiveObjectInspectorFactory.javaHiveIntervalDayTimeObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.getPrimitiveTypeInfo("binary"), PrimitiveObjectInspectorFactory.javaByteArrayObjectInspector);
        PrimitiveObjectInspectorFactory.cachedPrimitiveJavaInspectorCache.put(TypeInfoFactory.decimalTypeInfo, PrimitiveObjectInspectorFactory.javaHiveDecimalObjectInspector);
        (PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI = new EnumMap<PrimitiveObjectInspector.PrimitiveCategory, AbstractPrimitiveJavaObjectInspector>(PrimitiveObjectInspector.PrimitiveCategory.class)).put(PrimitiveObjectInspector.PrimitiveCategory.BOOLEAN, PrimitiveObjectInspectorFactory.javaBooleanObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.BYTE, PrimitiveObjectInspectorFactory.javaByteObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.SHORT, PrimitiveObjectInspectorFactory.javaShortObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.INT, PrimitiveObjectInspectorFactory.javaIntObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.LONG, PrimitiveObjectInspectorFactory.javaLongObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.FLOAT, PrimitiveObjectInspectorFactory.javaFloatObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.DOUBLE, PrimitiveObjectInspectorFactory.javaDoubleObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.STRING, PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.CHAR, PrimitiveObjectInspectorFactory.javaHiveCharObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.VARCHAR, PrimitiveObjectInspectorFactory.javaHiveVarcharObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.VOID, PrimitiveObjectInspectorFactory.javaVoidObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.DATE, PrimitiveObjectInspectorFactory.javaDateObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.TIMESTAMP, PrimitiveObjectInspectorFactory.javaTimestampObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.INTERVAL_YEAR_MONTH, PrimitiveObjectInspectorFactory.javaHiveIntervalYearMonthObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.INTERVAL_DAY_TIME, PrimitiveObjectInspectorFactory.javaHiveIntervalDayTimeObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.BINARY, PrimitiveObjectInspectorFactory.javaByteArrayObjectInspector);
        PrimitiveObjectInspectorFactory.primitiveCategoryToJavaOI.put(PrimitiveObjectInspector.PrimitiveCategory.DECIMAL, PrimitiveObjectInspectorFactory.javaHiveDecimalObjectInspector);
    }
}
