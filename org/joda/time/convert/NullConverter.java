// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.convert;

import org.joda.time.DateTimeUtils;
import org.joda.time.ReadWritableInterval;
import org.joda.time.ReadablePeriod;
import org.joda.time.Period;
import org.joda.time.Chronology;
import org.joda.time.ReadWritablePeriod;

class NullConverter extends AbstractConverter implements InstantConverter, PartialConverter, DurationConverter, PeriodConverter, IntervalConverter
{
    static final NullConverter INSTANCE;
    
    protected NullConverter() {
    }
    
    public long getDurationMillis(final Object o) {
        return 0L;
    }
    
    public void setInto(final ReadWritablePeriod readWritablePeriod, final Object o, final Chronology chronology) {
        readWritablePeriod.setPeriod((ReadablePeriod)null);
    }
    
    public void setInto(final ReadWritableInterval readWritableInterval, final Object o, final Chronology chronology) {
        readWritableInterval.setChronology(chronology);
        final long currentTimeMillis = DateTimeUtils.currentTimeMillis();
        readWritableInterval.setInterval(currentTimeMillis, currentTimeMillis);
    }
    
    public Class<?> getSupportedType() {
        return null;
    }
    
    static {
        INSTANCE = new NullConverter();
    }
}
