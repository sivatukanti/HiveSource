// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DisabledListDelimiterHandler extends AbstractListDelimiterHandler
{
    public static final ListDelimiterHandler INSTANCE;
    
    @Override
    public Object escapeList(final List<?> values, final ValueTransformer transformer) {
        throw new UnsupportedOperationException("Escaping lists is not supported!");
    }
    
    @Override
    protected Collection<String> splitString(final String s, final boolean trim) {
        final Collection<String> result = new ArrayList<String>(1);
        result.add(s);
        return result;
    }
    
    @Override
    protected String escapeString(final String s) {
        return s;
    }
    
    static {
        INSTANCE = new DisabledListDelimiterHandler();
    }
}
