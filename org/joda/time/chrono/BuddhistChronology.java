// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.field.RemainderDateTimeField;
import org.joda.time.field.DividedDateTimeField;
import org.joda.time.field.DelegatedDateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.field.OffsetDateTimeField;
import org.joda.time.field.SkipUndoDateTimeField;
import org.joda.time.field.UnsupportedDurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.ReadableDateTime;
import org.joda.time.DateTime;
import org.joda.time.Chronology;
import org.joda.time.ReadableInstant;
import org.joda.time.DateTimeZone;
import java.util.concurrent.ConcurrentHashMap;
import org.joda.time.DateTimeField;

public final class BuddhistChronology extends AssembledChronology
{
    private static final long serialVersionUID = -3474595157769370126L;
    public static final int BE = 1;
    private static final DateTimeField ERA_FIELD;
    private static final int BUDDHIST_OFFSET = 543;
    private static final ConcurrentHashMap<DateTimeZone, BuddhistChronology> cCache;
    private static final BuddhistChronology INSTANCE_UTC;
    
    public static BuddhistChronology getInstanceUTC() {
        return BuddhistChronology.INSTANCE_UTC;
    }
    
    public static BuddhistChronology getInstance() {
        return getInstance(DateTimeZone.getDefault());
    }
    
    public static BuddhistChronology getInstance(DateTimeZone default1) {
        if (default1 == null) {
            default1 = DateTimeZone.getDefault();
        }
        BuddhistChronology value = BuddhistChronology.cCache.get(default1);
        if (value == null) {
            final BuddhistChronology buddhistChronology = new BuddhistChronology(GJChronology.getInstance(default1, null), null);
            value = new BuddhistChronology(LimitChronology.getInstance(buddhistChronology, new DateTime(1, 1, 1, 0, 0, 0, 0, buddhistChronology), null), "");
            final BuddhistChronology buddhistChronology2 = BuddhistChronology.cCache.putIfAbsent(default1, value);
            if (buddhistChronology2 != null) {
                value = buddhistChronology2;
            }
        }
        return value;
    }
    
    private BuddhistChronology(final Chronology chronology, final Object o) {
        super(chronology, o);
    }
    
    private Object readResolve() {
        final Chronology base = this.getBase();
        return (base == null) ? getInstanceUTC() : getInstance(base.getZone());
    }
    
    @Override
    public Chronology withUTC() {
        return BuddhistChronology.INSTANCE_UTC;
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
    public boolean equals(final Object o) {
        return this == o || (o instanceof BuddhistChronology && this.getZone().equals(((BuddhistChronology)o).getZone()));
    }
    
    @Override
    public int hashCode() {
        return "Buddhist".hashCode() * 11 + this.getZone().hashCode();
    }
    
    @Override
    public String toString() {
        String string = "BuddhistChronology";
        final DateTimeZone zone = this.getZone();
        if (zone != null) {
            string = string + '[' + zone.getID() + ']';
        }
        return string;
    }
    
    @Override
    protected void assemble(final Fields fields) {
        if (this.getParam() == null) {
            fields.eras = UnsupportedDurationField.getInstance(DurationFieldType.eras());
            fields.year = new OffsetDateTimeField(new SkipUndoDateTimeField(this, fields.year), 543);
            final DateTimeField yearOfEra = fields.yearOfEra;
            fields.yearOfEra = new DelegatedDateTimeField(fields.year, fields.eras, DateTimeFieldType.yearOfEra());
            fields.weekyear = new OffsetDateTimeField(new SkipUndoDateTimeField(this, fields.weekyear), 543);
            fields.centuryOfEra = new DividedDateTimeField(new OffsetDateTimeField(fields.yearOfEra, 99), fields.eras, DateTimeFieldType.centuryOfEra(), 100);
            fields.centuries = fields.centuryOfEra.getDurationField();
            fields.yearOfCentury = new OffsetDateTimeField(new RemainderDateTimeField((DividedDateTimeField)fields.centuryOfEra), DateTimeFieldType.yearOfCentury(), 1);
            fields.weekyearOfCentury = new OffsetDateTimeField(new RemainderDateTimeField(fields.weekyear, fields.centuries, DateTimeFieldType.weekyearOfCentury(), 100), DateTimeFieldType.weekyearOfCentury(), 1);
            fields.era = BuddhistChronology.ERA_FIELD;
        }
    }
    
    static {
        ERA_FIELD = new BasicSingleEraDateTimeField("BE");
        cCache = new ConcurrentHashMap<DateTimeZone, BuddhistChronology>();
        INSTANCE_UTC = getInstance(DateTimeZone.UTC);
    }
}
