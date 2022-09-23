// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.Instant;
import org.joda.time.DateTimeZone;

class GJCacheKey
{
    private final DateTimeZone zone;
    private final Instant cutoverInstant;
    private final int minDaysInFirstWeek;
    
    GJCacheKey(final DateTimeZone zone, final Instant cutoverInstant, final int minDaysInFirstWeek) {
        this.zone = zone;
        this.cutoverInstant = cutoverInstant;
        this.minDaysInFirstWeek = minDaysInFirstWeek;
    }
    
    @Override
    public int hashCode() {
        return 31 * (31 * (31 * 1 + ((this.cutoverInstant == null) ? 0 : this.cutoverInstant.hashCode())) + this.minDaysInFirstWeek) + ((this.zone == null) ? 0 : this.zone.hashCode());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof GJCacheKey)) {
            return false;
        }
        final GJCacheKey gjCacheKey = (GJCacheKey)o;
        if (this.cutoverInstant == null) {
            if (gjCacheKey.cutoverInstant != null) {
                return false;
            }
        }
        else if (!this.cutoverInstant.equals(gjCacheKey.cutoverInstant)) {
            return false;
        }
        if (this.minDaysInFirstWeek != gjCacheKey.minDaysInFirstWeek) {
            return false;
        }
        if (this.zone == null) {
            if (gjCacheKey.zone != null) {
                return false;
            }
        }
        else if (!this.zone.equals(gjCacheKey.zone)) {
            return false;
        }
        return true;
    }
}
