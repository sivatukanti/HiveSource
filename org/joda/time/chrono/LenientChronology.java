// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.field.LenientDateTimeField;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.Chronology;

public final class LenientChronology extends AssembledChronology
{
    private static final long serialVersionUID = -3148237568046877177L;
    private transient Chronology iWithUTC;
    
    public static LenientChronology getInstance(final Chronology chronology) {
        if (chronology == null) {
            throw new IllegalArgumentException("Must supply a chronology");
        }
        return new LenientChronology(chronology);
    }
    
    private LenientChronology(final Chronology chronology) {
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
        fields.year = this.convertField(fields.year);
        fields.yearOfEra = this.convertField(fields.yearOfEra);
        fields.yearOfCentury = this.convertField(fields.yearOfCentury);
        fields.centuryOfEra = this.convertField(fields.centuryOfEra);
        fields.era = this.convertField(fields.era);
        fields.dayOfWeek = this.convertField(fields.dayOfWeek);
        fields.dayOfMonth = this.convertField(fields.dayOfMonth);
        fields.dayOfYear = this.convertField(fields.dayOfYear);
        fields.monthOfYear = this.convertField(fields.monthOfYear);
        fields.weekOfWeekyear = this.convertField(fields.weekOfWeekyear);
        fields.weekyear = this.convertField(fields.weekyear);
        fields.weekyearOfCentury = this.convertField(fields.weekyearOfCentury);
        fields.millisOfSecond = this.convertField(fields.millisOfSecond);
        fields.millisOfDay = this.convertField(fields.millisOfDay);
        fields.secondOfMinute = this.convertField(fields.secondOfMinute);
        fields.secondOfDay = this.convertField(fields.secondOfDay);
        fields.minuteOfHour = this.convertField(fields.minuteOfHour);
        fields.minuteOfDay = this.convertField(fields.minuteOfDay);
        fields.hourOfDay = this.convertField(fields.hourOfDay);
        fields.hourOfHalfday = this.convertField(fields.hourOfHalfday);
        fields.clockhourOfDay = this.convertField(fields.clockhourOfDay);
        fields.clockhourOfHalfday = this.convertField(fields.clockhourOfHalfday);
        fields.halfdayOfDay = this.convertField(fields.halfdayOfDay);
    }
    
    private final DateTimeField convertField(final DateTimeField dateTimeField) {
        return LenientDateTimeField.getInstance(dateTimeField, this.getBase());
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof LenientChronology && this.getBase().equals(((LenientChronology)o).getBase()));
    }
    
    @Override
    public int hashCode() {
        return 236548278 + this.getBase().hashCode() * 7;
    }
    
    @Override
    public String toString() {
        return "LenientChronology[" + this.getBase().toString() + ']';
    }
}
