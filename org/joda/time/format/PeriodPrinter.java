// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import org.joda.time.ReadablePeriod;

public interface PeriodPrinter
{
    int calculatePrintedLength(final ReadablePeriod p0, final Locale p1);
    
    int countFieldsToPrint(final ReadablePeriod p0, final int p1, final Locale p2);
    
    void printTo(final StringBuffer p0, final ReadablePeriod p1, final Locale p2);
    
    void printTo(final Writer p0, final ReadablePeriod p1, final Locale p2) throws IOException;
}
