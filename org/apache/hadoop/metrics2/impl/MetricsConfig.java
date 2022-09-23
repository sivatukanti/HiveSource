// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import org.slf4j.LoggerFactory;
import java.io.Writer;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import org.apache.hadoop.metrics2.filter.GlobFilter;
import org.apache.hadoop.metrics2.MetricsFilter;
import java.security.AccessController;
import java.net.URLClassLoader;
import java.security.PrivilegedAction;
import java.net.URL;
import com.google.common.collect.Iterables;
import org.apache.hadoop.metrics2.MetricsPlugin;
import java.util.regex.Matcher;
import java.util.Iterator;
import com.google.common.collect.Maps;
import java.util.Map;
import com.google.common.base.Joiner;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileBased;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.hadoop.util.StringUtils;
import org.apache.commons.configuration2.Configuration;
import com.google.common.base.Splitter;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.apache.commons.configuration2.SubsetConfiguration;

class MetricsConfig extends SubsetConfiguration
{
    static final Logger LOG;
    static final String DEFAULT_FILE_NAME = "hadoop-metrics2.properties";
    static final String PREFIX_DEFAULT = "*.";
    static final String PERIOD_KEY = "period";
    static final int PERIOD_DEFAULT = 10;
    static final String PERIOD_MILLIS_KEY = "periodMillis";
    static final String QUEUE_CAPACITY_KEY = "queue.capacity";
    static final int QUEUE_CAPACITY_DEFAULT = 1;
    static final String RETRY_DELAY_KEY = "retry.delay";
    static final int RETRY_DELAY_DEFAULT = 10;
    static final String RETRY_BACKOFF_KEY = "retry.backoff";
    static final int RETRY_BACKOFF_DEFAULT = 2;
    static final String RETRY_COUNT_KEY = "retry.count";
    static final int RETRY_COUNT_DEFAULT = 1;
    static final String JMX_CACHE_TTL_KEY = "jmx.cache.ttl";
    static final String START_MBEANS_KEY = "source.start_mbeans";
    static final String PLUGIN_URLS_KEY = "plugin.urls";
    static final String CONTEXT_KEY = "context";
    static final String NAME_KEY = "name";
    static final String DESC_KEY = "description";
    static final String SOURCE_KEY = "source";
    static final String SINK_KEY = "sink";
    static final String METRIC_FILTER_KEY = "metric.filter";
    static final String RECORD_FILTER_KEY = "record.filter";
    static final String SOURCE_FILTER_KEY = "source.filter";
    static final Pattern INSTANCE_REGEX;
    static final Splitter SPLITTER;
    private ClassLoader pluginLoader;
    
    MetricsConfig(final Configuration c, final String prefix) {
        super(c, StringUtils.toLowerCase(prefix), ".");
    }
    
    static MetricsConfig create(final String prefix) {
        return loadFirst(prefix, "hadoop-metrics2-" + StringUtils.toLowerCase(prefix) + ".properties", "hadoop-metrics2.properties");
    }
    
    static MetricsConfig create(final String prefix, final String... fileNames) {
        return loadFirst(prefix, fileNames);
    }
    
    static MetricsConfig loadFirst(final String prefix, final String... fileNames) {
        final int length = fileNames.length;
        int i = 0;
        while (i < length) {
            final String fname = fileNames[i];
            try {
                final PropertiesConfiguration pcf = new PropertiesConfiguration();
                final FileHandler fh = new FileHandler(pcf);
                fh.setFileName(fname);
                fh.load();
                final Configuration cf = pcf.interpolatedConfiguration();
                MetricsConfig.LOG.info("Loaded properties from {}", fname);
                if (MetricsConfig.LOG.isDebugEnabled()) {
                    MetricsConfig.LOG.debug("Properties: {}", toString(cf));
                }
                final MetricsConfig mc = new MetricsConfig(cf, prefix);
                MetricsConfig.LOG.debug("Metrics Config: {}", mc);
                return mc;
            }
            catch (ConfigurationException e) {
                if (e.getMessage().startsWith("Could not locate")) {
                    MetricsConfig.LOG.debug("Could not locate file {}", fname, e);
                    ++i;
                    continue;
                }
                throw new MetricsConfigException(e);
            }
            break;
        }
        MetricsConfig.LOG.warn("Cannot locate configuration: tried " + Joiner.on(",").join(fileNames));
        return new MetricsConfig(new PropertiesConfiguration(), prefix);
    }
    
    @Override
    public MetricsConfig subset(final String prefix) {
        return new MetricsConfig(this, prefix);
    }
    
    Map<String, MetricsConfig> getInstanceConfigs(final String type) {
        final Map<String, MetricsConfig> map = (Map<String, MetricsConfig>)Maps.newHashMap();
        final MetricsConfig sub = this.subset(type);
        for (final String key : sub.keys()) {
            final Matcher matcher = MetricsConfig.INSTANCE_REGEX.matcher(key);
            if (matcher.matches()) {
                final String instance = matcher.group(1);
                if (map.containsKey(instance)) {
                    continue;
                }
                map.put(instance, sub.subset(instance));
            }
        }
        return map;
    }
    
    Iterable<String> keys() {
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return MetricsConfig.this.getKeys();
            }
        };
    }
    
    public Object getPropertyInternal(final String key) {
        final Object value = super.getPropertyInternal(key);
        if (value == null) {
            if (MetricsConfig.LOG.isDebugEnabled()) {
                MetricsConfig.LOG.debug("poking parent '" + this.getParent().getClass().getSimpleName() + "' for key: " + key);
            }
            return this.getParent().getProperty(key.startsWith("*.") ? key : ("*." + key));
        }
        MetricsConfig.LOG.debug("Returning '{}' for key: {}", value, key);
        return value;
    }
    
     <T extends MetricsPlugin> T getPlugin(final String name) {
        final String clsName = this.getClassName(name);
        if (clsName == null) {
            return null;
        }
        try {
            final Class<?> cls = Class.forName(clsName, true, this.getPluginLoader());
            final T plugin = (T)cls.newInstance();
            plugin.init(name.isEmpty() ? this : this.subset(name));
            return plugin;
        }
        catch (Exception e) {
            throw new MetricsConfigException("Error creating plugin: " + clsName, e);
        }
    }
    
    String getClassName(final String prefix) {
        final String classKey = prefix.isEmpty() ? "class" : prefix.concat(".class");
        final String clsName = this.getString(classKey);
        MetricsConfig.LOG.debug("Class name for prefix {} is {}", prefix, clsName);
        if (clsName == null || clsName.isEmpty()) {
            return null;
        }
        return clsName;
    }
    
    ClassLoader getPluginLoader() {
        if (this.pluginLoader != null) {
            return this.pluginLoader;
        }
        final ClassLoader defaultLoader = this.getClass().getClassLoader();
        final Object purls = super.getProperty("plugin.urls");
        if (purls == null) {
            return defaultLoader;
        }
        final Iterable<String> jars = MetricsConfig.SPLITTER.split((CharSequence)purls);
        final int len = Iterables.size(jars);
        if (len > 0) {
            final URL[] urls = new URL[len];
            try {
                int i = 0;
                for (final String jar : jars) {
                    MetricsConfig.LOG.debug("Parsing URL for {}", jar);
                    urls[i++] = new URL(jar);
                }
            }
            catch (Exception e) {
                throw new MetricsConfigException(e);
            }
            if (MetricsConfig.LOG.isDebugEnabled()) {
                MetricsConfig.LOG.debug("Using plugin jars: {}", Iterables.toString(jars));
            }
            return this.pluginLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                    return new URLClassLoader(urls, defaultLoader);
                }
            });
        }
        if (this.parent instanceof MetricsConfig) {
            return ((MetricsConfig)this.parent).getPluginLoader();
        }
        return defaultLoader;
    }
    
    MetricsFilter getFilter(final String prefix) {
        final MetricsConfig conf = this.subset(prefix);
        if (conf.isEmpty()) {
            return null;
        }
        MetricsFilter filter = this.getPlugin(prefix);
        if (filter != null) {
            return filter;
        }
        filter = new GlobFilter();
        filter.init(conf);
        return filter;
    }
    
    @Override
    public String toString() {
        return toString(this);
    }
    
    static String toString(final Configuration c) {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            final PrintWriter pw = new PrintWriter(buffer, false);
            final PropertiesConfiguration tmp = new PropertiesConfiguration();
            tmp.copy(c);
            tmp.write(pw);
            return buffer.toString("UTF-8");
        }
        catch (Exception e) {
            throw new MetricsConfigException(e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(MetricsConfig.class);
        INSTANCE_REGEX = Pattern.compile("([^.*]+)\\..+");
        SPLITTER = Splitter.on(',').trimResults();
    }
}
