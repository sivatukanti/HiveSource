// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.convert;

import java.util.List;
import java.util.Collection;

public interface ListDelimiterHandler
{
    public static final ValueTransformer NOOP_TRANSFORMER = new ValueTransformer() {
        @Override
        public Object transformValue(final Object value) {
            return value;
        }
    };
    
    Iterable<?> parse(final Object p0);
    
    Collection<String> split(final String p0, final boolean p1);
    
    Object escape(final Object p0, final ValueTransformer p1);
    
    Object escapeList(final List<?> p0, final ValueTransformer p1);
}
