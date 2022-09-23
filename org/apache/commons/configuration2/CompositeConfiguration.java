// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CompositeConfiguration extends AbstractConfiguration implements Cloneable
{
    private List<Configuration> configList;
    private Configuration inMemoryConfiguration;
    private boolean inMemoryConfigIsChild;
    
    public CompositeConfiguration() {
        this.configList = new LinkedList<Configuration>();
        this.clear();
    }
    
    public CompositeConfiguration(final Configuration inMemoryConfiguration) {
        (this.configList = new LinkedList<Configuration>()).clear();
        this.inMemoryConfiguration = inMemoryConfiguration;
        this.configList.add(inMemoryConfiguration);
    }
    
    public CompositeConfiguration(final Collection<? extends Configuration> configurations) {
        this(new BaseConfiguration(), configurations);
    }
    
    public CompositeConfiguration(final Configuration inMemoryConfiguration, final Collection<? extends Configuration> configurations) {
        this(inMemoryConfiguration);
        if (configurations != null) {
            for (final Configuration c : configurations) {
                this.addConfiguration(c);
            }
        }
    }
    
    public void addConfiguration(final Configuration config) {
        this.addConfiguration(config, false);
    }
    
    public void addConfiguration(final Configuration config, final boolean asInMemory) {
        this.beginWrite(false);
        try {
            if (!this.configList.contains(config)) {
                if (asInMemory) {
                    this.replaceInMemoryConfiguration(config);
                    this.inMemoryConfigIsChild = true;
                }
                if (!this.inMemoryConfigIsChild) {
                    this.configList.add(this.configList.indexOf(this.inMemoryConfiguration), config);
                }
                else {
                    this.configList.add(config);
                }
                if (config instanceof AbstractConfiguration) {
                    ((AbstractConfiguration)config).setThrowExceptionOnMissing(this.isThrowExceptionOnMissing());
                }
            }
        }
        finally {
            this.endWrite();
        }
    }
    
    public void removeConfiguration(final Configuration config) {
        this.beginWrite(false);
        try {
            if (!config.equals(this.inMemoryConfiguration)) {
                this.configList.remove(config);
            }
        }
        finally {
            this.endWrite();
        }
    }
    
    public int getNumberOfConfigurations() {
        this.beginRead(false);
        try {
            return this.configList.size();
        }
        finally {
            this.endRead();
        }
    }
    
    @Override
    protected void clearInternal() {
        this.configList.clear();
        this.inMemoryConfiguration = new BaseConfiguration();
        ((BaseConfiguration)this.inMemoryConfiguration).setThrowExceptionOnMissing(this.isThrowExceptionOnMissing());
        ((BaseConfiguration)this.inMemoryConfiguration).setListDelimiterHandler(this.getListDelimiterHandler());
        this.configList.add(this.inMemoryConfiguration);
        this.inMemoryConfigIsChild = false;
    }
    
    @Override
    protected void addPropertyDirect(final String key, final Object token) {
        this.inMemoryConfiguration.addProperty(key, token);
    }
    
    @Override
    protected Object getPropertyInternal(final String key) {
        Configuration firstMatchingConfiguration = null;
        for (final Configuration config : this.configList) {
            if (config.containsKey(key)) {
                firstMatchingConfiguration = config;
                break;
            }
        }
        if (firstMatchingConfiguration != null) {
            return firstMatchingConfiguration.getProperty(key);
        }
        return null;
    }
    
    @Override
    protected Iterator<String> getKeysInternal() {
        final Set<String> keys = new LinkedHashSet<String>();
        for (final Configuration config : this.configList) {
            final Iterator<String> it = config.getKeys();
            while (it.hasNext()) {
                keys.add(it.next());
            }
        }
        return keys.iterator();
    }
    
    @Override
    protected Iterator<String> getKeysInternal(final String key) {
        final Set<String> keys = new LinkedHashSet<String>();
        for (final Configuration config : this.configList) {
            final Iterator<String> it = config.getKeys(key);
            while (it.hasNext()) {
                keys.add(it.next());
            }
        }
        return keys.iterator();
    }
    
    @Override
    protected boolean isEmptyInternal() {
        for (final Configuration config : this.configList) {
            if (!config.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected void clearPropertyDirect(final String key) {
        for (final Configuration config : this.configList) {
            config.clearProperty(key);
        }
    }
    
    @Override
    protected boolean containsKeyInternal(final String key) {
        for (final Configuration config : this.configList) {
            if (config.containsKey(key)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<Object> getList(final String key, final List<?> defaultValue) {
        final List<Object> list = new ArrayList<Object>();
        final Iterator<Configuration> it = this.configList.iterator();
        while (it.hasNext() && list.isEmpty()) {
            final Configuration config = it.next();
            if (config != this.inMemoryConfiguration && config.containsKey(key)) {
                this.appendListProperty(list, config, key);
            }
        }
        this.appendListProperty(list, this.inMemoryConfiguration, key);
        if (list.isEmpty()) {
            final List<Object> resultList = (List<Object>)defaultValue;
            return resultList;
        }
        final ListIterator<Object> lit = list.listIterator();
        while (lit.hasNext()) {
            lit.set(this.interpolate(lit.next()));
        }
        return list;
    }
    
    @Override
    public String[] getStringArray(final String key) {
        final List<Object> list = this.getList(key);
        final String[] tokens = new String[list.size()];
        for (int i = 0; i < tokens.length; ++i) {
            tokens[i] = String.valueOf(list.get(i));
        }
        return tokens;
    }
    
    public Configuration getConfiguration(final int index) {
        this.beginRead(false);
        try {
            return this.configList.get(index);
        }
        finally {
            this.endRead();
        }
    }
    
    public Configuration getInMemoryConfiguration() {
        this.beginRead(false);
        try {
            return this.inMemoryConfiguration;
        }
        finally {
            this.endRead();
        }
    }
    
    public Object clone() {
        try {
            final CompositeConfiguration copy = (CompositeConfiguration)super.clone();
            copy.configList = new LinkedList<Configuration>();
            copy.inMemoryConfiguration = ConfigurationUtils.cloneConfiguration(this.getInMemoryConfiguration());
            copy.configList.add(copy.inMemoryConfiguration);
            for (final Configuration config : this.configList) {
                if (config != this.getInMemoryConfiguration()) {
                    copy.addConfiguration(ConfigurationUtils.cloneConfiguration(config));
                }
            }
            copy.cloneInterpolator(this);
            return copy;
        }
        catch (CloneNotSupportedException cnex) {
            throw new ConfigurationRuntimeException(cnex);
        }
    }
    
    @Override
    public void setListDelimiterHandler(final ListDelimiterHandler listDelimiterHandler) {
        if (this.inMemoryConfiguration instanceof AbstractConfiguration) {
            ((AbstractConfiguration)this.inMemoryConfiguration).setListDelimiterHandler(listDelimiterHandler);
        }
        super.setListDelimiterHandler(listDelimiterHandler);
    }
    
    public Configuration getSource(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null!");
        }
        Configuration source = null;
        for (final Configuration conf : this.configList) {
            if (conf.containsKey(key)) {
                if (source != null) {
                    throw new IllegalArgumentException("The key " + key + " is defined by multiple sources!");
                }
                source = conf;
            }
        }
        return source;
    }
    
    private void replaceInMemoryConfiguration(final Configuration config) {
        if (!this.inMemoryConfigIsChild) {
            this.configList.remove(this.inMemoryConfiguration);
        }
        this.inMemoryConfiguration = config;
    }
    
    private void appendListProperty(final List<Object> dest, final Configuration config, final String key) {
        final Object value = this.interpolate(config.getProperty(key));
        if (value != null) {
            if (value instanceof Collection) {
                final Collection<?> col = (Collection<?>)value;
                dest.addAll(col);
            }
            else {
                dest.add(value);
            }
        }
    }
}
