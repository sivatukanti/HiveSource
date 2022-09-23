// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.interpol.InterpolatorSpecification;
import java.util.Iterator;
import org.apache.commons.configuration2.ConfigurationDecoder;
import org.apache.commons.configuration2.beanutils.BeanHelper;
import org.apache.commons.configuration2.convert.ConversionHandler;
import org.apache.commons.configuration2.sync.Synchronizer;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import java.util.HashMap;
import java.util.Map;

public class BasicBuilderParameters implements Cloneable, BuilderParameters, BasicBuilderProperties<BasicBuilderParameters>
{
    private static final String PROP_THROW_EXCEPTION_ON_MISSING = "throwExceptionOnMissing";
    private static final String PROP_LIST_DELIMITER_HANDLER = "listDelimiterHandler";
    private static final String PROP_LOGGER = "logger";
    private static final String PROP_INTERPOLATOR = "interpolator";
    private static final String PROP_PREFIX_LOOKUPS = "prefixLookups";
    private static final String PROP_DEFAULT_LOOKUPS = "defaultLookups";
    private static final String PROP_PARENT_INTERPOLATOR = "parentInterpolator";
    private static final String PROP_SYNCHRONIZER = "synchronizer";
    private static final String PROP_CONVERSION_HANDLER = "conversionHandler";
    private static final String PROP_CONFIGURATION_DECODER = "configurationDecoder";
    private static final String PROP_BEAN_HELPER = "config-BeanHelper";
    private Map<String, Object> properties;
    
    public BasicBuilderParameters() {
        this.properties = new HashMap<String, Object>();
    }
    
    @Override
    public Map<String, Object> getParameters() {
        final HashMap<String, Object> result = new HashMap<String, Object>(this.properties);
        if (result.containsKey("interpolator")) {
            result.remove("prefixLookups");
            result.remove("defaultLookups");
            result.remove("parentInterpolator");
        }
        createDefensiveCopies(result);
        return result;
    }
    
    @Override
    public BasicBuilderParameters setLogger(final ConfigurationLogger log) {
        return this.setProperty("logger", log);
    }
    
    @Override
    public BasicBuilderParameters setThrowExceptionOnMissing(final boolean b) {
        return this.setProperty("throwExceptionOnMissing", b);
    }
    
    @Override
    public BasicBuilderParameters setListDelimiterHandler(final ListDelimiterHandler handler) {
        return this.setProperty("listDelimiterHandler", handler);
    }
    
    @Override
    public BasicBuilderParameters setInterpolator(final ConfigurationInterpolator ci) {
        return this.setProperty("interpolator", ci);
    }
    
    @Override
    public BasicBuilderParameters setPrefixLookups(final Map<String, ? extends Lookup> lookups) {
        if (lookups == null) {
            this.properties.remove("prefixLookups");
            return this;
        }
        return this.setProperty("prefixLookups", new HashMap(lookups));
    }
    
    @Override
    public BasicBuilderParameters setDefaultLookups(final Collection<? extends Lookup> lookups) {
        if (lookups == null) {
            this.properties.remove("defaultLookups");
            return this;
        }
        return this.setProperty("defaultLookups", new ArrayList(lookups));
    }
    
    @Override
    public BasicBuilderParameters setParentInterpolator(final ConfigurationInterpolator parent) {
        return this.setProperty("parentInterpolator", parent);
    }
    
    @Override
    public BasicBuilderParameters setSynchronizer(final Synchronizer sync) {
        return this.setProperty("synchronizer", sync);
    }
    
    @Override
    public BasicBuilderParameters setConversionHandler(final ConversionHandler handler) {
        return this.setProperty("conversionHandler", handler);
    }
    
    @Override
    public BasicBuilderParameters setBeanHelper(final BeanHelper beanHelper) {
        return this.setProperty("config-BeanHelper", beanHelper);
    }
    
    @Override
    public BasicBuilderParameters setConfigurationDecoder(final ConfigurationDecoder decoder) {
        return this.setProperty("configurationDecoder", decoder);
    }
    
    public void merge(final BuilderParameters p) {
        if (p == null) {
            throw new IllegalArgumentException("Parameters to merge must not be null!");
        }
        for (final Map.Entry<String, Object> e : p.getParameters().entrySet()) {
            if (!this.properties.containsKey(e.getKey()) && !e.getKey().startsWith("config-")) {
                this.storeProperty(e.getKey(), e.getValue());
            }
        }
    }
    
    public void inheritFrom(final Map<String, ?> source) {
        if (source == null) {
            throw new IllegalArgumentException("Source properties must not be null!");
        }
        this.copyPropertiesFrom(source, "config-BeanHelper", "configurationDecoder", "conversionHandler", "listDelimiterHandler", "logger", "synchronizer", "throwExceptionOnMissing");
    }
    
    public static InterpolatorSpecification fetchInterpolatorSpecification(final Map<String, Object> params) {
        checkParameters(params);
        return new InterpolatorSpecification.Builder().withInterpolator(fetchParameter(params, "interpolator", ConfigurationInterpolator.class)).withParentInterpolator(fetchParameter(params, "parentInterpolator", ConfigurationInterpolator.class)).withPrefixLookups(fetchAndCheckPrefixLookups(params)).withDefaultLookups(fetchAndCheckDefaultLookups(params)).create();
    }
    
    public static BeanHelper fetchBeanHelper(final Map<String, Object> params) {
        checkParameters(params);
        return params.get("config-BeanHelper");
    }
    
    public BasicBuilderParameters clone() {
        try {
            final BasicBuilderParameters copy = (BasicBuilderParameters)super.clone();
            copy.properties = this.getParameters();
            return copy;
        }
        catch (CloneNotSupportedException cnex) {
            throw new AssertionError((Object)cnex);
        }
    }
    
    protected void storeProperty(final String key, final Object value) {
        if (value == null) {
            this.properties.remove(key);
        }
        else {
            this.properties.put(key, value);
        }
    }
    
    protected Object fetchProperty(final String key) {
        return this.properties.get(key);
    }
    
    protected void copyPropertiesFrom(final Map<String, ?> source, final String... keys) {
        for (final String key : keys) {
            final Object value = source.get(key);
            if (value != null) {
                this.storeProperty(key, value);
            }
        }
    }
    
    private BasicBuilderParameters setProperty(final String key, final Object value) {
        this.storeProperty(key, value);
        return this;
    }
    
    private static void createDefensiveCopies(final HashMap<String, Object> params) {
        final Map<String, ? extends Lookup> prefixLookups = fetchPrefixLookups(params);
        if (prefixLookups != null) {
            params.put("prefixLookups", new HashMap<String, HashMap>((Map<? extends String, ? extends HashMap>)prefixLookups));
        }
        final Collection<? extends Lookup> defLookups = fetchDefaultLookups(params);
        if (defLookups != null) {
            params.put("defaultLookups", new ArrayList(defLookups));
        }
    }
    
    private static Map<String, ? extends Lookup> fetchPrefixLookups(final Map<String, Object> params) {
        final Map<String, ? extends Lookup> prefixLookups = params.get("prefixLookups");
        return prefixLookups;
    }
    
    private static Map<String, ? extends Lookup> fetchAndCheckPrefixLookups(final Map<String, Object> params) {
        final Map<?, ?> prefixes = fetchParameter(params, "prefixLookups", (Class<Map<?, ?>>)Map.class);
        if (prefixes == null) {
            return null;
        }
        for (final Map.Entry<?, ?> e : prefixes.entrySet()) {
            if (!(e.getKey() instanceof String) || !(e.getValue() instanceof Lookup)) {
                throw new IllegalArgumentException("Map with prefix lookups contains invalid data: " + prefixes);
            }
        }
        return fetchPrefixLookups(params);
    }
    
    private static Collection<? extends Lookup> fetchDefaultLookups(final Map<String, Object> params) {
        final Collection<? extends Lookup> defLookups = params.get("defaultLookups");
        return defLookups;
    }
    
    private static Collection<? extends Lookup> fetchAndCheckDefaultLookups(final Map<String, Object> params) {
        final Collection<?> col = fetchParameter(params, "defaultLookups", (Class<Collection<?>>)Collection.class);
        if (col == null) {
            return null;
        }
        for (final Object o : col) {
            if (!(o instanceof Lookup)) {
                throw new IllegalArgumentException("Collection with default lookups contains invalid data: " + col);
            }
        }
        return fetchDefaultLookups(params);
    }
    
    private static <T> T fetchParameter(final Map<String, Object> params, final String key, final Class<T> expClass) {
        final Object value = params.get(key);
        if (value == null) {
            return null;
        }
        if (!expClass.isInstance(value)) {
            throw new IllegalArgumentException(String.format("Parameter %s is not of type %s!", key, expClass.getSimpleName()));
        }
        return expClass.cast(value);
    }
    
    private static void checkParameters(final Map<String, Object> params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters map must not be null!");
        }
    }
}
