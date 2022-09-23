// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.field.StrictDateTimeField;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.Chronology;

public final class StrictChronology extends AssembledChronology
{
    private static final long serialVersionUID = 6633006628097111960L;
    private transient Chronology iWithUTC;
    
    public static StrictChronology getInstance(final Chronology chronology) {
        if (chronology == null) {
            throw new IllegalArgumentException("Must supply a chronology");
        }
        return new StrictChronology(chronology);
    }
    
    private StrictChronology(final Chronology chronology) {
        super(chronology, null);
    }
    
    @Override
    public Chronology withUTC() {
        if (this.iWithUTC == null) {
            if (this.getZone() == DateTimeZone.UTC) {
                this.iWithUTC = this;
            }
            else {
                this.iWithUTC = getInstance(this.getBase().withUTC());
            }
        }
        return this.iWithUTC;
    }
    
    @Override
    public Chronology withZone(DateTimeZone default1) {
        if (default1 == null) {
            default1 = DateTimeZone.getDefault();
        }
        if (default1 == DateTimeZone.UTC) {
            return this.withUTC();
        }
        if (default1 == this.getZone()) {
            return this;
        }
        return getInstance(this.getBase().withZone(default1));
    }
    
    @Override
    protected void assemble(final Fields fields) {
        fields.year = convertField(fields.year);
        fields.yearOfEra = convertField(fields.yearOfEra);
        fields.yearOfCentury = convertField(fields.yearOfCentury);
        fields.centuryOfEra = convertField(fields.centuryOfEra);
        fields.era = convertField(fields.era);
        fields.dayOfWeek = convertField(fields.dayOfWeek);
        fields.dayOfMonth = convertField(fields.dayOfMonth);
        fields.dayOfYear = convertField(fields.dayOfYear);
        fields.monthOfYear = convertField(fields.monthOfYear);
        fields.weekOfWeekyear = convertField(fields.weekOfWeekyear);
        fields.weekyear = convertField(fields.weekyear);
        fields.weekyearOfCentury = convertField(fields.weekyearOfCentury);
        fields.millisOfSecond = convertField(fields.millisOfSecond);
        fields.millisOfDay = convertField(fields.millisOfDay);
        fields.secondOfMinute = convertField(fields.secondOfMinute);
        fields.secondOfDay = convertField(fields.secondOfDay);
        fields.minuteOfHour = convertField(fields.minuteOfHour);
        fields.minuteOfDay = convertField(fields.minuteOfDay);
        fields.hourOfDay = convertField(fields.hourOfDay);
        fields.hourOfHalfday = convertField(fields.hourOfHalfday);
        fields.clockhourOfDay = convertField(fields.clockhourOfDay);
        fields.clockhourOfHalfday = convertField(fields.clockhourOfHalfday);
        fields.halfdayOfDay = convertField(fields.halfdayOfDay);
    }
    
    private static final DateTimeField convertField(final DateTimeField dateTimeField) {
        return StrictDateTimeField.getInstance(dateTimeField);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof StrictChronology && this.getBase().equals(((StrictChronology)o).getBase()));
    }
    
    @Override
    public int hashCode() {
        return 352831696 + this.getBase().hashCode() * 7;
    }
    
    @Override
    public String toString() {
        return "StrictChronology[" + this.getBase().toString() + ']';
    }
}
