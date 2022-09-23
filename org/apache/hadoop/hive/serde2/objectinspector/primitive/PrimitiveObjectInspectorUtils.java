// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.WritableUtils;
import java.io.DataInput;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.io.ByteWritable;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.BooleanWritable;
import java.util.HashMap;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import java.sql.Timestamp;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import java.sql.Date;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.common.type.HiveChar;
import java.nio.charset.CharacterCodingException;
import org.apache.hadoop.hive.serde2.lazy.LazyLong;
import org.apache.hadoop.hive.serde2.lazy.LazyInteger;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import java.util.Map;
import org.apache.commons.logging.Log;

public final class PrimitiveObjectInspectorUtils
{
    private static Log LOG;
    static final Map<PrimitiveObjectInspector.PrimitiveCategory, PrimitiveTypeEntry> primitiveCategoryToTypeEntry;
    static final Map<Class<?>, PrimitiveTypeEntry> primitiveJavaTypeToTypeEntry;
    static final Map<Class<?>, PrimitiveTypeEntry> primitiveJavaClassToTypeEntry;
    static final Map<Class<?>, PrimitiveTypeEntry> primitiveWritableClassToTypeEntry;
    private static final Map<String, PrimitiveTypeEntry> typeNameToTypeEntry;
    public static final PrimitiveTypeEntry binaryTypeEntry;
    public static final PrimitiveTypeEntry stringTypeEntry;
    public static final PrimitiveTypeEntry booleanTypeEntry;
    public static final PrimitiveTypeEntry intTypeEntry;
    public static final PrimitiveTypeEntry longTypeEntry;
    public static final PrimitiveTypeEntry floatTypeEntry;
    public static final PrimitiveTypeEntry voidTypeEntry;
    public static final PrimitiveTypeEntry doubleTypeEntry;
    public static final PrimitiveTypeEntry byteTypeEntry;
    public static final PrimitiveTypeEntry shortTypeEntry;
    public static final PrimitiveTypeEntry dateTypeEntry;
    public static final PrimitiveTypeEntry timestampTypeEntry;
    public static final PrimitiveTypeEntry intervalYearMonthTypeEntry;
    public static final PrimitiveTypeEntry intervalDayTimeTypeEntry;
    public static final PrimitiveTypeEntry decimalTypeEntry;
    public static final PrimitiveTypeEntry varcharTypeEntry;
    public static final PrimitiveTypeEntry charTypeEntry;
    public static final PrimitiveTypeEntry unknownTypeEntry;
    
    static void addParameterizedType(final PrimitiveTypeEntry t) {
        PrimitiveObjectInspectorUtils.typeNameToTypeEntry.put(t.toString(), t);
    }
    
    static void registerType(final PrimitiveTypeEntry t) {
        if (t.primitiveCategory != PrimitiveObjectInspector.PrimitiveCategory.UNKNOWN) {
            PrimitiveObjectInspectorUtils.primitiveCategoryToTypeEntry.put(t.primitiveCategory, t);
        }
        if (t.primitiveJavaType != null) {
            PrimitiveObjectInspectorUtils.primitiveJavaTypeToTypeEntry.put(t.primitiveJavaType, t);
        }
        if (t.primitiveJavaClass != null) {
            PrimitiveObjectInspectorUtils.primitiveJavaClassToTypeEntry.put(t.primitiveJavaClass, t);
        }
        if (t.primitiveWritableClass != null) {
            PrimitiveObjectInspectorUtils.primitiveWritableClassToTypeEntry.put(t.primitiveWritableClass, t);
        }
        if (t.typeName != null) {
            PrimitiveObjectInspectorUtils.typeNameToTypeEntry.put(t.typeName, t);
        }
    }
    
    public static Class<?> primitiveJavaTypeToClass(final Class<?> clazz) {
        final PrimitiveTypeEntry t = PrimitiveObjectInspectorUtils.primitiveJavaTypeToTypeEntry.get(clazz);
        return (t == null) ? clazz : t.primitiveJavaClass;
    }
    
    public static boolean isPrimitiveJava(final Class<?> clazz) {
        return PrimitiveObjectInspectorUtils.primitiveJavaTypeToTypeEntry.get(clazz) != null || PrimitiveObjectInspectorUtils.primitiveJavaClassToTypeEntry.get(clazz) != null;
    }
    
    public static boolean isPrimitiveJavaType(final Class<?> clazz) {
        return PrimitiveObjectInspectorUtils.primitiveJavaTypeToTypeEntry.get(clazz) != null;
    }
    
    public static boolean isPrimitiveJavaClass(final Class<?> clazz) {
        return PrimitiveObjectInspectorUtils.primitiveJavaClassToTypeEntry.get(clazz) != null;
    }
    
    public static boolean isPrimitiveWritableClass(final Class<?> clazz) {
        return PrimitiveObjectInspectorUtils.primitiveWritableClassToTypeEntry.get(clazz) != null;
    }
    
    public static String getTypeNameFromPrimitiveJava(final Class<?> clazz) {
        PrimitiveTypeEntry t = PrimitiveObjectInspectorUtils.primitiveJavaTypeToTypeEntry.get(clazz);
        if (t == null) {
            t = PrimitiveObjectInspectorUtils.primitiveJavaClassToTypeEntry.get(clazz);
        }
        return (t == null) ? null : t.typeName;
    }
    
    public static String getTypeNameFromPrimitiveWritable(final Class<?> clazz) {
        final PrimitiveTypeEntry t = PrimitiveObjectInspectorUtils.primitiveWritableClassToTypeEntry.get(clazz);
        return (t == null) ? null : t.typeName;
    }
    
    public static PrimitiveTypeEntry getTypeEntryFromPrimitiveCategory(final PrimitiveObjectInspector.PrimitiveCategory category) {
        return PrimitiveObjectInspectorUtils.primitiveCategoryToTypeEntry.get(category);
    }
    
    public static PrimitiveTypeEntry getTypeEntryFromPrimitiveJava(final Class<?> clazz) {
        PrimitiveTypeEntry t = PrimitiveObjectInspectorUtils.primitiveJavaTypeToTypeEntry.get(clazz);
        if (t == null) {
            t = PrimitiveObjectInspectorUtils.primitiveJavaClassToTypeEntry.get(clazz);
        }
        return t;
    }
    
    public static PrimitiveTypeEntry getTypeEntryFromPrimitiveJavaType(final Class<?> clazz) {
        return PrimitiveObjectInspectorUtils.primitiveJavaTypeToTypeEntry.get(clazz);
    }
    
    public static PrimitiveTypeEntry getTypeEntryFromPrimitiveJavaClass(final Class<?> clazz) {
        return PrimitiveObjectInspectorUtils.primitiveJavaClassToTypeEntry.get(clazz);
    }
    
    public static PrimitiveTypeEntry getTypeEntryFromPrimitiveWritableClass(final Class<?> clazz) {
        return PrimitiveObjectInspectorUtils.primitiveWritableClassToTypeEntry.get(clazz);
    }
    
    public static PrimitiveTypeEntry getTypeEntryFromTypeName(final String typeName) {
        return PrimitiveObjectInspectorUtils.typeNameToTypeEntry.get(typeName);
    }
    
    public static boolean comparePrimitiveObjects(final Object o1, final PrimitiveObjectInspector oi1, final Object o2, final PrimitiveObjectInspector oi2) {
        if (o1 == null || o2 == null) {
            return false;
        }
        if (oi1.getPrimitiveCategory() != oi2.getPrimitiveCategory()) {
            return false;
        }
        switch (oi1.getPrimitiveCategory()) {
            case BOOLEAN: {
                return ((BooleanObjectInspector)oi1).get(o1) == ((BooleanObjectInspector)oi2).get(o2);
            }
            case BYTE: {
                return ((ByteObjectInspector)oi1).get(o1) == ((ByteObjectInspector)oi2).get(o2);
            }
            case SHORT: {
                return ((ShortObjectInspector)oi1).get(o1) == ((ShortObjectInspector)oi2).get(o2);
            }
            case INT: {
                return ((IntObjectInspector)oi1).get(o1) == ((IntObjectInspector)oi2).get(o2);
            }
            case LONG: {
                return ((LongObjectInspector)oi1).get(o1) == ((LongObjectInspector)oi2).get(o2);
            }
            case FLOAT: {
                return ((FloatObjectInspector)oi1).get(o1) == ((FloatObjectInspector)oi2).get(o2);
            }
            case DOUBLE: {
                return ((DoubleObjectInspector)oi1).get(o1) == ((DoubleObjectInspector)oi2).get(o2);
            }
            case STRING: {
                final Writable t1 = ((StringObjectInspector)oi1).getPrimitiveWritableObject(o1);
                final Writable t2 = ((StringObjectInspector)oi2).getPrimitiveWritableObject(o2);
                return t1.equals(t2);
            }
            case CHAR: {
                return ((HiveCharObjectInspector)oi1).getPrimitiveWritableObject(o1).equals(((HiveCharObjectInspector)oi2).getPrimitiveWritableObject(o2));
            }
            case VARCHAR: {
                return ((HiveVarcharObjectInspector)oi1).getPrimitiveWritableObject(o1).equals(((HiveVarcharObjectInspector)oi2).getPrimitiveWritableObject(o2));
            }
            case DATE: {
                return ((DateObjectInspector)oi1).getPrimitiveWritableObject(o1).equals(((DateObjectInspector)oi2).getPrimitiveWritableObject(o2));
            }
            case TIMESTAMP: {
                return ((TimestampObjectInspector)oi1).getPrimitiveWritableObject(o1).equals(((TimestampObjectInspector)oi2).getPrimitiveWritableObject(o2));
            }
            case INTERVAL_YEAR_MONTH: {
                return ((HiveIntervalYearMonthObjectInspector)oi1).getPrimitiveWritableObject(o1).equals(((HiveIntervalYearMonthObjectInspector)oi2).getPrimitiveWritableObject(o2));
            }
            case INTERVAL_DAY_TIME: {
                return ((HiveIntervalDayTimeObjectInspector)oi1).getPrimitiveWritableObject(o1).equals(((HiveIntervalDayTimeObjectInspector)oi2).getPrimitiveWritableObject(o2));
            }
            case BINARY: {
                return ((BinaryObjectInspector)oi1).getPrimitiveWritableObject(o1).equals(((BinaryObjectInspector)oi2).getPrimitiveWritableObject(o2));
            }
            case DECIMAL: {
                return ((HiveDecimalObjectInspector)oi1).getPrimitiveJavaObject(o1).compareTo(((HiveDecimalObjectInspector)oi2).getPrimitiveJavaObject(o2)) == 0;
            }
            default: {
                return false;
            }
        }
    }
    
    public static double convertPrimitiveToDouble(final Object o, final PrimitiveObjectInspector oi) {
        switch (oi.getPrimitiveCategory()) {
            case BOOLEAN: {
                return ((BooleanObjectInspector)oi).get(o) ? 1.0 : 0.0;
            }
            case BYTE: {
                return ((ByteObjectInspector)oi).get(o);
            }
            case SHORT: {
                return ((ShortObjectInspector)oi).get(o);
            }
            case INT: {
                return ((IntObjectInspector)oi).get(o);
            }
            case LONG: {
                return (double)((LongObjectInspector)oi).get(o);
            }
            case FLOAT: {
                return ((FloatObjectInspector)oi).get(o);
            }
            case DOUBLE: {
                return ((DoubleObjectInspector)oi).get(o);
            }
            case STRING: {
                return Double.valueOf(((StringObjectInspector)oi).getPrimitiveJavaObject(o));
            }
            case TIMESTAMP: {
                return ((TimestampObjectInspector)oi).getPrimitiveWritableObject(o).getDouble();
            }
            case DECIMAL: {
                return ((HiveDecimalObjectInspector)oi).getPrimitiveJavaObject(o).doubleValue();
            }
            default: {
                throw new NumberFormatException();
            }
        }
    }
    
    public static boolean comparePrimitiveObjectsWithConversion(final Object o1, final PrimitiveObjectInspector oi1, final Object o2, final PrimitiveObjectInspector oi2) {
        if (o1 == null || o2 == null) {
            return false;
        }
        if (oi1.getPrimitiveCategory() == oi2.getPrimitiveCategory()) {
            return comparePrimitiveObjects(o1, oi1, o2, oi2);
        }
        try {
            return convertPrimitiveToDouble(o1, oi1) == convertPrimitiveToDouble(o2, oi2);
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean getBoolean(final Object o, final PrimitiveObjectInspector oi) {
        boolean result = false;
        switch (oi.getPrimitiveCategory()) {
            case VOID: {
                result = false;
                break;
            }
            case BOOLEAN: {
                result = ((BooleanObjectInspector)oi).get(o);
                break;
            }
            case BYTE: {
                result = (((ByteObjectInspector)oi).get(o) != 0);
                break;
            }
            case SHORT: {
                result = (((ShortObjectInspector)oi).get(o) != 0);
                break;
            }
            case INT: {
                result = (((IntObjectInspector)oi).get(o) != 0);
                break;
            }
            case LONG: {
                result = ((int)((LongObjectInspector)oi).get(o) != 0);
                break;
            }
            case FLOAT: {
                result = ((int)((FloatObjectInspector)oi).get(o) != 0);
                break;
            }
            case DOUBLE: {
                result = ((int)((DoubleObjectInspector)oi).get(o) != 0);
                break;
            }
            case STRING: {
                final StringObjectInspector soi = (StringObjectInspector)oi;
                if (soi.preferWritable()) {
                    final Text t = soi.getPrimitiveWritableObject(o);
                    result = (t.getLength() != 0);
                    break;
                }
                final String s = soi.getPrimitiveJavaObject(o);
                result = (s.length() != 0);
                break;
            }
            case TIMESTAMP: {
                result = (((TimestampObjectInspector)oi).getPrimitiveWritableObject(o).getSeconds() != 0L);
                break;
            }
            case DECIMAL: {
                result = (HiveDecimal.ZERO.compareTo(((HiveDecimalObjectInspector)oi).getPrimitiveJavaObject(o)) != 0);
                break;
            }
            default: {
                throw new RuntimeException("Hive 2 Internal error: unsupported conversion from type: " + oi.getTypeName());
            }
        }
        return result;
    }
    
    public static byte getByte(final Object o, final PrimitiveObjectInspector oi) {
        return (byte)getInt(o, oi);
    }
    
    public static short getShort(final Object o, final PrimitiveObjectInspector oi) {
        return (short)getInt(o, oi);
    }
    
    public static int getInt(final Object o, final PrimitiveObjectInspector oi) {
        int result = 0;
        switch (oi.getPrimitiveCategory()) {
            case VOID: {
                result = 0;
                break;
            }
            case BOOLEAN: {
                result = (((BooleanObjectInspector)oi).get(o) ? 1 : 0);
                break;
            }
            case BYTE: {
                result = ((ByteObjectInspector)oi).get(o);
                break;
            }
            case SHORT: {
                result = ((ShortObjectInspector)oi).get(o);
                break;
            }
            case INT: {
                result = ((IntObjectInspector)oi).get(o);
                break;
            }
            case LONG: {
                result = (int)((LongObjectInspector)oi).get(o);
                break;
            }
            case FLOAT: {
                result = (int)((FloatObjectInspector)oi).get(o);
                break;
            }
            case DOUBLE: {
                result = (int)((DoubleObjectInspector)oi).get(o);
                break;
            }
            case STRING: {
                final StringObjectInspector soi = (StringObjectInspector)oi;
                if (soi.preferWritable()) {
                    final Text t = soi.getPrimitiveWritableObject(o);
                    result = LazyInteger.parseInt(t.getBytes(), 0, t.getLength());
                    break;
                }
                final String s = soi.getPrimitiveJavaObject(o);
                result = Integer.parseInt(s);
                break;
            }
            case CHAR:
            case VARCHAR: {
                result = Integer.parseInt(getString(o, oi));
                break;
            }
            case TIMESTAMP: {
                result = (int)((TimestampObjectInspector)oi).getPrimitiveWritableObject(o).getSeconds();
                break;
            }
            case DECIMAL: {
                result = ((HiveDecimalObjectInspector)oi).getPrimitiveJavaObject(o).intValue();
                break;
            }
            default: {
                throw new RuntimeException("Hive 2 Internal error: unsupported conversion from type: " + oi.getTypeName());
            }
        }
        return result;
    }
    
    public static long getLong(final Object o, final PrimitiveObjectInspector oi) {
        long result = 0L;
        switch (oi.getPrimitiveCategory()) {
            case VOID: {
                result = 0L;
                break;
            }
            case BOOLEAN: {
                result = (((BooleanObjectInspector)oi).get(o) ? 1 : 0);
                break;
            }
            case BYTE: {
                result = ((ByteObjectInspector)oi).get(o);
                break;
            }
            case SHORT: {
                result = ((ShortObjectInspector)oi).get(o);
                break;
            }
            case INT: {
                result = ((IntObjectInspector)oi).get(o);
                break;
            }
            case LONG: {
                result = ((LongObjectInspector)oi).get(o);
                break;
            }
            case FLOAT: {
                result = (long)((FloatObjectInspector)oi).get(o);
                break;
            }
            case DOUBLE: {
                result = (long)((DoubleObjectInspector)oi).get(o);
                break;
            }
            case STRING: {
                final StringObjectInspector soi = (StringObjectInspector)oi;
                if (soi.preferWritable()) {
                    final Text t = soi.getPrimitiveWritableObject(o);
                    result = LazyLong.parseLong(t.getBytes(), 0, t.getLength());
                    break;
                }
                final String s = soi.getPrimitiveJavaObject(o);
                result = Long.parseLong(s);
                break;
            }
            case CHAR:
            case VARCHAR: {
                result = Long.parseLong(getString(o, oi));
                break;
            }
            case TIMESTAMP: {
                result = ((TimestampObjectInspector)oi).getPrimitiveWritableObject(o).getSeconds();
                break;
            }
            case DECIMAL: {
                result = ((HiveDecimalObjectInspector)oi).getPrimitiveJavaObject(o).longValue();
                break;
            }
            default: {
                throw new RuntimeException("Hive 2 Internal error: unsupported conversion from type: " + oi.getTypeName());
            }
        }
        return result;
    }
    
    public static double getDouble(final Object o, final PrimitiveObjectInspector oi) {
        double result = 0.0;
        switch (oi.getPrimitiveCategory()) {
            case VOID: {
                result = 0.0;
                break;
            }
            case BOOLEAN: {
                result = (((BooleanObjectInspector)oi).get(o) ? 1 : 0);
                break;
            }
            case BYTE: {
                result = ((ByteObjectInspector)oi).get(o);
                break;
            }
            case SHORT: {
                result = ((ShortObjectInspector)oi).get(o);
                break;
            }
            case INT: {
                result = ((IntObjectInspector)oi).get(o);
                break;
            }
            case LONG: {
                result = (double)((LongObjectInspector)oi).get(o);
                break;
            }
            case FLOAT: {
                result = ((FloatObjectInspector)oi).get(o);
                break;
            }
            case DOUBLE: {
                result = ((DoubleObjectInspector)oi).get(o);
                break;
            }
            case STRING: {
                final StringObjectInspector soi = (StringObjectInspector)oi;
                final String s = soi.getPrimitiveJavaObject(o);
                result = Double.parseDouble(s);
                break;
            }
            case CHAR:
            case VARCHAR: {
                result = Double.parseDouble(getString(o, oi));
                break;
            }
            case TIMESTAMP: {
                result = ((TimestampObjectInspector)oi).getPrimitiveWritableObject(o).getDouble();
                break;
            }
            case DECIMAL: {
                result = ((HiveDecimalObjectInspector)oi).getPrimitiveJavaObject(o).doubleValue();
                break;
            }
            default: {
                throw new RuntimeException("Hive 2 Internal error: unsupported conversion from type: " + oi.getTypeName());
            }
        }
        return result;
    }
    
    public static float getFloat(final Object o, final PrimitiveObjectInspector oi) {
        return (float)getDouble(o, oi);
    }
    
    public static String getString(final Object o, final PrimitiveObjectInspector oi) {
        if (o == null) {
            return null;
        }
        String result = null;
        switch (oi.getPrimitiveCategory()) {
            case VOID: {
                result = null;
                break;
            }
            case BINARY: {
                try {
                    final byte[] bytes = ((BinaryObjectInspector)oi).getPrimitiveWritableObject(o).getBytes();
                    final int byteLen = ((BinaryObjectInspector)oi).getPrimitiveWritableObject(o).getLength();
                    result = Text.decode(bytes, 0, byteLen);
                }
                catch (CharacterCodingException err) {
                    result = null;
                }
                break;
            }
            case BOOLEAN: {
                result = String.valueOf(((BooleanObjectInspector)oi).get(o));
                break;
            }
            case BYTE: {
                result = String.valueOf(((ByteObjectInspector)oi).get(o));
                break;
            }
            case SHORT: {
                result = String.valueOf(((ShortObjectInspector)oi).get(o));
                break;
            }
            case INT: {
                result = String.valueOf(((IntObjectInspector)oi).get(o));
                break;
            }
            case LONG: {
                result = String.valueOf(((LongObjectInspector)oi).get(o));
                break;
            }
            case FLOAT: {
                result = String.valueOf(((FloatObjectInspector)oi).get(o));
                break;
            }
            case DOUBLE: {
                result = String.valueOf(((DoubleObjectInspector)oi).get(o));
                break;
            }
            case STRING: {
                final StringObjectInspector soi = (StringObjectInspector)oi;
                result = soi.getPrimitiveJavaObject(o);
                break;
            }
            case CHAR: {
                result = ((HiveCharObjectInspector)oi).getPrimitiveJavaObject(o).getStrippedValue();
                break;
            }
            case VARCHAR: {
                final HiveVarcharObjectInspector hcoi = (HiveVarcharObjectInspector)oi;
                result = hcoi.getPrimitiveJavaObject(o).toString();
                break;
            }
            case DATE: {
                result = ((DateObjectInspector)oi).getPrimitiveWritableObject(o).toString();
                break;
            }
            case TIMESTAMP: {
                result = ((TimestampObjectInspector)oi).getPrimitiveWritableObject(o).toString();
                break;
            }
            case INTERVAL_YEAR_MONTH: {
                result = ((HiveIntervalYearMonthObjectInspector)oi).getPrimitiveWritableObject(o).toString();
                break;
            }
            case INTERVAL_DAY_TIME: {
                result = ((HiveIntervalDayTimeObjectInspector)oi).getPrimitiveWritableObject(o).toString();
                break;
            }
            case DECIMAL: {
                result = ((HiveDecimalObjectInspector)oi).getPrimitiveJavaObject(o).toString();
                break;
            }
            default: {
                throw new RuntimeException("Hive 2 Internal error: unknown type: " + oi.getTypeName());
            }
        }
        return result;
    }
    
    public static HiveChar getHiveChar(final Object o, final PrimitiveObjectInspector oi) {
        if (o == null) {
            return null;
        }
        HiveChar result = null;
        switch (oi.getPrimitiveCategory()) {
            case CHAR: {
                result = ((HiveCharObjectInspector)oi).getPrimitiveJavaObject(o);
                break;
            }
            default: {
                result = new HiveChar();
                result.setValue(getString(o, oi));
                break;
            }
        }
        return result;
    }
    
    public static HiveVarchar getHiveVarchar(final Object o, final PrimitiveObjectInspector oi) {
        if (o == null) {
            return null;
        }
        HiveVarchar result = null;
        switch (oi.getPrimitiveCategory()) {
            case VARCHAR: {
                result = ((HiveVarcharObjectInspector)oi).getPrimitiveJavaObject(o);
                break;
            }
            default: {
                result = new HiveVarchar();
                result.setValue(getString(o, oi));
                break;
            }
        }
        return result;
    }
    
    public static BytesWritable getBinaryFromText(final Text text) {
        final BytesWritable bw = new BytesWritable();
        bw.set(text.getBytes(), 0, text.getLength());
        return bw;
    }
    
    public static BytesWritable getBinary(final Object o, final PrimitiveObjectInspector oi) {
        if (null == o) {
            return null;
        }
        switch (oi.getPrimitiveCategory()) {
            case VOID: {
                return null;
            }
            case STRING: {
                final Text text = ((StringObjectInspector)oi).getPrimitiveWritableObject(o);
                return getBinaryFromText(text);
            }
            case CHAR: {
                return getBinaryFromText(((HiveCharObjectInspector)oi).getPrimitiveWritableObject(o).getPaddedValue());
            }
            case VARCHAR: {
                return getBinaryFromText(((HiveVarcharObjectInspector)oi).getPrimitiveWritableObject(o).getTextValue());
            }
            case BINARY: {
                return ((BinaryObjectInspector)oi).getPrimitiveWritableObject(o);
            }
            default: {
                throw new RuntimeException("Cannot convert to Binary from: " + oi.getTypeName());
            }
        }
    }
    
    public static HiveDecimal getHiveDecimal(final Object o, final PrimitiveObjectInspector oi) {
        if (o == null) {
            return null;
        }
        HiveDecimal result = null;
        switch (oi.getPrimitiveCategory()) {
            case VOID: {
                result = null;
                break;
            }
            case BOOLEAN: {
                result = (((BooleanObjectInspector)oi).get(o) ? HiveDecimal.ONE : HiveDecimal.ZERO);
                break;
            }
            case BYTE: {
                result = HiveDecimal.create(((ByteObjectInspector)oi).get(o));
                break;
            }
            case SHORT: {
                result = HiveDecimal.create(((ShortObjectInspector)oi).get(o));
                break;
            }
            case INT: {
                result = HiveDecimal.create(((IntObjectInspector)oi).get(o));
                break;
            }
            case LONG: {
                result = HiveDecimal.create(((LongObjectInspector)oi).get(o));
                break;
            }
            case FLOAT: {
                final Float f = ((FloatObjectInspector)oi).get(o);
                result = HiveDecimal.create(f.toString());
                break;
            }
            case DOUBLE: {
                final Double d = ((DoubleObjectInspector)oi).get(o);
                result = HiveDecimal.create(d.toString());
                break;
            }
            case STRING: {
                result = HiveDecimal.create(((StringObjectInspector)oi).getPrimitiveJavaObject(o));
                break;
            }
            case CHAR:
            case VARCHAR: {
                result = HiveDecimal.create(getString(o, oi));
                break;
            }
            case TIMESTAMP: {
                final Double ts = ((TimestampObjectInspector)oi).getPrimitiveWritableObject(o).getDouble();
                result = HiveDecimal.create(ts.toString());
                break;
            }
            case DECIMAL: {
                result = ((HiveDecimalObjectInspector)oi).getPrimitiveJavaObject(o);
                break;
            }
            default: {
                throw new RuntimeException("Hive 2 Internal error: unsupported conversion from type: " + oi.getTypeName());
            }
        }
        return result;
    }
    
    public static Date getDate(final Object o, final PrimitiveObjectInspector oi) {
        if (o == null) {
            return null;
        }
        Date result = null;
        switch (oi.getPrimitiveCategory()) {
            case VOID: {
                result = null;
                break;
            }
            case STRING: {
                final StringObjectInspector soi = (StringObjectInspector)oi;
                final String s = soi.getPrimitiveJavaObject(o).trim();
                try {
                    result = Date.valueOf(s);
                }
                catch (IllegalArgumentException e) {
                    result = null;
                }
                break;
            }
            case CHAR:
            case VARCHAR: {
                try {
                    final String val = getString(o, oi).trim();
                    result = Date.valueOf(val);
                }
                catch (IllegalArgumentException e) {
                    result = null;
                }
                break;
            }
            case DATE: {
                result = ((DateObjectInspector)oi).getPrimitiveWritableObject(o).get();
                break;
            }
            case TIMESTAMP: {
                result = DateWritable.timeToDate(((TimestampObjectInspector)oi).getPrimitiveWritableObject(o).getSeconds());
                break;
            }
            default: {
                throw new RuntimeException("Cannot convert to Date from: " + oi.getTypeName());
            }
        }
        return result;
    }
    
    public static Timestamp getTimestamp(final Object o, final PrimitiveObjectInspector oi) {
        return getTimestamp(o, oi, false);
    }
    
    public static Timestamp getTimestamp(final Object o, final PrimitiveObjectInspector inputOI, final boolean intToTimestampInSeconds) {
        if (o == null) {
            return null;
        }
        Timestamp result = null;
        long longValue = 0L;
        switch (inputOI.getPrimitiveCategory()) {
            case VOID: {
                result = null;
                break;
            }
            case BOOLEAN: {
                longValue = (((BooleanObjectInspector)inputOI).get(o) ? 1 : 0);
                result = TimestampWritable.longToTimestamp(longValue, intToTimestampInSeconds);
                break;
            }
            case BYTE: {
                longValue = ((ByteObjectInspector)inputOI).get(o);
                result = TimestampWritable.longToTimestamp(longValue, intToTimestampInSeconds);
                break;
            }
            case SHORT: {
                longValue = ((ShortObjectInspector)inputOI).get(o);
                result = TimestampWritable.longToTimestamp(longValue, intToTimestampInSeconds);
                break;
            }
            case INT: {
                longValue = ((IntObjectInspector)inputOI).get(o);
                result = TimestampWritable.longToTimestamp(longValue, intToTimestampInSeconds);
                break;
            }
            case LONG: {
                longValue = ((LongObjectInspector)inputOI).get(o);
                result = TimestampWritable.longToTimestamp(longValue, intToTimestampInSeconds);
                break;
            }
            case FLOAT: {
                result = TimestampWritable.doubleToTimestamp(((FloatObjectInspector)inputOI).get(o));
                break;
            }
            case DOUBLE: {
                result = TimestampWritable.doubleToTimestamp(((DoubleObjectInspector)inputOI).get(o));
                break;
            }
            case DECIMAL: {
                result = TimestampWritable.decimalToTimestamp(((HiveDecimalObjectInspector)inputOI).getPrimitiveJavaObject(o));
                break;
            }
            case STRING: {
                final StringObjectInspector soi = (StringObjectInspector)inputOI;
                final String s = soi.getPrimitiveJavaObject(o);
                result = getTimestampFromString(s);
                break;
            }
            case CHAR:
            case VARCHAR: {
                result = getTimestampFromString(getString(o, inputOI));
                break;
            }
            case DATE: {
                result = new Timestamp(((DateObjectInspector)inputOI).getPrimitiveWritableObject(o).get().getTime());
                break;
            }
            case TIMESTAMP: {
                result = ((TimestampObjectInspector)inputOI).getPrimitiveWritableObject(o).getTimestamp();
                break;
            }
            default: {
                throw new RuntimeException("Hive 2 Internal error: unknown type: " + inputOI.getTypeName());
            }
        }
        return result;
    }
    
    static Timestamp getTimestampFromString(String s) {
        s = s.trim();
        final int periodIdx = s.indexOf(".");
        if (periodIdx != -1 && s.length() - periodIdx > 9) {
            s = s.substring(0, periodIdx + 10);
        }
        Timestamp result;
        try {
            result = Timestamp.valueOf(s);
        }
        catch (IllegalArgumentException e) {
            result = null;
        }
        return result;
    }
    
    public static HiveIntervalYearMonth getHiveIntervalYearMonth(final Object o, final PrimitiveObjectInspector oi) {
        if (o == null) {
            return null;
        }
        HiveIntervalYearMonth result = null;
        switch (oi.getPrimitiveCategory()) {
            case VOID: {
                result = null;
                break;
            }
            case STRING:
            case CHAR:
            case VARCHAR: {
                try {
                    final String val = getString(o, oi).trim();
                    result = HiveIntervalYearMonth.valueOf(val);
                }
                catch (IllegalArgumentException e) {
                    result = null;
                }
                break;
            }
            case INTERVAL_YEAR_MONTH: {
                result = ((HiveIntervalYearMonthObjectInspector)oi).getPrimitiveJavaObject(o);
                break;
            }
            default: {
                throw new RuntimeException("Cannot convert to IntervalYearMonth from: " + oi.getTypeName());
            }
        }
        return result;
    }
    
    public static HiveIntervalDayTime getHiveIntervalDayTime(final Object o, final PrimitiveObjectInspector oi) {
        if (o == null) {
            return null;
        }
        HiveIntervalDayTime result = null;
        switch (oi.getPrimitiveCategory()) {
            case VOID: {
                result = null;
                break;
            }
            case STRING:
            case CHAR:
            case VARCHAR: {
                try {
                    final String val = getString(o, oi).trim();
                    result = HiveIntervalDayTime.valueOf(val);
                }
                catch (IllegalArgumentException e) {
                    result = null;
                }
                break;
            }
            case INTERVAL_DAY_TIME: {
                result = ((HiveIntervalDayTimeObjectInspector)oi).getPrimitiveJavaObject(o);
                break;
            }
            default: {
                throw new RuntimeException("Cannot convert to IntervalDayTime from: " + oi.getTypeName());
            }
        }
        return result;
    }
    
    public static Class<?> getJavaPrimitiveClassFromObjectInspector(final ObjectInspector oi) {
        if (oi.getCategory() != ObjectInspector.Category.PRIMITIVE) {
            return null;
        }
        final PrimitiveObjectInspector poi = (PrimitiveObjectInspector)oi;
        final PrimitiveTypeEntry t = getTypeEntryFromPrimitiveCategory(poi.getPrimitiveCategory());
        return (t == null) ? null : t.primitiveJavaClass;
    }
    
    public static PrimitiveGrouping getPrimitiveGrouping(final PrimitiveObjectInspector.PrimitiveCategory primitiveCategory) {
        switch (primitiveCategory) {
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case DECIMAL: {
                return PrimitiveGrouping.NUMERIC_GROUP;
            }
            case STRING:
            case CHAR:
            case VARCHAR: {
                return PrimitiveGrouping.STRING_GROUP;
            }
            case BOOLEAN: {
                return PrimitiveGrouping.BOOLEAN_GROUP;
            }
            case DATE:
            case TIMESTAMP: {
                return PrimitiveGrouping.DATE_GROUP;
            }
            case INTERVAL_YEAR_MONTH:
            case INTERVAL_DAY_TIME: {
                return PrimitiveGrouping.INTERVAL_GROUP;
            }
            case BINARY: {
                return PrimitiveGrouping.BINARY_GROUP;
            }
            case VOID: {
                return PrimitiveGrouping.VOID_GROUP;
            }
            default: {
                return PrimitiveGrouping.UNKNOWN_GROUP;
            }
        }
    }
    
    private PrimitiveObjectInspectorUtils() {
    }
    
    static {
        PrimitiveObjectInspectorUtils.LOG = LogFactory.getLog(PrimitiveObjectInspectorUtils.class);
        primitiveCategoryToTypeEntry = new HashMap<PrimitiveObjectInspector.PrimitiveCategory, PrimitiveTypeEntry>();
        primitiveJavaTypeToTypeEntry = new HashMap<Class<?>, PrimitiveTypeEntry>();
        primitiveJavaClassToTypeEntry = new HashMap<Class<?>, PrimitiveTypeEntry>();
        primitiveWritableClassToTypeEntry = new HashMap<Class<?>, PrimitiveTypeEntry>();
        typeNameToTypeEntry = new HashMap<String, PrimitiveTypeEntry>();
        binaryTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.BINARY, "binary", byte[].class, byte[].class, BytesWritable.class);
        stringTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.STRING, "string", null, String.class, Text.class);
        booleanTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.BOOLEAN, "boolean", Boolean.TYPE, Boolean.class, BooleanWritable.class);
        intTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.INT, "int", Integer.TYPE, Integer.class, IntWritable.class);
        longTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.LONG, "bigint", Long.TYPE, Long.class, LongWritable.class);
        floatTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.FLOAT, "float", Float.TYPE, Float.class, FloatWritable.class);
        voidTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.VOID, "void", Void.TYPE, Void.class, NullWritable.class);
        doubleTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.DOUBLE, "double", Double.TYPE, Double.class, DoubleWritable.class);
        byteTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.BYTE, "tinyint", Byte.TYPE, Byte.class, ByteWritable.class);
        shortTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.SHORT, "smallint", Short.TYPE, Short.class, ShortWritable.class);
        dateTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.DATE, "date", null, Date.class, DateWritable.class);
        timestampTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.TIMESTAMP, "timestamp", null, Timestamp.class, TimestampWritable.class);
        intervalYearMonthTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.INTERVAL_YEAR_MONTH, "interval_year_month", null, HiveIntervalYearMonth.class, HiveIntervalYearMonthWritable.class);
        intervalDayTimeTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.INTERVAL_DAY_TIME, "interval_day_time", null, HiveIntervalDayTime.class, HiveIntervalDayTimeWritable.class);
        decimalTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.DECIMAL, "decimal", null, HiveDecimal.class, HiveDecimalWritable.class);
        varcharTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.VARCHAR, "varchar", null, HiveVarchar.class, HiveVarcharWritable.class);
        charTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.CHAR, "char", null, HiveChar.class, HiveCharWritable.class);
        unknownTypeEntry = new PrimitiveTypeEntry(PrimitiveObjectInspector.PrimitiveCategory.UNKNOWN, "unknown", null, Object.class, null);
        registerType(PrimitiveObjectInspectorUtils.binaryTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.stringTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.charTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.varcharTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.booleanTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.intTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.longTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.floatTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.voidTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.doubleTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.byteTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.shortTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.dateTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.timestampTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.intervalYearMonthTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.intervalDayTimeTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.decimalTypeEntry);
        registerType(PrimitiveObjectInspectorUtils.unknownTypeEntry);
    }
    
    public static class PrimitiveTypeEntry implements Writable, Cloneable
    {
        public PrimitiveObjectInspector.PrimitiveCategory primitiveCategory;
        public Class<?> primitiveJavaType;
        public Class<?> primitiveJavaClass;
        public Class<?> primitiveWritableClass;
        public String typeName;
        
        protected PrimitiveTypeEntry() {
        }
        
        PrimitiveTypeEntry(final PrimitiveObjectInspector.PrimitiveCategory primitiveCategory, final String typeName, final Class<?> primitiveType, final Class<?> javaClass, final Class<?> hiveClass) {
            this.primitiveCategory = primitiveCategory;
            this.primitiveJavaType = primitiveType;
            this.primitiveJavaClass = javaClass;
            this.primitiveWritableClass = hiveClass;
            this.typeName = typeName;
        }
        
        @Override
        public void readFields(final DataInput in) throws IOException {
            this.primitiveCategory = WritableUtils.readEnum(in, PrimitiveObjectInspector.PrimitiveCategory.class);
            this.typeName = WritableUtils.readString(in);
            try {
                this.primitiveJavaType = Class.forName(WritableUtils.readString(in));
                this.primitiveJavaClass = Class.forName(WritableUtils.readString(in));
                this.primitiveWritableClass = Class.forName(WritableUtils.readString(in));
            }
            catch (ClassNotFoundException e) {
                throw new IOException(e);
            }
        }
        
        @Override
        public void write(final DataOutput out) throws IOException {
            WritableUtils.writeEnum(out, this.primitiveCategory);
            WritableUtils.writeString(out, this.typeName);
            WritableUtils.writeString(out, this.primitiveJavaType.getName());
            WritableUtils.writeString(out, this.primitiveJavaClass.getName());
            WritableUtils.writeString(out, this.primitiveWritableClass.getName());
        }
        
        public Object clone() {
            final PrimitiveTypeEntry result = new PrimitiveTypeEntry(this.primitiveCategory, this.typeName, this.primitiveJavaType, this.primitiveJavaClass, this.primitiveWritableClass);
            return result;
        }
        
        @Override
        public String toString() {
            return this.typeName;
        }
    }
    
    public enum PrimitiveGrouping
    {
        NUMERIC_GROUP, 
        STRING_GROUP, 
        BOOLEAN_GROUP, 
        DATE_GROUP, 
        INTERVAL_GROUP, 
        BINARY_GROUP, 
        VOID_GROUP, 
        UNKNOWN_GROUP;
    }
}
