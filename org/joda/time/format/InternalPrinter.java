// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

import org.joda.time.ReadablePartial;
import java.io.IOException;
import java.util.Locale;
import org.joda.time.DateTimeZone;
import org.joda.time.Chronology;

interface InternalPrinter
{
    int estimatePrintedLength();
    
    void printTo(final Appendable p0, final long p1, final Chronology p2, final int p3, final DateTimeZone p4, final Locale p5) throws IOException;
    
    void printTo(final Appendable p0, final ReadablePartial p1, final Locale p2) throws IOException;
}
