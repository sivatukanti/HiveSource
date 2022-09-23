// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.convert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.net.UnknownHostException;
import org.apache.commons.lang3.StringUtils;
import java.net.MalformedURLException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.configuration2.ex.ConversionException;
import java.net.InetAddress;
import java.awt.Color;
import java.util.Locale;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.math.BigDecimal;
import java.math.BigInteger;

final class PropertyConverter
{
    private static final String HEX_PREFIX = "0x";
    private static final int HEX_RADIX = 16;
    private static final String BIN_PREFIX = "0b";
    private static final int BIN_RADIX = 2;
    private static final Class<?>[] CONSTR_ARGS;
    private static final String INTERNET_ADDRESS_CLASSNAME = "javax.mail.internet.InternetAddress";
    
    private PropertyConverter() {
    }
    
    public static Object to(final Class<?> cls, final Object value, final DefaultConversionHandler convHandler) throws ConversionException {
        if (cls.isInstance(value)) {
            return value;
        }
        if (String.class.equals(cls)) {
            return String.valueOf(value);
        }
        if (Boolean.class.equals(cls) || Boolean.TYPE.equals(cls)) {
            return toBoolean(value);
        }
        if (Character.class.equals(cls) || Character.TYPE.equals(cls)) {
            return toCharacter(value);
        }
        if (Number.class.isAssignableFrom(cls) || cls.isPrimitive()) {
            if (Integer.class.equals(cls) || Integer.TYPE.equals(cls)) {
                return toInteger(value);
            }
            if (Long.class.equals(cls) || Long.TYPE.equals(cls)) {
                return toLong(value);
            }
            if (Byte.class.equals(cls) || Byte.TYPE.equals(cls)) {
                return toByte(value);
            }
            if (Short.class.equals(cls) || Short.TYPE.equals(cls)) {
                return toShort(value);
            }
            if (Float.class.equals(cls) || Float.TYPE.equals(cls)) {
                return toFloat(value);
            }
            if (Double.class.equals(cls) || Double.TYPE.equals(cls)) {
                return toDouble(value);
            }
            if (BigInteger.class.equals(cls)) {
                return toBigInteger(value);
            }
            if (BigDecimal.class.equals(cls)) {
                return toBigDecimal(value);
            }
        }
        else {
            if (Date.class.equals(cls)) {
                return toDate(value, convHandler.getDateFormat());
            }
            if (Calendar.class.equals(cls)) {
                return toCalendar(value, convHandler.getDateFormat());
            }
            if (URL.class.equals(cls)) {
                return toURL(value);
            }
            if (Locale.class.equals(cls)) {
                return toLocale(value);
            }
            if (isEnum(cls)) {
                return convertToEnum(cls, value);
            }
            if (Color.class.equals(cls)) {
                return toColor(value);
            }
            if (cls.getName().equals("javax.mail.internet.InternetAddress")) {
                return toInternetAddress(value);
            }
            if (InetAddress.class.isAssignableFrom(cls)) {
                return toInetAddress(value);
            }
        }
        throw new ConversionException("The value '" + value + "' (" + value.getClass() + ") can't be converted to a " + cls.getName() + " object");
    }
    
    public static Boolean toBoolean(final Object value) throws ConversionException {
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        if (!(value instanceof String)) {
            throw new ConversionException("The value " + value + " can't be converted to a Boolean object");
        }
        final Boolean b = BooleanUtils.toBooleanObject((String)value);
        if (b == null) {
            throw new ConversionException("The value " + value + " can't be converted to a Boolean object");
        }
        return b;
    }
    
    public static Character toCharacter(final Object value) throws ConversionException {
        final String strValue = String.valueOf(value);
        if (strValue.length() == 1) {
            return strValue.charAt(0);
        }
        throw new ConversionException(String.format("The value '%s' cannot be converted to a Character object!", strValue));
    }
    
    public static Byte toByte(final Object value) throws ConversionException {
        final Number n = toNumber(value, Byte.class);
        if (n instanceof Byte) {
            return (Byte)n;
        }
        return n.byteValue();
    }
    
    public static Short toShort(final Object value) throws ConversionException {
        final Number n = toNumber(value, Short.class);
        if (n instanceof Short) {
            return (Short)n;
        }
        return n.shortValue();
    }
    
    public static Integer toInteger(final Object value) throws ConversionException {
        final Number n = toNumber(value, Integer.class);
        if (n instanceof Integer) {
            return (Integer)n;
        }
        return n.intValue();
    }
    
    public static Long toLong(final Object value) throws ConversionException {
        final Number n = toNumber(value, Long.class);
        if (n instanceof Long) {
            return (Long)n;
        }
        return n.longValue();
    }
    
    public static Float toFloat(final Object value) throws ConversionException {
        final Number n = toNumber(value, Float.class);
        if (n instanceof Float) {
            return (Float)n;
        }
        return new Float(n.floatValue());
    }
    
    public static Double toDouble(final Object value) throws ConversionException {
        final Number n = toNumber(value, Double.class);
        if (n instanceof Double) {
            return (Double)n;
        }
        return new Double(n.doubleValue());
    }
    
    public static BigInteger toBigInteger(final Object value) throws ConversionException {
        final Number n = toNumber(value, BigInteger.class);
        if (n instanceof BigInteger) {
            return (BigInteger)n;
        }
        return BigInteger.valueOf(n.longValue());
    }
    
    public static BigDecimal toBigDecimal(final Object value) throws ConversionException {
        final Number n = toNumber(value, BigDecimal.class);
        if (n instanceof BigDecimal) {
            return (BigDecimal)n;
        }
        return new BigDecimal(n.doubleValue());
    }
    
    static Number toNumber(final Object value, final Class<?> targetClass) throws ConversionException {
        if (value instanceof Number) {
            return (Number)value;
        }
        final String str = value.toString();
        if (str.startsWith("0x")) {
            try {
                return new BigInteger(str.substring("0x".length()), 16);
            }
            catch (NumberFormatException nex) {
                throw new ConversionException("Could not convert " + str + " to " + targetClass.getName() + "! Invalid hex number.", nex);
            }
        }
        if (str.startsWith("0b")) {
            try {
                return new BigInteger(str.substring("0b".length()), 2);
            }
            catch (NumberFormatException nex) {
                throw new ConversionException("Could not convert " + str + " to " + targetClass.getName() + "! Invalid binary number.", nex);
            }
        }
        try {
            final Constructor<?> constr = targetClass.getConstructor(PropertyConverter.CONSTR_ARGS);
            return (Number)constr.newInstance(str);
        }
        catch (InvocationTargetException itex) {
            throw new ConversionException("Could not convert " + str + " to " + targetClass.getName(), itex.getTargetException());
        }
        catch (Exception ex) {
            throw new ConversionException("Conversion error when trying to convert " + str + " to " + targetClass.getName(), ex);
        }
    }
    
    public static URL toURL(final Object value) throws ConversionException {
        if (value instanceof URL) {
            return (URL)value;
        }
        if (value instanceof String) {
            try {
                return new URL((String)value);
            }
            catch (MalformedURLException e) {
                throw new ConversionException("The value " + value + " can't be converted to an URL", e);
            }
        }
        throw new ConversionException("The value " + value + " can't be converted to an URL");
    }
    
    public static Locale toLocale(final Object value) throws ConversionException {
        if (value instanceof Locale) {
            return (Locale)value;
        }
        if (!(value instanceof String)) {
            throw new ConversionException("The value " + value + " can't be converted to a Locale");
        }
        final String[] elements = ((String)value).split("_");
        final int size = elements.length;
        if (size >= 1 && (elements[0].length() == 2 || elements[0].length() == 0)) {
            final String language = elements[0];
            final String country = (size >= 2) ? elements[1] : "";
            final String variant = (size >= 3) ? elements[2] : "";
            return new Locale(language, country, variant);
        }
        throw new ConversionException("The value " + value + " can't be converted to a Locale");
    }
    
    public static Color toColor(final Object value) throws ConversionException {
        if (value instanceof Color) {
            return (Color)value;
        }
        if (value instanceof String && !StringUtils.isBlank((CharSequence)value)) {
            String color = ((String)value).trim();
            final int[] components = new int[3];
            final int minlength = components.length * 2;
            if (color.length() < minlength) {
                throw new ConversionException("The value " + value + " can't be converted to a Color");
            }
            if (color.startsWith("#")) {
                color = color.substring(1);
            }
            try {
                for (int i = 0; i < components.length; ++i) {
                    components[i] = Integer.parseInt(color.substring(2 * i, 2 * i + 2), 16);
                }
                int alpha;
                if (color.length() >= minlength + 2) {
                    alpha = Integer.parseInt(color.substring(minlength, minlength + 2), 16);
                }
                else {
                    alpha = Color.black.getAlpha();
                }
                return new Color(components[0], components[1], components[2], alpha);
            }
            catch (Exception e) {
                throw new ConversionException("The value " + value + " can't be converted to a Color", e);
            }
        }
        throw new ConversionException("The value " + value + " can't be converted to a Color");
    }
    
    static InetAddress toInetAddress(final Object value) throws ConversionException {
        if (value instanceof InetAddress) {
            return (InetAddress)value;
        }
        if (value instanceof String) {
            try {
                return InetAddress.getByName((String)value);
            }
            catch (UnknownHostException e) {
                throw new ConversionException("The value " + value + " can't be converted to a InetAddress", e);
            }
        }
        throw new ConversionException("The value " + value + " can't be converted to a InetAddress");
    }
    
    static Object toInternetAddress(final Object value) throws ConversionException {
        if (value.getClass().getName().equals("javax.mail.internet.InternetAddress")) {
            return value;
        }
        if (value instanceof String) {
            try {
                final Constructor<?> ctor = Class.forName("javax.mail.internet.InternetAddress").getConstructor(String.class);
                return ctor.newInstance(value);
            }
            catch (Exception e) {
                throw new ConversionException("The value " + value + " can't be converted to a InternetAddress", e);
            }
        }
        throw new ConversionException("The value " + value + " can't be converted to a InternetAddress");
    }
    
    static boolean isEnum(final Class<?> cls) {
        return cls.isEnum();
    }
    
    static <E extends Enum<E>> E toEnum(final Object value, final Class<E> cls) throws ConversionException {
        if (value.getClass().equals(cls)) {
            return cls.cast(value);
        }
        if (value instanceof String) {
            try {
                return Enum.valueOf(cls, (String)value);
            }
            catch (Exception e) {
                throw new ConversionException("The value " + value + " can't be converted to a " + cls.getName());
            }
        }
        if (value instanceof Number) {
            try {
                final E[] enumConstants = cls.getEnumConstants();
                return enumConstants[((Number)value).intValue()];
            }
            catch (Exception e) {
                throw new ConversionException("The value " + value + " can't be converted to a " + cls.getName());
            }
        }
        throw new ConversionException("The value " + value + " can't be converted to a " + cls.getName());
    }
    
    public static Date toDate(final Object value, final String format) throws ConversionException {
        if (value instanceof Date) {
            return (Date)value;
        }
        if (value instanceof Calendar) {
            return ((Calendar)value).getTime();
        }
        if (value instanceof String) {
            try {
                return new SimpleDateFormat(format).parse((String)value);
            }
            catch (ParseException e) {
                throw new ConversionException("The value " + value + " can't be converted to a Date", e);
            }
        }
        throw new ConversionException("The value " + value + " can't be converted to a Date");
    }
    
    public static Calendar toCalendar(final Object value, final String format) throws ConversionException {
        if (value instanceof Calendar) {
            return (Calendar)value;
        }
        if (value instanceof Date) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime((Date)value);
            return calendar;
        }
        if (value instanceof String) {
            try {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(new SimpleDateFormat(format).parse((String)value));
                return calendar;
            }
            catch (ParseException e) {
                throw new ConversionException("The value " + value + " can't be converted to a Calendar", e);
            }
        }
        throw new ConversionException("The value " + value + " can't be converted to a Calendar");
    }
    
    private static Object convertToEnum(final Class<?> enumClass, final Object value) {
        return toEnum(value, (Class<Object>)enumClass.asSubclass(Enum.class));
    }
    
    static {
        CONSTR_ARGS = new Class[] { String.class };
    }
}
