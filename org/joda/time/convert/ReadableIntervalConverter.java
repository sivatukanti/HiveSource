// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.convert;

import org.joda.time.ReadWritableInterval;
import org.joda.time.ReadablePeriod;
import org.joda.time.DateTimeUtils;
import org.joda.time.Chronology;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.ReadableInterval;

class ReadableIntervalConverter extends AbstractConverter implements IntervalConverter, DurationConverter, PeriodConverter
{
    static final ReadableIntervalConverter INSTANCE;
    
    protected ReadableIntervalConverter() {
    }
    
    public long getDurationMillis(final Object o) {
        return ((ReadableInterval)o).toDurationMillis();
    }
    
    public void setInto(final ReadWritablePeriod readWritablePeriod, final Object o, Chronology chronology) {
        final ReadableInterval readableInterval = (ReadableInterval)o;
        chronology = ((chronology != null) ? chronology : DateTimeUtils.getIntervalChronology(readableInterval));
        final int[] value = chronology.get(readWritablePeriod, readableInterval.getStartMillis(), readableInterval.getEndMillis());
        for (int i = 0; i < value.length; ++i) {
            readWritablePeriod.setValue(i, value[i]);
        }
    }
    
    @Override
    public boolean isReadableInterval(final Object o, final Chronology chronology) {
        return true;
    }
    
    public void setInto(final ReadWritableInterval readWritableInterval, final Object o, final Chronology chronology) {
        final ReadableInterval interval = (ReadableInterval)o;
        readWritableInterval.setInterval(interval);
        if (chronology != null) {
            readWritableInterval.setChronology(chronology);
        }
        else {
            readWritableInterval.setChronology(interval.getChronology());
        }
    }
    
    public Class<?> getSupportedType() {
        return ReadableInterval.class;
    }
    
    static {
        INSTANCE = new ReadableIntervalConverter();
    }
}
