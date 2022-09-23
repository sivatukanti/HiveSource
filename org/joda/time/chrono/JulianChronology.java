// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.field.SkipDateTimeField;
import org.joda.time.Chronology;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import java.util.concurrent.ConcurrentHashMap;

public final class JulianChronology extends BasicGJChronology
{
    private static final long serialVersionUID = -8731039522547897247L;
    private static final long MILLIS_PER_YEAR = 31557600000L;
    private static final long MILLIS_PER_MONTH = 2629800000L;
    private static final int MIN_YEAR = -292269054;
    private static final int MAX_YEAR = 292272992;
    private static final JulianChronology INSTANCE_UTC;
    private static final ConcurrentHashMap<DateTimeZone, JulianChronology[]> cCache;
    
    static int adjustYearForSet(int i) {
        if (i <= 0) {
            if (i == 0) {
                throw new IllegalFieldValueException(DateTimeFieldType.year(), i, null, null);
            }
            ++i;
        }
        return i;
    }
    
    public static JulianChronology getInstanceUTC() {
        return JulianChronology.INSTANCE_UTC;
    }
    
    public static JulianChronology getInstance() {
        return getInstance(DateTimeZone.getDefault(), 4);
    }
    
    public static JulianChronology getInstance(final DateTimeZone dateTimeZone) {
        return getInstance(dateTimeZone, 4);
    }
    
    public static JulianChronology getInstance(DateTimeZone default1, final int i) {
        if (default1 == null) {
            default1 = DateTimeZone.getDefault();
        }
        JulianChronology[] value = JulianChronology.cCache.get(default1);
        if (value == null) {
            value = new JulianChronology[7];
            final JulianChronology[] array = JulianChronology.cCache.putIfAbsent(default1, value);
            if (array != null) {
                value = array;
            }
        }
        JulianChronology instance;
        try {
            instance = value[i - 1];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("Invalid min days in first week: " + i);
        }
        if (instance == null) {
            synchronized (value) {
                instance = value[i - 1];
                if (instance == null) {
                    if (default1 == DateTimeZone.UTC) {
                        instance = new JulianChronology(null, null, i);
                    }
                    else {
                        instance = getInstance(DateTimeZone.UTC, i);
                        instance = new JulianChronology(ZonedChronology.getInstance(instance, default1), null, i);
                    }
                    value[i - 1] = instance;
                }
            }
        }
        return instance;
    }
    
    JulianChronology(final Chronology chronology, final Object o, final int n) {
        super(chronology, o, n);
    }
    
    private Object readResolve() {
        final Chronology base = this.getBase();
        final int minimumDaysInFirstWeek = this.getMinimumDaysInFirstWeek();
        final int n = (minimumDaysInFirstWeek == 0) ? 4 : minimumDaysInFirstWeek;
        return (base == null) ? getInstance(DateTimeZone.UTC, n) : getInstance(base.getZone(), n);
    }
    
    @Override
    public Chronology withUTC() {
        return JulianChronology.INSTANCE_UTC;
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
    long getDateMidnightMillis(final int n, final int n2, final int n3) throws IllegalArgumentException {
        return super.getDateMidnightMillis(adjustYearForSet(n), n2, n3);
    }
    
    @Override
    boolean isLeapYear(final int n) {
        return (n & 0x3) == 0x0;
    }
    
    @Override
    long calculateFirstDayOfYearMillis(final int n) {
        final int n2 = n - 1968;
        int n3;
        if (n2 <= 0) {
            n3 = n2 + 3 >> 2;
        }
        else {
            n3 = n2 >> 2;
            if (!this.isLeapYear(n)) {
                ++n3;
            }
        }
        return (n2 * 365L + n3) * 86400000L - 62035200000L;
    }
    
    @Override
    int getMinYear() {
        return -292269054;
    }
    
    @Override
    int getMaxYear() {
        return 292272992;
    }
    
    @Override
    long getAverageMillisPerYear() {
        return 31557600000L;
    }
    
    @Override
    long getAverageMillisPerYearDividedByTwo() {
        return 15778800000L;
    }
    
    @Override
    long getAverageMillisPerMonth() {
        return 2629800000L;
    }
    
    @Override
    long getApproxMillisAtEpochDividedByTwo() {
        return 31083663600000L;
    }
    
    @Override
    protected void assemble(final Fields fields) {
        if (this.getBase() == null) {
            super.assemble(fields);
            fields.year = new SkipDateTimeField(this, fields.year);
            fields.weekyear = new SkipDateTimeField(this, fields.weekyear);
        }
    }
    
    static {
        cCache = new ConcurrentHashMap<DateTimeZone, JulianChronology[]>();
        INSTANCE_UTC = getInstance(DateTimeZone.UTC);
    }
}
