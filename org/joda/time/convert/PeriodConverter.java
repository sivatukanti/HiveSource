// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.convert;

import org.joda.time.PeriodType;
import org.joda.time.Chronology;
import org.joda.time.ReadWritablePeriod;

public interface PeriodConverter extends Converter
{
    void setInto(final ReadWritablePeriod p0, final Object p1, final Chronology p2);
    
    PeriodType getPeriodType(final Object p0);
}
