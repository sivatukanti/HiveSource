// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.convert;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractListDelimiterHandler implements ListDelimiterHandler
{
    @Override
    public Iterable<?> parse(final Object value) {
        return this.flatten(value);
    }
    
    @Override
    public Collection<String> split(final String s, final boolean trim) {
        if (s == null) {
            return new ArrayList<String>(0);
        }
        return this.splitString(s, trim);
    }
    
    @Override
    public Object escape(final Object value, final ValueTransformer transformer) {
        final Object escValue = (value instanceof String) ? this.escapeString((String)value) : value;
        return transformer.transformValue(escValue);
    }
    
    protected abstract Collection<String> splitString(final String p0, final boolean p1);
    
    protected abstract String escapeString(final String p0);
    
    Collection<?> flatten(final Object value, final int limit) {
        if (value instanceof String) {
            return this.split((String)value, true);
        }
        final Collection<Object> result = new LinkedList<Object>();
        if (value instanceof Iterable) {
            this.flattenIterator(result, ((Iterable)value).iterator(), limit);
        }
        else if (value instanceof Iterator) {
            this.flattenIterator(result, (Iterator<?>)value, limit);
        }
        else if (value != null) {
            if (value.getClass().isArray()) {
                for (int len = Array.getLength(value), idx = 0, size = 0; idx < len && size < limit; ++idx, size = result.size()) {
                    result.addAll(this.flatten(Array.get(value, idx), limit - size));
                }
            }
            else {
                result.add(value);
            }
        }
        return result;
    }
    
    private Collection<?> flatten(final Object value) {
        return this.flatten(value, Integer.MAX_VALUE);
    }
    
    private void flattenIterator(final Collection<Object> target, final Iterator<?> it, final int limit) {
        for (int size = target.size(); size < limit && it.hasNext(); size = target.size()) {
            target.addAll(this.flatten(it.next(), limit - size));
        }
    }
}
