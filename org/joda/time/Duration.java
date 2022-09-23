// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import org.joda.time.field.FieldUtils;
import org.joda.convert.FromString;
import java.io.Serializable;
import org.joda.time.base.BaseDuration;

public final class Duration extends BaseDuration implements ReadableDuration, Serializable
{
    public static final Duration ZERO;
    private static final long serialVersionUID = 2471658376918L;
    
    @FromString
    public static Duration parse(final String s) {
        return new Duration(s);
    }
    
    public static Duration standardDays(final long n) {
        if (n == 0L) {
            return Duration.ZERO;
        }
        return new Duration(FieldUtils.safeMultiply(n, 86400000));
    }
    
    public static Duration standardHours(final long n) {
        if (n == 0L) {
            return Duration.ZERO;
        }
        return new Duration(FieldUtils.safeMultiply(n, 3600000));
    }
    
    public static Duration standardMinutes(final long n) {
        if (n == 0L) {
            return Duration.ZERO;
        }
        return new Duration(FieldUtils.safeMultiply(n, 60000));
    }
    
    public static Duration standardSeconds(final long n) {
        if (n == 0L) {
            return Duration.ZERO;
        }
        return new Duration(FieldUtils.safeMultiply(n, 1000));
    }
    
    public static Duration millis(final long n) {
        if (n == 0L) {
            return Duration.ZERO;
        }
        return new Duration(n);
    }
    
    public Duration(final long n) {
        super(n);
    }
    
    public Duration(final long n, final long n2) {
        super(n, n2);
    }
    
    public Duration(final ReadableInstant readableInstant, final ReadableInstant readableInstant2) {
        super(readableInstant, readableInstant2);
    }
    
    public Duration(final Object o) {
        super(o);
    }
    
    public long getStandardDays() {
        return this.getMillis() / 86400000L;
    }
    
    public long getStandardHours() {
        return this.getMillis() / 3600000L;
    }
    
    public long getStandardMinutes() {
        return this.getMillis() / 60000L;
    }
    
    public long getStandardSeconds() {
        return this.getMillis() / 1000L;
    }
    
    @Override
    public Duration toDuration() {
        return this;
    }
    
    public Days toStandardDays() {
        return Days.days(FieldUtils.safeToInt(this.getStandardDays()));
    }
    
    public Hours toStandardHours() {
        return Hours.hours(FieldUtils.safeToInt(this.getStandardHours()));
    }
    
    public Minutes toStandardMinutes() {
        return Minutes.minutes(FieldUtils.safeToInt(this.getStandardMinutes()));
    }
    
    public Seconds toStandardSeconds() {
        return Seconds.seconds(FieldUtils.safeToInt(this.getStandardSeconds()));
    }
    
    public Duration withMillis(final long n) {
        if (n == this.getMillis()) {
            return this;
        }
        return new Duration(n);
    }
    
    public Duration withDurationAdded(final long n, final int n2) {
        if (n == 0L || n2 == 0) {
            return this;
        }
        return new Duration(FieldUtils.safeAdd(this.getMillis(), FieldUtils.safeMultiply(n, n2)));
    }
    
    public Duration withDurationAdded(final ReadableDuration readableDuration, final int n) {
        if (readableDuration == null || n == 0) {
            return this;
        }
        return this.withDurationAdded(readableDuration.getMillis(), n);
    }
    
    public Duration plus(final long n) {
        return this.withDurationAdded(n, 1);
    }
    
    public Duration plus(final ReadableDuration readableDuration) {
        if (readableDuration == null) {
            return this;
        }
        return this.withDurationAdded(readableDuration.getMillis(), 1);
    }
    
    public Duration minus(final long n) {
        return this.withDurationAdded(n, -1);
    }
    
    public Duration minus(final ReadableDuration readableDuration) {
        if (readableDuration == null) {
            return this;
        }
        return this.withDurationAdded(readableDuration.getMillis(), -1);
    }
    
    public Duration multipliedBy(final long n) {
        if (n == 1L) {
            return this;
        }
        return new Duration(FieldUtils.safeMultiply(this.getMillis(), n));
    }
    
    public Duration dividedBy(final long n) {
        if (n == 1L) {
            return this;
        }
        return new Duration(FieldUtils.safeDivide(this.getMillis(), n));
    }
    
    public Duration negated() {
        if (this.getMillis() == Long.MIN_VALUE) {
            throw new ArithmeticException("Negation of this duration would overflow");
        }
        return new Duration(-this.getMillis());
    }
    
    static {
        ZERO = new Duration(0L);
    }
}
