// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.StringUtils;

public enum IndexedValue
{
    TRUE, 
    FALSE, 
    UNIQUE;
    
    public static IndexedValue getIndexedValue(final String value) {
        if (StringUtils.isWhitespace(value)) {
            return null;
        }
        if (IndexedValue.TRUE.toString().equals(value)) {
            return IndexedValue.TRUE;
        }
        if (IndexedValue.FALSE.toString().equals(value)) {
            return IndexedValue.FALSE;
        }
        if (IndexedValue.UNIQUE.toString().equals(value)) {
            return IndexedValue.UNIQUE;
        }
        return IndexedValue.TRUE;
    }
}
