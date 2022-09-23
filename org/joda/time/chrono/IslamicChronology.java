// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import java.io.Serializable;
import org.joda.time.ReadableDateTime;
import org.joda.time.DateTime;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import java.util.concurrent.ConcurrentHashMap;
import org.joda.time.DateTimeField;

public final class IslamicChronology extends BasicChronology
{
    private static final long serialVersionUID = -3663823829888L;
    public static final int AH = 1;
    private static final DateTimeField ERA_FIELD;
    public static final LeapYearPatternType LEAP_YEAR_15_BASED;
    public static final LeapYearPatternType LEAP_YEAR_16_BASED;
    public static final LeapYearPatternType LEAP_YEAR_INDIAN;
    public static final LeapYearPatternType LEAP_YEAR_HABASH_AL_HASIB;
    private static final int MIN_YEAR = -292269337;
    private static final int MAX_YEAR = 292271022;
    private static final int MONTH_PAIR_LENGTH = 59;
    private static final int LONG_MONTH_LENGTH = 30;
    private static final int SHORT_MONTH_LENGTH = 29;
    private static final long MILLIS_PER_MONTH_PAIR = 5097600000L;
    private static final long MILLIS_PER_MONTH = 2551440384L;
    private static final long MILLIS_PER_LONG_MONTH = 2592000000L;
    private static final long MILLIS_PER_YEAR = 30617280288L;
    private static final long MILLIS_PER_SHORT_YEAR = 30585600000L;
    private static final long MILLIS_PER_LONG_YEAR = 30672000000L;
    private static final long MILLIS_YEAR_1 = -42521587200000L;
    private static final int CYCLE = 30;
    private static final long MILLIS_PER_CYCLE = 918518400000L;
    private static final ConcurrentHashMap<DateTimeZone, IslamicChronology[]> cCache;
    private static final IslamicChronology INSTANCE_UTC;
    private final LeapYearPatternType iLeapYears;
    
    public static IslamicChronology getInstanceUTC() {
        return IslamicChronology.INSTANCE_UTC;
    }
    
    public static IslamicChronology getInstance() {
        return getInstance(DateTimeZone.getDefault(), IslamicChronology.LEAP_YEAR_16_BASED);
    }
    
    public static IslamicChronology getInstance(final DateTimeZone dateTimeZone) {
        return getInstance(dateTimeZone, IslamicChronology.LEAP_YEAR_16_BASED);
    }
    
    public static IslamicChronology getInstance(DateTimeZone default1, final LeapYearPatternType leapYearPatternType) {
        if (default1 == null) {
            default1 = DateTimeZone.getDefault();
        }
        IslamicChronology[] value = IslamicChronology.cCache.get(default1);
        if (value == null) {
            value = new IslamicChronology[4];
            final IslamicChronology[] array = IslamicChronology.cCache.putIfAbsent(default1, value);
            if (array != null) {
                value = array;
            }
        }
        IslamicChronology instance = value[leapYearPatternType.index];
        if (instance == null) {
            synchronized (value) {
                instance = value[leapYearPatternType.index];
                if (instance == null) {
                    if (default1 == DateTimeZone.UTC) {
                        instance = new IslamicChronology(null, null, leapYearPatternType);
                        instance = new IslamicChronology(LimitChronology.getInstance(instance, new DateTime(1, 1, 1, 0, 0, 0, 0, instance), null), null, leapYearPatternType);
                    }
                    else {
                        instance = getInstance(DateTimeZone.UTC, leapYearPatternType);
                        instance = new IslamicChronology(ZonedChronology.getInstance(instance, default1), null, leapYearPatternType);
                    }
                    value[leapYearPatternType.index] = instance;
                }
            }
        }
        return instance;
    }
    
    IslamicChronology(final Chronology chronology, final Object o, final LeapYearPatternType iLeapYears) {
        super(chronology, o, 4);
        this.iLeapYears = iLeapYears;
    }
    
    private Object readResolve() {
        final Chronology base = this.getBase();
        return (base == null) ? getInstanceUTC() : getInstance(base.getZone());
    }
    
    public LeapYearPatternType getLeapYearPatternType() {
        return this.iLeapYears;
    }
    
    @Override
    public Chronology withUTC() {
        return IslamicChronology.INSTANCE_UTC;
    }
    
    @Override
    public Chronology withZone(DateTimeZone default1) {
        if (default1 == null) {
            default1 = DateTimeZone.getDefault();
        }
        if (default1 == this.getZone()) {
            return this;
        }
        return getInstance(default1);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof IslamicChronology && this.getLeapYearPatternType().index == ((IslamicChronology)o).getLeapYearPatternType().index && super.equals(o));
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() * 13 + this.getLeapYearPatternType().hashCode();
    }
    
    @Override
    int getYear(final long n) {
        final long n2 = n + 42521587200000L;
        final long n3 = n2 / 918518400000L;
        long n4 = n2 % 918518400000L;
        int n5 = (int)(n3 * 30L + 1L);
        for (long n6 = this.isLeapYear(n5) ? 30672000000L : 30585600000L; n4 >= n6; n4 -= n6, n6 = (this.isLeapYear(++n5) ? 30672000000L : 30585600000L)) {}
        return n5;
    }
    
    @Override
    long setYear(long yearMonthDayMillis, final int n) {
        int dayOfYear = this.getDayOfYear(yearMonthDayMillis, this.getYear(yearMonthDayMillis));
        final int millisOfDay = this.getMillisOfDay(yearMonthDayMillis);
        if (dayOfYear > 354 && !this.isLeapYear(n)) {
            --dayOfYear;
        }
        yearMonthDayMillis = this.getYearMonthDayMillis(n, 1, dayOfYear);
        yearMonthDayMillis += millisOfDay;
        return yearMonthDayMillis;
    }
    
    @Override
    long getYearDifference(final long n, final long n2) {
        final int year = this.getYear(n);
        final int year2 = this.getYear(n2);
        final long n3 = n - this.getYearMillis(year);
        final long n4 = n2 - this.getYearMillis(year2);
        int n5 = year - year2;
        if (n3 < n4) {
            --n5;
        }
        return n5;
    }
    
    @Override
    long getTotalMillisByYearMonth(final int n, int n2) {
        if (--n2 % 2 == 1) {
            n2 /= 2;
            return n2 * 5097600000L + 2592000000L;
        }
        n2 /= 2;
        return n2 * 5097600000L;
    }
    
    @Override
    int getDayOfMonth(final long n) {
        final int n2 = this.getDayOfYear(n) - 1;
        if (n2 == 354) {
            return 30;
        }
        return n2 % 59 % 30 + 1;
    }
    
    @Override
    boolean isLeapYear(final int n) {
        return this.iLeapYears.isLeapYear(n);
    }
    
    @Override
    int getDaysInYearMax() {
        return 355;
    }
    
    @Override
    int getDaysInYear(final int n) {
        return this.isLeapYear(n) ? 355 : 354;
    }
    
    @Override
    int getDaysInYearMonth(final int n, int n2) {
        if (n2 == 12 && this.isLeapYear(n)) {
            return 30;
        }
        return (--n2 % 2 == 0) ? 30 : 29;
    }
    
    @Override
    int getDaysInMonthMax() {
        return 30;
    }
    
    @Override
    int getDaysInMonthMax(int n) {
        if (n == 12) {
            return 30;
        }
        return (--n % 2 == 0) ? 30 : 29;
    }
    
    @Override
    int getMonthOfYear(final long n, final int n2) {
        final int n3 = (int)((n - this.getYearMillis(n2)) / 86400000L);
        if (n3 == 354) {
            return 12;
        }
        return n3 * 2 / 59 + 1;
    }
    
    @Override
    long getAverageMillisPerYear() {
        return 30617280288L;
    }
    
    @Override
    long getAverageMillisPerYearDividedByTwo() {
        return 15308640144L;
    }
    
    @Override
    long getAverageMillisPerMonth() {
        return 2551440384L;
    }
    
    @Override
    long calculateFirstDayOfYearMillis(int n) {
        if (n > 292271022) {
            throw new ArithmeticException("Year is too large: " + n + " > " + 292271022);
        }
        if (n < -292269337) {
            throw new ArithmeticException("Year is too small: " + n + " < " + -292269337);
        }
        long n2 = -42521587200000L + --n / 30 * 918518400000L;
        for (int n3 = n % 30 + 1, i = 1; i < n3; ++i) {
            n2 += (this.isLeapYear(i) ? 30672000000L : 30585600000L);
        }
        return n2;
    }
    
    @Override
    int getMinYear() {
        return 1;
    }
    
    @Override
    int getMaxYear() {
        return 292271022;
    }
    
    @Override
    long getApproxMillisAtEpochDividedByTwo() {
        return 21260793600000L;
    }
    
    @Override
    protected void assemble(final Fields fields) {
        if (this.getBase() == null) {
            super.assemble(fields);
            fields.era = IslamicChronology.ERA_FIELD;
            fields.monthOfYear = new BasicMonthOfYearDateTimeField(this, 12);
            fields.months = fields.monthOfYear.getDurationField();
        }
    }
    
    static {
        ERA_FIELD = new BasicSingleEraDateTimeField("AH");
        LEAP_YEAR_15_BASED = new LeapYearPatternType(0, 623158436);
        LEAP_YEAR_16_BASED = new LeapYearPatternType(1, 623191204);
        LEAP_YEAR_INDIAN = new LeapYearPatternType(2, 690562340);
        LEAP_YEAR_HABASH_AL_HASIB = new LeapYearPatternType(3, 153692453);
        cCache = new ConcurrentHashMap<DateTimeZone, IslamicChronology[]>();
        INSTANCE_UTC = getInstance(DateTimeZone.UTC);
    }
    
    public static class LeapYearPatternType implements Serializable
    {
        private static final long serialVersionUID = 26581275372698L;
        final byte index;
        final int pattern;
        
        LeapYearPatternType(final int n, final int pattern) {
            this.index = (byte)n;
            this.pattern = pattern;
        }
        
        boolean isLeapYear(final int n) {
            return (this.pattern & 1 << n % 30) > 0;
        }
        
        private Object readResolve() {
            switch (this.index) {
                case 0: {
                    return IslamicChronology.LEAP_YEAR_15_BASED;
                }
                case 1: {
                    return IslamicChronology.LEAP_YEAR_16_BASED;
                }
                case 2: {
                    return IslamicChronology.LEAP_YEAR_INDIAN;
                }
                case 3: {
                    return IslamicChronology.LEAP_YEAR_HABASH_AL_HASIB;
                }
                default: {
                    return this;
                }
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof LeapYearPatternType && this.index == ((LeapYearPatternType)o).index;
        }
        
        @Override
        public int hashCode() {
            return this.index;
        }
    }
}
