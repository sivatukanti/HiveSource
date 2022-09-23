// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.joda.time.field.RemainderDateTimeField;
import org.joda.time.field.DividedDateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import java.util.concurrent.ConcurrentHashMap;

public final class ISOChronology extends AssembledChronology
{
    private static final long serialVersionUID = -6212696554273812441L;
    private static final ISOChronology INSTANCE_UTC;
    private static final ConcurrentHashMap<DateTimeZone, ISOChronology> cCache;
    
    public static ISOChronology getInstanceUTC() {
        return ISOChronology.INSTANCE_UTC;
    }
    
    public static ISOChronology getInstance() {
        return getInstance(DateTimeZone.getDefault());
    }
    
    public static ISOChronology getInstance(DateTimeZone default1) {
        if (default1 == null) {
            default1 = DateTimeZone.getDefault();
        }
        ISOChronology value = ISOChronology.cCache.get(default1);
        if (value == null) {
            value = new ISOChronology(ZonedChronology.getInstance(ISOChronology.INSTANCE_UTC, default1));
            final ISOChronology isoChronology = ISOChronology.cCache.putIfAbsent(default1, value);
            if (isoChronology != null) {
                value = isoChronology;
            }
        }
        return value;
    }
    
    private ISOChronology(final Chronology chronology) {
        super(chronology, null);
    }
    
    @Override
    public Chronology withUTC() {
        return ISOChronology.INSTANCE_UTC;
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
    public String toString() {
        String string = "ISOChronology";
        final DateTimeZone zone = this.getZone();
        if (zone != null) {
            string = string + '[' + zone.getID() + ']';
        }
        return string;
    }
    
    @Override
    protected void assemble(final Fields fields) {
        if (this.getBase().getZone() == DateTimeZone.UTC) {
            fields.centuryOfEra = new DividedDateTimeField(ISOYearOfEraDateTimeField.INSTANCE, DateTimeFieldType.centuryOfEra(), 100);
            fields.centuries = fields.centuryOfEra.getDurationField();
            fields.yearOfCentury = new RemainderDateTimeField((DividedDateTimeField)fields.centuryOfEra, DateTimeFieldType.yearOfCentury());
            fields.weekyearOfCentury = new RemainderDateTimeField((DividedDateTimeField)fields.centuryOfEra, fields.weekyears, DateTimeFieldType.weekyearOfCentury());
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ISOChronology && this.getZone().equals(((ISOChronology)o).getZone()));
    }
    
    @Override
    public int hashCode() {
        return "ISO".hashCode() * 11 + this.getZone().hashCode();
    }
    
    private Object writeReplace() {
        return new Stub(this.getZone());
    }
    
    static {
        cCache = new ConcurrentHashMap<DateTimeZone, ISOChronology>();
        INSTANCE_UTC = new ISOChronology(GregorianChronology.getInstanceUTC());
        ISOChronology.cCache.put(DateTimeZone.UTC, ISOChronology.INSTANCE_UTC);
    }
    
    private static final class Stub implements Serializable
    {
        private static final long serialVersionUID = -6212696554273812441L;
        private transient DateTimeZone iZone;
        
        Stub(final DateTimeZone iZone) {
            this.iZone = iZone;
        }
        
        private Object readResolve() {
            return ISOChronology.getInstance(this.iZone);
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(this.iZone);
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            this.iZone = (DateTimeZone)objectInputStream.readObject();
        }
    }
}
