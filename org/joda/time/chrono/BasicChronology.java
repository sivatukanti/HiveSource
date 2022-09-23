// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import java.util.Locale;
import org.joda.time.field.ZeroIsMaxDateTimeField;
import org.joda.time.field.PreciseDateTimeField;
import org.joda.time.field.PreciseDurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.field.MillisDurationField;
import org.joda.time.field.RemainderDateTimeField;
import org.joda.time.field.DividedDateTimeField;
import org.joda.time.field.OffsetDateTimeField;
import org.joda.time.field.FieldUtils;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DurationField;

abstract class BasicChronology extends AssembledChronology
{
    private static final long serialVersionUID = 8283225332206808863L;
    private static final DurationField cMillisField;
    private static final DurationField cSecondsField;
    private static final DurationField cMinutesField;
    private static final DurationField cHoursField;
    private static final DurationField cHalfdaysField;
    private static final DurationField cDaysField;
    private static final DurationField cWeeksField;
    private static final DateTimeField cMillisOfSecondField;
    private static final DateTimeField cMillisOfDayField;
    private static final DateTimeField cSecondOfMinuteField;
    private static final DateTimeField cSecondOfDayField;
    private static final DateTimeField cMinuteOfHourField;
    private static final DateTimeField cMinuteOfDayField;
    private static final DateTimeField cHourOfDayField;
    private static final DateTimeField cHourOfHalfdayField;
    private static final DateTimeField cClockhourOfDayField;
    private static final DateTimeField cClockhourOfHalfdayField;
    private static final DateTimeField cHalfdayOfDayField;
    private static final int CACHE_SIZE = 1024;
    private static final int CACHE_MASK = 1023;
    private final transient YearInfo[] iYearInfoCache;
    private final int iMinDaysInFirstWeek;
    
    BasicChronology(final Chronology chronology, final Object o, final int n) {
        super(chronology, o);
        this.iYearInfoCache = new YearInfo[1024];
        if (n < 1 || n > 7) {
            throw new IllegalArgumentException("Invalid min days in first week: " + n);
        }
        this.iMinDaysInFirstWeek = n;
    }
    
    @Override
    public DateTimeZone getZone() {
        final Chronology base;
        if ((base = this.getBase()) != null) {
            return base.getZone();
        }
        return DateTimeZone.UTC;
    }
    
    @Override
    public long getDateTimeMillis(final int n, final int n2, final int n3, final int n4) throws IllegalArgumentException {
        final Chronology base;
        if ((base = this.getBase()) != null) {
            return base.getDateTimeMillis(n, n2, n3, n4);
        }
        FieldUtils.verifyValueBounds(DateTimeFieldType.millisOfDay(), n4, 0, 86399999);
        return this.getDateMidnightMillis(n, n2, n3) + n4;
    }
    
    @Override
    public long getDateTimeMillis(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) throws IllegalArgumentException {
        final Chronology base;
        if ((base = this.getBase()) != null) {
            return base.getDateTimeMillis(n, n2, n3, n4, n5, n6, n7);
        }
        FieldUtils.verifyValueBounds(DateTimeFieldType.hourOfDay(), n4, 0, 23);
        FieldUtils.verifyValueBounds(DateTimeFieldType.minuteOfHour(), n5, 0, 59);
        FieldUtils.verifyValueBounds(DateTimeFieldType.secondOfMinute(), n6, 0, 59);
        FieldUtils.verifyValueBounds(DateTimeFieldType.millisOfSecond(), n7, 0, 999);
        return this.getDateMidnightMillis(n, n2, n3) + n4 * 3600000 + n5 * 60000 + n6 * 1000 + n7;
    }
    
    public int getMinimumDaysInFirstWeek() {
        return this.iMinDaysInFirstWeek;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && this.getClass() == o.getClass()) {
            final BasicChronology basicChronology = (BasicChronology)o;
            return this.getMinimumDaysInFirstWeek() == basicChronology.getMinimumDaysInFirstWeek() && this.getZone().equals(basicChronology.getZone());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getClass().getName().hashCode() * 11 + this.getZone().hashCode() + this.getMinimumDaysInFirstWeek();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(60);
        String str = this.getClass().getName();
        final int lastIndex = str.lastIndexOf(46);
        if (lastIndex >= 0) {
            str = str.substring(lastIndex + 1);
        }
        sb.append(str);
        sb.append('[');
        final DateTimeZone zone = this.getZone();
        if (zone != null) {
            sb.append(zone.getID());
        }
        if (this.getMinimumDaysInFirstWeek() != 4) {
            sb.append(",mdfw=");
            sb.append(this.getMinimumDaysInFirstWeek());
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    protected void assemble(final Fields fields) {
        fields.millis = BasicChronology.cMillisField;
        fields.seconds = BasicChronology.cSecondsField;
        fields.minutes = BasicChronology.cMinutesField;
        fields.hours = BasicChronology.cHoursField;
        fields.halfdays = BasicChronology.cHalfdaysField;
        fields.days = BasicChronology.cDaysField;
        fields.weeks = BasicChronology.cWeeksField;
        fields.millisOfSecond = BasicChronology.cMillisOfSecondField;
        fields.millisOfDay = BasicChronology.cMillisOfDayField;
        fields.secondOfMinute = BasicChronology.cSecondOfMinuteField;
        fields.secondOfDay = BasicChronology.cSecondOfDayField;
        fields.minuteOfHour = BasicChronology.cMinuteOfHourField;
        fields.minuteOfDay = BasicChronology.cMinuteOfDayField;
        fields.hourOfDay = BasicChronology.cHourOfDayField;
        fields.hourOfHalfday = BasicChronology.cHourOfHalfdayField;
        fields.clockhourOfDay = BasicChronology.cClockhourOfDayField;
        fields.clockhourOfHalfday = BasicChronology.cClockhourOfHalfdayField;
        fields.halfdayOfDay = BasicChronology.cHalfdayOfDayField;
        fields.year = new BasicYearDateTimeField(this);
        fields.yearOfEra = new GJYearOfEraDateTimeField(fields.year, this);
        fields.centuryOfEra = new DividedDateTimeField(new OffsetDateTimeField(fields.yearOfEra, 99), DateTimeFieldType.centuryOfEra(), 100);
        fields.centuries = fields.centuryOfEra.getDurationField();
        fields.yearOfCentury = new OffsetDateTimeField(new RemainderDateTimeField((DividedDateTimeField)fields.centuryOfEra), DateTimeFieldType.yearOfCentury(), 1);
        fields.era = new GJEraDateTimeField(this);
        fields.dayOfWeek = new GJDayOfWeekDateTimeField(this, fields.days);
        fields.dayOfMonth = new BasicDayOfMonthDateTimeField(this, fields.days);
        fields.dayOfYear = new BasicDayOfYearDateTimeField(this, fields.days);
        fields.monthOfYear = new GJMonthOfYearDateTimeField(this);
        fields.weekyear = new BasicWeekyearDateTimeField(this);
        fields.weekOfWeekyear = new BasicWeekOfWeekyearDateTimeField(this, fields.weeks);
        fields.weekyearOfCentury = new OffsetDateTimeField(new RemainderDateTimeField(fields.weekyear, fields.centuries, DateTimeFieldType.weekyearOfCentury(), 100), DateTimeFieldType.weekyearOfCentury(), 1);
        fields.years = fields.year.getDurationField();
        fields.months = fields.monthOfYear.getDurationField();
        fields.weekyears = fields.weekyear.getDurationField();
    }
    
    int getDaysInYearMax() {
        return 366;
    }
    
    int getDaysInYear(final int n) {
        return this.isLeapYear(n) ? 366 : 365;
    }
    
    int getWeeksInYear(final int n) {
        return (int)((this.getFirstWeekOfYearMillis(n + 1) - this.getFirstWeekOfYearMillis(n)) / 604800000L);
    }
    
    long getFirstWeekOfYearMillis(final int n) {
        final long yearMillis = this.getYearMillis(n);
        final int dayOfWeek = this.getDayOfWeek(yearMillis);
        if (dayOfWeek > 8 - this.iMinDaysInFirstWeek) {
            return yearMillis + (8 - dayOfWeek) * 86400000L;
        }
        return yearMillis - (dayOfWeek - 1) * 86400000L;
    }
    
    long getYearMillis(final int n) {
        return this.getYearInfo(n).iFirstDayMillis;
    }
    
    long getYearMonthMillis(final int n, final int n2) {
        return this.getYearMillis(n) + this.getTotalMillisByYearMonth(n, n2);
    }
    
    long getYearMonthDayMillis(final int n, final int n2, final int n3) {
        return this.getYearMillis(n) + this.getTotalMillisByYearMonth(n, n2) + (n3 - 1) * 86400000L;
    }
    
    int getYear(final long n) {
        final long averageMillisPerYearDividedByTwo = this.getAverageMillisPerYearDividedByTwo();
        long n2 = (n >> 1) + this.getApproxMillisAtEpochDividedByTwo();
        if (n2 < 0L) {
            n2 = n2 - averageMillisPerYearDividedByTwo + 1L;
        }
        int n3 = (int)(n2 / averageMillisPerYearDividedByTwo);
        final long yearMillis = this.getYearMillis(n3);
        final long n4 = n - yearMillis;
        if (n4 < 0L) {
            --n3;
        }
        else if (n4 >= 31536000000L) {
            long n5;
            if (this.isLeapYear(n3)) {
                n5 = 31622400000L;
            }
            else {
                n5 = 31536000000L;
            }
            if (yearMillis + n5 <= n) {
                ++n3;
            }
        }
        return n3;
    }
    
    int getMonthOfYear(final long n) {
        return this.getMonthOfYear(n, this.getYear(n));
    }
    
    abstract int getMonthOfYear(final long p0, final int p1);
    
    int getDayOfMonth(final long n) {
        final int year = this.getYear(n);
        return this.getDayOfMonth(n, year, this.getMonthOfYear(n, year));
    }
    
    int getDayOfMonth(final long n, final int n2) {
        return this.getDayOfMonth(n, n2, this.getMonthOfYear(n, n2));
    }
    
    int getDayOfMonth(final long n, final int n2, final int n3) {
        return (int)((n - (this.getYearMillis(n2) + this.getTotalMillisByYearMonth(n2, n3))) / 86400000L) + 1;
    }
    
    int getDayOfYear(final long n) {
        return this.getDayOfYear(n, this.getYear(n));
    }
    
    int getDayOfYear(final long n, final int n2) {
        return (int)((n - this.getYearMillis(n2)) / 86400000L) + 1;
    }
    
    int getWeekyear(final long n) {
        final int year = this.getYear(n);
        final int weekOfWeekyear = this.getWeekOfWeekyear(n, year);
        if (weekOfWeekyear == 1) {
            return this.getYear(n + 604800000L);
        }
        if (weekOfWeekyear > 51) {
            return this.getYear(n - 1209600000L);
        }
        return year;
    }
    
    int getWeekOfWeekyear(final long n) {
        return this.getWeekOfWeekyear(n, this.getYear(n));
    }
    
    int getWeekOfWeekyear(final long n, final int n2) {
        final long firstWeekOfYearMillis = this.getFirstWeekOfYearMillis(n2);
        if (n < firstWeekOfYearMillis) {
            return this.getWeeksInYear(n2 - 1);
        }
        if (n >= this.getFirstWeekOfYearMillis(n2 + 1)) {
            return 1;
        }
        return (int)((n - firstWeekOfYearMillis) / 604800000L) + 1;
    }
    
    int getDayOfWeek(final long n) {
        long n2;
        if (n >= 0L) {
            n2 = n / 86400000L;
        }
        else {
            n2 = (n - 86399999L) / 86400000L;
            if (n2 < -3L) {
                return 7 + (int)((n2 + 4L) % 7L);
            }
        }
        return 1 + (int)((n2 + 3L) % 7L);
    }
    
    int getMillisOfDay(final long n) {
        if (n >= 0L) {
            return (int)(n % 86400000L);
        }
        return 86399999 + (int)((n + 1L) % 86400000L);
    }
    
    int getDaysInMonthMax() {
        return 31;
    }
    
    int getDaysInMonthMax(final long n) {
        final int year = this.getYear(n);
        return this.getDaysInYearMonth(year, this.getMonthOfYear(n, year));
    }
    
    int getDaysInMonthMaxForSet(final long n, final int n2) {
        return this.getDaysInMonthMax(n);
    }
    
    long getDateMidnightMillis(final int n, final int n2, final int n3) {
        FieldUtils.verifyValueBounds(DateTimeFieldType.year(), n, this.getMinYear(), this.getMaxYear());
        FieldUtils.verifyValueBounds(DateTimeFieldType.monthOfYear(), n2, 1, this.getMaxMonth(n));
        FieldUtils.verifyValueBounds(DateTimeFieldType.dayOfMonth(), n3, 1, this.getDaysInYearMonth(n, n2));
        return this.getYearMonthDayMillis(n, n2, n3);
    }
    
    abstract long getYearDifference(final long p0, final long p1);
    
    abstract boolean isLeapYear(final int p0);
    
    boolean isLeapDay(final long n) {
        return false;
    }
    
    abstract int getDaysInYearMonth(final int p0, final int p1);
    
    abstract int getDaysInMonthMax(final int p0);
    
    abstract long getTotalMillisByYearMonth(final int p0, final int p1);
    
    abstract long calculateFirstDayOfYearMillis(final int p0);
    
    abstract int getMinYear();
    
    abstract int getMaxYear();
    
    int getMaxMonth(final int n) {
        return this.getMaxMonth();
    }
    
    int getMaxMonth() {
        return 12;
    }
    
    abstract long getAverageMillisPerYear();
    
    abstract long getAverageMillisPerYearDividedByTwo();
    
    abstract long getAverageMillisPerMonth();
    
    abstract long getApproxMillisAtEpochDividedByTwo();
    
    abstract long setYear(final long p0, final int p1);
    
    private YearInfo getYearInfo(final int n) {
        YearInfo yearInfo = this.iYearInfoCache[n & 0x3FF];
        if (yearInfo == null || yearInfo.iYear != n) {
            yearInfo = new YearInfo(n, this.calculateFirstDayOfYearMillis(n));
            this.iYearInfoCache[n & 0x3FF] = yearInfo;
        }
        return yearInfo;
    }
    
    static {
        cMillisField = MillisDurationField.INSTANCE;
        cSecondsField = new PreciseDurationField(DurationFieldType.seconds(), 1000L);
        cMinutesField = new PreciseDurationField(DurationFieldType.minutes(), 60000L);
        cHoursField = new PreciseDurationField(DurationFieldType.hours(), 3600000L);
        cHalfdaysField = new PreciseDurationField(DurationFieldType.halfdays(), 43200000L);
        cDaysField = new PreciseDurationField(DurationFieldType.days(), 86400000L);
        cWeeksField = new PreciseDurationField(DurationFieldType.weeks(), 604800000L);
        cMillisOfSecondField = new PreciseDateTimeField(DateTimeFieldType.millisOfSecond(), BasicChronology.cMillisField, BasicChronology.cSecondsField);
        cMillisOfDayField = new PreciseDateTimeField(DateTimeFieldType.millisOfDay(), BasicChronology.cMillisField, BasicChronology.cDaysField);
        cSecondOfMinuteField = new PreciseDateTimeField(DateTimeFieldType.secondOfMinute(), BasicChronology.cSecondsField, BasicChronology.cMinutesField);
        cSecondOfDayField = new PreciseDateTimeField(DateTimeFieldType.secondOfDay(), BasicChronology.cSecondsField, BasicChronology.cDaysField);
        cMinuteOfHourField = new PreciseDateTimeField(DateTimeFieldType.minuteOfHour(), BasicChronology.cMinutesField, BasicChronology.cHoursField);
        cMinuteOfDayField = new PreciseDateTimeField(DateTimeFieldType.minuteOfDay(), BasicChronology.cMinutesField, BasicChronology.cDaysField);
        cHourOfDayField = new PreciseDateTimeField(DateTimeFieldType.hourOfDay(), BasicChronology.cHoursField, BasicChronology.cDaysField);
        cHourOfHalfdayField = new PreciseDateTimeField(DateTimeFieldType.hourOfHalfday(), BasicChronology.cHoursField, BasicChronology.cHalfdaysField);
        cClockhourOfDayField = new ZeroIsMaxDateTimeField(BasicChronology.cHourOfDayField, DateTimeFieldType.clockhourOfDay());
        cClockhourOfHalfdayField = new ZeroIsMaxDateTimeField(BasicChronology.cHourOfHalfdayField, DateTimeFieldType.clockhourOfHalfday());
        cHalfdayOfDayField = new HalfdayField();
    }
    
    private static class HalfdayField extends PreciseDateTimeField
    {
        private static final long serialVersionUID = 581601443656929254L;
        
        HalfdayField() {
            super(DateTimeFieldType.halfdayOfDay(), BasicChronology.cHalfdaysField, BasicChronology.cDaysField);
        }
        
        @Override
        public String getAsText(final int n, final Locale locale) {
            return GJLocaleSymbols.forLocale(locale).halfdayValueToText(n);
        }
        
        @Override
        public long set(final long n, final String s, final Locale locale) {
            return this.set(n, GJLocaleSymbols.forLocale(locale).halfdayTextToValue(s));
        }
        
        @Override
        public int getMaximumTextLength(final Locale locale) {
            return GJLocaleSymbols.forLocale(locale).getHalfdayMaxTextLength();
        }
    }
    
    private static class YearInfo
    {
        public final int iYear;
        public final long iFirstDayMillis;
        
        YearInfo(final int iYear, final long iFirstDayMillis) {
            this.iYear = iYear;
            this.iFirstDayMillis = iFirstDayMillis;
        }
    }
}
