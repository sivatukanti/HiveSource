// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.tz;

import java.util.Locale;

public interface NameProvider
{
    String getShortName(final Locale p0, final String p1, final String p2);
    
    String getName(final Locale p0, final String p1, final String p2);
}
