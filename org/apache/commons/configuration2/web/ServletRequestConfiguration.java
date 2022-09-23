// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.web;

import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import javax.servlet.ServletRequest;

public class ServletRequestConfiguration extends BaseWebConfiguration
{
    protected ServletRequest request;
    
    public ServletRequestConfiguration(final ServletRequest request) {
        this.request = request;
    }
    
    @Override
    protected Object getPropertyInternal(final String key) {
        final String[] values = this.request.getParameterValues(key);
        if (values == null || values.length == 0) {
            return null;
        }
        if (values.length == 1) {
            return this.handleDelimiters(values[0]);
        }
        final List<Object> result = new ArrayList<Object>(values.length);
        for (final String value : values) {
            final Object val = this.handleDelimiters(value);
            if (val instanceof Collection) {
                result.addAll((Collection<?>)val);
            }
            else {
                result.add(val);
            }
        }
        return result;
    }
    
    @Override
    protected Iterator<String> getKeysInternal() {
        final Map<String, ?> parameterMap = this.request.getParameterMap();
        return parameterMap.keySet().iterator();
    }
}
