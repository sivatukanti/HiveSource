// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.StringUtils;

public enum NullValue
{
    EXCEPTION, 
    DEFAULT, 
    NONE;
    
    public static NullValue getNullValue(final String value) {
        if (StringUtils.isWhitespace(value)) {
            return NullValue.NONE;
        }
        if (NullValue.DEFAULT.toString().equalsIgnoreCase(value)) {
            return NullValue.DEFAULT;
        }
        if (NullValue.EXCEPTION.toString().equalsIgnoreCase(value)) {
            return NullValue.EXCEPTION;
        }
        if (NullValue.NONE.toString().equalsIgnoreCase(value)) {
            return NullValue.NONE;
        }
        return NullValue.NONE;
    }
}
