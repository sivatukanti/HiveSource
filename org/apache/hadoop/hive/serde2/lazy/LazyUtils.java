// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.hadoop.hive.serde2.SerDeException;
import java.util.Arrays;
import java.io.DataOutputStream;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import java.nio.ByteBuffer;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveDecimalObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveIntervalDayTimeObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveIntervalYearMonthObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.TimestampObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.DateObjectInspector;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BinaryObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveVarcharObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveCharObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.DoubleObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.FloatObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.LongObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.IntObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.ShortObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.ByteObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BooleanObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.CharacterCodingException;
import org.apache.hadoop.io.Text;

public final class LazyUtils
{
    public static byte[] trueBytes;
    public static byte[] falseBytes;
    
    public static int digit(final int b, final int radix) {
        int r = -1;
        if (b >= 48 && b <= 57) {
            r = b - 48;
        }
        else if (b >= 65 && b <= 90) {
            r = b - 65 + 10;
        }
        else if (b >= 97 && b <= 122) {
            r = b - 97 + 10;
        }
        if (r >= radix) {
            r = -1;
        }
        return r;
    }
    
    public static int compare(final byte[] b1, final int start1, final int length1, final byte[] b2, final int start2, final int length2) {
        final int min = Math.min(length1, length2);
        int i = 0;
        while (i < min) {
            if (b1[start1 + i] == b2[start2 + i]) {
                ++i;
            }
            else {
                if (b1[start1 + i] < b2[start2 + i]) {
                    return -1;
                }
                return 1;
            }
        }
        if (length1 < length2) {
            return -1;
        }
        if (length1 > length2) {
            return 1;
        }
        return 0;
    }
    
    public static String convertToString(final byte[] bytes, final int start, final int length) {
        try {
            return Text.decode(bytes, start, length);
        }
        catch (CharacterCodingException e) {
            return null;
        }
    }
    
    public static void writeEscaped(final OutputStream out, final byte[] bytes, int start, final int len, final boolean escaped, final byte escapeChar, final boolean[] needsEscape) throws IOException {
        if (escaped) {
            for (int end = start + len, i = start; i <= end; ++i) {
                if (i == end || needsEscape[bytes[i] & 0xFF]) {
                    if (i > start) {
                        out.write(bytes, start, i - start);
                    }
                    if ((start = i) < len) {
                        out.write(escapeChar);
                    }
                }
            }
        }
        else {
            out.write(bytes, start, len);
        }
    }
    
    public static void writePrimitiveUTF8(final OutputStream out, final Object o, final PrimitiveObjectInspector oi, final boolean escaped, final byte escapeChar, final boolean[] needsEscape) throws IOException {
        switch (oi.getPrimitiveCategory()) {
            case BOOLEAN: {
                final boolean b = ((BooleanObjectInspector)oi).get(o);
                if (b) {
                    out.write(LazyUtils.trueBytes, 0, LazyUtils.trueBytes.length);
                    break;
                }
                out.write(LazyUtils.falseBytes, 0, LazyUtils.falseBytes.length);
                break;
            }
            case BYTE: {
                LazyInteger.writeUTF8(out, ((ByteObjectInspector)oi).get(o));
                break;
            }
            case SHORT: {
                LazyInteger.writeUTF8(out, ((ShortObjectInspector)oi).get(o));
                break;
            }
            case INT: {
                LazyInteger.writeUTF8(out, ((IntObjectInspector)oi).get(o));
                break;
            }
            case LONG: {
                LazyLong.writeUTF8(out, ((LongObjectInspector)oi).get(o));
                break;
            }
            case FLOAT: {
                final float f = ((FloatObjectInspector)oi).get(o);
                final ByteBuffer b2 = Text.encode(String.valueOf(f));
                out.write(b2.array(), 0, b2.limit());
                break;
            }
            case DOUBLE: {
                final double d = ((DoubleObjectInspector)oi).get(o);
                final ByteBuffer b3 = Text.encode(String.valueOf(d));
                out.write(b3.array(), 0, b3.limit());
                break;
            }
            case STRING: {
                final Text t = ((StringObjectInspector)oi).getPrimitiveWritableObject(o);
                writeEscaped(out, t.getBytes(), 0, t.getLength(), escaped, escapeChar, needsEscape);
                break;
            }
            case CHAR: {
                final HiveCharWritable hc = ((HiveCharObjectInspector)oi).getPrimitiveWritableObject(o);
                final Text t2 = hc.getPaddedValue();
                writeEscaped(out, t2.getBytes(), 0, t2.getLength(), escaped, escapeChar, needsEscape);
                break;
            }
            case VARCHAR: {
                final HiveVarcharWritable hc2 = ((HiveVarcharObjectInspector)oi).getPrimitiveWritableObject(o);
                final Text t2 = hc2.getTextValue();
                writeEscaped(out, t2.getBytes(), 0, t2.getLength(), escaped, escapeChar, needsEscape);
                break;
            }
            case BINARY: {
                final BytesWritable bw = ((BinaryObjectInspector)oi).getPrimitiveWritableObject(o);
                final byte[] toEncode = new byte[bw.getLength()];
                System.arraycopy(bw.getBytes(), 0, toEncode, 0, bw.getLength());
                final byte[] toWrite = Base64.encodeBase64(toEncode);
                out.write(toWrite, 0, toWrite.length);
                break;
            }
            case DATE: {
                LazyDate.writeUTF8(out, ((DateObjectInspector)oi).getPrimitiveWritableObject(o));
                break;
            }
            case TIMESTAMP: {
                LazyTimestamp.writeUTF8(out, ((TimestampObjectInspector)oi).getPrimitiveWritableObject(o));
                break;
            }
            case INTERVAL_YEAR_MONTH: {
                LazyHiveIntervalYearMonth.writeUTF8(out, ((HiveIntervalYearMonthObjectInspector)oi).getPrimitiveWritableObject(o));
                break;
            }
            case INTERVAL_DAY_TIME: {
                LazyHiveIntervalDayTime.writeUTF8(out, ((HiveIntervalDayTimeObjectInspector)oi).getPrimitiveWritableObject(o));
                break;
            }
            case DECIMAL: {
                LazyHiveDecimal.writeUTF8(out, ((HiveDecimalObjectInspector)oi).getPrimitiveJavaObject(o));
                break;
            }
            default: {
                throw new RuntimeException("Hive internal error.");
            }
        }
    }
    
    public static void writePrimitive(final OutputStream out, final Object o, final PrimitiveObjectInspector oi) throws IOException {
        final DataOutputStream dos = new DataOutputStream(out);
        try {
            switch (oi.getPrimitiveCategory()) {
                case BOOLEAN: {
                    final boolean b = ((BooleanObjectInspector)oi).get(o);
                    dos.writeBoolean(b);
                    break;
                }
                case BYTE: {
                    final byte bt = ((ByteObjectInspector)oi).get(o);
                    dos.writeByte(bt);
                    break;
                }
                case SHORT: {
                    final short s = ((ShortObjectInspector)oi).get(o);
                    dos.writeShort(s);
                    break;
                }
                case INT: {
                    final int i = ((IntObjectInspector)oi).get(o);
                    dos.writeInt(i);
                    break;
                }
                case LONG: {
                    final long l = ((LongObjectInspector)oi).get(o);
                    dos.writeLong(l);
                    break;
                }
                case FLOAT: {
                    final float f = ((FloatObjectInspector)oi).get(o);
                    dos.writeFloat(f);
                    break;
                }
                case DOUBLE: {
                    final double d = ((DoubleObjectInspector)oi).get(o);
                    dos.writeDouble(d);
                    break;
                }
                default: {
                    throw new RuntimeException("Hive internal error.");
                }
            }
        }
        finally {
            dos.close();
        }
    }
    
    public static int hashBytes(final byte[] data, final int start, final int len) {
        int hash = 1;
        for (int i = start; i < len; ++i) {
            hash = 31 * hash + data[i];
        }
        return hash;
    }
    
    public static byte[] createByteArray(final BytesWritable sourceBw) {
        return Arrays.copyOf(sourceBw.getBytes(), sourceBw.getLength());
    }
    
    static byte getSeparator(final byte[] separators, final int level) throws SerDeException {
        try {
            return separators[level];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            String msg = "Number of levels of nesting supported for LazySimpleSerde is " + (separators.length - 1) + " Unable to work with level " + level;
            final String txt = ". Use %s serde property for tables using LazySimpleSerde.";
            if (separators.length < 9) {
                msg += String.format(txt, "hive.serialization.extend.nesting.levels");
            }
            else if (separators.length < 25) {
                msg += String.format(txt, "hive.serialization.extend.additional.nesting.levels");
            }
            throw new SerDeException(msg, e);
        }
    }
    
    public static void copyAndEscapeStringDataToText(final byte[] inputBytes, final int start, final int length, final byte escapeChar, final Text data) {
        int outputLength = 0;
        for (int i = 0; i < length; ++i) {
            if (inputBytes[start + i] != escapeChar) {
                ++outputLength;
            }
            else {
                ++outputLength;
                ++i;
            }
        }
        data.set(inputBytes, start, outputLength);
        if (outputLength < length) {
            int k = 0;
            final byte[] outputBytes = data.getBytes();
            for (int j = 0; j < length; ++j) {
                final byte b = inputBytes[start + j];
                if (b != escapeChar || j == length - 1) {
                    outputBytes[k++] = b;
                }
                else {
                    ++j;
                    outputBytes[k++] = inputBytes[start + j];
                }
            }
            assert k == outputLength;
        }
    }
    
    public static byte getByte(final String altValue, final byte defaultVal) {
        if (altValue != null && altValue.length() > 0) {
            try {
                return Byte.valueOf(altValue);
            }
            catch (NumberFormatException e) {
                return (byte)altValue.charAt(0);
            }
        }
        return defaultVal;
    }
    
    private LazyUtils() {
    }
    
    static {
        LazyUtils.trueBytes = new byte[] { 116, 114, 117, 101 };
        LazyUtils.falseBytes = new byte[] { 102, 97, 108, 115, 101 };
    }
}
