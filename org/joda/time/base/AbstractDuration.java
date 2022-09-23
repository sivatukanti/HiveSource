// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.base;

import org.joda.convert.ToString;
import org.joda.time.format.FormatUtils;
import org.joda.time.Period;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

public abstract class AbstractDuration implements ReadableDuration
{
    protected AbstractDuration() {
    }
    
    public Duration toDuration() {
        return new Duration(this.getMillis());
    }
    
    public Period toPeriod() {
        return new Period(this.getMillis());
    }
    
    public int compareTo(final ReadableDuration readableDuration) {
        final long millis = this.getMillis();
        final long millis2 = readableDuration.getMillis();
        if (millis < millis2) {
            return -1;
        }
        if (millis > millis2) {
            return 1;
        }
        return 0;
    }
    
    public boolean isEqual(ReadableDuration zero) {
        if (zero == null) {
            zero = Duration.ZERO;
        }
        return this.compareTo(zero) == 0;
    }
    
    public boolean isLongerThan(ReadableDuration zero) {
        if (zero == null) {
            zero = Duration.ZERO;
        }
        return this.compareTo(zero) > 0;
    }
    
    public boolean isShorterThan(ReadableDuration zero) {
        if (zero == null) {
            zero = Duration.ZERO;
        }
        return this.compareTo(zero) < 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ReadableDuration && this.getMillis() == ((ReadableDuration)o).getMillis());
    }
    
    @Override
    public int hashCode() {
        final long millis = this.getMillis();
        return (int)(millis ^ millis >>> 32);
    }
    
    @ToString
    @Override
    public String toString() {
        final long millis = this.getMillis();
        final StringBuffer sb = new StringBuffer();
        sb.append("PT");
        final boolean b = millis < 0L;
        FormatUtils.appendUnpaddedInteger(sb, millis);
        while (sb.length() < (b ? 7 : 6)) {
            sb.insert(b ? 3 : 2, "0");
        }
        if (millis / 1000L * 1000L == millis) {
            sb.setLength(sb.length() - 3);
        }
        else {
            sb.insert(sb.length() - 3, ".");
        }
        sb.append('S');
        return sb.toString();
    }
}
