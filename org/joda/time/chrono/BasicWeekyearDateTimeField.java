// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.DurationField;
import org.joda.time.DateTimeField;
import org.joda.time.field.FieldUtils;
import org.joda.time.DateTimeFieldType;
import org.joda.time.field.ImpreciseDateTimeField;

final class BasicWeekyearDateTimeField extends ImpreciseDateTimeField
{
    private static final long serialVersionUID = 6215066916806820644L;
    private static final long WEEK_53 = 31449600000L;
    private final BasicChronology iChronology;
    
    BasicWeekyearDateTimeField(final BasicChronology iChronology) {
        super(DateTimeFieldType.weekyear(), iChronology.getAverageMillisPerYear());
        this.iChronology = iChronology;
    }
    
    @Override
    public boolean isLenient() {
        return false;
    }
    
    @Override
    public int get(final long n) {
        return this.iChronology.getWeekyear(n);
    }
    
    @Override
    public long add(final long n, final int n2) {
        if (n2 == 0) {
            return n;
        }
        return this.set(n, this.get(n) + n2);
    }
    
    @Override
    public long add(final long n, final long n2) {
        return this.add(n, FieldUtils.safeToInt(n2));
    }
    
    @Override
    public long addWrapField(final long n, final int n2) {
        return this.add(n, n2);
    }
    
    @Override
    public long getDifferenceAsLong(final long n, final long n2) {
        if (n < n2) {
            return -this.getDifference(n2, n);
        }
        final int value = this.get(n);
        final int value2 = this.get(n2);
        final long remainder = this.remainder(n);
        long remainder2 = this.remainder(n2);
        if (remainder2 >= 31449600000L && this.iChronology.getWeeksInYear(value) <= 52) {
            remainder2 -= 604800000L;
        }
        int n3 = value - value2;
        if (remainder < remainder2) {
            --n3;
        }
        return n3;
    }
    
    @Override
    public long set(final long n, final int a) {
        FieldUtils.verifyValueBounds(this, Math.abs(a), this.iChronology.getMinYear(), this.iChronology.getMaxYear());
        final int value = this.get(n);
        if (value == a) {
            return n;
        }
        final int dayOfWeek = this.iChronology.getDayOfWeek(n);
        final int weeksInYear = this.iChronology.getWeeksInYear(value);
        final int weeksInYear2 = this.iChronology.getWeeksInYear(a);
        final int n2 = (weeksInYear2 < weeksInYear) ? weeksInYear2 : weeksInYear;
        int weekOfWeekyear = this.iChronology.getWeekOfWeekyear(n);
        if (weekOfWeekyear > n2) {
            weekOfWeekyear = n2;
        }
        long setYear = this.iChronology.setYear(n, a);
        final int value2 = this.get(setYear);
        if (value2 < a) {
            setYear += 604800000L;
        }
        else if (value2 > a) {
            setYear -= 604800000L;
        }
        return this.iChronology.dayOfWeek().set(setYear + (weekOfWeekyear - this.iChronology.getWeekOfWeekyear(setYear)) * 604800000L, dayOfWeek);
    }
    
    @Override
    public DurationField getRangeDurationField() {
        return null;
    }
    
    @Override
    public boolean isLeap(final long n) {
        return this.iChronology.getWeeksInYear(this.iChronology.getWeekyear(n)) > 52;
    }
    
    @Override
    public int getLeapAmount(final long n) {
        return this.iChronology.getWeeksInYear(this.iChronology.getWeekyear(n)) - 52;
    }
    
    @Override
    public DurationField getLeapDurationField() {
        return this.iChronology.weeks();
    }
    
    @Override
    public int getMinimumValue() {
        return this.iChronology.getMinYear();
    }
    
    @Override
    public int getMaximumValue() {
        return this.iChronology.getMaxYear();
    }
    
    @Override
    public long roundFloor(long roundFloor) {
        roundFloor = this.iChronology.weekOfWeekyear().roundFloor(roundFloor);
        final int weekOfWeekyear = this.iChronology.getWeekOfWeekyear(roundFloor);
        if (weekOfWeekyear > 1) {
            roundFloor -= 604800000L * (weekOfWeekyear - 1);
        }
        return roundFloor;
    }
    
    @Override
    public long remainder(final long n) {
        return n - this.roundFloor(n);
    }
    
    private Object readResolve() {
        return this.iChronology.weekyear();
    }
}
