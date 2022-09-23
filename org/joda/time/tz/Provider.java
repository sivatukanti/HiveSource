// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.tz;

import java.util.Set;
import org.joda.time.DateTimeZone;

public interface Provider
{
    DateTimeZone getZone(final String p0);
    
    Set<String> getAvailableIDs();
}
