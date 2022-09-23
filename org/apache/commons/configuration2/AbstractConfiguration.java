// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.util.NoSuchElementException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.configuration2.ex.ConversionException;
import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Properties;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.sync.LockMode;
import org.apache.commons.configuration2.sync.NoOpSynchronizer;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.ConfigurationErrorEvent;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.configuration2.interpol.InterpolatorSpecification;
import java.util.Collection;
import org.apache.commons.configuration2.interpol.Lookup;
import java.util.Map;
import org.apache.commons.configuration2.convert.DefaultConversionHandler;
import org.apache.commons.configuration2.convert.DisabledListDelimiterHandler;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.configuration2.sync.Synchronizer;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.configuration2.convert.ConversionHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.event.BaseEventSource;

public abstract class AbstractConfiguration extends BaseEventSource implements Configuration
{
    private ListDelimiterHandler listDelimiterHandler;
    private ConversionHandler conversionHandler;
    private boolean throwExceptionOnMissing;
    private AtomicReference<ConfigurationInterpolator> interpolator;
    private volatile Synchronizer synchronizer;
    private ConfigurationDecoder configurationDecoder;
    private ConfigurationLogger log;
    
    public AbstractConfiguration() {
        this.interpolator = new AtomicReference<ConfigurationInterpolator>();
        this.initLogger(null);
        this.installDefaultInterpolator();
        this.listDelimiterHandler = DisabledListDelimiterHandler.INSTANCE;
        this.conversionHandler = DefaultConversionHandler.INSTANCE;
    }
    
    public ListDelimiterHandler getListDelimiterHandler() {
        return this.listDelimiterHandler;
    }
    
    public void setListDelimiterHandler(final ListDelimiterHandler listDelimiterHandler) {
        if (listDelimiterHandler == null) {
            throw new IllegalArgumentException("List delimiter handler must not be null!");
        }
        this.listDelimiterHandler = listDelimiterHandler;
    }
    
    public ConversionHandler getConversionHandler() {
        return this.conversionHandler;
    }
    
    public void setConversionHandler(final ConversionHandler conversionHandler) {
        if (conversionHandler == null) {
            throw new IllegalArgumentException("ConversionHandler must not be null!");
        }
        this.conversionHandler = conversionHandler;
    }
    
    public void setThrowExceptionOnMissing(final boolean throwExceptionOnMissing) {
        this.throwExceptionOnMissing = throwExceptionOnMissing;
    }
    
    public boolean isThrowExceptionOnMissing() {
        return this.throwExceptionOnMissing;
    }
    
    @Override
    public ConfigurationInterpolator getInterpolator() {
        return this.interpolator.get();
    }
    
    @Override
    public final void setInterpolator(final ConfigurationInterpolator ci) {
        this.interpolator.set(ci);
    }
    
    @Override
    public final void installInterpolator(final Map<String, ? extends Lookup> prefixLookups, final Collection<? extends Lookup> defLookups) {
        final InterpolatorSpecification spec = new InterpolatorSpecification.Builder().withPrefixLookups(prefixLookups).withDefaultLookups(defLookups).withDefaultLookup(new ConfigurationLookup(this)).create();
        this.setInterpolator(ConfigurationInterpolator.fromSpecification(spec));
    }
    
    public void setPrefixLookups(final Map<String, ? extends Lookup> lookups) {
        boolean success;
        do {
            final ConfigurationInterpolator ciOld = this.getInterpolator();
            final ConfigurationInterpolator ciNew = (ciOld != null) ? ciOld : new ConfigurationInterpolator();
            ciNew.registerLookups(lookups);
            success = this.interpolator.compareAndSet(ciOld, ciNew);
        } while (!success);
    }
    
    public void setDefaultLookups(final Collection<? extends Lookup> lookups) {
        boolean success;
        do {
            final ConfigurationInterpolator ciOld = this.getInterpolator();
            final ConfigurationInterpolator ciNew = (ciOld != null) ? ciOld : new ConfigurationInterpolator();
            Lookup confLookup = this.findConfigurationLookup(ciNew);
            if (confLookup == null) {
                confLookup = new ConfigurationLookup(this);
            }
            else {
                ciNew.removeDefaultLookup(confLookup);
            }
            ciNew.addDefaultLookups(lookups);
            ciNew.addDefaultLookup(confLookup);
            success = this.interpolator.compareAndSet(ciOld, ciNew);
        } while (!success);
    }
    
    public void setParentInterpolator(final ConfigurationInterpolator parent) {
        boolean success;
        do {
            final ConfigurationInterpolator ciOld = this.getInterpolator();
            final ConfigurationInterpolator ciNew = (ciOld != null) ? ciOld : new ConfigurationInterpolator();
            ciNew.setParentInterpolator(parent);
            success = this.interpolator.compareAndSet(ciOld, ciNew);
        } while (!success);
    }
    
    public void setConfigurationDecoder(final ConfigurationDecoder configurationDecoder) {
        this.configurationDecoder = configurationDecoder;
    }
    
    public ConfigurationDecoder getConfigurationDecoder() {
        return this.configurationDecoder;
    }
    
    protected void cloneInterpolator(final AbstractConfiguration orgConfig) {
        this.interpolator = new AtomicReference<ConfigurationInterpolator>();
        final ConfigurationInterpolator orgInterpolator = orgConfig.getInterpolator();
        final List<Lookup> defaultLookups = orgInterpolator.getDefaultLookups();
        final Lookup lookup = findConfigurationLookup(orgInterpolator, orgConfig);
        if (lookup != null) {
            defaultLookups.remove(lookup);
        }
        this.installInterpolator(orgInterpolator.getLookups(), defaultLookups);
    }
    
    private void installDefaultInterpolator() {
        this.installInterpolator(ConfigurationInterpolator.getDefaultPrefixLookups(), null);
    }
    
    private Lookup findConfigurationLookup(final ConfigurationInterpolator ci) {
        return findConfigurationLookup(ci, this);
    }
    
    private static Lookup findConfigurationLookup(final ConfigurationInterpolator ci, final ImmutableConfiguration targetConf) {
        for (final Lookup l : ci.getDefaultLookups()) {
            if (l instanceof ConfigurationLookup && targetConf == ((ConfigurationLookup)l).getConfiguration()) {
                return l;
            }
        }
        return null;
    }
    
    public ConfigurationLogger getLogger() {
        return this.log;
    }
    
    public void setLogger(final ConfigurationLogger log) {
        this.initLogger(log);
    }
    
    public final void addErrorLogListener() {
        this.addEventListener(ConfigurationErrorEvent.ANY, new EventListener<ConfigurationErrorEvent>() {
            @Override
            public void onEvent(final ConfigurationErrorEvent event) {
                AbstractConfiguration.this.getLogger().warn("Internal error", event.getCause());
            }
        });
    }
    
    @Override
    public final Synchronizer getSynchronizer() {
        final Synchronizer sync = this.synchronizer;
        return (sync != null) ? sync : NoOpSynchronizer.INSTANCE;
    }
    
    @Override
    public final void setSynchronizer(final Synchronizer synchronizer) {
        this.synchronizer = synchronizer;
    }
    
    @Override
    public final void lock(final LockMode mode) {
        switch (mode) {
            case READ: {
                this.beginRead(false);
                break;
            }
            case WRITE: {
                this.beginWrite(false);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported LockMode: " + mode);
            }
        }
    }
    
    @Override
    public final void unlock(final LockMode mode) {
        switch (mode) {
            case READ: {
                this.endRead();
                break;
            }
            case WRITE: {
                this.endWrite();
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported LockMode: " + mode);
            }
        }
    }
    
    protected void beginRead(final boolean optimize) {
        this.getSynchronizer().beginRead();
    }
    
    protected void endRead() {
        this.getSynchronizer().endRead();
    }
    
    protected void beginWrite(final boolean optimize) {
        this.getSynchronizer().beginWrite();
    }
    
    protected void endWrite() {
        this.getSynchronizer().endWrite();
    }
    
    @Override
    public final void addProperty(final String key, final Object value) {
        this.beginWrite(false);
        try {
            this.fireEvent(ConfigurationEvent.ADD_PROPERTY, key, value, true);
            this.addPropertyInternal(key, value);
            this.fireEvent(ConfigurationEvent.ADD_PROPERTY, key, value, false);
        }
        finally {
            this.endWrite();
        }
    }
    
    protected void addPropertyInternal(final String key, final Object value) {
        for (final Object obj : this.getListDelimiterHandler().parse(value)) {
            this.addPropertyDirect(key, obj);
        }
    }
    
    protected abstract void addPropertyDirect(final String p0, final Object p1);
    
    protected String interpolate(final String base) {
        final Object result = this.interpolate((Object)base);
        return (result == null) ? null : result.toString();
    }
    
    protected Object interpolate(final Object value) {
        final ConfigurationInterpolator ci = this.getInterpolator();
        return (ci != null) ? ci.interpolate(value) : value;
    }
    
    @Override
    public Configuration subset(final String prefix) {
        return new SubsetConfiguration(this, prefix, ".");
    }
    
    @Override
    public ImmutableConfiguration immutableSubset(final String prefix) {
        return ConfigurationUtils.unmodifiableConfiguration(this.subset(prefix));
    }
    
    @Override
    public final void setProperty(final String key, final Object value) {
        this.beginWrite(false);
        try {
            this.fireEvent(ConfigurationEvent.SET_PROPERTY, key, value, true);
            this.setPropertyInternal(key, value);
            this.fireEvent(ConfigurationEvent.SET_PROPERTY, key, value, false);
        }
        finally {
            this.endWrite();
        }
    }
    
    protected void setPropertyInternal(final String key, final Object value) {
        this.setDetailEvents(false);
        try {
            this.clearProperty(key);
            this.addProperty(key, value);
        }
        finally {
            this.setDetailEvents(true);
        }
    }
    
    @Override
    public final void clearProperty(final String key) {
        this.beginWrite(false);
        try {
            this.fireEvent(ConfigurationEvent.CLEAR_PROPERTY, key, null, true);
            this.clearPropertyDirect(key);
            this.fireEvent(ConfigurationEvent.CLEAR_PROPERTY, key, null, false);
        }
        finally {
            this.endWrite();
        }
    }
    
    protected abstract void clearPropertyDirect(final String p0);
    
    @Override
    public final void clear() {
        this.beginWrite(false);
        try {
            this.fireEvent(ConfigurationEvent.CLEAR, null, null, true);
            this.clearInternal();
            this.fireEvent(ConfigurationEvent.CLEAR, null, null, false);
        }
        finally {
            this.endWrite();
        }
    }
    
    protected void clearInternal() {
        this.setDetailEvents(false);
        boolean useIterator = true;
        try {
            final Iterator<String> it = this.getKeys();
            while (it.hasNext()) {
                final String key = it.next();
                if (useIterator) {
                    try {
                        it.remove();
                    }
                    catch (UnsupportedOperationException usoex) {
                        useIterator = false;
                    }
                }
                if (useIterator && this.containsKey(key)) {
                    useIterator = false;
                }
                if (!useIterator) {
                    this.clearProperty(key);
                }
            }
        }
        finally {
            this.setDetailEvents(true);
        }
    }
    
    @Override
    public final Iterator<String> getKeys() {
        this.beginRead(false);
        try {
            return this.getKeysInternal();
        }
        finally {
            this.endRead();
        }
    }
    
    @Override
    public final Iterator<String> getKeys(final String prefix) {
        this.beginRead(false);
        try {
            return this.getKeysInternal(prefix);
        }
        finally {
            this.endRead();
        }
    }
    
    protected abstract Iterator<String> getKeysInternal();
    
    protected Iterator<String> getKeysInternal(final String prefix) {
        return new PrefixedKeysIterator(this.getKeysInternal(), prefix);
    }
    
    @Override
    public final Object getProperty(final String key) {
        this.beginRead(false);
        try {
            return this.getPropertyInternal(key);
        }
        finally {
            this.endRead();
        }
    }
    
    protected abstract Object getPropertyInternal(final String p0);
    
    @Override
    public final boolean isEmpty() {
        this.beginRead(false);
        try {
            return this.isEmptyInternal();
        }
        finally {
            this.endRead();
        }
    }
    
    protected abstract boolean isEmptyInternal();
    
    @Override
    public final int size() {
        this.beginRead(false);
        try {
            return this.sizeInternal();
        }
        finally {
            this.endRead();
        }
    }
    
    protected int sizeInternal() {
        int size = 0;
        final Iterator<String> keyIt = this.getKeysInternal();
        while (keyIt.hasNext()) {
            keyIt.next();
            ++size;
        }
        return size;
    }
    
    @Override
    public final boolean containsKey(final String key) {
        this.beginRead(false);
        try {
            return this.containsKeyInternal(key);
        }
        finally {
            this.endRead();
        }
    }
    
    protected abstract boolean containsKeyInternal(final String p0);
    
    @Override
    public Properties getProperties(final String key) {
        return this.getProperties(key, null);
    }
    
    public Properties getProperties(final String key, final Properties defaults) {
        final String[] tokens = this.getStringArray(key);
        final Properties props = (defaults == null) ? new Properties() : new Properties(defaults);
        final String[] array = tokens;
        final int length = array.length;
        int i = 0;
        while (i < length) {
            final String token = array[i];
            final int equalSign = token.indexOf(61);
            if (equalSign > 0) {
                final String pkey = token.substring(0, equalSign).trim();
                final String pvalue = token.substring(equalSign + 1).trim();
                props.put(pkey, pvalue);
                ++i;
            }
            else {
                if (tokens.length == 1 && "".equals(token)) {
                    break;
                }
                throw new IllegalArgumentException('\'' + token + "' does not contain an equals sign");
            }
        }
        return props;
    }
    
    @Override
    public boolean getBoolean(final String key) {
        final Boolean b = this.convert(Boolean.class, key, null, true);
        return checkNonNullValue(key, b);
    }
    
    @Override
    public boolean getBoolean(final String key, final boolean defaultValue) {
        return this.getBoolean(key, Boolean.valueOf(defaultValue));
    }
    
    @Override
    public Boolean getBoolean(final String key, final Boolean defaultValue) {
        return this.convert(Boolean.class, key, defaultValue, false);
    }
    
    @Override
    public byte getByte(final String key) {
        final Byte b = this.convert(Byte.class, key, null, true);
        return checkNonNullValue(key, b);
    }
    
    @Override
    public byte getByte(final String key, final byte defaultValue) {
        return this.getByte(key, Byte.valueOf(defaultValue));
    }
    
    @Override
    public Byte getByte(final String key, final Byte defaultValue) {
        return this.convert(Byte.class, key, defaultValue, false);
    }
    
    @Override
    public double getDouble(final String key) {
        final Double d = this.convert(Double.class, key, null, true);
        return checkNonNullValue(key, d);
    }
    
    @Override
    public double getDouble(final String key, final double defaultValue) {
        return this.getDouble(key, Double.valueOf(defaultValue));
    }
    
    @Override
    public Double getDouble(final String key, final Double defaultValue) {
        return this.convert(Double.class, key, defaultValue, false);
    }
    
    @Override
    public float getFloat(final String key) {
        final Float f = this.convert(Float.class, key, null, true);
        return checkNonNullValue(key, f);
    }
    
    @Override
    public float getFloat(final String key, final float defaultValue) {
        return this.getFloat(key, Float.valueOf(defaultValue));
    }
    
    @Override
    public Float getFloat(final String key, final Float defaultValue) {
        return this.convert(Float.class, key, defaultValue, false);
    }
    
    @Override
    public int getInt(final String key) {
        final Integer i = this.convert(Integer.class, key, null, true);
        return checkNonNullValue(key, i);
    }
    
    @Override
    public int getInt(final String key, final int defaultValue) {
        return this.getInteger(key, defaultValue);
    }
    
    @Override
    public Integer getInteger(final String key, final Integer defaultValue) {
        return this.convert(Integer.class, key, defaultValue, false);
    }
    
    @Override
    public long getLong(final String key) {
        final Long l = this.convert(Long.class, key, null, true);
        return checkNonNullValue(key, l);
    }
    
    @Override
    public long getLong(final String key, final long defaultValue) {
        return this.getLong(key, Long.valueOf(defaultValue));
    }
    
    @Override
    public Long getLong(final String key, final Long defaultValue) {
        return this.convert(Long.class, key, defaultValue, false);
    }
    
    @Override
    public short getShort(final String key) {
        final Short s = this.convert(Short.class, key, null, true);
        return checkNonNullValue(key, s);
    }
    
    @Override
    public short getShort(final String key, final short defaultValue) {
        return this.getShort(key, Short.valueOf(defaultValue));
    }
    
    @Override
    public Short getShort(final String key, final Short defaultValue) {
        return this.convert(Short.class, key, defaultValue, false);
    }
    
    @Override
    public BigDecimal getBigDecimal(final String key) {
        return this.convert(BigDecimal.class, key, null, true);
    }
    
    @Override
    public BigDecimal getBigDecimal(final String key, final BigDecimal defaultValue) {
        return this.convert(BigDecimal.class, key, defaultValue, false);
    }
    
    @Override
    public BigInteger getBigInteger(final String key) {
        return this.convert(BigInteger.class, key, null, true);
    }
    
    @Override
    public BigInteger getBigInteger(final String key, final BigInteger defaultValue) {
        return this.convert(BigInteger.class, key, defaultValue, false);
    }
    
    @Override
    public String getString(final String key) {
        return this.convert(String.class, key, null, true);
    }
    
    @Override
    public String getString(final String key, final String defaultValue) {
        final String result = this.convert(String.class, key, null, false);
        return (result != null) ? result : this.interpolate(defaultValue);
    }
    
    @Override
    public String getEncodedString(final String key, final ConfigurationDecoder decoder) {
        if (decoder == null) {
            throw new IllegalArgumentException("ConfigurationDecoder must not be null!");
        }
        final String value = this.getString(key);
        return (value != null) ? decoder.decode(value) : null;
    }
    
    @Override
    public String getEncodedString(final String key) {
        final ConfigurationDecoder decoder = this.getConfigurationDecoder();
        if (decoder == null) {
            throw new IllegalStateException("No default ConfigurationDecoder defined!");
        }
        return this.getEncodedString(key, decoder);
    }
    
    @Override
    public String[] getStringArray(final String key) {
        final String[] result = (String[])this.getArray(String.class, key);
        return (result == null) ? new String[0] : result;
    }
    
    @Override
    public List<Object> getList(final String key) {
        return this.getList(key, new ArrayList<Object>());
    }
    
    @Override
    public List<Object> getList(final String key, final List<?> defaultValue) {
        final Object value = this.getProperty(key);
        List<Object> list;
        if (value instanceof String) {
            list = new ArrayList<Object>(1);
            list.add(this.interpolate((String)value));
        }
        else if (value instanceof List) {
            list = new ArrayList<Object>();
            final List<?> l = (List<?>)value;
            for (final Object elem : l) {
                list.add(this.interpolate(elem));
            }
        }
        else if (value == null) {
            final List<Object> resultList = list = (List<Object>)defaultValue;
        }
        else {
            if (value.getClass().isArray()) {
                return Arrays.asList((Object[])value);
            }
            if (this.isScalarValue(value)) {
                return (List<Object>)Collections.singletonList(value.toString());
            }
            throw new ConversionException('\'' + key + "' doesn't map to a List object: " + value + ", a " + value.getClass().getName());
        }
        return list;
    }
    
    @Override
    public <T> T get(final Class<T> cls, final String key) {
        return this.convert(cls, key, (T)null, true);
    }
    
    @Override
    public <T> T get(final Class<T> cls, final String key, final T defaultValue) {
        return this.convert(cls, key, defaultValue, false);
    }
    
    @Override
    public Object getArray(final Class<?> cls, final String key) {
        return this.getArray(cls, key, null);
    }
    
    @Override
    public Object getArray(final Class<?> cls, final String key, final Object defaultValue) {
        return this.convertToArray(cls, key, defaultValue);
    }
    
    @Override
    public <T> List<T> getList(final Class<T> cls, final String key) {
        return this.getList(cls, key, null);
    }
    
    @Override
    public <T> List<T> getList(final Class<T> cls, final String key, final List<T> defaultValue) {
        final List<T> result = new ArrayList<T>();
        if (this.getCollection(cls, key, result, defaultValue) == null) {
            return null;
        }
        return result;
    }
    
    @Override
    public <T> Collection<T> getCollection(final Class<T> cls, final String key, final Collection<T> target) {
        return this.getCollection(cls, key, target, null);
    }
    
    @Override
    public <T> Collection<T> getCollection(final Class<T> cls, final String key, final Collection<T> target, final Collection<T> defaultValue) {
        final Object src = this.getProperty(key);
        if (src == null) {
            return handleDefaultCollection(target, defaultValue);
        }
        final Collection<T> targetCol = (target != null) ? target : new ArrayList<T>();
        this.getConversionHandler().toCollection(src, cls, this.getInterpolator(), targetCol);
        return targetCol;
    }
    
    protected boolean isScalarValue(final Object value) {
        return ClassUtils.wrapperToPrimitive(value.getClass()) != null;
    }
    
    public void copy(final Configuration c) {
        if (c != null) {
            c.lock(LockMode.READ);
            try {
                final Iterator<String> it = c.getKeys();
                while (it.hasNext()) {
                    final String key = it.next();
                    final Object value = this.encodeForCopy(c.getProperty(key));
                    this.setProperty(key, value);
                }
            }
            finally {
                c.unlock(LockMode.READ);
            }
        }
    }
    
    public void append(final Configuration c) {
        if (c != null) {
            c.lock(LockMode.READ);
            try {
                final Iterator<String> it = c.getKeys();
                while (it.hasNext()) {
                    final String key = it.next();
                    final Object value = this.encodeForCopy(c.getProperty(key));
                    this.addProperty(key, value);
                }
            }
            finally {
                c.unlock(LockMode.READ);
            }
        }
    }
    
    public Configuration interpolatedConfiguration() {
        final AbstractConfiguration c = (AbstractConfiguration)ConfigurationUtils.cloneConfiguration(this);
        c.setListDelimiterHandler(new DisabledListDelimiterHandler());
        final Iterator<String> it = this.getKeys();
        while (it.hasNext()) {
            final String key = it.next();
            c.setProperty(key, this.getList(key));
        }
        c.setListDelimiterHandler(this.getListDelimiterHandler());
        return c;
    }
    
    protected final void initLogger(final ConfigurationLogger log) {
        this.log = ((log != null) ? log : ConfigurationLogger.newDummyLogger());
    }
    
    private Object encodeForCopy(final Object value) {
        if (value instanceof Collection) {
            return this.encodeListForCopy((Collection<?>)value);
        }
        return this.getListDelimiterHandler().escape(value, ListDelimiterHandler.NOOP_TRANSFORMER);
    }
    
    private Object encodeListForCopy(final Collection<?> values) {
        final List<Object> result = new ArrayList<Object>(values.size());
        for (final Object value : values) {
            result.add(this.encodeForCopy(value));
        }
        return result;
    }
    
    private <T> T getAndConvertProperty(final Class<T> cls, final String key, final T defaultValue) {
        final Object value = this.getProperty(key);
        try {
            return ObjectUtils.defaultIfNull((T)this.getConversionHandler().to(value, (Class<T>)cls, this.getInterpolator()), defaultValue);
        }
        catch (ConversionException cex) {
            throw new ConversionException(String.format("Key '%s' cannot be converted to class %s. Value is: '%s'.", key, cls.getName(), String.valueOf(value)));
        }
    }
    
    private <T> T convert(final Class<T> cls, final String key, final T defValue, final boolean throwOnMissing) {
        if (cls.isArray()) {
            return cls.cast(this.convertToArray(cls.getComponentType(), key, defValue));
        }
        final T result = this.getAndConvertProperty(cls, key, defValue);
        if (result == null) {
            if (throwOnMissing && this.isThrowExceptionOnMissing()) {
                throwMissingPropertyException(key);
            }
            return defValue;
        }
        return result;
    }
    
    private Object convertToArray(final Class<?> cls, final String key, final Object defaultValue) {
        checkDefaultValueArray(cls, defaultValue);
        return ObjectUtils.defaultIfNull(this.getConversionHandler().toArray(this.getProperty(key), cls, this.getInterpolator()), defaultValue);
    }
    
    private static void checkDefaultValueArray(final Class<?> cls, final Object defaultValue) {
        if (defaultValue != null && (!defaultValue.getClass().isArray() || !cls.isAssignableFrom(defaultValue.getClass().getComponentType()))) {
            throw new IllegalArgumentException("The type of the default value (" + defaultValue.getClass() + ") is not an array of the specified class (" + cls + ")");
        }
    }
    
    private static <T> Collection<T> handleDefaultCollection(final Collection<T> target, final Collection<T> defaultValue) {
        if (defaultValue == null) {
            return null;
        }
        Collection<T> result;
        if (target == null) {
            result = new ArrayList<T>((Collection<? extends T>)defaultValue);
        }
        else {
            target.addAll((Collection<? extends T>)defaultValue);
            result = target;
        }
        return result;
    }
    
    private static <T> T checkNonNullValue(final String key, final T value) {
        if (value == null) {
            throwMissingPropertyException(key);
        }
        return value;
    }
    
    private static void throwMissingPropertyException(final String key) {
        throw new NoSuchElementException(String.format("Key '%s' does not map to an existing object!", key));
    }
}
