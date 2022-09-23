// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.convert;

import java.util.Date;
import org.joda.time.Chronology;

final class DateConverter extends AbstractConverter implements InstantConverter, PartialConverter
{
    static final DateConverter INSTANCE;
    
    protected DateConverter() {
    }
    
    @Override
    public long getInstantMillis(final Object o, final Chronology chronology) {
        return ((Date)o).getTime();
    }
    
    public Class<?> getSupportedType() {
        return Date.class;
    }
    
    static {
        INSTANCE = new DateConverter();
    }
}
