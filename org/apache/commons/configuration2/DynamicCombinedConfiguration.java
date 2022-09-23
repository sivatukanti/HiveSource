// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.configuration2.event.BaseEventSource;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventType;
import java.util.Collections;
import org.apache.commons.configuration2.tree.ImmutableNode;
import java.util.Collection;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import java.util.Properties;
import java.util.Iterator;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.tree.NodeCombiner;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class DynamicCombinedConfiguration extends CombinedConfiguration
{
    private static final ThreadLocal<CurrentConfigHolder> CURRENT_CONFIG;
    private final ConcurrentMap<String, CombinedConfiguration> configs;
    private final List<ConfigData> configurations;
    private final Map<String, Configuration> namedConfigurations;
    private String keyPattern;
    private NodeCombiner nodeCombiner;
    private String loggerName;
    private final ConfigurationInterpolator localSubst;
    
    public DynamicCombinedConfiguration(final NodeCombiner comb) {
        this.configs = new ConcurrentHashMap<String, CombinedConfiguration>();
        this.configurations = new ArrayList<ConfigData>();
        this.namedConfigurations = new HashMap<String, Configuration>();
        this.loggerName = DynamicCombinedConfiguration.class.getName();
        this.setNodeCombiner(comb);
        this.initLogger(new ConfigurationLogger(DynamicCombinedConfiguration.class));
        this.localSubst = this.initLocalInterpolator();
    }
    
    public DynamicCombinedConfiguration() {
        this.configs = new ConcurrentHashMap<String, CombinedConfiguration>();
        this.configurations = new ArrayList<ConfigData>();
        this.namedConfigurations = new HashMap<String, Configuration>();
        this.loggerName = DynamicCombinedConfiguration.class.getName();
        this.initLogger(new ConfigurationLogger(DynamicCombinedConfiguration.class));
        this.localSubst = this.initLocalInterpolator();
    }
    
    public void setKeyPattern(final String pattern) {
        this.keyPattern = pattern;
    }
    
    public String getKeyPattern() {
        return this.keyPattern;
    }
    
    public void setLoggerName(final String name) {
        this.loggerName = name;
    }
    
    @Override
    public NodeCombiner getNodeCombiner() {
        return this.nodeCombiner;
    }
    
    @Override
    public void setNodeCombiner(final NodeCombiner nodeCombiner) {
        if (nodeCombiner == null) {
            throw new IllegalArgumentException("Node combiner must not be null!");
        }
        this.nodeCombiner = nodeCombiner;
        this.invalidateAll();
    }
    
    @Override
    public void addConfiguration(final Configuration config, final String name, final String at) {
        this.beginWrite(true);
        try {
            final ConfigData cd = new ConfigData(config, name, at);
            this.configurations.add(cd);
            if (name != null) {
                this.namedConfigurations.put(name, config);
            }
            this.configs.clear();
        }
        finally {
            this.endWrite();
        }
    }
    
    @Override
    public int getNumberOfConfigurations() {
        this.beginRead(false);
        try {
            return this.configurations.size();
        }
        finally {
            this.endRead();
        }
    }
    
    @Override
    public Configuration getConfiguration(final int index) {
        this.beginRead(false);
        try {
            final ConfigData cd = this.configurations.get(index);
            return cd.getConfiguration();
        }
        finally {
            this.endRead();
        }
    }
    
    @Override
    public Configuration getConfiguration(final String name) {
        this.beginRead(false);
        try {
            return this.namedConfigurations.get(name);
        }
        finally {
            this.endRead();
        }
    }
    
    @Override
    public Set<String> getConfigurationNames() {
        this.beginRead(false);
        try {
            return this.namedConfigurations.keySet();
        }
        finally {
            this.endRead();
        }
    }
    
    @Override
    public Configuration removeConfiguration(final String name) {
        final Configuration conf = this.getConfiguration(name);
        if (conf != null) {
            this.removeConfiguration(conf);
        }
        return conf;
    }
    
    @Override
    public boolean removeConfiguration(final Configuration config) {
        this.beginWrite(false);
        try {
            for (int index = 0; index < this.getNumberOfConfigurations(); ++index) {
                if (this.configurations.get(index).getConfiguration() == config) {
                    this.removeConfigurationAt(index);
                    return true;
                }
            }
            return false;
        }
        finally {
            this.endWrite();
        }
    }
    
    @Override
    public Configuration removeConfigurationAt(final int index) {
        this.beginWrite(false);
        try {
            final ConfigData cd = this.configurations.remove(index);
            if (cd.getName() != null) {
                this.namedConfigurations.remove(cd.getName());
            }
            return cd.getConfiguration();
        }
        finally {
            this.endWrite();
        }
    }
    
    @Override
    protected void addPropertyInternal(final String key, final Object value) {
        this.getCurrentConfig().addProperty(key, value);
    }
    
    @Override
    protected void clearInternal() {
        if (this.configs != null) {
            this.getCurrentConfig().clear();
        }
    }
    
    @Override
    protected void clearPropertyDirect(final String key) {
        this.getCurrentConfig().clearProperty(key);
    }
    
    @Override
    protected boolean containsKeyInternal(final String key) {
        return this.getCurrentConfig().containsKey(key);
    }
    
    @Override
    public BigDecimal getBigDecimal(final String key, final BigDecimal defaultValue) {
        return this.getCurrentConfig().getBigDecimal(key, defaultValue);
    }
    
    @Override
    public BigDecimal getBigDecimal(final String key) {
        return this.getCurrentConfig().getBigDecimal(key);
    }
    
    @Override
    public BigInteger getBigInteger(final String key, final BigInteger defaultValue) {
        return this.getCurrentConfig().getBigInteger(key, defaultValue);
    }
    
    @Override
    public BigInteger getBigInteger(final String key) {
        return this.getCurrentConfig().getBigInteger(key);
    }
    
    @Override
    public boolean getBoolean(final String key, final boolean defaultValue) {
        return this.getCurrentConfig().getBoolean(key, defaultValue);
    }
    
    @Override
    public Boolean getBoolean(final String key, final Boolean defaultValue) {
        return this.getCurrentConfig().getBoolean(key, defaultValue);
    }
    
    @Override
    public boolean getBoolean(final String key) {
        return this.getCurrentConfig().getBoolean(key);
    }
    
    @Override
    public byte getByte(final String key, final byte defaultValue) {
        return this.getCurrentConfig().getByte(key, defaultValue);
    }
    
    @Override
    public Byte getByte(final String key, final Byte defaultValue) {
        return this.getCurrentConfig().getByte(key, defaultValue);
    }
    
    @Override
    public byte getByte(final String key) {
        return this.getCurrentConfig().getByte(key);
    }
    
    @Override
    public double getDouble(final String key, final double defaultValue) {
        return this.getCurrentConfig().getDouble(key, defaultValue);
    }
    
    @Override
    public Double getDouble(final String key, final Double defaultValue) {
        return this.getCurrentConfig().getDouble(key, defaultValue);
    }
    
    @Override
    public double getDouble(final String key) {
        return this.getCurrentConfig().getDouble(key);
    }
    
    @Override
    public float getFloat(final String key, final float defaultValue) {
        return this.getCurrentConfig().getFloat(key, defaultValue);
    }
    
    @Override
    public Float getFloat(final String key, final Float defaultValue) {
        return this.getCurrentConfig().getFloat(key, defaultValue);
    }
    
    @Override
    public float getFloat(final String key) {
        return this.getCurrentConfig().getFloat(key);
    }
    
    @Override
    public int getInt(final String key, final int defaultValue) {
        return this.getCurrentConfig().getInt(key, defaultValue);
    }
    
    @Override
    public int getInt(final String key) {
        return this.getCurrentConfig().getInt(key);
    }
    
    @Override
    public Integer getInteger(final String key, final Integer defaultValue) {
        return this.getCurrentConfig().getInteger(key, defaultValue);
    }
    
    @Override
    protected Iterator<String> getKeysInternal() {
        return this.getCurrentConfig().getKeys();
    }
    
    @Override
    protected Iterator<String> getKeysInternal(final String prefix) {
        return this.getCurrentConfig().getKeys(prefix);
    }
    
    @Override
    public List<Object> getList(final String key, final List<?> defaultValue) {
        return this.getCurrentConfig().getList(key, defaultValue);
    }
    
    @Override
    public List<Object> getList(final String key) {
        return this.getCurrentConfig().getList(key);
    }
    
    @Override
    public long getLong(final String key, final long defaultValue) {
        return this.getCurrentConfig().getLong(key, defaultValue);
    }
    
    @Override
    public Long getLong(final String key, final Long defaultValue) {
        return this.getCurrentConfig().getLong(key, defaultValue);
    }
    
    @Override
    public long getLong(final String key) {
        return this.getCurrentConfig().getLong(key);
    }
    
    @Override
    public Properties getProperties(final String key) {
        return this.getCurrentConfig().getProperties(key);
    }
    
    @Override
    protected Object getPropertyInternal(final String key) {
        return this.getCurrentConfig().getProperty(key);
    }
    
    @Override
    public short getShort(final String key, final short defaultValue) {
        return this.getCurrentConfig().getShort(key, defaultValue);
    }
    
    @Override
    public Short getShort(final String key, final Short defaultValue) {
        return this.getCurrentConfig().getShort(key, defaultValue);
    }
    
    @Override
    public short getShort(final String key) {
        return this.getCurrentConfig().getShort(key);
    }
    
    @Override
    public String getString(final String key, final String defaultValue) {
        return this.getCurrentConfig().getString(key, defaultValue);
    }
    
    @Override
    public String getString(final String key) {
        return this.getCurrentConfig().getString(key);
    }
    
    @Override
    public String[] getStringArray(final String key) {
        return this.getCurrentConfig().getStringArray(key);
    }
    
    @Override
    protected boolean isEmptyInternal() {
        return this.getCurrentConfig().isEmpty();
    }
    
    @Override
    protected int sizeInternal() {
        return this.getCurrentConfig().size();
    }
    
    @Override
    protected void setPropertyInternal(final String key, final Object value) {
        if (this.configs != null) {
            this.getCurrentConfig().setProperty(key, value);
        }
    }
    
    @Override
    public Configuration subset(final String prefix) {
        return this.getCurrentConfig().subset(prefix);
    }
    
    @Override
    public ExpressionEngine getExpressionEngine() {
        return super.getExpressionEngine();
    }
    
    @Override
    public void setExpressionEngine(final ExpressionEngine expressionEngine) {
        super.setExpressionEngine(expressionEngine);
    }
    
    @Override
    protected void addNodesInternal(final String key, final Collection<? extends ImmutableNode> nodes) {
        this.getCurrentConfig().addNodes(key, nodes);
    }
    
    @Override
    public HierarchicalConfiguration<ImmutableNode> configurationAt(final String key, final boolean supportUpdates) {
        return this.getCurrentConfig().configurationAt(key, supportUpdates);
    }
    
    @Override
    public HierarchicalConfiguration<ImmutableNode> configurationAt(final String key) {
        return this.getCurrentConfig().configurationAt(key);
    }
    
    @Override
    public List<HierarchicalConfiguration<ImmutableNode>> configurationsAt(final String key) {
        return this.getCurrentConfig().configurationsAt(key);
    }
    
    @Override
    protected Object clearTreeInternal(final String key) {
        this.getCurrentConfig().clearTree(key);
        return Collections.emptyList();
    }
    
    @Override
    protected int getMaxIndexInternal(final String key) {
        return this.getCurrentConfig().getMaxIndex(key);
    }
    
    @Override
    public Configuration interpolatedConfiguration() {
        return this.getCurrentConfig().interpolatedConfiguration();
    }
    
    @Override
    public Configuration getSource(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null!");
        }
        return this.getCurrentConfig().getSource(key);
    }
    
    @Override
    public void clearEventListeners() {
        for (final CombinedConfiguration cc : this.configs.values()) {
            cc.clearEventListeners();
        }
        super.clearEventListeners();
    }
    
    @Override
    public <T extends Event> void addEventListener(final EventType<T> eventType, final EventListener<? super T> listener) {
        for (final CombinedConfiguration cc : this.configs.values()) {
            cc.addEventListener(eventType, listener);
        }
        super.addEventListener(eventType, listener);
    }
    
    @Override
    public <T extends Event> boolean removeEventListener(final EventType<T> eventType, final EventListener<? super T> listener) {
        for (final CombinedConfiguration cc : this.configs.values()) {
            cc.removeEventListener(eventType, listener);
        }
        return super.removeEventListener(eventType, listener);
    }
    
    @Override
    public void clearErrorListeners() {
        for (final CombinedConfiguration cc : this.configs.values()) {
            cc.clearErrorListeners();
        }
        super.clearErrorListeners();
    }
    
    @Override
    public Object clone() {
        return super.clone();
    }
    
    @Override
    public void invalidate() {
        this.getCurrentConfig().invalidate();
    }
    
    public void invalidateAll() {
        for (final CombinedConfiguration cc : this.configs.values()) {
            cc.invalidate();
        }
    }
    
    @Override
    protected void beginRead(final boolean optimize) {
        final CurrentConfigHolder cch = this.ensureCurrentConfiguration();
        cch.incrementLockCount();
        if (!optimize && cch.getCurrentConfiguration() == null) {
            this.beginWrite(false);
            this.endWrite();
        }
        cch.getCurrentConfiguration().beginRead(optimize);
    }
    
    @Override
    protected void beginWrite(final boolean optimize) {
        final CurrentConfigHolder cch = this.ensureCurrentConfiguration();
        cch.incrementLockCount();
        super.beginWrite(optimize);
        if (!optimize && cch.getCurrentConfiguration() == null) {
            cch.setCurrentConfiguration(this.createChildConfiguration());
            this.configs.put(cch.getKey(), cch.getCurrentConfiguration());
            this.initChildConfiguration(cch.getCurrentConfiguration());
        }
    }
    
    @Override
    protected void endRead() {
        DynamicCombinedConfiguration.CURRENT_CONFIG.get().getCurrentConfiguration().endRead();
        this.releaseLock();
    }
    
    @Override
    protected void endWrite() {
        super.endWrite();
        this.releaseLock();
    }
    
    private void releaseLock() {
        final CurrentConfigHolder cch = DynamicCombinedConfiguration.CURRENT_CONFIG.get();
        assert cch != null : "No current configuration!";
        if (cch.decrementLockCountAndCheckRelease()) {
            DynamicCombinedConfiguration.CURRENT_CONFIG.remove();
        }
    }
    
    private CombinedConfiguration getCurrentConfig() {
        this.beginRead(false);
        CombinedConfiguration config;
        String key;
        try {
            config = DynamicCombinedConfiguration.CURRENT_CONFIG.get().getCurrentConfiguration();
            key = DynamicCombinedConfiguration.CURRENT_CONFIG.get().getKey();
        }
        finally {
            this.endRead();
        }
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("Returning config for " + key + ": " + config);
        }
        return config;
    }
    
    private CombinedConfiguration createChildConfiguration() {
        return new CombinedConfiguration(this.getNodeCombiner());
    }
    
    private void initChildConfiguration(final CombinedConfiguration config) {
        if (this.loggerName != null) {
            config.setLogger(new ConfigurationLogger(this.loggerName));
        }
        config.setExpressionEngine(this.getExpressionEngine());
        config.setConversionExpressionEngine(this.getConversionExpressionEngine());
        config.setListDelimiterHandler(this.getListDelimiterHandler());
        this.copyEventListeners(config);
        for (final ConfigData data : this.configurations) {
            config.addConfiguration(data.getConfiguration(), data.getName(), data.getAt());
        }
        config.setSynchronizer(this.getSynchronizer());
    }
    
    private ConfigurationInterpolator initLocalInterpolator() {
        return new ConfigurationInterpolator() {
            @Override
            protected Lookup fetchLookupForPrefix(final String prefix) {
                return ConfigurationInterpolator.nullSafeLookup(DynamicCombinedConfiguration.this.getInterpolator().getLookups().get(prefix));
            }
        };
    }
    
    private CurrentConfigHolder ensureCurrentConfiguration() {
        CurrentConfigHolder cch = DynamicCombinedConfiguration.CURRENT_CONFIG.get();
        if (cch == null) {
            final String key = String.valueOf(this.localSubst.interpolate(this.keyPattern));
            cch = new CurrentConfigHolder(key);
            cch.setCurrentConfiguration(this.configs.get(key));
            DynamicCombinedConfiguration.CURRENT_CONFIG.set(cch);
        }
        return cch;
    }
    
    static {
        CURRENT_CONFIG = new ThreadLocal<CurrentConfigHolder>();
    }
    
    static class ConfigData
    {
        private final Configuration configuration;
        private final String name;
        private final String at;
        
        public ConfigData(final Configuration config, final String n, final String at) {
            this.configuration = config;
            this.name = n;
            this.at = at;
        }
        
        public Configuration getConfiguration() {
            return this.configuration;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getAt() {
            return this.at;
        }
    }
    
    private static class CurrentConfigHolder
    {
        private CombinedConfiguration currentConfiguration;
        private final String key;
        private int lockCount;
        
        public CurrentConfigHolder(final String curKey) {
            this.key = curKey;
        }
        
        public CombinedConfiguration getCurrentConfiguration() {
            return this.currentConfiguration;
        }
        
        public void setCurrentConfiguration(final CombinedConfiguration currentConfiguration) {
            this.currentConfiguration = currentConfiguration;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public void incrementLockCount() {
            ++this.lockCount;
        }
        
        public boolean decrementLockCountAndCheckRelease() {
            final int lockCount = this.lockCount - 1;
            this.lockCount = lockCount;
            return lockCount == 0;
        }
    }
}
