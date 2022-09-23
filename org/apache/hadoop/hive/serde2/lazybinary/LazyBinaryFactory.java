// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import java.util.List;
import org.apache.hadoop.hive.serde2.lazybinary.objectinspector.LazyBinaryUnionObjectInspector;
import org.apache.hadoop.hive.serde2.lazybinary.objectinspector.LazyBinaryStructObjectInspector;
import org.apache.hadoop.hive.serde2.lazybinary.objectinspector.LazyBinaryListObjectInspector;
import org.apache.hadoop.hive.serde2.lazybinary.objectinspector.LazyBinaryMapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableHiveDecimalObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableBinaryObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableHiveIntervalDayTimeObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableHiveIntervalYearMonthObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableTimestampObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableDateObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableVoidObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableHiveVarcharObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableHiveCharObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableStringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableDoubleObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableFloatObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableLongObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableIntObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableShortObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableByteObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableBooleanObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;

public final class LazyBinaryFactory
{
    public static LazyBinaryPrimitive<?, ?> createLazyBinaryPrimitiveClass(final PrimitiveObjectInspector oi) {
        final PrimitiveObjectInspector.PrimitiveCategory p = oi.getPrimitiveCategory();
        switch (p) {
            case BOOLEAN: {
                return new LazyBinaryBoolean((WritableBooleanObjectInspector)oi);
            }
            case BYTE: {
                return new LazyBinaryByte((WritableByteObjectInspector)oi);
            }
            case SHORT: {
                return new LazyBinaryShort((WritableShortObjectInspector)oi);
            }
            case INT: {
                return new LazyBinaryInteger((WritableIntObjectInspector)oi);
            }
            case LONG: {
                return new LazyBinaryLong((WritableLongObjectInspector)oi);
            }
            case FLOAT: {
                return new LazyBinaryFloat((WritableFloatObjectInspector)oi);
            }
            case DOUBLE: {
                return new LazyBinaryDouble((WritableDoubleObjectInspector)oi);
            }
            case STRING: {
                return new LazyBinaryString((WritableStringObjectInspector)oi);
            }
            case CHAR: {
                return new LazyBinaryHiveChar((WritableHiveCharObjectInspector)oi);
            }
            case VARCHAR: {
                return new LazyBinaryHiveVarchar((WritableHiveVarcharObjectInspector)oi);
            }
            case VOID: {
                return new LazyBinaryVoid((WritableVoidObjectInspector)oi);
            }
            case DATE: {
                return new LazyBinaryDate((WritableDateObjectInspector)oi);
            }
            case TIMESTAMP: {
                return new LazyBinaryTimestamp((WritableTimestampObjectInspector)oi);
            }
            case INTERVAL_YEAR_MONTH: {
                return new LazyBinaryHiveIntervalYearMonth((WritableHiveIntervalYearMonthObjectInspector)oi);
            }
            case INTERVAL_DAY_TIME: {
                return new LazyBinaryHiveIntervalDayTime((WritableHiveIntervalDayTimeObjectInspector)oi);
            }
            case BINARY: {
                return new LazyBinaryBinary((WritableBinaryObjectInspector)oi);
            }
            case DECIMAL: {
                return new LazyBinaryHiveDecimal((WritableHiveDecimalObjectInspector)oi);
            }
            default: {
                throw new RuntimeException("Internal error: no LazyBinaryObject for " + p);
            }
        }
    }
    
    public static LazyBinaryObject createLazyBinaryObject(final ObjectInspector oi) {
        final ObjectInspector.Category c = oi.getCategory();
        switch (c) {
            case PRIMITIVE: {
                return createLazyBinaryPrimitiveClass((PrimitiveObjectInspector)oi);
            }
            case MAP: {
                return new LazyBinaryMap((LazyBinaryMapObjectInspector)oi);
            }
            case LIST: {
                return new LazyBinaryArray((LazyBinaryListObjectInspector)oi);
            }
            case STRUCT: {
                return new LazyBinaryStruct((LazyBinaryStructObjectInspector)oi);
            }
            case UNION: {
                return new LazyBinaryUnion((LazyBinaryUnionObjectInspector)oi);
            }
            default: {
                throw new RuntimeException("Hive LazyBinarySerDe Internal error.");
            }
        }
    }
    
    private LazyBinaryFactory() {
    }
    
    public static ObjectInspector createColumnarStructInspector(final List<String> columnNames, final List<TypeInfo> columnTypes) {
        final ArrayList<ObjectInspector> columnObjectInspectors = new ArrayList<ObjectInspector>(columnTypes.size());
        for (int i = 0; i < columnTypes.size(); ++i) {
            columnObjectInspectors.add(LazyBinaryUtils.getLazyBinaryObjectInspectorFromTypeInfo(columnTypes.get(i)));
        }
        return ObjectInspectorFactory.getColumnarStructObjectInspector(columnNames, columnObjectInspectors);
    }
}
