// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import java.util.concurrent.ConcurrentHashMap;

public final class GregorianChronology extends BasicGJChronology
{
    private static final long serialVersionUID = -861407383323710522L;
    private static final long MILLIS_PER_YEAR = 31556952000L;
    private static final long MILLIS_PER_MONTH = 2629746000L;
    private static final int DAYS_0000_TO_1970 = 719527;
    private static final int MIN_YEAR = -292275054;
    private static final int MAX_YEAR = 292278993;
    private static final GregorianChronology INSTANCE_UTC;
    private static final ConcurrentHashMap<DateTimeZone, GregorianChronology[]> cCache;
    
    public static GregorianChronology getInstanceUTC() {
        return GregorianChronology.INSTANCE_UTC;
    }
    
    public static GregorianChronology getInstance() {
        return getInstance(DateTimeZone.getDefault(), 4);
    }
    
    public static GregorianChronology getInstance(final DateTimeZone dateTimeZone) {
        return getInstance(dateTimeZone, 4);
    }
    
    public static GregorianChronology getInstance(DateTimeZone default1, final int i) {
        if (default1 == null) {
            default1 = DateTimeZone.getDefault();
        }
        GregorianChronology[] value = GregorianChronology.cCache.get(default1);
        if (value == null) {
            value = new GregorianChronology[7];
            final GregorianChronology[] array = GregorianChronology.cCache.putIfAbsent(default1, value);
            if (array != null) {
                value = array;
            }
        }
        GregorianChronology instance;
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
                        instance = new GregorianChronology(null, null, i);
                    }
                    else {
                        instance = getInstance(DateTimeZone.UTC, i);
                        instance = new GregorianChronology(ZonedChronology.getInstance(instance, default1), null, i);
                    }
                    value[i - 1] = instance;
                }
            }
        }
        return instance;
    }
    
    private GregorianChronology(final Chronology chronology, final Object o, final int n) {
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
        return GregorianChronology.INSTANCE_UTC;
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
    protected void assemble(final Fields fields) {
        if (this.getBase() == null) {
            super.assemble(fields);
        }
    }
    
    @Override
    boolean isLeapYear(final int n) {
        return (n & 0x3) == 0x0 && (n % 100 != 0 || n % 400 == 0);
    }
    
    @Override
    long calculateFirstDayOfYearMillis(final int n) {
        final int n2 = n / 100;
        int n3;
        if (n < 0) {
            n3 = (n + 3 >> 2) - n2 + (n2 + 3 >> 2) - 1;
        }
        else {
            n3 = (n >> 2) - n2 + (n2 >> 2);
            if (this.isLeapYear(n)) {
                --n3;
            }
        }
        return (n * 365L + (n3 - 719527)) * 86400000L;
    }
    
    @Override
    int getMinYear() {
        return -292275054;
    }
    
    @Override
    int getMaxYear() {
        return 292278993;
    }
    
    @Override
    long getAverageMillisPerYear() {
        return 31556952000L;
    }
    
    @Override
    long getAverageMillisPerYearDividedByTwo() {
        return 15778476000L;
    }
    
    @Override
    long getAverageMillisPerMonth() {
        return 2629746000L;
    }
    
    @Override
    long getApproxMillisAtEpochDividedByTwo() {
        return 31083597720000L;
    }
    
    static {
        cCache = new ConcurrentHashMap<DateTimeZone, GregorianChronology[]>();
        INSTANCE_UTC = getInstance(DateTimeZone.UTC);
    }
}
