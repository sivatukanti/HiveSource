// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.thrift.TUnion;
import java.math.BigDecimal;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import java.sql.Timestamp;
import java.sql.Date;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hive.service.cli.thrift.TStringValue;
import org.apache.hive.service.cli.thrift.TDoubleValue;
import org.apache.hive.service.cli.thrift.TI64Value;
import org.apache.hive.service.cli.thrift.TI32Value;
import org.apache.hive.service.cli.thrift.TI16Value;
import org.apache.hive.service.cli.thrift.TByteValue;
import org.apache.hive.service.cli.thrift.TBoolValue;
import org.apache.hive.service.cli.thrift.TColumnValue;

public class ColumnValue
{
    private static TColumnValue booleanValue(final Boolean value) {
        final TBoolValue tBoolValue = new TBoolValue();
        if (value != null) {
            tBoolValue.setValue(value);
        }
        return TColumnValue.boolVal(tBoolValue);
    }
    
    private static TColumnValue byteValue(final Byte value) {
        final TByteValue tByteValue = new TByteValue();
        if (value != null) {
            tByteValue.setValue(value);
        }
        return TColumnValue.byteVal(tByteValue);
    }
    
    private static TColumnValue shortValue(final Short value) {
        final TI16Value tI16Value = new TI16Value();
        if (value != null) {
            tI16Value.setValue(value);
        }
        return TColumnValue.i16Val(tI16Value);
    }
    
    private static TColumnValue intValue(final Integer value) {
        final TI32Value tI32Value = new TI32Value();
        if (value != null) {
            tI32Value.setValue(value);
        }
        return TColumnValue.i32Val(tI32Value);
    }
    
    private static TColumnValue longValue(final Long value) {
        final TI64Value tI64Value = new TI64Value();
        if (value != null) {
            tI64Value.setValue(value);
        }
        return TColumnValue.i64Val(tI64Value);
    }
    
    private static TColumnValue floatValue(final Float value) {
        final TDoubleValue tDoubleValue = new TDoubleValue();
        if (value != null) {
            tDoubleValue.setValue(value);
        }
        return TColumnValue.doubleVal(tDoubleValue);
    }
    
    private static TColumnValue doubleValue(final Double value) {
        final TDoubleValue tDoubleValue = new TDoubleValue();
        if (value != null) {
            tDoubleValue.setValue(value);
        }
        return TColumnValue.doubleVal(tDoubleValue);
    }
    
    private static TColumnValue stringValue(final String value) {
        final TStringValue tStringValue = new TStringValue();
        if (value != null) {
            tStringValue.setValue(value);
        }
        return TColumnValue.stringVal(tStringValue);
    }
    
    private static TColumnValue stringValue(final HiveChar value) {
        final TStringValue tStringValue = new TStringValue();
        if (value != null) {
            tStringValue.setValue(value.toString());
        }
        return TColumnValue.stringVal(tStringValue);
    }
    
    private static TColumnValue stringValue(final HiveVarchar value) {
        final TStringValue tStringValue = new TStringValue();
        if (value != null) {
            tStringValue.setValue(value.toString());
        }
        return TColumnValue.stringVal(tStringValue);
    }
    
    private static TColumnValue dateValue(final Date value) {
        final TStringValue tStringValue = new TStringValue();
        if (value != null) {
            tStringValue.setValue(value.toString());
        }
        return new TColumnValue(TColumnValue.stringVal(tStringValue));
    }
    
    private static TColumnValue timestampValue(final Timestamp value) {
        final TStringValue tStringValue = new TStringValue();
        if (value != null) {
            tStringValue.setValue(value.toString());
        }
        return TColumnValue.stringVal(tStringValue);
    }
    
    private static TColumnValue stringValue(final HiveDecimal value) {
        final TStringValue tStrValue = new TStringValue();
        if (value != null) {
            tStrValue.setValue(value.toString());
        }
        return TColumnValue.stringVal(tStrValue);
    }
    
    private static TColumnValue stringValue(final HiveIntervalYearMonth value) {
        final TStringValue tStrValue = new TStringValue();
        if (value != null) {
            tStrValue.setValue(value.toString());
        }
        return TColumnValue.stringVal(tStrValue);
    }
    
    private static TColumnValue stringValue(final HiveIntervalDayTime value) {
        final TStringValue tStrValue = new TStringValue();
        if (value != null) {
            tStrValue.setValue(value.toString());
        }
        return TColumnValue.stringVal(tStrValue);
    }
    
    public static TColumnValue toTColumnValue(final Type type, final Object value) {
        switch (type) {
            case BOOLEAN_TYPE: {
                return booleanValue((Boolean)value);
            }
            case TINYINT_TYPE: {
                return byteValue((Byte)value);
            }
            case SMALLINT_TYPE: {
                return shortValue((Short)value);
            }
            case INT_TYPE: {
                return intValue((Integer)value);
            }
            case BIGINT_TYPE: {
                return longValue((Long)value);
            }
            case FLOAT_TYPE: {
                return floatValue((Float)value);
            }
            case DOUBLE_TYPE: {
                return doubleValue((Double)value);
            }
            case STRING_TYPE: {
                return stringValue((String)value);
            }
            case CHAR_TYPE: {
                return stringValue((HiveChar)value);
            }
            case VARCHAR_TYPE: {
                return stringValue((HiveVarchar)value);
            }
            case DATE_TYPE: {
                return dateValue((Date)value);
            }
            case TIMESTAMP_TYPE: {
                return timestampValue((Timestamp)value);
            }
            case INTERVAL_YEAR_MONTH_TYPE: {
                return stringValue((HiveIntervalYearMonth)value);
            }
            case INTERVAL_DAY_TIME_TYPE: {
                return stringValue((HiveIntervalDayTime)value);
            }
            case DECIMAL_TYPE: {
                return stringValue((HiveDecimal)value);
            }
            case BINARY_TYPE: {
                return stringValue((String)value);
            }
            case ARRAY_TYPE:
            case MAP_TYPE:
            case STRUCT_TYPE:
            case UNION_TYPE:
            case USER_DEFINED_TYPE: {
                return stringValue((String)value);
            }
            case NULL_TYPE: {
                return stringValue((String)value);
            }
            default: {
                return null;
            }
        }
    }
    
    private static Boolean getBooleanValue(final TBoolValue tBoolValue) {
        if (tBoolValue.isSetValue()) {
            return tBoolValue.isValue();
        }
        return null;
    }
    
    private static Byte getByteValue(final TByteValue tByteValue) {
        if (tByteValue.isSetValue()) {
            return tByteValue.getValue();
        }
        return null;
    }
    
    private static Short getShortValue(final TI16Value tI16Value) {
        if (tI16Value.isSetValue()) {
            return tI16Value.getValue();
        }
        return null;
    }
    
    private static Integer getIntegerValue(final TI32Value tI32Value) {
        if (tI32Value.isSetValue()) {
            return tI32Value.getValue();
        }
        return null;
    }
    
    private static Long getLongValue(final TI64Value tI64Value) {
        if (tI64Value.isSetValue()) {
            return tI64Value.getValue();
        }
        return null;
    }
    
    private static Double getDoubleValue(final TDoubleValue tDoubleValue) {
        if (tDoubleValue.isSetValue()) {
            return tDoubleValue.getValue();
        }
        return null;
    }
    
    private static String getStringValue(final TStringValue tStringValue) {
        if (tStringValue.isSetValue()) {
            return tStringValue.getValue();
        }
        return null;
    }
    
    private static Timestamp getTimestampValue(final TStringValue tStringValue) {
        if (tStringValue.isSetValue()) {
            return Timestamp.valueOf(tStringValue.getValue());
        }
        return null;
    }
    
    private static byte[] getBinaryValue(final TStringValue tString) {
        if (tString.isSetValue()) {
            return tString.getValue().getBytes();
        }
        return null;
    }
    
    private static BigDecimal getBigDecimalValue(final TStringValue tStringValue) {
        if (tStringValue.isSetValue()) {
            return new BigDecimal(tStringValue.getValue());
        }
        return null;
    }
    
    public static Object toColumnValue(final TColumnValue value) {
        final TColumnValue._Fields field = ((TUnion<T, TColumnValue._Fields>)value).getSetField();
        switch (field) {
            case BOOL_VAL: {
                return getBooleanValue(value.getBoolVal());
            }
            case BYTE_VAL: {
                return getByteValue(value.getByteVal());
            }
            case I16_VAL: {
                return getShortValue(value.getI16Val());
            }
            case I32_VAL: {
                return getIntegerValue(value.getI32Val());
            }
            case I64_VAL: {
                return getLongValue(value.getI64Val());
            }
            case DOUBLE_VAL: {
                return getDoubleValue(value.getDoubleVal());
            }
            case STRING_VAL: {
                return getStringValue(value.getStringVal());
            }
            default: {
                throw new IllegalArgumentException("never");
            }
        }
    }
}
