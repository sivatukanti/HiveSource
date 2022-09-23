// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

import java.util.Locale;
import org.joda.time.ReadWritablePeriod;

public interface PeriodParser
{
    int parseInto(final ReadWritablePeriod p0, final String p1, final int p2, final Locale p3);
}
