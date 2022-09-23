// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.convert;

import org.joda.time.PeriodType;
import org.joda.time.ReadablePeriod;
import org.joda.time.Chronology;
import org.joda.time.ReadWritablePeriod;

class ReadablePeriodConverter extends AbstractConverter implements PeriodConverter
{
    static final ReadablePeriodConverter INSTANCE;
    
    protected ReadablePeriodConverter() {
    }
    
    public void setInto(final ReadWritablePeriod readWritablePeriod, final Object o, final Chronology chronology) {
        readWritablePeriod.setPeriod((ReadablePeriod)o);
    }
    
    @Override
    public PeriodType getPeriodType(final Object o) {
        return ((ReadablePeriod)o).getPeriodType();
    }
    
    public Class<?> getSupportedType() {
        return ReadablePeriod.class;
    }
    
    static {
        INSTANCE = new ReadablePeriodConverter();
    }
}
