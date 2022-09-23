// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.convert;

import org.joda.time.Chronology;

class LongConverter extends AbstractConverter implements InstantConverter, PartialConverter, DurationConverter
{
    static final LongConverter INSTANCE;
    
    protected LongConverter() {
    }
    
    @Override
    public long getInstantMillis(final Object o, final Chronology chronology) {
        return (long)o;
    }
    
    public long getDurationMillis(final Object o) {
        return (long)o;
    }
    
    public Class<?> getSupportedType() {
        return Long.class;
    }
    
    static {
        INSTANCE = new LongConverter();
    }
}
