// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.configuration2.convert.DefaultConversionHandler;
import java.awt.Color;
import java.util.Locale;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Date;
import java.net.URL;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.apache.commons.configuration2.convert.ConversionHandler;

public class DataConfiguration extends AbstractConfiguration
{
    public static final String DATE_FORMAT_KEY = "org.apache.commons.configuration.format.date";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final ThreadLocal<String> TEMP_DATE_FORMAT;
    private final Configuration configuration;
    private final ConversionHandler dataConversionHandler;
    
    public DataConfiguration(final Configuration configuration) {
        this.configuration = configuration;
        this.dataConversionHandler = new DataConversionHandler();
    }
    
    public Configuration getConfiguration() {
        return this.configuration;
    }
    
    @Override
    public ConversionHandler getConversionHandler() {
        return this.dataConversionHandler;
    }
    
    @Override
    protected Object getPropertyInternal(final String key) {
        return this.configuration.getProperty(key);
    }
    
    @Override
    protected void addPropertyInternal(final String key, final Object obj) {
        this.configuration.addProperty(key, obj);
    }
    
    @Override
    protected void addPropertyDirect(final String key, final Object value) {
        if (this.configuration instanceof AbstractConfiguration) {
            ((AbstractConfiguration)this.configuration).addPropertyDirect(key, value);
        }
        else {
            this.configuration.addProperty(key, value);
        }
    }
    
    @Override
    protected boolean isEmptyInternal() {
        return this.configuration.isEmpty();
    }
    
    @Override
    protected boolean containsKeyInternal(final String key) {
        return this.configuration.containsKey(key);
    }
    
    @Override
    protected void clearPropertyDirect(final String key) {
        this.configuration.clearProperty(key);
    }
    
    @Override
    protected void setPropertyInternal(final String key, final Object value) {
        this.configuration.setProperty(key, value);
    }
    
    @Override
    protected Iterator<String> getKeysInternal() {
        return this.configuration.getKeys();
    }
    
    public List<Boolean> getBooleanList(final String key) {
        return this.getBooleanList(key, new ArrayList<Boolean>());
    }
    
    public List<Boolean> getBooleanList(final String key, final List<Boolean> defaultValue) {
        return this.getList(Boolean.class, key, defaultValue);
    }
    
    public boolean[] getBooleanArray(final String key) {
        return (boolean[])this.getArray(Boolean.TYPE, key);
    }
    
    public boolean[] getBooleanArray(final String key, final boolean[] defaultValue) {
        return this.get(boolean[].class, key, defaultValue);
    }
    
    public List<Byte> getByteList(final String key) {
        return this.getByteList(key, new ArrayList<Byte>());
    }
    
    public List<Byte> getByteList(final String key, final List<Byte> defaultValue) {
        return this.getList(Byte.class, key, defaultValue);
    }
    
    public byte[] getByteArray(final String key) {
        return this.getByteArray(key, new byte[0]);
    }
    
    public byte[] getByteArray(final String key, final byte[] defaultValue) {
        return this.get(byte[].class, key, defaultValue);
    }
    
    public List<Short> getShortList(final String key) {
        return this.getShortList(key, new ArrayList<Short>());
    }
    
    public List<Short> getShortList(final String key, final List<Short> defaultValue) {
        return this.getList(Short.class, key, defaultValue);
    }
    
    public short[] getShortArray(final String key) {
        return this.getShortArray(key, new short[0]);
    }
    
    public short[] getShortArray(final String key, final short[] defaultValue) {
        return this.get(short[].class, key, defaultValue);
    }
    
    public List<Integer> getIntegerList(final String key) {
        return this.getIntegerList(key, new ArrayList<Integer>());
    }
    
    public List<Integer> getIntegerList(final String key, final List<Integer> defaultValue) {
        return this.getList(Integer.class, key, defaultValue);
    }
    
    public int[] getIntArray(final String key) {
        return this.getIntArray(key, new int[0]);
    }
    
    public int[] getIntArray(final String key, final int[] defaultValue) {
        return this.get(int[].class, key, defaultValue);
    }
    
    public List<Long> getLongList(final String key) {
        return this.getLongList(key, new ArrayList<Long>());
    }
    
    public List<Long> getLongList(final String key, final List<Long> defaultValue) {
        return this.getList(Long.class, key, defaultValue);
    }
    
    public long[] getLongArray(final String key) {
        return this.getLongArray(key, new long[0]);
    }
    
    public long[] getLongArray(final String key, final long[] defaultValue) {
        return this.get(long[].class, key, defaultValue);
    }
    
    public List<Float> getFloatList(final String key) {
        return this.getFloatList(key, new ArrayList<Float>());
    }
    
    public List<Float> getFloatList(final String key, final List<Float> defaultValue) {
        return this.getList(Float.class, key, defaultValue);
    }
    
    public float[] getFloatArray(final String key) {
        return this.getFloatArray(key, new float[0]);
    }
    
    public float[] getFloatArray(final String key, final float[] defaultValue) {
        return this.get(float[].class, key, defaultValue);
    }
    
    public List<Double> getDoubleList(final String key) {
        return this.getDoubleList(key, new ArrayList<Double>());
    }
    
    public List<Double> getDoubleList(final String key, final List<Double> defaultValue) {
        return this.getList(Double.class, key, defaultValue);
    }
    
    public double[] getDoubleArray(final String key) {
        return this.getDoubleArray(key, new double[0]);
    }
    
    public double[] getDoubleArray(final String key, final double[] defaultValue) {
        return this.get(double[].class, key, defaultValue);
    }
    
    public List<BigInteger> getBigIntegerList(final String key) {
        return this.getBigIntegerList(key, new ArrayList<BigInteger>());
    }
    
    public List<BigInteger> getBigIntegerList(final String key, final List<BigInteger> defaultValue) {
        return this.getList(BigInteger.class, key, defaultValue);
    }
    
    public BigInteger[] getBigIntegerArray(final String key) {
        return this.getBigIntegerArray(key, new BigInteger[0]);
    }
    
    public BigInteger[] getBigIntegerArray(final String key, final BigInteger[] defaultValue) {
        return this.get(BigInteger[].class, key, defaultValue);
    }
    
    public List<BigDecimal> getBigDecimalList(final String key) {
        return this.getBigDecimalList(key, new ArrayList<BigDecimal>());
    }
    
    public List<BigDecimal> getBigDecimalList(final String key, final List<BigDecimal> defaultValue) {
        return this.getList(BigDecimal.class, key, defaultValue);
    }
    
    public BigDecimal[] getBigDecimalArray(final String key) {
        return this.getBigDecimalArray(key, new BigDecimal[0]);
    }
    
    public BigDecimal[] getBigDecimalArray(final String key, final BigDecimal[] defaultValue) {
        return this.get(BigDecimal[].class, key, defaultValue);
    }
    
    public URL getURL(final String key) {
        return this.get(URL.class, key);
    }
    
    public URL getURL(final String key, final URL defaultValue) {
        return this.get(URL.class, key, defaultValue);
    }
    
    public List<URL> getURLList(final String key) {
        return this.getURLList(key, new ArrayList<URL>());
    }
    
    public List<URL> getURLList(final String key, final List<URL> defaultValue) {
        return this.getList(URL.class, key, defaultValue);
    }
    
    public URL[] getURLArray(final String key) {
        return this.getURLArray(key, new URL[0]);
    }
    
    public URL[] getURLArray(final String key, final URL[] defaultValue) {
        return this.get(URL[].class, key, defaultValue);
    }
    
    public Date getDate(final String key) {
        return this.get(Date.class, key);
    }
    
    public Date getDate(final String key, final String format) {
        final Date value = this.getDate(key, null, format);
        if (value != null) {
            return value;
        }
        if (this.isThrowExceptionOnMissing()) {
            throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
        }
        return null;
    }
    
    public Date getDate(final String key, final Date defaultValue) {
        return this.getDate(key, defaultValue, null);
    }
    
    public Date getDate(final String key, final Date defaultValue, final String format) {
        DataConfiguration.TEMP_DATE_FORMAT.set(format);
        try {
            return this.get(Date.class, key, defaultValue);
        }
        finally {
            DataConfiguration.TEMP_DATE_FORMAT.remove();
        }
    }
    
    public List<Date> getDateList(final String key) {
        return this.getDateList(key, new ArrayList<Date>());
    }
    
    public List<Date> getDateList(final String key, final String format) {
        return this.getDateList(key, new ArrayList<Date>(), format);
    }
    
    public List<Date> getDateList(final String key, final List<Date> defaultValue) {
        return this.getDateList(key, defaultValue, null);
    }
    
    public List<Date> getDateList(final String key, final List<Date> defaultValue, final String format) {
        DataConfiguration.TEMP_DATE_FORMAT.set(format);
        try {
            return this.getList(Date.class, key, defaultValue);
        }
        finally {
            DataConfiguration.TEMP_DATE_FORMAT.remove();
        }
    }
    
    public Date[] getDateArray(final String key) {
        return this.getDateArray(key, new Date[0]);
    }
    
    public Date[] getDateArray(final String key, final String format) {
        return this.getDateArray(key, new Date[0], format);
    }
    
    public Date[] getDateArray(final String key, final Date[] defaultValue) {
        return this.getDateArray(key, defaultValue, null);
    }
    
    public Date[] getDateArray(final String key, final Date[] defaultValue, final String format) {
        DataConfiguration.TEMP_DATE_FORMAT.set(format);
        try {
            return this.get(Date[].class, key, defaultValue);
        }
        finally {
            DataConfiguration.TEMP_DATE_FORMAT.remove();
        }
    }
    
    public Calendar getCalendar(final String key) {
        return this.get(Calendar.class, key);
    }
    
    public Calendar getCalendar(final String key, final String format) {
        final Calendar value = this.getCalendar(key, null, format);
        if (value != null) {
            return value;
        }
        if (this.isThrowExceptionOnMissing()) {
            throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
        }
        return null;
    }
    
    public Calendar getCalendar(final String key, final Calendar defaultValue) {
        return this.getCalendar(key, defaultValue, null);
    }
    
    public Calendar getCalendar(final String key, final Calendar defaultValue, final String format) {
        DataConfiguration.TEMP_DATE_FORMAT.set(format);
        try {
            return this.get(Calendar.class, key, defaultValue);
        }
        finally {
            DataConfiguration.TEMP_DATE_FORMAT.remove();
        }
    }
    
    public List<Calendar> getCalendarList(final String key) {
        return this.getCalendarList(key, new ArrayList<Calendar>());
    }
    
    public List<Calendar> getCalendarList(final String key, final String format) {
        return this.getCalendarList(key, new ArrayList<Calendar>(), format);
    }
    
    public List<Calendar> getCalendarList(final String key, final List<Calendar> defaultValue) {
        return this.getCalendarList(key, defaultValue, null);
    }
    
    public List<Calendar> getCalendarList(final String key, final List<Calendar> defaultValue, final String format) {
        DataConfiguration.TEMP_DATE_FORMAT.set(format);
        try {
            return this.getList(Calendar.class, key, defaultValue);
        }
        finally {
            DataConfiguration.TEMP_DATE_FORMAT.remove();
        }
    }
    
    public Calendar[] getCalendarArray(final String key) {
        return this.getCalendarArray(key, new Calendar[0]);
    }
    
    public Calendar[] getCalendarArray(final String key, final String format) {
        return this.getCalendarArray(key, new Calendar[0], format);
    }
    
    public Calendar[] getCalendarArray(final String key, final Calendar[] defaultValue) {
        return this.getCalendarArray(key, defaultValue, null);
    }
    
    public Calendar[] getCalendarArray(final String key, final Calendar[] defaultValue, final String format) {
        DataConfiguration.TEMP_DATE_FORMAT.set(format);
        try {
            return this.get(Calendar[].class, key, defaultValue);
        }
        finally {
            DataConfiguration.TEMP_DATE_FORMAT.remove();
        }
    }
    
    private String getDefaultDateFormat() {
        return this.getString("org.apache.commons.configuration.format.date", "yyyy-MM-dd HH:mm:ss");
    }
    
    public Locale getLocale(final String key) {
        return this.get(Locale.class, key);
    }
    
    public Locale getLocale(final String key, final Locale defaultValue) {
        return this.get(Locale.class, key, defaultValue);
    }
    
    public List<Locale> getLocaleList(final String key) {
        return this.getLocaleList(key, new ArrayList<Locale>());
    }
    
    public List<Locale> getLocaleList(final String key, final List<Locale> defaultValue) {
        return this.getList(Locale.class, key, defaultValue);
    }
    
    public Locale[] getLocaleArray(final String key) {
        return this.getLocaleArray(key, new Locale[0]);
    }
    
    public Locale[] getLocaleArray(final String key, final Locale[] defaultValue) {
        return this.get(Locale[].class, key, defaultValue);
    }
    
    public Color getColor(final String key) {
        return this.get(Color.class, key);
    }
    
    public Color getColor(final String key, final Color defaultValue) {
        return this.get(Color.class, key, defaultValue);
    }
    
    public List<Color> getColorList(final String key) {
        return this.getColorList(key, new ArrayList<Color>());
    }
    
    public List<Color> getColorList(final String key, final List<Color> defaultValue) {
        return this.getList(Color.class, key, defaultValue);
    }
    
    public Color[] getColorArray(final String key) {
        return this.getColorArray(key, new Color[0]);
    }
    
    public Color[] getColorArray(final String key, final Color[] defaultValue) {
        return this.get(Color[].class, key, defaultValue);
    }
    
    private DefaultConversionHandler getOriginalConversionHandler() {
        final ConversionHandler handler = super.getConversionHandler();
        return (DefaultConversionHandler)((handler instanceof DefaultConversionHandler) ? handler : null);
    }
    
    static {
        TEMP_DATE_FORMAT = new ThreadLocal<String>();
    }
    
    private class DataConversionHandler extends DefaultConversionHandler
    {
        @Override
        public String getDateFormat() {
            if (StringUtils.isNotEmpty(DataConfiguration.TEMP_DATE_FORMAT.get())) {
                return DataConfiguration.TEMP_DATE_FORMAT.get();
            }
            if (DataConfiguration.this.containsKey("org.apache.commons.configuration.format.date")) {
                return DataConfiguration.this.getDefaultDateFormat();
            }
            final DefaultConversionHandler orgHandler = DataConfiguration.this.getOriginalConversionHandler();
            return (orgHandler != null) ? orgHandler.getDateFormat() : null;
        }
    }
}
