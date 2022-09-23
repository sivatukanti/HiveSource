// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.convert;

import org.joda.time.ReadWritableInterval;
import org.joda.time.Chronology;

public interface IntervalConverter extends Converter
{
    boolean isReadableInterval(final Object p0, final Chronology p1);
    
    void setInto(final ReadWritableInterval p0, final Object p1, final Chronology p2);
}
