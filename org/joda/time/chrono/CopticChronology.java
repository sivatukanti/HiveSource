// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.field.SkipDateTimeField;
import org.joda.time.ReadableDateTime;
import org.joda.time.DateTime;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import java.util.concurrent.ConcurrentHashMap;
import org.joda.time.DateTimeField;

public final class CopticChronology extends BasicFixedMonthChronology
{
    private static final long serialVersionUID = -5972804258688333942L;
    public static final int AM = 1;
    private static final DateTimeField ERA_FIELD;
    private static final int MIN_YEAR = -292269337;
    private static final int MAX_YEAR = 292272708;
    private static final ConcurrentHashMap<DateTimeZone, CopticChronology[]> cCache;
    private static final CopticChronology INSTANCE_UTC;
    
    public static CopticChronology getInstanceUTC() {
        return CopticChronology.INSTANCE_UTC;
    }
    
    public static CopticChronology getInstance() {
        return getInstance(DateTimeZone.getDefault(), 4);
    }
    
    public static CopticChronology getInstance(final DateTimeZone dateTimeZone) {
        return getInstance(dateTimeZone, 4);
    }
    
    public static CopticChronology getInstance(DateTimeZone default1, final int i) {
        if (default1 == null) {
            default1 = DateTimeZone.getDefault();
        }
        CopticChronology[] value = CopticChronology.cCache.get(default1);
        if (value == null) {
            value = new CopticChronology[7];
            final CopticChronology[] array = CopticChronology.cCache.putIfAbsent(default1, value);
            if (array != null) {
                value = array;
            }
        }
        CopticChronology instance;
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
                        instance = new CopticChronology(null, null, i);
                        instance = new CopticChronology(LimitChronology.getInstance(instance, new DateTime(1, 1, 1, 0, 0, 0, 0, instance), null), null, i);
                    }
                    else {
                        instance = getInstance(DateTimeZone.UTC, i);
                        instance = new CopticChronology(ZonedChronology.getInstance(instance, default1), null, i);
                    }
                    value[i - 1] = instance;
                }
            }
        }
        return instance;
    }
    
    CopticChronology(final Chronology chronology, final Object o, final int n) {
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
        return CopticChronology.INSTANCE_UTC;
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
    boolean isLeapDay(final long n) {
        return this.dayOfMonth().get(n) == 6 && this.monthOfYear().isLeap(n);
    }
    
    @Override
    long calculateFirstDayOfYearMillis(final int n) {
        final int n2 = n - 1687;
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
        return (n2 * 365L + n3) * 86400000L + 21859200000L;
    }
    
    @Override
    int getMinYear() {
        return -292269337;
    }
    
    @Override
    int getMaxYear() {
        return 292272708;
    }
    
    @Override
    long getApproxMillisAtEpochDividedByTwo() {
        return 26607895200000L;
    }
    
    @Override
    protected void assemble(final Fields fields) {
        if (this.getBase() == null) {
            super.assemble(fields);
            fields.year = new SkipDateTimeField(this, fields.year);
            fields.weekyear = new SkipDateTimeField(this, fields.weekyear);
            fields.era = CopticChronology.ERA_FIELD;
            fields.monthOfYear = new BasicMonthOfYearDateTimeField(this, 13);
            fields.months = fields.monthOfYear.getDurationField();
        }
    }
    
    static {
        ERA_FIELD = new BasicSingleEraDateTimeField("AM");
        cCache = new ConcurrentHashMap<DateTimeZone, CopticChronology[]>();
        INSTANCE_UTC = getInstance(DateTimeZone.UTC);
    }
}
