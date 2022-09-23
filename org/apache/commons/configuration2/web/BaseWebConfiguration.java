// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.web;

import java.util.Collection;
import org.apache.commons.configuration2.AbstractConfiguration;

abstract class BaseWebConfiguration extends AbstractConfiguration
{
    @Override
    protected boolean isEmptyInternal() {
        return !this.getKeysInternal().hasNext();
    }
    
    @Override
    protected boolean containsKeyInternal(final String key) {
        return this.getPropertyInternal(key) != null;
    }
    
    @Override
    protected void clearPropertyDirect(final String key) {
        throw new UnsupportedOperationException("Read only configuration");
    }
    
    @Override
    protected void addPropertyDirect(final String key, final Object obj) {
        throw new UnsupportedOperationException("Read only configuration");
    }
    
    protected Object handleDelimiters(Object value) {
        if (value instanceof String) {
            final Collection<String> values = this.getListDelimiterHandler().split((String)value, true);
            value = ((values.size() > 1) ? values : values.iterator().next());
        }
        return value;
    }
}
