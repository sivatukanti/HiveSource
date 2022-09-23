// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.hive.serde2.lazy.LazyLong;
import java.io.OutputStream;
import org.apache.hadoop.hive.serde2.lazy.LazyInteger;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import java.sql.Timestamp;
import java.sql.Date;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorConverters;

public class PrimitiveObjectInspectorConverter
{
    public static class BooleanConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        SettableBooleanObjectInspector outputOI;
        Object r;
        
        public BooleanConverter(final PrimitiveObjectInspector inputOI, final SettableBooleanObjectInspector outputOI) {
            this.inputOI = inputOI;
            this.outputOI = outputOI;
            this.r = outputOI.create(false);
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            try {
                return this.outputOI.set(this.r, PrimitiveObjectInspectorUtils.getBoolean(input, this.inputOI));
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
    }
    
    public static class ByteConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        SettableByteObjectInspector outputOI;
        Object r;
        
        public ByteConverter(final PrimitiveObjectInspector inputOI, final SettableByteObjectInspector outputOI) {
            this.inputOI = inputOI;
            this.outputOI = outputOI;
            this.r = outputOI.create((byte)0);
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            try {
                return this.outputOI.set(this.r, PrimitiveObjectInspectorUtils.getByte(input, this.inputOI));
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
    }
    
    public static class ShortConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        SettableShortObjectInspector outputOI;
        Object r;
        
        public ShortConverter(final PrimitiveObjectInspector inputOI, final SettableShortObjectInspector outputOI) {
            this.inputOI = inputOI;
            this.outputOI = outputOI;
            this.r = outputOI.create((short)0);
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            try {
                return this.outputOI.set(this.r, PrimitiveObjectInspectorUtils.getShort(input, this.inputOI));
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
    }
    
    public static class IntConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        SettableIntObjectInspector outputOI;
        Object r;
        
        public IntConverter(final PrimitiveObjectInspector inputOI, final SettableIntObjectInspector outputOI) {
            this.inputOI = inputOI;
            this.outputOI = outputOI;
            this.r = outputOI.create(0);
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            try {
                return this.outputOI.set(this.r, PrimitiveObjectInspectorUtils.getInt(input, this.inputOI));
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
    }
    
    public static class LongConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        SettableLongObjectInspector outputOI;
        Object r;
        
        public LongConverter(final PrimitiveObjectInspector inputOI, final SettableLongObjectInspector outputOI) {
            this.inputOI = inputOI;
            this.outputOI = outputOI;
            this.r = outputOI.create(0L);
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            try {
                return this.outputOI.set(this.r, PrimitiveObjectInspectorUtils.getLong(input, this.inputOI));
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
    }
    
    public static class FloatConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        SettableFloatObjectInspector outputOI;
        Object r;
        
        public FloatConverter(final PrimitiveObjectInspector inputOI, final SettableFloatObjectInspector outputOI) {
            this.inputOI = inputOI;
            this.outputOI = outputOI;
            this.r = outputOI.create(0.0f);
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            try {
                return this.outputOI.set(this.r, PrimitiveObjectInspectorUtils.getFloat(input, this.inputOI));
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
    }
    
    public static class DoubleConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        SettableDoubleObjectInspector outputOI;
        Object r;
        
        public DoubleConverter(final PrimitiveObjectInspector inputOI, final SettableDoubleObjectInspector outputOI) {
            this.inputOI = inputOI;
            this.outputOI = outputOI;
            this.r = outputOI.create(0.0);
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            try {
                return this.outputOI.set(this.r, PrimitiveObjectInspectorUtils.getDouble(input, this.inputOI));
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
    }
    
    public static class DateConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        SettableDateObjectInspector outputOI;
        Object r;
        
        public DateConverter(final PrimitiveObjectInspector inputOI, final SettableDateObjectInspector outputOI) {
            this.inputOI = inputOI;
            this.outputOI = outputOI;
            this.r = outputOI.create(new Date(0L));
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            return this.outputOI.set(this.r, PrimitiveObjectInspectorUtils.getDate(input, this.inputOI));
        }
    }
    
    public static class TimestampConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        SettableTimestampObjectInspector outputOI;
        boolean intToTimestampInSeconds;
        Object r;
        
        public TimestampConverter(final PrimitiveObjectInspector inputOI, final SettableTimestampObjectInspector outputOI) {
            this.intToTimestampInSeconds = false;
            this.inputOI = inputOI;
            this.outputOI = outputOI;
            this.r = outputOI.create(new Timestamp(0L));
        }
        
        public void setIntToTimestampInSeconds(final boolean intToTimestampInSeconds) {
            this.intToTimestampInSeconds = intToTimestampInSeconds;
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            return this.outputOI.set(this.r, PrimitiveObjectInspectorUtils.getTimestamp(input, this.inputOI, this.intToTimestampInSeconds));
        }
    }
    
    public static class HiveIntervalYearMonthConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        SettableHiveIntervalYearMonthObjectInspector outputOI;
        Object r;
        
        public HiveIntervalYearMonthConverter(final PrimitiveObjectInspector inputOI, final SettableHiveIntervalYearMonthObjectInspector outputOI) {
            this.inputOI = inputOI;
            this.outputOI = outputOI;
            this.r = outputOI.create(new HiveIntervalYearMonth());
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            return this.outputOI.set(this.r, PrimitiveObjectInspectorUtils.getHiveIntervalYearMonth(input, this.inputOI));
        }
    }
    
    public static class HiveIntervalDayTimeConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        SettableHiveIntervalDayTimeObjectInspector outputOI;
        Object r;
        
        public HiveIntervalDayTimeConverter(final PrimitiveObjectInspector inputOI, final SettableHiveIntervalDayTimeObjectInspector outputOI) {
            this.inputOI = inputOI;
            this.outputOI = outputOI;
            this.r = outputOI.create(new HiveIntervalDayTime());
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            return this.outputOI.set(this.r, PrimitiveObjectInspectorUtils.getHiveIntervalDayTime(input, this.inputOI));
        }
    }
    
    public static class HiveDecimalConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        SettableHiveDecimalObjectInspector outputOI;
        Object r;
        
        public HiveDecimalConverter(final PrimitiveObjectInspector inputOI, final SettableHiveDecimalObjectInspector outputOI) {
            this.inputOI = inputOI;
            this.outputOI = outputOI;
            this.r = outputOI.create(HiveDecimal.ZERO);
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            return this.outputOI.set(this.r, PrimitiveObjectInspectorUtils.getHiveDecimal(input, this.inputOI));
        }
    }
    
    public static class BinaryConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        SettableBinaryObjectInspector outputOI;
        Object r;
        
        public BinaryConverter(final PrimitiveObjectInspector inputOI, final SettableBinaryObjectInspector outputOI) {
            this.inputOI = inputOI;
            this.outputOI = outputOI;
            this.r = outputOI.create(new byte[0]);
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            return this.outputOI.set(this.r, PrimitiveObjectInspectorUtils.getBinary(input, this.inputOI));
        }
    }
    
    public static class TextConverter implements ObjectInspectorConverters.Converter
    {
        private final PrimitiveObjectInspector inputOI;
        private final Text t;
        private final ByteStream.Output out;
        private static byte[] trueBytes;
        private static byte[] falseBytes;
        
        public TextConverter(final PrimitiveObjectInspector inputOI) {
            this.t = new Text();
            this.out = new ByteStream.Output();
            this.inputOI = inputOI;
        }
        
        @Override
        public Text convert(final Object input) {
            if (input == null) {
                return null;
            }
            switch (this.inputOI.getPrimitiveCategory()) {
                case VOID: {
                    return null;
                }
                case BOOLEAN: {
                    this.t.set(((BooleanObjectInspector)this.inputOI).get(input) ? TextConverter.trueBytes : TextConverter.falseBytes);
                    return this.t;
                }
                case BYTE: {
                    this.out.reset();
                    LazyInteger.writeUTF8NoException(this.out, ((ByteObjectInspector)this.inputOI).get(input));
                    this.t.set(this.out.getData(), 0, this.out.getLength());
                    return this.t;
                }
                case SHORT: {
                    this.out.reset();
                    LazyInteger.writeUTF8NoException(this.out, ((ShortObjectInspector)this.inputOI).get(input));
                    this.t.set(this.out.getData(), 0, this.out.getLength());
                    return this.t;
                }
                case INT: {
                    this.out.reset();
                    LazyInteger.writeUTF8NoException(this.out, ((IntObjectInspector)this.inputOI).get(input));
                    this.t.set(this.out.getData(), 0, this.out.getLength());
                    return this.t;
                }
                case LONG: {
                    this.out.reset();
                    LazyLong.writeUTF8NoException(this.out, ((LongObjectInspector)this.inputOI).get(input));
                    this.t.set(this.out.getData(), 0, this.out.getLength());
                    return this.t;
                }
                case FLOAT: {
                    this.t.set(String.valueOf(((FloatObjectInspector)this.inputOI).get(input)));
                    return this.t;
                }
                case DOUBLE: {
                    this.t.set(String.valueOf(((DoubleObjectInspector)this.inputOI).get(input)));
                    return this.t;
                }
                case STRING: {
                    if (this.inputOI.preferWritable()) {
                        this.t.set(((StringObjectInspector)this.inputOI).getPrimitiveWritableObject(input));
                    }
                    else {
                        this.t.set(((StringObjectInspector)this.inputOI).getPrimitiveJavaObject(input));
                    }
                    return this.t;
                }
                case CHAR: {
                    if (this.inputOI.preferWritable()) {
                        this.t.set(((HiveCharObjectInspector)this.inputOI).getPrimitiveWritableObject(input).getStrippedValue());
                    }
                    else {
                        this.t.set(((HiveCharObjectInspector)this.inputOI).getPrimitiveJavaObject(input).getStrippedValue());
                    }
                    return this.t;
                }
                case VARCHAR: {
                    if (this.inputOI.preferWritable()) {
                        this.t.set(((HiveVarcharObjectInspector)this.inputOI).getPrimitiveWritableObject(input).toString());
                    }
                    else {
                        this.t.set(((HiveVarcharObjectInspector)this.inputOI).getPrimitiveJavaObject(input).toString());
                    }
                    return this.t;
                }
                case DATE: {
                    this.t.set(((DateObjectInspector)this.inputOI).getPrimitiveWritableObject(input).toString());
                    return this.t;
                }
                case TIMESTAMP: {
                    this.t.set(((TimestampObjectInspector)this.inputOI).getPrimitiveWritableObject(input).toString());
                    return this.t;
                }
                case INTERVAL_YEAR_MONTH: {
                    this.t.set(((HiveIntervalYearMonthObjectInspector)this.inputOI).getPrimitiveWritableObject(input).toString());
                    return this.t;
                }
                case INTERVAL_DAY_TIME: {
                    this.t.set(((HiveIntervalDayTimeObjectInspector)this.inputOI).getPrimitiveWritableObject(input).toString());
                    return this.t;
                }
                case BINARY: {
                    final BinaryObjectInspector binaryOI = (BinaryObjectInspector)this.inputOI;
                    if (binaryOI.preferWritable()) {
                        final BytesWritable bytes = binaryOI.getPrimitiveWritableObject(input);
                        this.t.set(bytes.getBytes(), 0, bytes.getLength());
                    }
                    else {
                        this.t.set(binaryOI.getPrimitiveJavaObject(input));
                    }
                    return this.t;
                }
                case DECIMAL: {
                    this.t.set(((HiveDecimalObjectInspector)this.inputOI).getPrimitiveWritableObject(input).toString());
                    return this.t;
                }
                default: {
                    throw new RuntimeException("Hive 2 Internal error: type = " + this.inputOI.getTypeName());
                }
            }
        }
        
        static {
            TextConverter.trueBytes = new byte[] { 84, 82, 85, 69 };
            TextConverter.falseBytes = new byte[] { 70, 65, 76, 83, 69 };
        }
    }
    
    public static class StringConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        
        public StringConverter(final PrimitiveObjectInspector inputOI) {
            this.inputOI = inputOI;
        }
        
        @Override
        public Object convert(final Object input) {
            return PrimitiveObjectInspectorUtils.getString(input, this.inputOI);
        }
    }
    
    public static class HiveVarcharConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        SettableHiveVarcharObjectInspector outputOI;
        HiveVarcharWritable hc;
        
        public HiveVarcharConverter(final PrimitiveObjectInspector inputOI, final SettableHiveVarcharObjectInspector outputOI) {
            this.inputOI = inputOI;
            this.outputOI = outputOI;
            this.hc = new HiveVarcharWritable();
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            switch (this.inputOI.getPrimitiveCategory()) {
                case BOOLEAN: {
                    return this.outputOI.set(this.hc, ((BooleanObjectInspector)this.inputOI).get(input) ? new HiveVarchar("TRUE", -1) : new HiveVarchar("FALSE", -1));
                }
                default: {
                    return this.outputOI.set(this.hc, PrimitiveObjectInspectorUtils.getHiveVarchar(input, this.inputOI));
                }
            }
        }
    }
    
    public static class HiveCharConverter implements ObjectInspectorConverters.Converter
    {
        PrimitiveObjectInspector inputOI;
        SettableHiveCharObjectInspector outputOI;
        HiveCharWritable hc;
        
        public HiveCharConverter(final PrimitiveObjectInspector inputOI, final SettableHiveCharObjectInspector outputOI) {
            this.inputOI = inputOI;
            this.outputOI = outputOI;
            this.hc = new HiveCharWritable();
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            switch (this.inputOI.getPrimitiveCategory()) {
                case BOOLEAN: {
                    return this.outputOI.set(this.hc, ((BooleanObjectInspector)this.inputOI).get(input) ? new HiveChar("TRUE", -1) : new HiveChar("FALSE", -1));
                }
                default: {
                    return this.outputOI.set(this.hc, PrimitiveObjectInspectorUtils.getHiveChar(input, this.inputOI));
                }
            }
        }
    }
}
