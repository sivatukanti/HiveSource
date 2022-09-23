// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.configuration2.io.FileBased;
import java.io.Reader;
import java.io.IOException;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.io.Writer;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventType;
import java.util.Collections;
import java.util.Collection;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import java.util.Properties;
import java.util.List;
import java.util.Iterator;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.apache.commons.configuration2.tree.ImmutableNode;

public class PatternSubtreeConfigurationWrapper extends BaseHierarchicalConfiguration implements FileBasedConfiguration
{
    private final HierarchicalConfiguration<ImmutableNode> config;
    private final String path;
    private final boolean trailing;
    private final boolean init;
    
    public PatternSubtreeConfigurationWrapper(final HierarchicalConfiguration<ImmutableNode> config, final String path) {
        this.config = config;
        this.path = path;
        this.trailing = path.endsWith("/");
        this.init = true;
    }
    
    @Override
    protected void addPropertyInternal(final String key, final Object value) {
        this.config.addProperty(this.makePath(key), value);
    }
    
    @Override
    protected void clearInternal() {
        this.getConfig().clear();
    }
    
    @Override
    protected void clearPropertyDirect(final String key) {
        this.config.clearProperty(this.makePath(key));
    }
    
    @Override
    protected boolean containsKeyInternal(final String key) {
        return this.config.containsKey(this.makePath(key));
    }
    
    @Override
    public BigDecimal getBigDecimal(final String key, final BigDecimal defaultValue) {
        return this.config.getBigDecimal(this.makePath(key), defaultValue);
    }
    
    @Override
    public BigDecimal getBigDecimal(final String key) {
        return this.config.getBigDecimal(this.makePath(key));
    }
    
    @Override
    public BigInteger getBigInteger(final String key, final BigInteger defaultValue) {
        return this.config.getBigInteger(this.makePath(key), defaultValue);
    }
    
    @Override
    public BigInteger getBigInteger(final String key) {
        return this.config.getBigInteger(this.makePath(key));
    }
    
    @Override
    public boolean getBoolean(final String key, final boolean defaultValue) {
        return this.config.getBoolean(this.makePath(key), defaultValue);
    }
    
    @Override
    public Boolean getBoolean(final String key, final Boolean defaultValue) {
        return this.config.getBoolean(this.makePath(key), defaultValue);
    }
    
    @Override
    public boolean getBoolean(final String key) {
        return this.config.getBoolean(this.makePath(key));
    }
    
    @Override
    public byte getByte(final String key, final byte defaultValue) {
        return this.config.getByte(this.makePath(key), defaultValue);
    }
    
    @Override
    public Byte getByte(final String key, final Byte defaultValue) {
        return this.config.getByte(this.makePath(key), defaultValue);
    }
    
    @Override
    public byte getByte(final String key) {
        return this.config.getByte(this.makePath(key));
    }
    
    @Override
    public double getDouble(final String key, final double defaultValue) {
        return this.config.getDouble(this.makePath(key), defaultValue);
    }
    
    @Override
    public Double getDouble(final String key, final Double defaultValue) {
        return this.config.getDouble(this.makePath(key), defaultValue);
    }
    
    @Override
    public double getDouble(final String key) {
        return this.config.getDouble(this.makePath(key));
    }
    
    @Override
    public float getFloat(final String key, final float defaultValue) {
        return this.config.getFloat(this.makePath(key), defaultValue);
    }
    
    @Override
    public Float getFloat(final String key, final Float defaultValue) {
        return this.config.getFloat(this.makePath(key), defaultValue);
    }
    
    @Override
    public float getFloat(final String key) {
        return this.config.getFloat(this.makePath(key));
    }
    
    @Override
    public int getInt(final String key, final int defaultValue) {
        return this.config.getInt(this.makePath(key), defaultValue);
    }
    
    @Override
    public int getInt(final String key) {
        return this.config.getInt(this.makePath(key));
    }
    
    @Override
    public Integer getInteger(final String key, final Integer defaultValue) {
        return this.config.getInteger(this.makePath(key), defaultValue);
    }
    
    @Override
    protected Iterator<String> getKeysInternal() {
        return this.config.getKeys(this.makePath());
    }
    
    @Override
    protected Iterator<String> getKeysInternal(final String prefix) {
        return this.config.getKeys(this.makePath(prefix));
    }
    
    @Override
    public List<Object> getList(final String key, final List<?> defaultValue) {
        return this.config.getList(this.makePath(key), defaultValue);
    }
    
    @Override
    public List<Object> getList(final String key) {
        return this.config.getList(this.makePath(key));
    }
    
    @Override
    public long getLong(final String key, final long defaultValue) {
        return this.config.getLong(this.makePath(key), defaultValue);
    }
    
    @Override
    public Long getLong(final String key, final Long defaultValue) {
        return this.config.getLong(this.makePath(key), defaultValue);
    }
    
    @Override
    public long getLong(final String key) {
        return this.config.getLong(this.makePath(key));
    }
    
    @Override
    public Properties getProperties(final String key) {
        return this.config.getProperties(this.makePath(key));
    }
    
    @Override
    protected Object getPropertyInternal(final String key) {
        return this.config.getProperty(this.makePath(key));
    }
    
    @Override
    public short getShort(final String key, final short defaultValue) {
        return this.config.getShort(this.makePath(key), defaultValue);
    }
    
    @Override
    public Short getShort(final String key, final Short defaultValue) {
        return this.config.getShort(this.makePath(key), defaultValue);
    }
    
    @Override
    public short getShort(final String key) {
        return this.config.getShort(this.makePath(key));
    }
    
    @Override
    public String getString(final String key, final String defaultValue) {
        return this.config.getString(this.makePath(key), defaultValue);
    }
    
    @Override
    public String getString(final String key) {
        return this.config.getString(this.makePath(key));
    }
    
    @Override
    public String[] getStringArray(final String key) {
        return this.config.getStringArray(this.makePath(key));
    }
    
    @Override
    protected boolean isEmptyInternal() {
        return this.getConfig().isEmpty();
    }
    
    @Override
    protected void setPropertyInternal(final String key, final Object value) {
        this.getConfig().setProperty(key, value);
    }
    
    @Override
    public Configuration subset(final String prefix) {
        return this.getConfig().subset(prefix);
    }
    
    @Override
    public ExpressionEngine getExpressionEngine() {
        return this.config.getExpressionEngine();
    }
    
    @Override
    public void setExpressionEngine(final ExpressionEngine expressionEngine) {
        if (this.init) {
            this.config.setExpressionEngine(expressionEngine);
        }
        else {
            super.setExpressionEngine(expressionEngine);
        }
    }
    
    @Override
    protected void addNodesInternal(final String key, final Collection<? extends ImmutableNode> nodes) {
        this.getConfig().addNodes(key, nodes);
    }
    
    @Override
    public HierarchicalConfiguration<ImmutableNode> configurationAt(final String key, final boolean supportUpdates) {
        return this.config.configurationAt(this.makePath(key), supportUpdates);
    }
    
    @Override
    public HierarchicalConfiguration<ImmutableNode> configurationAt(final String key) {
        return this.config.configurationAt(this.makePath(key));
    }
    
    @Override
    public List<HierarchicalConfiguration<ImmutableNode>> configurationsAt(final String key) {
        return this.config.configurationsAt(this.makePath(key));
    }
    
    @Override
    protected Object clearTreeInternal(final String key) {
        this.config.clearTree(this.makePath(key));
        return Collections.emptyList();
    }
    
    @Override
    protected int getMaxIndexInternal(final String key) {
        return this.config.getMaxIndex(this.makePath(key));
    }
    
    @Override
    public Configuration interpolatedConfiguration() {
        return this.getConfig().interpolatedConfiguration();
    }
    
    @Override
    public <T extends Event> void addEventListener(final EventType<T> eventType, final EventListener<? super T> listener) {
        this.getConfig().addEventListener(eventType, listener);
    }
    
    @Override
    public <T extends Event> boolean removeEventListener(final EventType<T> eventType, final EventListener<? super T> listener) {
        return this.getConfig().removeEventListener(eventType, listener);
    }
    
    @Override
    public <T extends Event> Collection<EventListener<? super T>> getEventListeners(final EventType<T> eventType) {
        return this.getConfig().getEventListeners(eventType);
    }
    
    @Override
    public void clearEventListeners() {
        this.getConfig().clearEventListeners();
    }
    
    @Override
    public void clearErrorListeners() {
        this.getConfig().clearErrorListeners();
    }
    
    @Override
    public void write(final Writer writer) throws ConfigurationException, IOException {
        this.fetchFileBased().write(writer);
    }
    
    @Override
    public void read(final Reader reader) throws ConfigurationException, IOException {
        this.fetchFileBased().read(reader);
    }
    
    private BaseHierarchicalConfiguration getConfig() {
        return (BaseHierarchicalConfiguration)this.config.configurationAt(this.makePath());
    }
    
    private String makePath() {
        final String pathPattern = this.trailing ? this.path.substring(0, this.path.length() - 1) : this.path;
        return this.substitute(pathPattern);
    }
    
    private String makePath(final String item) {
        String pathPattern;
        if ((item.length() == 0 || item.startsWith("/")) && this.trailing) {
            pathPattern = this.path.substring(0, this.path.length() - 1);
        }
        else if (!item.startsWith("/") || !this.trailing) {
            pathPattern = this.path + "/";
        }
        else {
            pathPattern = this.path;
        }
        return this.substitute(pathPattern) + item;
    }
    
    private String substitute(final String pattern) {
        final Object value = this.getInterpolator().interpolate(pattern);
        return (value != null) ? value.toString() : null;
    }
    
    private FileBased fetchFileBased() throws ConfigurationException {
        if (!(this.config instanceof FileBased)) {
            throw new ConfigurationException("Wrapped configuration does not implement FileBased! No I/O operations are supported.");
        }
        return (FileBased)this.config;
    }
}
