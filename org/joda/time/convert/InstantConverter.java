// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.convert;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;

public interface InstantConverter extends Converter
{
    Chronology getChronology(final Object p0, final DateTimeZone p1);
    
    Chronology getChronology(final Object p0, final Chronology p1);
    
    long getInstantMillis(final Object p0, final Chronology p1);
}
