// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.conf;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ConfigurationWithLogging extends Configuration
{
    private static final Logger LOG;
    private final Logger log;
    private final ConfigRedactor redactor;
    
    public ConfigurationWithLogging(final Configuration conf) {
        super(conf);
        this.log = ConfigurationWithLogging.LOG;
        this.redactor = new ConfigRedactor(conf);
    }
    
    @Override
    public String get(final String name) {
        final String value = super.get(name);
        this.log.info("Got {} = '{}'", name, this.redactor.redact(name, value));
        return value;
    }
    
    @Override
    public String get(final String name, final String defaultValue) {
        final String value = super.get(name, defaultValue);
        this.log.info("Got {} = '{}' (default '{}')", name, this.redactor.redact(name, value), this.redactor.redact(name, defaultValue));
        return value;
    }
    
    @Override
    public boolean getBoolean(final String name, final boolean defaultValue) {
        final boolean value = super.getBoolean(name, defaultValue);
        this.log.info("Got {} = '{}' (default '{}')", name, value, defaultValue);
        return value;
    }
    
    @Override
    public float getFloat(final String name, final float defaultValue) {
        final float value = super.getFloat(name, defaultValue);
        this.log.info("Got {} = '{}' (default '{}')", name, value, defaultValue);
        return value;
    }
    
    @Override
    public int getInt(final String name, final int defaultValue) {
        final int value = super.getInt(name, defaultValue);
        this.log.info("Got {} = '{}' (default '{}')", name, value, defaultValue);
        return value;
    }
    
    @Override
    public long getLong(final String name, final long defaultValue) {
        final long value = super.getLong(name, defaultValue);
        this.log.info("Got {} = '{}' (default '{}')", name, value, defaultValue);
        return value;
    }
    
    @Override
    public void set(final String name, final String value, final String source) {
        this.log.info("Set {} to '{}'{}", name, this.redactor.redact(name, value), (source == null) ? "" : (" from " + source));
        super.set(name, value, source);
    }
    
    static {
        LOG = LoggerFactory.getLogger(ConfigurationWithLogging.class);
    }
}
