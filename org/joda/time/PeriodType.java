// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import java.util.HashMap;
import org.joda.time.field.FieldUtils;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.io.Serializable;

public class PeriodType implements Serializable
{
    private static final long serialVersionUID = 2274324892792009998L;
    private static final Map<PeriodType, Object> cTypes;
    static int YEAR_INDEX;
    static int MONTH_INDEX;
    static int WEEK_INDEX;
    static int DAY_INDEX;
    static int HOUR_INDEX;
    static int MINUTE_INDEX;
    static int SECOND_INDEX;
    static int MILLI_INDEX;
    private static PeriodType cStandard;
    private static PeriodType cYMDTime;
    private static PeriodType cYMD;
    private static PeriodType cYWDTime;
    private static PeriodType cYWD;
    private static PeriodType cYDTime;
    private static PeriodType cYD;
    private static PeriodType cDTime;
    private static PeriodType cTime;
    private static PeriodType cYears;
    private static PeriodType cMonths;
    private static PeriodType cWeeks;
    private static PeriodType cDays;
    private static PeriodType cHours;
    private static PeriodType cMinutes;
    private static PeriodType cSeconds;
    private static PeriodType cMillis;
    private final String iName;
    private final DurationFieldType[] iTypes;
    private final int[] iIndices;
    
    public static PeriodType standard() {
        PeriodType cStandard = PeriodType.cStandard;
        if (cStandard == null) {
            cStandard = (PeriodType.cStandard = new PeriodType("Standard", new DurationFieldType[] { DurationFieldType.years(), DurationFieldType.months(), DurationFieldType.weeks(), DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis() }, new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }));
        }
        return cStandard;
    }
    
    public static PeriodType yearMonthDayTime() {
        PeriodType cymdTime = PeriodType.cYMDTime;
        if (cymdTime == null) {
            cymdTime = (PeriodType.cYMDTime = new PeriodType("YearMonthDayTime", new DurationFieldType[] { DurationFieldType.years(), DurationFieldType.months(), DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis() }, new int[] { 0, 1, -1, 2, 3, 4, 5, 6 }));
        }
        return cymdTime;
    }
    
    public static PeriodType yearMonthDay() {
        PeriodType cymd = PeriodType.cYMD;
        if (cymd == null) {
            cymd = (PeriodType.cYMD = new PeriodType("YearMonthDay", new DurationFieldType[] { DurationFieldType.years(), DurationFieldType.months(), DurationFieldType.days() }, new int[] { 0, 1, -1, 2, -1, -1, -1, -1 }));
        }
        return cymd;
    }
    
    public static PeriodType yearWeekDayTime() {
        PeriodType cywdTime = PeriodType.cYWDTime;
        if (cywdTime == null) {
            cywdTime = (PeriodType.cYWDTime = new PeriodType("YearWeekDayTime", new DurationFieldType[] { DurationFieldType.years(), DurationFieldType.weeks(), DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis() }, new int[] { 0, -1, 1, 2, 3, 4, 5, 6 }));
        }
        return cywdTime;
    }
    
    public static PeriodType yearWeekDay() {
        PeriodType cywd = PeriodType.cYWD;
        if (cywd == null) {
            cywd = (PeriodType.cYWD = new PeriodType("YearWeekDay", new DurationFieldType[] { DurationFieldType.years(), DurationFieldType.weeks(), DurationFieldType.days() }, new int[] { 0, -1, 1, 2, -1, -1, -1, -1 }));
        }
        return cywd;
    }
    
    public static PeriodType yearDayTime() {
        PeriodType cydTime = PeriodType.cYDTime;
        if (cydTime == null) {
            cydTime = (PeriodType.cYDTime = new PeriodType("YearDayTime", new DurationFieldType[] { DurationFieldType.years(), DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis() }, new int[] { 0, -1, -1, 1, 2, 3, 4, 5 }));
        }
        return cydTime;
    }
    
    public static PeriodType yearDay() {
        PeriodType cyd = PeriodType.cYD;
        if (cyd == null) {
            cyd = (PeriodType.cYD = new PeriodType("YearDay", new DurationFieldType[] { DurationFieldType.years(), DurationFieldType.days() }, new int[] { 0, -1, -1, 1, -1, -1, -1, -1 }));
        }
        return cyd;
    }
    
    public static PeriodType dayTime() {
        PeriodType cdTime = PeriodType.cDTime;
        if (cdTime == null) {
            cdTime = (PeriodType.cDTime = new PeriodType("DayTime", new DurationFieldType[] { DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis() }, new int[] { -1, -1, -1, 0, 1, 2, 3, 4 }));
        }
        return cdTime;
    }
    
    public static PeriodType time() {
        PeriodType cTime = PeriodType.cTime;
        if (cTime == null) {
            cTime = (PeriodType.cTime = new PeriodType("Time", new DurationFieldType[] { DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis() }, new int[] { -1, -1, -1, -1, 0, 1, 2, 3 }));
        }
        return cTime;
    }
    
    public static PeriodType years() {
        PeriodType cYears = PeriodType.cYears;
        if (cYears == null) {
            cYears = (PeriodType.cYears = new PeriodType("Years", new DurationFieldType[] { DurationFieldType.years() }, new int[] { 0, -1, -1, -1, -1, -1, -1, -1 }));
        }
        return cYears;
    }
    
    public static PeriodType months() {
        PeriodType cMonths = PeriodType.cMonths;
        if (cMonths == null) {
            cMonths = (PeriodType.cMonths = new PeriodType("Months", new DurationFieldType[] { DurationFieldType.months() }, new int[] { -1, 0, -1, -1, -1, -1, -1, -1 }));
        }
        return cMonths;
    }
    
    public static PeriodType weeks() {
        PeriodType cWeeks = PeriodType.cWeeks;
        if (cWeeks == null) {
            cWeeks = (PeriodType.cWeeks = new PeriodType("Weeks", new DurationFieldType[] { DurationFieldType.weeks() }, new int[] { -1, -1, 0, -1, -1, -1, -1, -1 }));
        }
        return cWeeks;
    }
    
    public static PeriodType days() {
        PeriodType cDays = PeriodType.cDays;
        if (cDays == null) {
            cDays = (PeriodType.cDays = new PeriodType("Days", new DurationFieldType[] { DurationFieldType.days() }, new int[] { -1, -1, -1, 0, -1, -1, -1, -1 }));
        }
        return cDays;
    }
    
    public static PeriodType hours() {
        PeriodType cHours = PeriodType.cHours;
        if (cHours == null) {
            cHours = (PeriodType.cHours = new PeriodType("Hours", new DurationFieldType[] { DurationFieldType.hours() }, new int[] { -1, -1, -1, -1, 0, -1, -1, -1 }));
        }
        return cHours;
    }
    
    public static PeriodType minutes() {
        PeriodType cMinutes = PeriodType.cMinutes;
        if (cMinutes == null) {
            cMinutes = (PeriodType.cMinutes = new PeriodType("Minutes", new DurationFieldType[] { DurationFieldType.minutes() }, new int[] { -1, -1, -1, -1, -1, 0, -1, -1 }));
        }
        return cMinutes;
    }
    
    public static PeriodType seconds() {
        PeriodType cSeconds = PeriodType.cSeconds;
        if (cSeconds == null) {
            cSeconds = (PeriodType.cSeconds = new PeriodType("Seconds", new DurationFieldType[] { DurationFieldType.seconds() }, new int[] { -1, -1, -1, -1, -1, -1, 0, -1 }));
        }
        return cSeconds;
    }
    
    public static PeriodType millis() {
        PeriodType cMillis = PeriodType.cMillis;
        if (cMillis == null) {
            cMillis = (PeriodType.cMillis = new PeriodType("Millis", new DurationFieldType[] { DurationFieldType.millis() }, new int[] { -1, -1, -1, -1, -1, -1, -1, 0 }));
        }
        return cMillis;
    }
    
    public static synchronized PeriodType forFields(final DurationFieldType[] a) {
        if (a == null || a.length == 0) {
            throw new IllegalArgumentException("Types array must not be null or empty");
        }
        for (int i = 0; i < a.length; ++i) {
            if (a[i] == null) {
                throw new IllegalArgumentException("Types array must not contain null");
            }
        }
        final Map<PeriodType, Object> cTypes = PeriodType.cTypes;
        if (cTypes.isEmpty()) {
            cTypes.put(standard(), standard());
            cTypes.put(yearMonthDayTime(), yearMonthDayTime());
            cTypes.put(yearMonthDay(), yearMonthDay());
            cTypes.put(yearWeekDayTime(), yearWeekDayTime());
            cTypes.put(yearWeekDay(), yearWeekDay());
            cTypes.put(yearDayTime(), yearDayTime());
            cTypes.put(yearDay(), yearDay());
            cTypes.put(dayTime(), dayTime());
            cTypes.put(time(), time());
            cTypes.put(years(), years());
            cTypes.put(months(), months());
            cTypes.put(weeks(), weeks());
            cTypes.put(days(), days());
            cTypes.put(hours(), hours());
            cTypes.put(minutes(), minutes());
            cTypes.put(seconds(), seconds());
            cTypes.put(millis(), millis());
        }
        final PeriodType periodType = new PeriodType(null, a, null);
        final PeriodType value = cTypes.get(periodType);
        if (value instanceof PeriodType) {
            return value;
        }
        if (value != null) {
            throw new IllegalArgumentException("PeriodType does not support fields: " + value);
        }
        PeriodType periodType2 = standard();
        final ArrayList obj = new ArrayList(Arrays.asList(a));
        if (!obj.remove(DurationFieldType.years())) {
            periodType2 = periodType2.withYearsRemoved();
        }
        if (!obj.remove(DurationFieldType.months())) {
            periodType2 = periodType2.withMonthsRemoved();
        }
        if (!obj.remove(DurationFieldType.weeks())) {
            periodType2 = periodType2.withWeeksRemoved();
        }
        if (!obj.remove(DurationFieldType.days())) {
            periodType2 = periodType2.withDaysRemoved();
        }
        if (!obj.remove(DurationFieldType.hours())) {
            periodType2 = periodType2.withHoursRemoved();
        }
        if (!obj.remove(DurationFieldType.minutes())) {
            periodType2 = periodType2.withMinutesRemoved();
        }
        if (!obj.remove(DurationFieldType.seconds())) {
            periodType2 = periodType2.withSecondsRemoved();
        }
        if (!obj.remove(DurationFieldType.millis())) {
            periodType2 = periodType2.withMillisRemoved();
        }
        if (obj.size() > 0) {
            cTypes.put(periodType, obj);
            throw new IllegalArgumentException("PeriodType does not support fields: " + obj);
        }
        final PeriodType periodType3 = new PeriodType(null, periodType2.iTypes, null);
        final PeriodType periodType4 = cTypes.get(periodType3);
        if (periodType4 != null) {
            cTypes.put(periodType3, periodType4);
            return periodType4;
        }
        cTypes.put(periodType3, periodType2);
        return periodType2;
    }
    
    protected PeriodType(final String iName, final DurationFieldType[] iTypes, final int[] iIndices) {
        this.iName = iName;
        this.iTypes = iTypes;
        this.iIndices = iIndices;
    }
    
    public String getName() {
        return this.iName;
    }
    
    public int size() {
        return this.iTypes.length;
    }
    
    public DurationFieldType getFieldType(final int n) {
        return this.iTypes[n];
    }
    
    public boolean isSupported(final DurationFieldType durationFieldType) {
        return this.indexOf(durationFieldType) >= 0;
    }
    
    public int indexOf(final DurationFieldType durationFieldType) {
        for (int i = 0; i < this.size(); ++i) {
            if (this.iTypes[i] == durationFieldType) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public String toString() {
        return "PeriodType[" + this.getName() + "]";
    }
    
    int getIndexedField(final ReadablePeriod readablePeriod, final int n) {
        final int n2 = this.iIndices[n];
        return (n2 == -1) ? 0 : readablePeriod.getValue(n2);
    }
    
    boolean setIndexedField(final ReadablePeriod readablePeriod, final int n, final int[] array, final int n2) {
        final int n3 = this.iIndices[n];
        if (n3 == -1) {
            throw new UnsupportedOperationException("Field is not supported");
        }
        array[n3] = n2;
        return true;
    }
    
    boolean addIndexedField(final ReadablePeriod readablePeriod, final int n, final int[] array, final int n2) {
        if (n2 == 0) {
            return false;
        }
        final int n3 = this.iIndices[n];
        if (n3 == -1) {
            throw new UnsupportedOperationException("Field is not supported");
        }
        array[n3] = FieldUtils.safeAdd(array[n3], n2);
        return true;
    }
    
    public PeriodType withYearsRemoved() {
        return this.withFieldRemoved(0, "NoYears");
    }
    
    public PeriodType withMonthsRemoved() {
        return this.withFieldRemoved(1, "NoMonths");
    }
    
    public PeriodType withWeeksRemoved() {
        return this.withFieldRemoved(2, "NoWeeks");
    }
    
    public PeriodType withDaysRemoved() {
        return this.withFieldRemoved(3, "NoDays");
    }
    
    public PeriodType withHoursRemoved() {
        return this.withFieldRemoved(4, "NoHours");
    }
    
    public PeriodType withMinutesRemoved() {
        return this.withFieldRemoved(5, "NoMinutes");
    }
    
    public PeriodType withSecondsRemoved() {
        return this.withFieldRemoved(6, "NoSeconds");
    }
    
    public PeriodType withMillisRemoved() {
        return this.withFieldRemoved(7, "NoMillis");
    }
    
    private PeriodType withFieldRemoved(final int n, final String str) {
        final int n2 = this.iIndices[n];
        if (n2 == -1) {
            return this;
        }
        final DurationFieldType[] array = new DurationFieldType[this.size() - 1];
        for (int i = 0; i < this.iTypes.length; ++i) {
            if (i < n2) {
                array[i] = this.iTypes[i];
            }
            else if (i > n2) {
                array[i - 1] = this.iTypes[i];
            }
        }
        final int[] array2 = new int[8];
        for (int j = 0; j < array2.length; ++j) {
            if (j < n) {
                array2[j] = this.iIndices[j];
            }
            else if (j > n) {
                array2[j] = ((this.iIndices[j] == -1) ? -1 : (this.iIndices[j] - 1));
            }
            else {
                array2[j] = -1;
            }
        }
        return new PeriodType(this.getName() + str, array, array2);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof PeriodType && Arrays.equals(this.iTypes, ((PeriodType)o).iTypes));
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (int i = 0; i < this.iTypes.length; ++i) {
            n += this.iTypes[i].hashCode();
        }
        return n;
    }
    
    static {
        cTypes = new HashMap<PeriodType, Object>(32);
        PeriodType.YEAR_INDEX = 0;
        PeriodType.MONTH_INDEX = 1;
        PeriodType.WEEK_INDEX = 2;
        PeriodType.DAY_INDEX = 3;
        PeriodType.HOUR_INDEX = 4;
        PeriodType.MINUTE_INDEX = 5;
        PeriodType.SECOND_INDEX = 6;
        PeriodType.MILLI_INDEX = 7;
    }
}
