// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import java.util.Calendar;
import java.util.TimeZone;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class UTCClock implements Clock
{
    private final TimeZone utcZone;
    
    public UTCClock() {
        this.utcZone = TimeZone.getTimeZone("UTC");
    }
    
    @Override
    public long getTime() {
        return Calendar.getInstance(this.utcZone).getTimeInMillis();
    }
}
