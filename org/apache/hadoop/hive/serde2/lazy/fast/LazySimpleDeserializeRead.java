// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.fast;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import java.io.IOException;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.HiveDecimalUtils;
import java.io.UnsupportedEncodingException;
import org.apache.hadoop.hive.serde2.lazy.LazyBinary;
import org.apache.hadoop.hive.serde2.lazy.LazyUtils;
import java.nio.charset.CharacterCodingException;
import org.apache.hadoop.hive.serde2.lazy.LazyLong;
import org.apache.hadoop.hive.serde2.lazy.LazyInteger;
import org.apache.hadoop.hive.serde2.lazy.LazyShort;
import org.apache.hadoop.hive.serde2.lazy.LazyByte;
import org.apache.hive.common.util.TimestampParser;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import java.sql.Timestamp;
import java.sql.Date;
import org.apache.hadoop.hive.serde2.lazy.LazySerDeParameters;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.fast.DeserializeRead;

public class LazySimpleDeserializeRead implements DeserializeRead
{
    public static final Log LOG;
    private PrimitiveTypeInfo[] primitiveTypeInfos;
    private LazySerDeParameters lazyParams;
    private byte separator;
    private boolean lastColumnTakesRest;
    private boolean isEscaped;
    private byte escapeChar;
    private byte[] nullSequenceBytes;
    private boolean isExtendedBooleanLiteral;
    private byte[] bytes;
    private int start;
    private int offset;
    private int end;
    private int fieldCount;
    private int fieldIndex;
    private int fieldStart;
    private int fieldLength;
    private boolean saveBool;
    private byte saveByte;
    private short saveShort;
    private int saveInt;
    private long saveLong;
    private float saveFloat;
    private double saveDouble;
    private byte[] saveBytes;
    private int saveBytesStart;
    private int saveBytesLength;
    private Date saveDate;
    private Timestamp saveTimestamp;
    private HiveIntervalYearMonth saveIntervalYearMonth;
    private HiveIntervalDayTime saveIntervalDayTime;
    private HiveDecimal saveDecimal;
    private DecimalTypeInfo saveDecimalTypeInfo;
    private Text tempText;
    private TimestampParser timestampParser;
    private boolean readBeyondConfiguredFieldsWarned;
    private boolean readBeyondBufferRangeWarned;
    private boolean bufferRangeHasExtraDataWarned;
    private static byte[] maxLongBytes;
    private static int maxLongDigitsCount;
    private static byte[] minLongNoSignBytes;
    
    public LazySimpleDeserializeRead(final PrimitiveTypeInfo[] primitiveTypeInfos, final byte separator, final LazySerDeParameters lazyParams) {
        this.primitiveTypeInfos = primitiveTypeInfos;
        this.separator = separator;
        this.lazyParams = lazyParams;
        this.lastColumnTakesRest = lazyParams.isLastColumnTakesRest();
        this.isEscaped = lazyParams.isEscaped();
        this.escapeChar = lazyParams.getEscapeChar();
        this.nullSequenceBytes = lazyParams.getNullSequence().getBytes();
        this.isExtendedBooleanLiteral = lazyParams.isExtendedBooleanLiteral();
        this.fieldCount = primitiveTypeInfos.length;
        this.tempText = new Text();
        this.readBeyondConfiguredFieldsWarned = false;
        this.readBeyondBufferRangeWarned = false;
        this.bufferRangeHasExtraDataWarned = false;
    }
    
    private LazySimpleDeserializeRead() {
    }
    
    @Override
    public PrimitiveTypeInfo[] primitiveTypeInfos() {
        return this.primitiveTypeInfos;
    }
    
    @Override
    public void set(final byte[] bytes, final int offset, final int length) {
        this.bytes = bytes;
        this.offset = offset;
        this.start = offset;
        this.end = offset + length;
        this.fieldIndex = -1;
    }
    
    @Override
    public boolean readCheckNull() {
        if (++this.fieldIndex >= this.fieldCount) {
            if (!this.readBeyondConfiguredFieldsWarned) {
                LazySimpleDeserializeRead.LOG.info("Reading beyond configured fields! Configured " + this.fieldCount + " fields but " + " reading more (NULLs returned).  Ignoring similar problems.");
                this.readBeyondConfiguredFieldsWarned = true;
            }
            return true;
        }
        if (this.offset > this.end) {
            if (!this.readBeyondBufferRangeWarned) {
                final int length = this.end - this.start;
                LazySimpleDeserializeRead.LOG.info("Reading beyond buffer range! Buffer range " + this.start + " for length " + length + " but reading more (NULLs returned)." + "  Ignoring similar problems.");
                this.readBeyondBufferRangeWarned = true;
            }
            return true;
        }
        this.fieldStart = this.offset;
        while (true) {
            while (this.offset < this.end) {
                if (this.bytes[this.offset] == this.separator) {
                    this.fieldLength = this.offset++ - this.fieldStart;
                    final char[] charField = new char[this.fieldLength];
                    for (int c = 0; c < charField.length; ++c) {
                        charField[c] = (char)(this.bytes[this.fieldStart + c] & 0xFF);
                    }
                    if (this.nullSequenceBytes != null && this.fieldLength == this.nullSequenceBytes.length) {
                        int i = 0;
                        while (this.bytes[this.fieldStart + i] == this.nullSequenceBytes[i]) {
                            if (++i >= this.fieldLength) {
                                return true;
                            }
                        }
                    }
                    switch (this.primitiveTypeInfos[this.fieldIndex].getPrimitiveCategory()) {
                        case BOOLEAN: {
                            final int i = this.fieldStart;
                            if (this.fieldLength == 4) {
                                if ((this.bytes[i] != 84 && this.bytes[i] != 116) || (this.bytes[i + 1] != 82 && this.bytes[i + 1] != 114) || (this.bytes[i + 2] != 85 && this.bytes[i + 1] != 117) || (this.bytes[i + 3] != 69 && this.bytes[i + 3] != 101)) {
                                    return true;
                                }
                                this.saveBool = true;
                            }
                            else if (this.fieldLength == 5) {
                                if ((this.bytes[i] != 70 && this.bytes[i] != 102) || (this.bytes[i + 1] != 65 && this.bytes[i + 1] != 97) || (this.bytes[i + 2] != 76 && this.bytes[i + 2] != 108) || (this.bytes[i + 3] != 83 && this.bytes[i + 3] != 115) || (this.bytes[i + 4] != 69 && this.bytes[i + 4] != 101)) {
                                    return true;
                                }
                                this.saveBool = false;
                            }
                            else {
                                if (!this.isExtendedBooleanLiteral || this.fieldLength != 1) {
                                    return true;
                                }
                                final byte b = this.bytes[this.fieldStart];
                                if (b == 49 || b == 116 || b == 84) {
                                    this.saveBool = true;
                                }
                                else {
                                    if (b != 48 && b != 102 && b != 70) {
                                        return true;
                                    }
                                    this.saveBool = false;
                                }
                            }
                            break;
                        }
                        case BYTE: {
                            try {
                                this.saveByte = LazyByte.parseByte(this.bytes, this.fieldStart, this.fieldLength, 10);
                                break;
                            }
                            catch (NumberFormatException e4) {
                                this.logExceptionMessage(this.bytes, this.fieldStart, this.fieldLength, "TINYINT");
                                return true;
                            }
                        }
                        case SHORT: {
                            try {
                                this.saveShort = LazyShort.parseShort(this.bytes, this.fieldStart, this.fieldLength, 10);
                                break;
                            }
                            catch (NumberFormatException e4) {
                                this.logExceptionMessage(this.bytes, this.fieldStart, this.fieldLength, "SMALLINT");
                                return true;
                            }
                        }
                        case INT: {
                            try {
                                this.saveInt = LazyInteger.parseInt(this.bytes, this.fieldStart, this.fieldLength, 10);
                                break;
                            }
                            catch (NumberFormatException e4) {
                                this.logExceptionMessage(this.bytes, this.fieldStart, this.fieldLength, "INT");
                                return true;
                            }
                        }
                        case LONG: {
                            try {
                                this.saveLong = LazyLong.parseLong(this.bytes, this.fieldStart, this.fieldLength, 10);
                                break;
                            }
                            catch (NumberFormatException e4) {
                                this.logExceptionMessage(this.bytes, this.fieldStart, this.fieldLength, "BIGINT");
                                return true;
                            }
                        }
                        case FLOAT: {
                            String byteData = null;
                            try {
                                byteData = Text.decode(this.bytes, this.fieldStart, this.fieldLength);
                                this.saveFloat = Float.parseFloat(byteData);
                            }
                            catch (NumberFormatException e) {
                                LazySimpleDeserializeRead.LOG.debug("Data not in the Float data type range so converted to null. Given data is :" + byteData, e);
                                return true;
                            }
                            catch (CharacterCodingException e2) {
                                LazySimpleDeserializeRead.LOG.debug("Data not in the Float data type range so converted to null.", e2);
                                return true;
                            }
                            break;
                        }
                        case DOUBLE: {
                            String byteData = null;
                            try {
                                byteData = Text.decode(this.bytes, this.fieldStart, this.fieldLength);
                                this.saveDouble = Double.parseDouble(byteData);
                            }
                            catch (NumberFormatException e) {
                                LazySimpleDeserializeRead.LOG.debug("Data not in the Double data type range so converted to null. Given data is :" + byteData, e);
                                return true;
                            }
                            catch (CharacterCodingException e2) {
                                LazySimpleDeserializeRead.LOG.debug("Data not in the Double data type range so converted to null.", e2);
                                return true;
                            }
                            break;
                        }
                        case STRING:
                        case CHAR:
                        case VARCHAR: {
                            if (this.isEscaped) {
                                LazyUtils.copyAndEscapeStringDataToText(this.bytes, this.fieldStart, this.fieldLength, this.escapeChar, this.tempText);
                                this.saveBytes = this.tempText.getBytes();
                                this.saveBytesStart = 0;
                                this.saveBytesLength = this.tempText.getLength();
                                break;
                            }
                            this.saveBytes = this.bytes;
                            this.saveBytesStart = this.fieldStart;
                            this.saveBytesLength = this.fieldLength;
                            break;
                        }
                        case BINARY: {
                            final byte[] recv = new byte[this.fieldLength];
                            System.arraycopy(this.bytes, this.fieldStart, recv, 0, this.fieldLength);
                            byte[] decoded = LazyBinary.decodeIfNeeded(recv);
                            decoded = ((decoded.length > 0) ? decoded : recv);
                            this.saveBytes = decoded;
                            this.saveBytesStart = 0;
                            this.saveBytesLength = decoded.length;
                            break;
                        }
                        case DATE: {
                            String s = null;
                            try {
                                s = Text.decode(this.bytes, this.fieldStart, this.fieldLength);
                                this.saveDate = Date.valueOf(s);
                            }
                            catch (Exception e5) {
                                this.logExceptionMessage(this.bytes, this.fieldStart, this.fieldLength, "DATE");
                                return true;
                            }
                            break;
                        }
                        case TIMESTAMP: {
                            String s = null;
                            try {
                                s = new String(this.bytes, this.fieldStart, this.fieldLength, "US-ASCII");
                            }
                            catch (UnsupportedEncodingException e3) {
                                LazySimpleDeserializeRead.LOG.error(e3);
                                s = "";
                            }
                            if (s.compareTo("NULL") == 0) {
                                this.logExceptionMessage(this.bytes, this.fieldStart, this.fieldLength, "TIMESTAMP");
                                return true;
                            }
                            try {
                                if (this.timestampParser == null) {
                                    this.timestampParser = new TimestampParser();
                                }
                                this.saveTimestamp = this.timestampParser.parseTimestamp(s);
                            }
                            catch (IllegalArgumentException e6) {
                                this.logExceptionMessage(this.bytes, this.fieldStart, this.fieldLength, "TIMESTAMP");
                                return true;
                            }
                            break;
                        }
                        case INTERVAL_YEAR_MONTH: {
                            String s = null;
                            try {
                                s = Text.decode(this.bytes, this.fieldStart, this.fieldLength);
                                this.saveIntervalYearMonth = HiveIntervalYearMonth.valueOf(s);
                            }
                            catch (Exception e5) {
                                this.logExceptionMessage(this.bytes, this.fieldStart, this.fieldLength, "INTERVAL_YEAR_MONTH");
                                return true;
                            }
                            break;
                        }
                        case INTERVAL_DAY_TIME: {
                            String s = null;
                            try {
                                s = Text.decode(this.bytes, this.fieldStart, this.fieldLength);
                                this.saveIntervalDayTime = HiveIntervalDayTime.valueOf(s);
                            }
                            catch (Exception e5) {
                                this.logExceptionMessage(this.bytes, this.fieldStart, this.fieldLength, "INTERVAL_DAY_TIME");
                                return true;
                            }
                            break;
                        }
                        case DECIMAL: {
                            String byteData = null;
                            try {
                                byteData = Text.decode(this.bytes, this.fieldStart, this.fieldLength);
                            }
                            catch (CharacterCodingException e2) {
                                LazySimpleDeserializeRead.LOG.debug("Data not in the HiveDecimal data type range so converted to null.", e2);
                                return true;
                            }
                            this.saveDecimal = HiveDecimal.create(byteData);
                            this.saveDecimalTypeInfo = (DecimalTypeInfo)this.primitiveTypeInfos[this.fieldIndex];
                            final int precision = this.saveDecimalTypeInfo.getPrecision();
                            final int scale = this.saveDecimalTypeInfo.getScale();
                            this.saveDecimal = HiveDecimalUtils.enforcePrecisionScale(this.saveDecimal, precision, scale);
                            if (this.saveDecimal == null) {
                                LazySimpleDeserializeRead.LOG.debug("Data not in the HiveDecimal data type range so converted to null. Given data is :" + byteData);
                                return true;
                            }
                            break;
                        }
                        default: {
                            throw new Error("Unexpected primitive category " + this.primitiveTypeInfos[this.fieldIndex].getPrimitiveCategory());
                        }
                    }
                    return false;
                }
                if (this.isEscaped && this.bytes[this.offset] == this.escapeChar && this.offset + 1 < this.end) {
                    this.offset += 2;
                }
                else {
                    ++this.offset;
                }
            }
            this.fieldLength = this.offset - this.fieldStart;
            continue;
        }
    }
    
    public void logExceptionMessage(final byte[] bytes, final int bytesStart, final int bytesLength, final String dataType) {
        try {
            if (LazySimpleDeserializeRead.LOG.isDebugEnabled()) {
                final String byteData = Text.decode(bytes, bytesStart, bytesLength);
                LazySimpleDeserializeRead.LOG.debug("Data not in the " + dataType + " data type range so converted to null. Given data is :" + byteData, new Exception("For debugging purposes"));
            }
        }
        catch (CharacterCodingException e1) {
            LazySimpleDeserializeRead.LOG.debug("Data not in the " + dataType + " data type range so converted to null.", e1);
        }
    }
    
    @Override
    public void extraFieldsCheck() {
        if (this.offset < this.end && !this.bufferRangeHasExtraDataWarned) {
            final int length = this.end - this.start;
            LazySimpleDeserializeRead.LOG.info("Not all fields were read in the buffer range! Buffer range " + this.start + " for length " + length + " but reading more (NULLs returned)." + "  Ignoring similar problems.");
            this.bufferRangeHasExtraDataWarned = true;
        }
    }
    
    @Override
    public boolean readBeyondConfiguredFieldsWarned() {
        return this.readBeyondConfiguredFieldsWarned;
    }
    
    @Override
    public boolean readBeyondBufferRangeWarned() {
        return this.readBeyondBufferRangeWarned;
    }
    
    @Override
    public boolean bufferRangeHasExtraDataWarned() {
        return this.bufferRangeHasExtraDataWarned;
    }
    
    @Override
    public boolean readBoolean() {
        return this.saveBool;
    }
    
    @Override
    public byte readByte() {
        return this.saveByte;
    }
    
    @Override
    public short readShort() {
        return this.saveShort;
    }
    
    @Override
    public int readInt() {
        return this.saveInt;
    }
    
    @Override
    public long readLong() {
        return this.saveLong;
    }
    
    @Override
    public float readFloat() {
        return this.saveFloat;
    }
    
    @Override
    public double readDouble() {
        return this.saveDouble;
    }
    
    @Override
    public ReadStringResults createReadStringResults() {
        return new LazySimpleReadStringResults();
    }
    
    @Override
    public void readString(final ReadStringResults readStringResults) {
        readStringResults.bytes = this.saveBytes;
        readStringResults.start = this.saveBytesStart;
        readStringResults.length = this.saveBytesLength;
    }
    
    @Override
    public ReadHiveCharResults createReadHiveCharResults() {
        return new LazySimpleReadHiveCharResults();
    }
    
    @Override
    public void readHiveChar(final ReadHiveCharResults readHiveCharResults) throws IOException {
        final LazySimpleReadHiveCharResults LazySimpleReadHiveCharResults = (LazySimpleReadHiveCharResults)readHiveCharResults;
        if (!LazySimpleReadHiveCharResults.isInit()) {
            LazySimpleReadHiveCharResults.init((CharTypeInfo)this.primitiveTypeInfos[this.fieldIndex]);
        }
        if (LazySimpleReadHiveCharResults.readStringResults == null) {
            LazySimpleReadHiveCharResults.readStringResults = new LazySimpleReadStringResults();
        }
        final LazySimpleReadStringResults readStringResults = LazySimpleReadHiveCharResults.readStringResults;
        this.readString(readStringResults);
        final HiveCharWritable hiveCharWritable = LazySimpleReadHiveCharResults.getHiveCharWritable();
        hiveCharWritable.getTextValue().set(readStringResults.bytes, readStringResults.start, readStringResults.length);
        hiveCharWritable.enforceMaxLength(LazySimpleReadHiveCharResults.getMaxLength());
        readHiveCharResults.bytes = hiveCharWritable.getTextValue().getBytes();
        readHiveCharResults.start = 0;
        readHiveCharResults.length = hiveCharWritable.getTextValue().getLength();
    }
    
    @Override
    public ReadHiveVarcharResults createReadHiveVarcharResults() {
        return new LazySimpleReadHiveVarcharResults();
    }
    
    @Override
    public void readHiveVarchar(final ReadHiveVarcharResults readHiveVarcharResults) throws IOException {
        final LazySimpleReadHiveVarcharResults lazySimpleReadHiveVarvarcharResults = (LazySimpleReadHiveVarcharResults)readHiveVarcharResults;
        if (!lazySimpleReadHiveVarvarcharResults.isInit()) {
            lazySimpleReadHiveVarvarcharResults.init((VarcharTypeInfo)this.primitiveTypeInfos[this.fieldIndex]);
        }
        if (lazySimpleReadHiveVarvarcharResults.readStringResults == null) {
            lazySimpleReadHiveVarvarcharResults.readStringResults = new LazySimpleReadStringResults();
        }
        final LazySimpleReadStringResults readStringResults = lazySimpleReadHiveVarvarcharResults.readStringResults;
        this.readString(readStringResults);
        final HiveVarcharWritable hiveVarcharWritable = lazySimpleReadHiveVarvarcharResults.getHiveVarcharWritable();
        hiveVarcharWritable.getTextValue().set(readStringResults.bytes, readStringResults.start, readStringResults.length);
        hiveVarcharWritable.enforceMaxLength(lazySimpleReadHiveVarvarcharResults.getMaxLength());
        readHiveVarcharResults.bytes = hiveVarcharWritable.getTextValue().getBytes();
        readHiveVarcharResults.start = 0;
        readHiveVarcharResults.length = hiveVarcharWritable.getTextValue().getLength();
    }
    
    @Override
    public ReadBinaryResults createReadBinaryResults() {
        return new LazySimpleReadBinaryResults();
    }
    
    @Override
    public void readBinary(final ReadBinaryResults readBinaryResults) {
        readBinaryResults.bytes = this.saveBytes;
        readBinaryResults.start = this.saveBytesStart;
        readBinaryResults.length = this.saveBytesLength;
    }
    
    @Override
    public ReadDateResults createReadDateResults() {
        return new LazySimpleReadDateResults();
    }
    
    @Override
    public void readDate(final ReadDateResults readDateResults) {
        final LazySimpleReadDateResults lazySimpleReadDateResults = (LazySimpleReadDateResults)readDateResults;
        final DateWritable dateWritable = lazySimpleReadDateResults.getDateWritable();
        dateWritable.set(this.saveDate);
        this.saveDate = null;
    }
    
    @Override
    public ReadIntervalYearMonthResults createReadIntervalYearMonthResults() {
        return new LazySimpleReadIntervalYearMonthResults();
    }
    
    @Override
    public void readIntervalYearMonth(final ReadIntervalYearMonthResults readIntervalYearMonthResults) throws IOException {
        final LazySimpleReadIntervalYearMonthResults lazySimpleReadIntervalYearMonthResults = (LazySimpleReadIntervalYearMonthResults)readIntervalYearMonthResults;
        final HiveIntervalYearMonthWritable hiveIntervalYearMonthWritable = lazySimpleReadIntervalYearMonthResults.getHiveIntervalYearMonthWritable();
        hiveIntervalYearMonthWritable.set(this.saveIntervalYearMonth);
        this.saveIntervalYearMonth = null;
    }
    
    @Override
    public ReadIntervalDayTimeResults createReadIntervalDayTimeResults() {
        return new LazySimpleReadIntervalDayTimeResults();
    }
    
    @Override
    public void readIntervalDayTime(final ReadIntervalDayTimeResults readIntervalDayTimeResults) throws IOException {
        final LazySimpleReadIntervalDayTimeResults lazySimpleReadIntervalDayTimeResults = (LazySimpleReadIntervalDayTimeResults)readIntervalDayTimeResults;
        final HiveIntervalDayTimeWritable hiveIntervalDayTimeWritable = lazySimpleReadIntervalDayTimeResults.getHiveIntervalDayTimeWritable();
        hiveIntervalDayTimeWritable.set(this.saveIntervalDayTime);
        this.saveIntervalDayTime = null;
    }
    
    @Override
    public ReadTimestampResults createReadTimestampResults() {
        return new LazySimpleReadTimestampResults();
    }
    
    @Override
    public void readTimestamp(final ReadTimestampResults readTimestampResults) {
        final LazySimpleReadTimestampResults lazySimpleReadTimestampResults = (LazySimpleReadTimestampResults)readTimestampResults;
        final TimestampWritable timestampWritable = lazySimpleReadTimestampResults.getTimestampWritable();
        timestampWritable.set(this.saveTimestamp);
        this.saveTimestamp = null;
    }
    
    @Override
    public ReadDecimalResults createReadDecimalResults() {
        return new LazySimpleReadDecimalResults();
    }
    
    @Override
    public void readHiveDecimal(final ReadDecimalResults readDecimalResults) {
        final LazySimpleReadDecimalResults lazySimpleReadDecimalResults = (LazySimpleReadDecimalResults)readDecimalResults;
        if (!lazySimpleReadDecimalResults.isInit()) {
            lazySimpleReadDecimalResults.init(this.saveDecimalTypeInfo);
        }
        lazySimpleReadDecimalResults.hiveDecimal = this.saveDecimal;
        this.saveDecimal = null;
        this.saveDecimalTypeInfo = null;
    }
    
    private boolean parseLongFast() {
        int i = this.fieldStart;
        final int end = this.fieldStart + this.fieldLength;
        boolean negative = false;
        if (i >= end) {
            return false;
        }
        if (this.bytes[i] == 43) {
            if (++i >= end) {
                return false;
            }
        }
        else if (this.bytes[i] == 45) {
            negative = true;
            if (++i >= end) {
                return false;
            }
        }
        boolean atLeastOneZero = false;
        while (this.bytes[i] == 48) {
            if (++i >= end) {
                this.saveLong = 0L;
                return true;
            }
            atLeastOneZero = true;
        }
        if (this.bytes[i] == 46) {
            if (!atLeastOneZero) {
                return false;
            }
            this.saveLong = 0L;
        }
        else {
            if (!Character.isDigit(this.bytes[i])) {
                return false;
            }
            final int nonLeadingZeroStart = i;
            int digitCount = 1;
            this.saveLong = Character.digit(this.bytes[i], 10);
            ++i;
            while (i < end && Character.isDigit(this.bytes[i])) {
                if (++digitCount > LazySimpleDeserializeRead.maxLongDigitsCount) {
                    return false;
                }
                if (digitCount == LazySimpleDeserializeRead.maxLongDigitsCount) {
                    if (!negative) {
                        if (byteArrayCompareRanges(this.bytes, nonLeadingZeroStart, LazySimpleDeserializeRead.maxLongBytes, 0, digitCount) >= 1) {
                            return false;
                        }
                    }
                    else if (byteArrayCompareRanges(this.bytes, nonLeadingZeroStart, LazySimpleDeserializeRead.minLongNoSignBytes, 0, digitCount) >= 1) {
                        return false;
                    }
                }
                this.saveLong = this.saveLong * 10L + Character.digit(this.bytes[i], 10);
            }
            if (negative) {
                this.saveLong = -this.saveLong;
            }
            if (i >= end) {
                return true;
            }
            if (this.bytes[i] != 46) {
                return false;
            }
        }
        while (++i < end) {
            if (!Character.isDigit(this.bytes[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static int byteArrayCompareRanges(final byte[] arg1, final int start1, final byte[] arg2, final int start2, final int len) {
        for (int i = 0; i < len; ++i) {
            final int b1 = arg1[i + start1] & 0xFF;
            final int b2 = arg2[i + start2] & 0xFF;
            if (b1 != b2) {
                return b1 - b2;
            }
        }
        return 0;
    }
    
    static {
        LOG = LogFactory.getLog(LazySimpleDeserializeRead.class.getName());
        LazySimpleDeserializeRead.maxLongBytes = Long.valueOf(Long.MAX_VALUE).toString().getBytes();
        LazySimpleDeserializeRead.maxLongDigitsCount = LazySimpleDeserializeRead.maxLongBytes.length;
        LazySimpleDeserializeRead.minLongNoSignBytes = Long.valueOf(Long.MIN_VALUE).toString().substring(1).getBytes();
    }
    
    private class LazySimpleReadStringResults extends ReadStringResults
    {
        public LazySimpleReadStringResults() {
        }
    }
    
    private static class LazySimpleReadHiveCharResults extends ReadHiveCharResults
    {
        public LazySimpleReadStringResults readStringResults;
        
        public LazySimpleReadHiveCharResults() {
        }
        
        public HiveCharWritable getHiveCharWritable() {
            return this.hiveCharWritable;
        }
    }
    
    private static class LazySimpleReadHiveVarcharResults extends ReadHiveVarcharResults
    {
        public LazySimpleReadStringResults readStringResults;
        
        public LazySimpleReadHiveVarcharResults() {
        }
        
        public HiveVarcharWritable getHiveVarcharWritable() {
            return this.hiveVarcharWritable;
        }
    }
    
    private class LazySimpleReadBinaryResults extends ReadBinaryResults
    {
        public LazySimpleReadBinaryResults() {
        }
    }
    
    private static class LazySimpleReadDateResults extends ReadDateResults
    {
        public LazySimpleReadDateResults() {
        }
        
        public DateWritable getDateWritable() {
            return this.dateWritable;
        }
    }
    
    private static class LazySimpleReadIntervalYearMonthResults extends ReadIntervalYearMonthResults
    {
        public LazySimpleReadIntervalYearMonthResults() {
        }
        
        public HiveIntervalYearMonthWritable getHiveIntervalYearMonthWritable() {
            return this.hiveIntervalYearMonthWritable;
        }
    }
    
    private static class LazySimpleReadIntervalDayTimeResults extends ReadIntervalDayTimeResults
    {
        public LazySimpleReadIntervalDayTimeResults() {
        }
        
        public HiveIntervalDayTimeWritable getHiveIntervalDayTimeWritable() {
            return this.hiveIntervalDayTimeWritable;
        }
    }
    
    private static class LazySimpleReadTimestampResults extends ReadTimestampResults
    {
        public LazySimpleReadTimestampResults() {
        }
        
        public TimestampWritable getTimestampWritable() {
            return this.timestampWritable;
        }
    }
    
    private static class LazySimpleReadDecimalResults extends ReadDecimalResults
    {
        HiveDecimal hiveDecimal;
        
        public LazySimpleReadDecimalResults() {
        }
        
        @Override
        public HiveDecimal getHiveDecimal() {
            return this.hiveDecimal;
        }
    }
}
