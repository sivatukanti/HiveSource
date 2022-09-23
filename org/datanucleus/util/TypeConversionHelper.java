// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import org.datanucleus.ClassConstants;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.sql.Timestamp;
import java.util.Calendar;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.BitSet;

public class TypeConversionHelper
{
    private static final Localiser LOCALISER;
    private static int NR_BIGINTEGER_BYTES;
    private static int NR_SCALE_BYTES;
    private static int NR_SIGNAL_BYTES;
    private static int TOTAL_BYTES;
    private static final String ZEROES = "000000000";
    
    public static boolean[] getBooleanArrayFromBitSet(final BitSet value) {
        if (value == null) {
            return null;
        }
        final boolean[] a = new boolean[value.length()];
        for (int i = 0; i < a.length; ++i) {
            a[i] = value.get(i);
        }
        return a;
    }
    
    public static BitSet getBitSetFromBooleanArray(final boolean[] buf) {
        final BitSet set = new BitSet();
        for (int i = 0; i < buf.length; ++i) {
            if (buf[i]) {
                set.set(i);
            }
        }
        return set;
    }
    
    public static Object getBooleanArrayFromByteArray(final byte[] buf) {
        final int n = buf.length;
        final boolean[] a = new boolean[n];
        for (int i = 0; i < n; ++i) {
            a[i] = (buf[i] != 0);
        }
        return a;
    }
    
    public static byte[] getByteArrayFromBooleanArray(final Object value) {
        if (value == null) {
            return null;
        }
        final boolean[] a = (boolean[])value;
        final int n = a.length;
        final byte[] buf = new byte[n];
        for (int i = 0; i < n; ++i) {
            buf[i] = (byte)(a[i] ? 1 : 0);
        }
        return buf;
    }
    
    public static Object getCharArrayFromByteArray(final byte[] buf) {
        final int n = buf.length / 2;
        final char[] a = new char[n];
        for (int i = 0, j = 0; i < n; a[i++] = (char)(((buf[j++] & 0xFF) << 8) + (buf[j++] & 0xFF))) {}
        return a;
    }
    
    public static byte[] getByteArrayFromCharArray(final Object value) {
        if (value == null) {
            return null;
        }
        final char[] a = (char[])value;
        final int n = a.length;
        final byte[] buf = new byte[n * 2];
        char x;
        for (int i = 0, j = 0; i < n; x = a[i++], buf[j++] = (byte)(x >>> 8 & 0xFF), buf[j++] = (byte)(x & '\u00ff')) {}
        return buf;
    }
    
    public static Object getDoubleArrayFromByteArray(final byte[] buf) {
        final int n = buf.length / 8;
        final double[] a = new double[n];
        for (int i = 0, j = 0; i < n; a[i++] = Double.longBitsToDouble(((long)(buf[j++] & 0xFF) << 56) + ((long)(buf[j++] & 0xFF) << 48) + ((long)(buf[j++] & 0xFF) << 40) + ((long)(buf[j++] & 0xFF) << 32) + ((long)(buf[j++] & 0xFF) << 24) + ((buf[j++] & 0xFF) << 16) + ((buf[j++] & 0xFF) << 8) + (buf[j++] & 0xFF))) {}
        return a;
    }
    
    public static byte[] getByteArrayFromDoubleArray(final Object value) {
        if (value == null) {
            return null;
        }
        final double[] a = (double[])value;
        final int n = a.length;
        final byte[] buf = new byte[n * 8];
        long x;
        for (int i = 0, j = 0; i < n; x = Double.doubleToRawLongBits(a[i++]), buf[j++] = (byte)(x >>> 56 & 0xFFL), buf[j++] = (byte)(x >>> 48 & 0xFFL), buf[j++] = (byte)(x >>> 40 & 0xFFL), buf[j++] = (byte)(x >>> 32 & 0xFFL), buf[j++] = (byte)(x >>> 24 & 0xFFL), buf[j++] = (byte)(x >>> 16 & 0xFFL), buf[j++] = (byte)(x >>> 8 & 0xFFL), buf[j++] = (byte)(x & 0xFFL)) {}
        return buf;
    }
    
    public static Object getFloatArrayFromByteArray(final byte[] buf) {
        final int n = buf.length / 4;
        final float[] a = new float[n];
        for (int i = 0, j = 0; i < n; a[i++] = Float.intBitsToFloat(((buf[j++] & 0xFF) << 24) + ((buf[j++] & 0xFF) << 16) + ((buf[j++] & 0xFF) << 8) + (buf[j++] & 0xFF))) {}
        return a;
    }
    
    public static byte[] getByteArrayFromFloatArray(final Object value) {
        if (value == null) {
            return null;
        }
        final float[] a = (float[])value;
        final int n = a.length;
        final byte[] buf = new byte[n * 4];
        int x;
        for (int i = 0, j = 0; i < n; x = Float.floatToRawIntBits(a[i++]), buf[j++] = (byte)(x >>> 24 & 0xFF), buf[j++] = (byte)(x >>> 16 & 0xFF), buf[j++] = (byte)(x >>> 8 & 0xFF), buf[j++] = (byte)(x & 0xFF)) {}
        return buf;
    }
    
    public static Object getIntArrayFromByteArray(final byte[] buf) {
        final int n = buf.length / 4;
        final int[] a = new int[n];
        for (int i = 0, j = 0; i < n; a[i++] = ((buf[j++] & 0xFF) << 24) + ((buf[j++] & 0xFF) << 16) + ((buf[j++] & 0xFF) << 8) + (buf[j++] & 0xFF)) {}
        return a;
    }
    
    public static byte[] getByteArrayFromIntArray(final Object value) {
        if (value == null) {
            return null;
        }
        final int[] a = (int[])value;
        final int n = a.length;
        final byte[] buf = new byte[n * 4];
        int x;
        for (int i = 0, j = 0; i < n; x = a[i++], buf[j++] = (byte)(x >>> 24 & 0xFF), buf[j++] = (byte)(x >>> 16 & 0xFF), buf[j++] = (byte)(x >>> 8 & 0xFF), buf[j++] = (byte)(x & 0xFF)) {}
        return buf;
    }
    
    public static Object getLongArrayFromByteArray(final byte[] buf) {
        final int n = buf.length / 8;
        final long[] a = new long[n];
        for (int i = 0, j = 0; i < n; a[i++] = ((long)(buf[j++] & 0xFF) << 56) + ((long)(buf[j++] & 0xFF) << 48) + ((long)(buf[j++] & 0xFF) << 40) + ((long)(buf[j++] & 0xFF) << 32) + ((long)(buf[j++] & 0xFF) << 24) + ((buf[j++] & 0xFF) << 16) + ((buf[j++] & 0xFF) << 8) + (buf[j++] & 0xFF)) {}
        return a;
    }
    
    public static byte[] getByteArrayFromLongArray(final Object value) {
        if (value == null) {
            return null;
        }
        final long[] a = (long[])value;
        final int n = a.length;
        final byte[] buf = new byte[n * 8];
        long x;
        for (int i = 0, j = 0; i < n; x = a[i++], buf[j++] = (byte)(x >>> 56 & 0xFFL), buf[j++] = (byte)(x >>> 48 & 0xFFL), buf[j++] = (byte)(x >>> 40 & 0xFFL), buf[j++] = (byte)(x >>> 32 & 0xFFL), buf[j++] = (byte)(x >>> 24 & 0xFFL), buf[j++] = (byte)(x >>> 16 & 0xFFL), buf[j++] = (byte)(x >>> 8 & 0xFFL), buf[j++] = (byte)(x & 0xFFL)) {}
        return buf;
    }
    
    public static Object getShortArrayFromByteArray(final byte[] buf) {
        final int n = buf.length / 2;
        final short[] a = new short[n];
        for (int i = 0, j = 0; i < n; a[i++] = (short)(((buf[j++] & 0xFF) << 8) + (buf[j++] & 0xFF))) {}
        return a;
    }
    
    public static byte[] getByteArrayFromShortArray(final Object value) {
        if (value == null) {
            return null;
        }
        final short[] a = (short[])value;
        final int n = a.length;
        final byte[] buf = new byte[n * 2];
        short x;
        for (int i = 0, j = 0; i < n; x = a[i++], buf[j++] = (byte)(x >>> 8 & 0xFF), buf[j++] = (byte)(x & 0xFF)) {}
        return buf;
    }
    
    public static byte[] getByteArrayFromBigDecimalArray(final Object value) {
        if (value == null) {
            return null;
        }
        final BigDecimal[] a = (BigDecimal[])value;
        final byte[] total = new byte[a.length * TypeConversionHelper.TOTAL_BYTES];
        int index = 0;
        for (int i = 0; i < a.length; ++i) {
            System.arraycopy(new byte[] { (byte)a[i].signum() }, 0, total, index, TypeConversionHelper.NR_SIGNAL_BYTES);
            index += TypeConversionHelper.NR_SIGNAL_BYTES;
            final byte[] b = a[i].unscaledValue().abs().toByteArray();
            System.arraycopy(b, 0, total, index + (TypeConversionHelper.NR_BIGINTEGER_BYTES - b.length), b.length);
            index += TypeConversionHelper.NR_BIGINTEGER_BYTES;
            final byte[] s = getByteArrayFromIntArray(new int[] { a[i].scale() });
            System.arraycopy(s, 0, total, index, TypeConversionHelper.NR_SCALE_BYTES);
            index += TypeConversionHelper.NR_SCALE_BYTES;
        }
        return total;
    }
    
    public static Object getBigDecimalArrayFromByteArray(final byte[] buf) {
        final BigDecimal[] a = new BigDecimal[buf.length / TypeConversionHelper.TOTAL_BYTES];
        int index = 0;
        for (int i = 0; i < a.length; ++i) {
            final byte[] signal = new byte[TypeConversionHelper.NR_SIGNAL_BYTES];
            System.arraycopy(buf, index, signal, 0, TypeConversionHelper.NR_SIGNAL_BYTES);
            index += TypeConversionHelper.NR_SIGNAL_BYTES;
            final byte[] b = new byte[TypeConversionHelper.NR_BIGINTEGER_BYTES];
            System.arraycopy(buf, index, b, 0, TypeConversionHelper.NR_BIGINTEGER_BYTES);
            final BigInteger integer = new BigInteger(signal[0], b);
            index += TypeConversionHelper.NR_BIGINTEGER_BYTES;
            final byte[] s = new byte[4];
            System.arraycopy(buf, index, s, 0, TypeConversionHelper.NR_SCALE_BYTES);
            final int[] scale = (int[])getIntArrayFromByteArray(s);
            a[i] = new BigDecimal(integer, scale[0]);
            index += TypeConversionHelper.NR_SCALE_BYTES;
        }
        return a;
    }
    
    public static byte[] getByteArrayFromBigIntegerArray(final Object value) {
        if (value == null) {
            return null;
        }
        final BigInteger[] a = (BigInteger[])value;
        final long[] d = new long[a.length];
        for (int i = 0; i < a.length; ++i) {
            d[i] = a[i].longValue();
        }
        return getByteArrayFromLongArray(d);
    }
    
    public static Object getBigIntegerArrayFromByteArray(final byte[] buf) {
        final long[] d = (long[])getLongArrayFromByteArray(buf);
        final BigInteger[] a = new BigInteger[d.length];
        for (int i = 0; i < a.length; ++i) {
            a[i] = BigInteger.valueOf(d[i]);
        }
        return a;
    }
    
    public static byte[] getByteArrayFromBooleanObjectArray(final Object value) {
        if (value == null) {
            return null;
        }
        final Boolean[] a = (Boolean[])value;
        final boolean[] d = new boolean[a.length];
        for (int i = 0; i < a.length; ++i) {
            d[i] = a[i];
        }
        return getByteArrayFromBooleanArray(d);
    }
    
    public static Object getBooleanObjectArrayFromByteArray(final byte[] buf) {
        final boolean[] d = (boolean[])getBooleanArrayFromByteArray(buf);
        final Boolean[] a = new Boolean[d.length];
        for (int i = 0; i < a.length; ++i) {
            a[i] = d[i];
        }
        return a;
    }
    
    public static byte[] getByteArrayFromByteObjectArray(final Object value) {
        if (value == null) {
            return null;
        }
        final Byte[] a = (Byte[])value;
        final byte[] d = new byte[a.length];
        for (int i = 0; i < a.length; ++i) {
            d[i] = a[i];
        }
        return d;
    }
    
    public static Object getByteObjectArrayFromByteArray(final byte[] buf) {
        if (buf == null) {
            return null;
        }
        final Byte[] a = new Byte[buf.length];
        for (int i = 0; i < a.length; ++i) {
            a[i] = buf[i];
        }
        return a;
    }
    
    public static byte[] getByteArrayFromCharObjectArray(final Object value) {
        if (value == null) {
            return null;
        }
        final Character[] a = (Character[])value;
        final char[] d = new char[a.length];
        for (int i = 0; i < a.length; ++i) {
            d[i] = a[i];
        }
        return getByteArrayFromCharArray(d);
    }
    
    public static Object getCharObjectArrayFromByteArray(final byte[] buf) {
        final char[] d = (char[])getCharArrayFromByteArray(buf);
        final Character[] a = new Character[d.length];
        for (int i = 0; i < a.length; ++i) {
            a[i] = d[i];
        }
        return a;
    }
    
    public static byte[] getByteArrayFromDoubleObjectArray(final Object value) {
        if (value == null) {
            return null;
        }
        final Double[] a = (Double[])value;
        final double[] d = new double[a.length];
        for (int i = 0; i < a.length; ++i) {
            d[i] = a[i];
        }
        return getByteArrayFromDoubleArray(d);
    }
    
    public static Object getDoubleObjectArrayFromByteArray(final byte[] buf) {
        final double[] d = (double[])getDoubleArrayFromByteArray(buf);
        final Double[] a = new Double[d.length];
        for (int i = 0; i < a.length; ++i) {
            a[i] = new Double(d[i]);
        }
        return a;
    }
    
    public static byte[] getByteArrayFromFloatObjectArray(final Object value) {
        if (value == null) {
            return null;
        }
        final Float[] a = (Float[])value;
        final float[] d = new float[a.length];
        for (int i = 0; i < a.length; ++i) {
            d[i] = a[i];
        }
        return getByteArrayFromFloatArray(d);
    }
    
    public static Object getFloatObjectArrayFromByteArray(final byte[] buf) {
        final float[] d = (float[])getFloatArrayFromByteArray(buf);
        final Float[] a = new Float[d.length];
        for (int i = 0; i < a.length; ++i) {
            a[i] = new Float(d[i]);
        }
        return a;
    }
    
    public static byte[] getByteArrayFromIntObjectArray(final Object value) {
        if (value == null) {
            return null;
        }
        final Integer[] a = (Integer[])value;
        final int[] d = new int[a.length];
        for (int i = 0; i < a.length; ++i) {
            d[i] = a[i];
        }
        return getByteArrayFromIntArray(d);
    }
    
    public static Object getIntObjectArrayFromByteArray(final byte[] buf) {
        final int[] d = (int[])getIntArrayFromByteArray(buf);
        final Integer[] a = new Integer[d.length];
        for (int i = 0; i < a.length; ++i) {
            a[i] = d[i];
        }
        return a;
    }
    
    public static byte[] getByteArrayFromLongObjectArray(final Object value) {
        if (value == null) {
            return null;
        }
        final Long[] a = (Long[])value;
        final long[] d = new long[a.length];
        for (int i = 0; i < a.length; ++i) {
            d[i] = a[i];
        }
        return getByteArrayFromLongArray(d);
    }
    
    public static Object getLongObjectArrayFromByteArray(final byte[] buf) {
        final long[] d = (long[])getLongArrayFromByteArray(buf);
        final Long[] a = new Long[d.length];
        for (int i = 0; i < a.length; ++i) {
            a[i] = d[i];
        }
        return a;
    }
    
    public static byte[] getByteArrayFromShortObjectArray(final Object value) {
        if (value == null) {
            return null;
        }
        final Short[] a = (Short[])value;
        final short[] d = new short[a.length];
        for (int i = 0; i < a.length; ++i) {
            d[i] = a[i];
        }
        return getByteArrayFromShortArray(d);
    }
    
    public static Object getShortObjectArrayFromByteArray(final byte[] buf) {
        final short[] d = (short[])getShortArrayFromByteArray(buf);
        final Short[] a = new Short[d.length];
        for (int i = 0; i < a.length; ++i) {
            a[i] = d[i];
        }
        return a;
    }
    
    public static Object convertTo(final Object value, final Class type) {
        if (type.isAssignableFrom(value.getClass())) {
            return value;
        }
        if (type == Short.TYPE || type == Short.class) {
            return Short.valueOf(value.toString());
        }
        if (type == Character.TYPE || type == Character.class) {
            return value.toString().charAt(0);
        }
        if (type == Integer.TYPE || type == Integer.class) {
            return Integer.valueOf(value.toString());
        }
        if (type == Long.TYPE || type == Long.class) {
            return Long.valueOf(value.toString());
        }
        if (type == Boolean.TYPE || type == Boolean.class) {
            return Boolean.valueOf(value.toString());
        }
        if (type == Byte.TYPE || type == Byte.class) {
            return Byte.valueOf(value.toString());
        }
        if (type == Float.TYPE || type == Float.class) {
            return Float.valueOf(value.toString());
        }
        if (type == Double.TYPE || type == Double.class) {
            return Double.valueOf(value.toString());
        }
        if (type == BigDecimal.class) {
            return new BigDecimal(value.toString());
        }
        if (type == BigInteger.class) {
            return new BigInteger(value.toString());
        }
        if (type == String.class) {
            return value.toString();
        }
        return null;
    }
    
    public static byte[] getBytesFromInt(int val) {
        final byte[] arr = new byte[4];
        for (int i = 3; i >= 0; --i) {
            arr[i] = (byte)((0xFFL & (long)val) - 128L);
            val >>>= 8;
        }
        return arr;
    }
    
    public static byte[] getBytesFromShort(short val) {
        final byte[] arr = new byte[2];
        for (int i = 1; i >= 0; --i) {
            arr[i] = (byte)((0xFFL & (long)val) - 128L);
            val >>>= 8;
        }
        return arr;
    }
    
    public static String getStringFromInt(int val) {
        final byte[] arr = new byte[4];
        for (int i = 3; i >= 0; --i) {
            arr[i] = (byte)((0xFFL & (long)val) - 128L);
            val >>>= 8;
        }
        return new String(arr);
    }
    
    public static String getStringFromShort(short val) {
        final byte[] arr = new byte[2];
        for (int i = 1; i >= 0; --i) {
            arr[i] = (byte)((0xFFL & (long)val) - 128L);
            val >>>= 8;
        }
        return new String(arr);
    }
    
    public static String getHexFromInt(final int val) {
        final StringBuilder str = new StringBuilder("00000000");
        final String hexstr = Integer.toHexString(val);
        str.replace(8 - hexstr.length(), 8, hexstr);
        return str.toString();
    }
    
    public static String getHexFromShort(final short val) {
        final StringBuilder str = new StringBuilder("0000");
        final String hexstr = Integer.toHexString(val);
        str.replace(4 - hexstr.length(), 4, hexstr);
        return str.toString();
    }
    
    public static int getIntFromByteArray(final byte[] bytes) {
        int val = 0;
        for (int i = 0; i < 4; ++i) {
            val = (val << 8) + 128 + bytes[i];
        }
        return val;
    }
    
    public static Timestamp stringToTimestamp(final String s, final Calendar cal) {
        final int[] numbers = convertStringToIntArray(s);
        if (numbers == null || numbers.length < 6) {
            throw new IllegalArgumentException(TypeConversionHelper.LOCALISER.msg("030003", s));
        }
        final int year = numbers[0];
        final int month = numbers[1];
        final int day = numbers[2];
        final int hour = numbers[3];
        final int minute = numbers[4];
        final int second = numbers[5];
        int nanos = 0;
        if (numbers.length > 6) {
            nanos = numbers[6];
        }
        Calendar thecal;
        if ((thecal = cal) == null) {
            thecal = new GregorianCalendar();
        }
        thecal.set(0, 1);
        thecal.set(1, year);
        thecal.set(2, month - 1);
        thecal.set(5, day);
        thecal.set(11, hour);
        thecal.set(12, minute);
        thecal.set(13, second);
        final Timestamp ts = new Timestamp(thecal.getTime().getTime());
        ts.setNanos(nanos);
        return ts;
    }
    
    private static int[] convertStringToIntArray(final String str) {
        if (str == null) {
            return null;
        }
        int[] values = null;
        final ArrayList list = new ArrayList();
        int start = -1;
        for (int i = 0; i < str.length(); ++i) {
            if (start == -1 && Character.isDigit(str.charAt(i))) {
                start = i;
            }
            if (start != i && start >= 0 && !Character.isDigit(str.charAt(i))) {
                list.add(Integer.valueOf(str.substring(start, i)));
                start = -1;
            }
        }
        if (list.size() > 0) {
            values = new int[list.size()];
            final Iterator iter = list.iterator();
            int n = 0;
            while (iter.hasNext()) {
                values[n++] = iter.next();
            }
        }
        return values;
    }
    
    public static String timestampToString(final Timestamp ts, final Calendar cal) {
        cal.setTime(ts);
        final int year = cal.get(1);
        final int month = cal.get(2) + 1;
        final int day = cal.get(5);
        final int hour = cal.get(11);
        final int minute = cal.get(12);
        final int second = cal.get(13);
        final String yearString = Integer.toString(year);
        final String monthString = (month < 10) ? ("0" + month) : Integer.toString(month);
        final String dayString = (day < 10) ? ("0" + day) : Integer.toString(day);
        final String hourString = (hour < 10) ? ("0" + hour) : Integer.toString(hour);
        final String minuteString = (minute < 10) ? ("0" + minute) : Integer.toString(minute);
        final String secondString = (second < 10) ? ("0" + second) : Integer.toString(second);
        String nanosString = Integer.toString(ts.getNanos());
        if (ts.getNanos() != 0) {
            int truncIndex;
            for (nanosString = "000000000".substring(0, "000000000".length() - nanosString.length()) + nanosString, truncIndex = nanosString.length() - 1; nanosString.charAt(truncIndex) == '0'; --truncIndex) {}
            nanosString = nanosString.substring(0, truncIndex + 1);
        }
        return yearString + "-" + monthString + "-" + dayString + " " + hourString + ":" + minuteString + ":" + secondString + "." + nanosString;
    }
    
    public static int intFromString(final String str, final int dflt) {
        try {
            final Integer val = Integer.valueOf(str);
            return val;
        }
        catch (NumberFormatException nfe) {
            return dflt;
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        TypeConversionHelper.NR_BIGINTEGER_BYTES = 40;
        TypeConversionHelper.NR_SCALE_BYTES = 4;
        TypeConversionHelper.NR_SIGNAL_BYTES = 1;
        TypeConversionHelper.TOTAL_BYTES = TypeConversionHelper.NR_BIGINTEGER_BYTES + TypeConversionHelper.NR_SCALE_BYTES + TypeConversionHelper.NR_SIGNAL_BYTES;
    }
}
