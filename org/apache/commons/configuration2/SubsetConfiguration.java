// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import java.util.Iterator;

public class SubsetConfiguration extends AbstractConfiguration
{
    protected Configuration parent;
    protected String prefix;
    protected String delimiter;
    
    public SubsetConfiguration(final Configuration parent, final String prefix) {
        this(parent, prefix, null);
    }
    
    public SubsetConfiguration(final Configuration parent, final String prefix, final String delimiter) {
        if (parent == null) {
            throw new IllegalArgumentException("Parent configuration must not be null!");
        }
        this.parent = parent;
        this.prefix = prefix;
        this.delimiter = delimiter;
        this.initInterpolator();
    }
    
    protected String getParentKey(final String key) {
        if ("".equals(key) || key == null) {
            return this.prefix;
        }
        return (this.delimiter == null) ? (this.prefix + key) : (this.prefix + this.delimiter + key);
    }
    
    protected String getChildKey(final String key) {
        if (!key.startsWith(this.prefix)) {
            throw new IllegalArgumentException("The parent key '" + key + "' is not in the subset.");
        }
        String modifiedKey = null;
        if (key.length() == this.prefix.length()) {
            modifiedKey = "";
        }
        else {
            final int i = this.prefix.length() + ((this.delimiter != null) ? this.delimiter.length() : 0);
            modifiedKey = key.substring(i);
        }
        return modifiedKey;
    }
    
    public Configuration getParent() {
        return this.parent;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
    
    @Override
    public Configuration subset(final String prefix) {
        return this.parent.subset(this.getParentKey(prefix));
    }
    
    @Override
    protected boolean isEmptyInternal() {
        return !this.getKeysInternal().hasNext();
    }
    
    @Override
    protected boolean containsKeyInternal(final String key) {
        return this.parent.containsKey(this.getParentKey(key));
    }
    
    public void addPropertyDirect(final String key, final Object value) {
        this.parent.addProperty(this.getParentKey(key), value);
    }
    
    @Override
    protected void clearPropertyDirect(final String key) {
        this.parent.clearProperty(this.getParentKey(key));
    }
    
    @Override
    protected Object getPropertyInternal(final String key) {
        return this.parent.getProperty(this.getParentKey(key));
    }
    
    @Override
    protected Iterator<String> getKeysInternal(final String prefix) {
        return new SubsetIterator(this.parent.getKeys(this.getParentKey(prefix)));
    }
    
    @Override
    protected Iterator<String> getKeysInternal() {
        return new SubsetIterator(this.parent.getKeys(this.prefix));
    }
    
    @Override
    public void setThrowExceptionOnMissing(final boolean throwExceptionOnMissing) {
        if (this.parent instanceof AbstractConfiguration) {
            ((AbstractConfiguration)this.parent).setThrowExceptionOnMissing(throwExceptionOnMissing);
        }
        else {
            super.setThrowExceptionOnMissing(throwExceptionOnMissing);
        }
    }
    
    @Override
    public boolean isThrowExceptionOnMissing() {
        if (this.parent instanceof AbstractConfiguration) {
            return ((AbstractConfiguration)this.parent).isThrowExceptionOnMissing();
        }
        return super.isThrowExceptionOnMissing();
    }
    
    @Override
    public ListDelimiterHandler getListDelimiterHandler() {
        return (this.parent instanceof AbstractConfiguration) ? ((AbstractConfiguration)this.parent).getListDelimiterHandler() : super.getListDelimiterHandler();
    }
    
    @Override
    public void setListDelimiterHandler(final ListDelimiterHandler listDelimiterHandler) {
        if (this.parent instanceof AbstractConfiguration) {
            ((AbstractConfiguration)this.parent).setListDelimiterHandler(listDelimiterHandler);
        }
        else {
            super.setListDelimiterHandler(listDelimiterHandler);
        }
    }
    
    private void initInterpolator() {
        this.getInterpolator().setParentInterpolator(this.getParent().getInterpolator());
    }
    
    private class SubsetIterator implements Iterator<String>
    {
        private final Iterator<String> parentIterator;
        
        public SubsetIterator(final Iterator<String> it) {
            this.parentIterator = it;
        }
        
        @Override
        public boolean hasNext() {
            return this.parentIterator.hasNext();
        }
        
        @Override
        public String next() {
            return SubsetConfiguration.this.getChildKey(this.parentIterator.next());
        }
        
        @Override
        public void remove() {
            this.parentIterator.remove();
        }
    }
}
