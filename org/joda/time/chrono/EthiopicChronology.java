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

public final class EthiopicChronology extends BasicFixedMonthChronology
{
    private static final long serialVersionUID = -5972804258688333942L;
    public static final int EE = 1;
    private static final DateTimeField ERA_FIELD;
    private static final int MIN_YEAR = -292269337;
    private static final int MAX_YEAR = 292272984;
    private static final ConcurrentHashMap<DateTimeZone, EthiopicChronology[]> cCache;
    private static final EthiopicChronology INSTANCE_UTC;
    
    public static EthiopicChronology getInstanceUTC() {
        return EthiopicChronology.INSTANCE_UTC;
    }
    
    public static EthiopicChronology getInstance() {
        return getInstance(DateTimeZone.getDefault(), 4);
    }
    
    public static EthiopicChronology getInstance(final DateTimeZone dateTimeZone) {
        return getInstance(dateTimeZone, 4);
    }
    
    public static EthiopicChronology getInstance(DateTimeZone default1, final int i) {
        if (default1 == null) {
            default1 = DateTimeZone.getDefault();
        }
        EthiopicChronology[] value = EthiopicChronology.cCache.get(default1);
        if (value == null) {
            value = new EthiopicChronology[7];
            final EthiopicChronology[] array = EthiopicChronology.cCache.putIfAbsent(default1, value);
            if (array != null) {
                value = array;
            }
        }
        EthiopicChronology instance;
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
                        instance = new EthiopicChronology(null, null, i);
                        instance = new EthiopicChronology(LimitChronology.getInstance(instance, new DateTime(1, 1, 1, 0, 0, 0, 0, instance), null), null, i);
                    }
                    else {
                        instance = getInstance(DateTimeZone.UTC, i);
                        instance = new EthiopicChronology(ZonedChronology.getInstance(instance, default1), null, i);
                    }
                    value[i - 1] = instance;
                }
            }
        }
        return instance;
    }
    
    EthiopicChronology(final Chronology chronology, final Object o, final int n) {
        super(chronology, o, n);
    }
    
    private Object readResolve() {
        final Chronology base = this.getBase();
        return (base == null) ? getInstance(DateTimeZone.UTC, this.getMinimumDaysInFirstWeek()) : getInstance(base.getZone(), this.getMinimumDaysInFirstWeek());
    }
    
    @Override
    public Chronology withUTC() {
        return EthiopicChronology.INSTANCE_UTC;
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
        final int n2 = n - 1963;
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
        return 292272984;
    }
    
    @Override
    long getApproxMillisAtEpochDividedByTwo() {
        return 30962844000000L;
    }
    
    @Override
    protected void assemble(final Fields fields) {
        if (this.getBase() == null) {
            super.assemble(fields);
            fields.year = new SkipDateTimeField(this, fields.year);
            fields.weekyear = new SkipDateTimeField(this, fields.weekyear);
            fields.era = EthiopicChronology.ERA_FIELD;
            fields.monthOfYear = new BasicMonthOfYearDateTimeField(this, 13);
            fields.months = fields.monthOfYear.getDurationField();
        }
    }
    
    static {
        ERA_FIELD = new BasicSingleEraDateTimeField("EE");
        cCache = new ConcurrentHashMap<DateTimeZone, EthiopicChronology[]>();
        INSTANCE_UTC = getInstance(DateTimeZone.UTC);
    }
}
