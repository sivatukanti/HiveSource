// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.convert;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.ReadablePartial;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;

public interface PartialConverter extends Converter
{
    Chronology getChronology(final Object p0, final DateTimeZone p1);
    
    Chronology getChronology(final Object p0, final Chronology p1);
    
    int[] getPartialValues(final ReadablePartial p0, final Object p1, final Chronology p2);
    
    int[] getPartialValues(final ReadablePartial p0, final Object p1, final Chronology p2, final DateTimeFormatter p3);
}
