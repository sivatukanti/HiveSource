// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.tz;

import java.util.Collections;
import java.util.Set;
import org.joda.time.DateTimeZone;

public final class UTCProvider implements Provider
{
    public DateTimeZone getZone(final String anotherString) {
        if ("UTC".equalsIgnoreCase(anotherString)) {
            return DateTimeZone.UTC;
        }
        return null;
    }
    
    public Set<String> getAvailableIDs() {
        return Collections.singleton("UTC");
    }
}
