// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import java.io.IOException;
import java.io.ObjectInputStream;
import org.joda.time.DateTimeZone;
import org.joda.time.DateTimeField;
import org.joda.time.DurationField;
import org.joda.time.Chronology;

public abstract class AssembledChronology extends BaseChronology
{
    private static final long serialVersionUID = -6728465968995518215L;
    private final Chronology iBase;
    private final Object iParam;
    private transient DurationField iMillis;
    private transient DurationField iSeconds;
    private transient DurationField iMinutes;
    private transient DurationField iHours;
    private transient DurationField iHalfdays;
    private transient DurationField iDays;
    private transient DurationField iWeeks;
    private transient DurationField iWeekyears;
    private transient DurationField iMonths;
    private transient DurationField iYears;
    private transient DurationField iCenturies;
    private transient DurationField iEras;
    private transient DateTimeField iMillisOfSecond;
    private transient DateTimeField iMillisOfDay;
    private transient DateTimeField iSecondOfMinute;
    private transient DateTimeField iSecondOfDay;
    private transient DateTimeField iMinuteOfHour;
    private transient DateTimeField iMinuteOfDay;
    private transient DateTimeField iHourOfDay;
    private transient DateTimeField iClockhourOfDay;
    private transient DateTimeField iHourOfHalfday;
    private transient DateTimeField iClockhourOfHalfday;
    private transient DateTimeField iHalfdayOfDay;
    private transient DateTimeField iDayOfWeek;
    private transient DateTimeField iDayOfMonth;
    private transient DateTimeField iDayOfYear;
    private transient DateTimeField iWeekOfWeekyear;
    private transient DateTimeField iWeekyear;
    private transient DateTimeField iWeekyearOfCentury;
    private transient DateTimeField iMonthOfYear;
    private transient DateTimeField iYear;
    private transient DateTimeField iYearOfEra;
    private transient DateTimeField iYearOfCentury;
    private transient DateTimeField iCenturyOfEra;
    private transient DateTimeField iEra;
    private transient int iBaseFlags;
    
    protected AssembledChronology(final Chronology iBase, final Object iParam) {
        this.iBase = iBase;
        this.iParam = iParam;
        this.setFields();
    }
    
    @Override
    public DateTimeZone getZone() {
        final Chronology iBase;
        if ((iBase = this.iBase) != null) {
            return iBase.getZone();
        }
        return null;
    }
    
    @Override
    public long getDateTimeMillis(final int n, final int n2, final int n3, final int n4) throws IllegalArgumentException {
        final Chronology iBase;
        if ((iBase = this.iBase) != null && (this.iBaseFlags & 0x6) == 0x6) {
            return iBase.getDateTimeMillis(n, n2, n3, n4);
        }
        return super.getDateTimeMillis(n, n2, n3, n4);
    }
    
    @Override
    public long getDateTimeMillis(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) throws IllegalArgumentException {
        final Chronology iBase;
        if ((iBase = this.iBase) != null && (this.iBaseFlags & 0x5) == 0x5) {
            return iBase.getDateTimeMillis(n, n2, n3, n4, n5, n6, n7);
        }
        return super.getDateTimeMillis(n, n2, n3, n4, n5, n6, n7);
    }
    
    @Override
    public long getDateTimeMillis(final long n, final int n2, final int n3, final int n4, final int n5) throws IllegalArgumentException {
        final Chronology iBase;
        if ((iBase = this.iBase) != null && (this.iBaseFlags & 0x1) == 0x1) {
            return iBase.getDateTimeMillis(n, n2, n3, n4, n5);
        }
        return super.getDateTimeMillis(n, n2, n3, n4, n5);
    }
    
    @Override
    public final DurationField millis() {
        return this.iMillis;
    }
    
    @Override
    public final DateTimeField millisOfSecond() {
        return this.iMillisOfSecond;
    }
    
    @Override
    public final DateTimeField millisOfDay() {
        return this.iMillisOfDay;
    }
    
    @Override
    public final DurationField seconds() {
        return this.iSeconds;
    }
    
    @Override
    public final DateTimeField secondOfMinute() {
        return this.iSecondOfMinute;
    }
    
    @Override
    public final DateTimeField secondOfDay() {
        return this.iSecondOfDay;
    }
    
    @Override
    public final DurationField minutes() {
        return this.iMinutes;
    }
    
    @Override
    public final DateTimeField minuteOfHour() {
        return this.iMinuteOfHour;
    }
    
    @Override
    public final DateTimeField minuteOfDay() {
        return this.iMinuteOfDay;
    }
    
    @Override
    public final DurationField hours() {
        return this.iHours;
    }
    
    @Override
    public final DateTimeField hourOfDay() {
        return this.iHourOfDay;
    }
    
    @Override
    public final DateTimeField clockhourOfDay() {
        return this.iClockhourOfDay;
    }
    
    @Override
    public final DurationField halfdays() {
        return this.iHalfdays;
    }
    
    @Override
    public final DateTimeField hourOfHalfday() {
        return this.iHourOfHalfday;
    }
    
    @Override
    public final DateTimeField clockhourOfHalfday() {
        return this.iClockhourOfHalfday;
    }
    
    @Override
    public final DateTimeField halfdayOfDay() {
        return this.iHalfdayOfDay;
    }
    
    @Override
    public final DurationField days() {
        return this.iDays;
    }
    
    @Override
    public final DateTimeField dayOfWeek() {
        return this.iDayOfWeek;
    }
    
    @Override
    public final DateTimeField dayOfMonth() {
        return this.iDayOfMonth;
    }
    
    @Override
    public final DateTimeField dayOfYear() {
        return this.iDayOfYear;
    }
    
    @Override
    public final DurationField weeks() {
        return this.iWeeks;
    }
    
    @Override
    public final DateTimeField weekOfWeekyear() {
        return this.iWeekOfWeekyear;
    }
    
    @Override
    public final DurationField weekyears() {
        return this.iWeekyears;
    }
    
    @Override
    public final DateTimeField weekyear() {
        return this.iWeekyear;
    }
    
    @Override
    public final DateTimeField weekyearOfCentury() {
        return this.iWeekyearOfCentury;
    }
    
    @Override
    public final DurationField months() {
        return this.iMonths;
    }
    
    @Override
    public final DateTimeField monthOfYear() {
        return this.iMonthOfYear;
    }
    
    @Override
    public final DurationField years() {
        return this.iYears;
    }
    
    @Override
    public final DateTimeField year() {
        return this.iYear;
    }
    
    @Override
    public final DateTimeField yearOfEra() {
        return this.iYearOfEra;
    }
    
    @Override
    public final DateTimeField yearOfCentury() {
        return this.iYearOfCentury;
    }
    
    @Override
    public final DurationField centuries() {
        return this.iCenturies;
    }
    
    @Override
    public final DateTimeField centuryOfEra() {
        return this.iCenturyOfEra;
    }
    
    @Override
    public final DurationField eras() {
        return this.iEras;
    }
    
    @Override
    public final DateTimeField era() {
        return this.iEra;
    }
    
    protected abstract void assemble(final Fields p0);
    
    protected final Chronology getBase() {
        return this.iBase;
    }
    
    protected final Object getParam() {
        return this.iParam;
    }
    
    private void setFields() {
        final Fields fields = new Fields();
        if (this.iBase != null) {
            fields.copyFieldsFrom(this.iBase);
        }
        this.assemble(fields);
        final DurationField millis;
        this.iMillis = (((millis = fields.millis) != null) ? millis : super.millis());
        final DurationField seconds;
        this.iSeconds = (((seconds = fields.seconds) != null) ? seconds : super.seconds());
        final DurationField minutes;
        this.iMinutes = (((minutes = fields.minutes) != null) ? minutes : super.minutes());
        final DurationField hours;
        this.iHours = (((hours = fields.hours) != null) ? hours : super.hours());
        final DurationField halfdays;
        this.iHalfdays = (((halfdays = fields.halfdays) != null) ? halfdays : super.halfdays());
        final DurationField days;
        this.iDays = (((days = fields.days) != null) ? days : super.days());
        final DurationField weeks;
        this.iWeeks = (((weeks = fields.weeks) != null) ? weeks : super.weeks());
        final DurationField weekyears;
        this.iWeekyears = (((weekyears = fields.weekyears) != null) ? weekyears : super.weekyears());
        final DurationField months;
        this.iMonths = (((months = fields.months) != null) ? months : super.months());
        final DurationField years;
        this.iYears = (((years = fields.years) != null) ? years : super.years());
        final DurationField centuries;
        this.iCenturies = (((centuries = fields.centuries) != null) ? centuries : super.centuries());
        final DurationField eras;
        this.iEras = (((eras = fields.eras) != null) ? eras : super.eras());
        final DateTimeField millisOfSecond;
        this.iMillisOfSecond = (((millisOfSecond = fields.millisOfSecond) != null) ? millisOfSecond : super.millisOfSecond());
        final DateTimeField millisOfDay;
        this.iMillisOfDay = (((millisOfDay = fields.millisOfDay) != null) ? millisOfDay : super.millisOfDay());
        final DateTimeField secondOfMinute;
        this.iSecondOfMinute = (((secondOfMinute = fields.secondOfMinute) != null) ? secondOfMinute : super.secondOfMinute());
        final DateTimeField secondOfDay;
        this.iSecondOfDay = (((secondOfDay = fields.secondOfDay) != null) ? secondOfDay : super.secondOfDay());
        final DateTimeField minuteOfHour;
        this.iMinuteOfHour = (((minuteOfHour = fields.minuteOfHour) != null) ? minuteOfHour : super.minuteOfHour());
        final DateTimeField minuteOfDay;
        this.iMinuteOfDay = (((minuteOfDay = fields.minuteOfDay) != null) ? minuteOfDay : super.minuteOfDay());
        final DateTimeField hourOfDay;
        this.iHourOfDay = (((hourOfDay = fields.hourOfDay) != null) ? hourOfDay : super.hourOfDay());
        final DateTimeField clockhourOfDay;
        this.iClockhourOfDay = (((clockhourOfDay = fields.clockhourOfDay) != null) ? clockhourOfDay : super.clockhourOfDay());
        final DateTimeField hourOfHalfday;
        this.iHourOfHalfday = (((hourOfHalfday = fields.hourOfHalfday) != null) ? hourOfHalfday : super.hourOfHalfday());
        final DateTimeField clockhourOfHalfday;
        this.iClockhourOfHalfday = (((clockhourOfHalfday = fields.clockhourOfHalfday) != null) ? clockhourOfHalfday : super.clockhourOfHalfday());
        final DateTimeField halfdayOfDay;
        this.iHalfdayOfDay = (((halfdayOfDay = fields.halfdayOfDay) != null) ? halfdayOfDay : super.halfdayOfDay());
        final DateTimeField dayOfWeek;
        this.iDayOfWeek = (((dayOfWeek = fields.dayOfWeek) != null) ? dayOfWeek : super.dayOfWeek());
        final DateTimeField dayOfMonth;
        this.iDayOfMonth = (((dayOfMonth = fields.dayOfMonth) != null) ? dayOfMonth : super.dayOfMonth());
        final DateTimeField dayOfYear;
        this.iDayOfYear = (((dayOfYear = fields.dayOfYear) != null) ? dayOfYear : super.dayOfYear());
        final DateTimeField weekOfWeekyear;
        this.iWeekOfWeekyear = (((weekOfWeekyear = fields.weekOfWeekyear) != null) ? weekOfWeekyear : super.weekOfWeekyear());
        final DateTimeField weekyear;
        this.iWeekyear = (((weekyear = fields.weekyear) != null) ? weekyear : super.weekyear());
        final DateTimeField weekyearOfCentury;
        this.iWeekyearOfCentury = (((weekyearOfCentury = fields.weekyearOfCentury) != null) ? weekyearOfCentury : super.weekyearOfCentury());
        final DateTimeField monthOfYear;
        this.iMonthOfYear = (((monthOfYear = fields.monthOfYear) != null) ? monthOfYear : super.monthOfYear());
        final DateTimeField year;
        this.iYear = (((year = fields.year) != null) ? year : super.year());
        final DateTimeField yearOfEra;
        this.iYearOfEra = (((yearOfEra = fields.yearOfEra) != null) ? yearOfEra : super.yearOfEra());
        final DateTimeField yearOfCentury;
        this.iYearOfCentury = (((yearOfCentury = fields.yearOfCentury) != null) ? yearOfCentury : super.yearOfCentury());
        final DateTimeField centuryOfEra;
        this.iCenturyOfEra = (((centuryOfEra = fields.centuryOfEra) != null) ? centuryOfEra : super.centuryOfEra());
        final DateTimeField era;
        this.iEra = (((era = fields.era) != null) ? era : super.era());
        this.iBaseFlags = ((this.iBase != null && (((this.iHourOfDay == this.iBase.hourOfDay() && this.iMinuteOfHour == this.iBase.minuteOfHour() && this.iSecondOfMinute == this.iBase.secondOfMinute() && this.iMillisOfSecond == this.iBase.millisOfSecond()) ? 1 : 0) | ((this.iMillisOfDay == this.iBase.millisOfDay()) ? 2 : 0) | ((this.iYear == this.iBase.year() && this.iMonthOfYear == this.iBase.monthOfYear() && this.iDayOfMonth == this.iBase.dayOfMonth()) ? 4 : 0))) ? 1 : 0);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.setFields();
    }
    
    public static final class Fields
    {
        public DurationField millis;
        public DurationField seconds;
        public DurationField minutes;
        public DurationField hours;
        public DurationField halfdays;
        public DurationField days;
        public DurationField weeks;
        public DurationField weekyears;
        public DurationField months;
        public DurationField years;
        public DurationField centuries;
        public DurationField eras;
        public DateTimeField millisOfSecond;
        public DateTimeField millisOfDay;
        public DateTimeField secondOfMinute;
        public DateTimeField secondOfDay;
        public DateTimeField minuteOfHour;
        public DateTimeField minuteOfDay;
        public DateTimeField hourOfDay;
        public DateTimeField clockhourOfDay;
        public DateTimeField hourOfHalfday;
        public DateTimeField clockhourOfHalfday;
        public DateTimeField halfdayOfDay;
        public DateTimeField dayOfWeek;
        public DateTimeField dayOfMonth;
        public DateTimeField dayOfYear;
        public DateTimeField weekOfWeekyear;
        public DateTimeField weekyear;
        public DateTimeField weekyearOfCentury;
        public DateTimeField monthOfYear;
        public DateTimeField year;
        public DateTimeField yearOfEra;
        public DateTimeField yearOfCentury;
        public DateTimeField centuryOfEra;
        public DateTimeField era;
        
        Fields() {
        }
        
        public void copyFieldsFrom(final Chronology chronology) {
            final DurationField millis;
            if (isSupported(millis = chronology.millis())) {
                this.millis = millis;
            }
            final DurationField seconds;
            if (isSupported(seconds = chronology.seconds())) {
                this.seconds = seconds;
            }
            final DurationField minutes;
            if (isSupported(minutes = chronology.minutes())) {
                this.minutes = minutes;
            }
            final DurationField hours;
            if (isSupported(hours = chronology.hours())) {
                this.hours = hours;
            }
            final DurationField halfdays;
            if (isSupported(halfdays = chronology.halfdays())) {
                this.halfdays = halfdays;
            }
            final DurationField days;
            if (isSupported(days = chronology.days())) {
                this.days = days;
            }
            final DurationField weeks;
            if (isSupported(weeks = chronology.weeks())) {
                this.weeks = weeks;
            }
            final DurationField weekyears;
            if (isSupported(weekyears = chronology.weekyears())) {
                this.weekyears = weekyears;
            }
            final DurationField months;
            if (isSupported(months = chronology.months())) {
                this.months = months;
            }
            final DurationField years;
            if (isSupported(years = chronology.years())) {
                this.years = years;
            }
            final DurationField centuries;
            if (isSupported(centuries = chronology.centuries())) {
                this.centuries = centuries;
            }
            final DurationField eras;
            if (isSupported(eras = chronology.eras())) {
                this.eras = eras;
            }
            final DateTimeField millisOfSecond;
            if (isSupported(millisOfSecond = chronology.millisOfSecond())) {
                this.millisOfSecond = millisOfSecond;
            }
            final DateTimeField millisOfDay;
            if (isSupported(millisOfDay = chronology.millisOfDay())) {
                this.millisOfDay = millisOfDay;
            }
            final DateTimeField secondOfMinute;
            if (isSupported(secondOfMinute = chronology.secondOfMinute())) {
                this.secondOfMinute = secondOfMinute;
            }
            final DateTimeField secondOfDay;
            if (isSupported(secondOfDay = chronology.secondOfDay())) {
                this.secondOfDay = secondOfDay;
            }
            final DateTimeField minuteOfHour;
            if (isSupported(minuteOfHour = chronology.minuteOfHour())) {
                this.minuteOfHour = minuteOfHour;
            }
            final DateTimeField minuteOfDay;
            if (isSupported(minuteOfDay = chronology.minuteOfDay())) {
                this.minuteOfDay = minuteOfDay;
            }
            final DateTimeField hourOfDay;
            if (isSupported(hourOfDay = chronology.hourOfDay())) {
                this.hourOfDay = hourOfDay;
            }
            final DateTimeField clockhourOfDay;
            if (isSupported(clockhourOfDay = chronology.clockhourOfDay())) {
                this.clockhourOfDay = clockhourOfDay;
            }
            final DateTimeField hourOfHalfday;
            if (isSupported(hourOfHalfday = chronology.hourOfHalfday())) {
                this.hourOfHalfday = hourOfHalfday;
            }
            final DateTimeField clockhourOfHalfday;
            if (isSupported(clockhourOfHalfday = chronology.clockhourOfHalfday())) {
                this.clockhourOfHalfday = clockhourOfHalfday;
            }
            final DateTimeField halfdayOfDay;
            if (isSupported(halfdayOfDay = chronology.halfdayOfDay())) {
                this.halfdayOfDay = halfdayOfDay;
            }
            final DateTimeField dayOfWeek;
            if (isSupported(dayOfWeek = chronology.dayOfWeek())) {
                this.dayOfWeek = dayOfWeek;
            }
            final DateTimeField dayOfMonth;
            if (isSupported(dayOfMonth = chronology.dayOfMonth())) {
                this.dayOfMonth = dayOfMonth;
            }
            final DateTimeField dayOfYear;
            if (isSupported(dayOfYear = chronology.dayOfYear())) {
                this.dayOfYear = dayOfYear;
            }
            final DateTimeField weekOfWeekyear;
            if (isSupported(weekOfWeekyear = chronology.weekOfWeekyear())) {
                this.weekOfWeekyear = weekOfWeekyear;
            }
            final DateTimeField weekyear;
            if (isSupported(weekyear = chronology.weekyear())) {
                this.weekyear = weekyear;
            }
            final DateTimeField weekyearOfCentury;
            if (isSupported(weekyearOfCentury = chronology.weekyearOfCentury())) {
                this.weekyearOfCentury = weekyearOfCentury;
            }
            final DateTimeField monthOfYear;
            if (isSupported(monthOfYear = chronology.monthOfYear())) {
                this.monthOfYear = monthOfYear;
            }
            final DateTimeField year;
            if (isSupported(year = chronology.year())) {
                this.year = year;
            }
            final DateTimeField yearOfEra;
            if (isSupported(yearOfEra = chronology.yearOfEra())) {
                this.yearOfEra = yearOfEra;
            }
            final DateTimeField yearOfCentury;
            if (isSupported(yearOfCentury = chronology.yearOfCentury())) {
                this.yearOfCentury = yearOfCentury;
            }
            final DateTimeField centuryOfEra;
            if (isSupported(centuryOfEra = chronology.centuryOfEra())) {
                this.centuryOfEra = centuryOfEra;
            }
            final DateTimeField era;
            if (isSupported(era = chronology.era())) {
                this.era = era;
            }
        }
        
        private static boolean isSupported(final DurationField durationField) {
            return durationField != null && durationField.isSupported();
        }
        
        private static boolean isSupported(final DateTimeField dateTimeField) {
            return dateTimeField != null && dateTimeField.isSupported();
        }
    }
}
