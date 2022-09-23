// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary.fast;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hive.common.util.DateUtils;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import java.sql.Timestamp;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import java.sql.Date;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryUtils;
import java.io.IOException;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.fast.SerializeWrite;

public class LazyBinarySerializeWrite implements SerializeWrite
{
    public static final Log LOG;
    private ByteStream.Output output;
    private int fieldCount;
    private int fieldIndex;
    private byte nullByte;
    private long nullOffset;
    private HiveDecimalWritable hiveDecimalWritable;
    private TimestampWritable timestampWritable;
    private HiveIntervalYearMonthWritable hiveIntervalYearMonthWritable;
    private HiveIntervalDayTimeWritable hiveIntervalDayTimeWritable;
    private HiveIntervalDayTime hiveIntervalDayTime;
    
    public LazyBinarySerializeWrite(final int fieldCount) {
        this();
        this.fieldCount = fieldCount;
    }
    
    private LazyBinarySerializeWrite() {
    }
    
    @Override
    public void set(final ByteStream.Output output) {
        (this.output = output).reset();
        this.fieldIndex = 0;
        this.nullByte = 0;
        this.nullOffset = 0L;
    }
    
    @Override
    public void setAppend(final ByteStream.Output output) {
        this.output = output;
        this.fieldIndex = 0;
        this.nullByte = 0;
        this.nullOffset = output.getLength();
    }
    
    @Override
    public void reset() {
        this.output.reset();
        this.fieldIndex = 0;
        this.nullByte = 0;
        this.nullOffset = 0L;
    }
    
    @Override
    public void writeNull() throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeBoolean(final boolean v) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        this.output.write((byte)(byte)(v ? 1 : 0));
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeByte(final byte v) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        this.output.write(v);
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeShort(final short v) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        this.output.write((byte)(v >> 8));
        this.output.write((byte)v);
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeInt(final int v) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        LazyBinaryUtils.writeVInt(this.output, v);
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeLong(final long v) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        LazyBinaryUtils.writeVLong(this.output, v);
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeFloat(final float vf) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        final int v = Float.floatToIntBits(vf);
        this.output.write((byte)(v >> 24));
        this.output.write((byte)(v >> 16));
        this.output.write((byte)(v >> 8));
        this.output.write((byte)v);
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeDouble(final double v) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        LazyBinaryUtils.writeDouble(this.output, v);
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeString(final byte[] v) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        final int length = v.length;
        LazyBinaryUtils.writeVInt(this.output, length);
        this.output.write(v, 0, length);
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeString(final byte[] v, final int start, final int length) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        LazyBinaryUtils.writeVInt(this.output, length);
        this.output.write(v, start, length);
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeHiveChar(final HiveChar hiveChar) throws IOException {
        final String string = hiveChar.getStrippedValue();
        final byte[] bytes = string.getBytes();
        this.writeString(bytes);
    }
    
    @Override
    public void writeHiveVarchar(final HiveVarchar hiveVarchar) throws IOException {
        final String string = hiveVarchar.getValue();
        final byte[] bytes = string.getBytes();
        this.writeString(bytes);
    }
    
    @Override
    public void writeBinary(final byte[] v) throws IOException {
        this.writeString(v);
    }
    
    @Override
    public void writeBinary(final byte[] v, final int start, final int length) throws IOException {
        this.writeString(v, start, length);
    }
    
    @Override
    public void writeDate(final Date date) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        LazyBinaryUtils.writeVInt(this.output, DateWritable.dateToDays(date));
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeDate(final int dateAsDays) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        LazyBinaryUtils.writeVInt(this.output, dateAsDays);
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeTimestamp(final Timestamp v) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        if (this.timestampWritable == null) {
            this.timestampWritable = new TimestampWritable();
        }
        this.timestampWritable.set(v);
        this.timestampWritable.writeToByteStream(this.output);
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeHiveIntervalYearMonth(final HiveIntervalYearMonth viyt) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        if (this.hiveIntervalYearMonthWritable == null) {
            this.hiveIntervalYearMonthWritable = new HiveIntervalYearMonthWritable();
        }
        this.hiveIntervalYearMonthWritable.set(viyt);
        this.hiveIntervalYearMonthWritable.writeToByteStream(this.output);
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeHiveIntervalYearMonth(final int totalMonths) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        if (this.hiveIntervalYearMonthWritable == null) {
            this.hiveIntervalYearMonthWritable = new HiveIntervalYearMonthWritable();
        }
        this.hiveIntervalYearMonthWritable.set(totalMonths);
        this.hiveIntervalYearMonthWritable.writeToByteStream(this.output);
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeHiveIntervalDayTime(final HiveIntervalDayTime vidt) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        if (this.hiveIntervalDayTimeWritable == null) {
            this.hiveIntervalDayTimeWritable = new HiveIntervalDayTimeWritable();
        }
        this.hiveIntervalDayTimeWritable.set(vidt);
        this.hiveIntervalDayTimeWritable.writeToByteStream(this.output);
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeHiveIntervalDayTime(final long totalNanos) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        if (this.hiveIntervalDayTime == null) {
            this.hiveIntervalDayTime = new HiveIntervalDayTime();
        }
        if (this.hiveIntervalDayTimeWritable == null) {
            this.hiveIntervalDayTimeWritable = new HiveIntervalDayTimeWritable();
        }
        DateUtils.setIntervalDayTimeTotalNanos(this.hiveIntervalDayTime, totalNanos);
        this.hiveIntervalDayTimeWritable.set(this.hiveIntervalDayTime);
        this.hiveIntervalDayTimeWritable.writeToByteStream(this.output);
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    @Override
    public void writeHiveDecimal(final HiveDecimal v) throws IOException {
        if (this.fieldIndex % 8 == 0) {
            if (this.fieldIndex > 0) {
                this.output.writeByte(this.nullOffset, this.nullByte);
                this.nullByte = 0;
                this.nullOffset = this.output.getLength();
            }
            this.output.reserve(1);
        }
        this.nullByte |= (byte)(1 << this.fieldIndex % 8);
        if (this.hiveDecimalWritable == null) {
            this.hiveDecimalWritable = new HiveDecimalWritable();
        }
        this.hiveDecimalWritable.set(v);
        this.hiveDecimalWritable.writeToByteStream(this.output);
        ++this.fieldIndex;
        if (this.fieldIndex == this.fieldCount) {
            this.output.writeByte(this.nullOffset, this.nullByte);
        }
    }
    
    static {
        LOG = LogFactory.getLog(LazyBinarySerializeWrite.class.getName());
    }
}
