// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.fast;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.lazy.LazyHiveDecimal;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hive.common.util.DateUtils;
import org.apache.hadoop.hive.serde2.lazy.LazyHiveIntervalDayTime;
import org.apache.hadoop.hive.serde2.lazy.LazyHiveIntervalYearMonth;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import org.apache.hadoop.hive.serde2.lazy.LazyTimestamp;
import java.sql.Timestamp;
import org.apache.hadoop.hive.serde2.lazy.LazyDate;
import java.sql.Date;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.common.type.HiveChar;
import java.nio.ByteBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.lazy.LazyLong;
import java.io.OutputStream;
import org.apache.hadoop.hive.serde2.lazy.LazyInteger;
import org.apache.hadoop.hive.serde2.lazy.LazyUtils;
import java.io.IOException;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.hadoop.hive.serde2.lazy.LazySerDeParameters;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.fast.SerializeWrite;

public class LazySimpleSerializeWrite implements SerializeWrite
{
    public static final Log LOG;
    private LazySerDeParameters lazyParams;
    private byte separator;
    private boolean[] needsEscape;
    private boolean isEscaped;
    private byte escapeChar;
    private byte[] nullSequenceBytes;
    private ByteStream.Output output;
    private int fieldCount;
    private int index;
    private DateWritable dateWritable;
    private TimestampWritable timestampWritable;
    private HiveIntervalYearMonthWritable hiveIntervalYearMonthWritable;
    private HiveIntervalDayTimeWritable hiveIntervalDayTimeWritable;
    private HiveIntervalDayTime hiveIntervalDayTime;
    
    public LazySimpleSerializeWrite(final int fieldCount, final byte separator, final LazySerDeParameters lazyParams) {
        this();
        this.fieldCount = fieldCount;
        this.separator = separator;
        this.lazyParams = lazyParams;
        this.isEscaped = lazyParams.isEscaped();
        this.escapeChar = lazyParams.getEscapeChar();
        this.needsEscape = lazyParams.getNeedsEscape();
        this.nullSequenceBytes = lazyParams.getNullSequence().getBytes();
    }
    
    private LazySimpleSerializeWrite() {
    }
    
    @Override
    public void set(final ByteStream.Output output) {
        (this.output = output).reset();
        this.index = 0;
    }
    
    @Override
    public void setAppend(final ByteStream.Output output) {
        this.output = output;
        this.index = 0;
    }
    
    @Override
    public void reset() {
        this.output.reset();
        this.index = 0;
    }
    
    @Override
    public void writeNull() throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        this.output.write(this.nullSequenceBytes);
        ++this.index;
    }
    
    @Override
    public void writeBoolean(final boolean v) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        if (v) {
            this.output.write(LazyUtils.trueBytes, 0, LazyUtils.trueBytes.length);
        }
        else {
            this.output.write(LazyUtils.falseBytes, 0, LazyUtils.falseBytes.length);
        }
        ++this.index;
    }
    
    @Override
    public void writeByte(final byte v) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        LazyInteger.writeUTF8(this.output, v);
        ++this.index;
    }
    
    @Override
    public void writeShort(final short v) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        LazyInteger.writeUTF8(this.output, v);
        ++this.index;
    }
    
    @Override
    public void writeInt(final int v) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        LazyInteger.writeUTF8(this.output, v);
        ++this.index;
    }
    
    @Override
    public void writeLong(final long v) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        LazyLong.writeUTF8(this.output, v);
        ++this.index;
    }
    
    @Override
    public void writeFloat(final float vf) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        final ByteBuffer b = Text.encode(String.valueOf(vf));
        this.output.write(b.array(), 0, b.limit());
        ++this.index;
    }
    
    @Override
    public void writeDouble(final double v) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        final ByteBuffer b = Text.encode(String.valueOf(v));
        this.output.write(b.array(), 0, b.limit());
        ++this.index;
    }
    
    @Override
    public void writeString(final byte[] v) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        LazyUtils.writeEscaped(this.output, v, 0, v.length, this.isEscaped, this.escapeChar, this.needsEscape);
        ++this.index;
    }
    
    @Override
    public void writeString(final byte[] v, final int start, final int length) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        LazyUtils.writeEscaped(this.output, v, start, length, this.isEscaped, this.escapeChar, this.needsEscape);
        ++this.index;
    }
    
    @Override
    public void writeHiveChar(final HiveChar hiveChar) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        final ByteBuffer b = Text.encode(hiveChar.getPaddedValue());
        LazyUtils.writeEscaped(this.output, b.array(), 0, b.limit(), this.isEscaped, this.escapeChar, this.needsEscape);
        ++this.index;
    }
    
    @Override
    public void writeHiveVarchar(final HiveVarchar hiveVarchar) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        final ByteBuffer b = Text.encode(hiveVarchar.getValue());
        LazyUtils.writeEscaped(this.output, b.array(), 0, b.limit(), this.isEscaped, this.escapeChar, this.needsEscape);
        ++this.index;
    }
    
    @Override
    public void writeBinary(final byte[] v) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        final byte[] toEncode = new byte[v.length];
        System.arraycopy(v, 0, toEncode, 0, v.length);
        final byte[] toWrite = Base64.encodeBase64(toEncode);
        this.output.write(toWrite, 0, toWrite.length);
        ++this.index;
    }
    
    @Override
    public void writeBinary(final byte[] v, final int start, final int length) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        final byte[] toEncode = new byte[length];
        System.arraycopy(v, start, toEncode, 0, length);
        final byte[] toWrite = Base64.encodeBase64(toEncode);
        this.output.write(toWrite, 0, toWrite.length);
        ++this.index;
    }
    
    @Override
    public void writeDate(final Date date) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        if (this.dateWritable == null) {
            this.dateWritable = new DateWritable();
        }
        this.dateWritable.set(date);
        LazyDate.writeUTF8(this.output, this.dateWritable);
        ++this.index;
    }
    
    @Override
    public void writeDate(final int dateAsDays) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        if (this.dateWritable == null) {
            this.dateWritable = new DateWritable();
        }
        this.dateWritable.set(dateAsDays);
        LazyDate.writeUTF8(this.output, this.dateWritable);
        ++this.index;
    }
    
    @Override
    public void writeTimestamp(final Timestamp v) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        if (this.timestampWritable == null) {
            this.timestampWritable = new TimestampWritable();
        }
        this.timestampWritable.set(v);
        LazyTimestamp.writeUTF8(this.output, this.timestampWritable);
        ++this.index;
    }
    
    @Override
    public void writeHiveIntervalYearMonth(final HiveIntervalYearMonth viyt) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        if (this.hiveIntervalYearMonthWritable == null) {
            this.hiveIntervalYearMonthWritable = new HiveIntervalYearMonthWritable();
        }
        this.hiveIntervalYearMonthWritable.set(viyt);
        LazyHiveIntervalYearMonth.writeUTF8(this.output, this.hiveIntervalYearMonthWritable);
        ++this.index;
    }
    
    @Override
    public void writeHiveIntervalYearMonth(final int totalMonths) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        if (this.hiveIntervalYearMonthWritable == null) {
            this.hiveIntervalYearMonthWritable = new HiveIntervalYearMonthWritable();
        }
        this.hiveIntervalYearMonthWritable.set(totalMonths);
        LazyHiveIntervalYearMonth.writeUTF8(this.output, this.hiveIntervalYearMonthWritable);
        ++this.index;
    }
    
    @Override
    public void writeHiveIntervalDayTime(final HiveIntervalDayTime vidt) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        if (this.hiveIntervalDayTimeWritable == null) {
            this.hiveIntervalDayTimeWritable = new HiveIntervalDayTimeWritable();
        }
        this.hiveIntervalDayTimeWritable.set(vidt);
        LazyHiveIntervalDayTime.writeUTF8(this.output, this.hiveIntervalDayTimeWritable);
        ++this.index;
    }
    
    @Override
    public void writeHiveIntervalDayTime(final long totalNanos) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        if (this.hiveIntervalDayTime == null) {
            this.hiveIntervalDayTime = new HiveIntervalDayTime();
        }
        if (this.hiveIntervalDayTimeWritable == null) {
            this.hiveIntervalDayTimeWritable = new HiveIntervalDayTimeWritable();
        }
        DateUtils.setIntervalDayTimeTotalNanos(this.hiveIntervalDayTime, totalNanos);
        this.hiveIntervalDayTimeWritable.set(this.hiveIntervalDayTime);
        LazyHiveIntervalDayTime.writeUTF8(this.output, this.hiveIntervalDayTimeWritable);
        ++this.index;
    }
    
    @Override
    public void writeHiveDecimal(final HiveDecimal v) throws IOException {
        if (this.index > 0) {
            this.output.write(this.separator);
        }
        LazyHiveDecimal.writeUTF8(this.output, v);
        ++this.index;
    }
    
    static {
        LOG = LogFactory.getLog(LazySimpleSerializeWrite.class.getName());
    }
}
