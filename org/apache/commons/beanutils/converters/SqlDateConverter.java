// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.beanutils.converters;

import java.sql.Date;

public final class SqlDateConverter extends DateTimeConverter
{
    public SqlDateConverter() {
    }
    
    public SqlDateConverter(final Object defaultValue) {
        super(defaultValue);
    }
    
    @Override
    protected Class<?> getDefaultType() {
        return Date.class;
    }
}
