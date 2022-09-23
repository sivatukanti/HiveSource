// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import org.joda.time.convert.InstantConverter;
import org.joda.time.convert.ConverterManager;
import java.io.Serializable;
import java.util.Comparator;

public class DateTimeComparator implements Comparator<Object>, Serializable
{
    private static final long serialVersionUID = -6097339773320178364L;
    private static final DateTimeComparator ALL_INSTANCE;
    private static final DateTimeComparator DATE_INSTANCE;
    private static final DateTimeComparator TIME_INSTANCE;
    private final DateTimeFieldType iLowerLimit;
    private final DateTimeFieldType iUpperLimit;
    
    public static DateTimeComparator getInstance() {
        return DateTimeComparator.ALL_INSTANCE;
    }
    
    public static DateTimeComparator getInstance(final DateTimeFieldType dateTimeFieldType) {
        return getInstance(dateTimeFieldType, null);
    }
    
    public static DateTimeComparator getInstance(final DateTimeFieldType dateTimeFieldType, final DateTimeFieldType dateTimeFieldType2) {
        if (dateTimeFieldType == null && dateTimeFieldType2 == null) {
            return DateTimeComparator.ALL_INSTANCE;
        }
        if (dateTimeFieldType == DateTimeFieldType.dayOfYear() && dateTimeFieldType2 == null) {
            return DateTimeComparator.DATE_INSTANCE;
        }
        if (dateTimeFieldType == null && dateTimeFieldType2 == DateTimeFieldType.dayOfYear()) {
            return DateTimeComparator.TIME_INSTANCE;
        }
        return new DateTimeComparator(dateTimeFieldType, dateTimeFieldType2);
    }
    
    public static DateTimeComparator getDateOnlyInstance() {
        return DateTimeComparator.DATE_INSTANCE;
    }
    
    public static DateTimeComparator getTimeOnlyInstance() {
        return DateTimeComparator.TIME_INSTANCE;
    }
    
    protected DateTimeComparator(final DateTimeFieldType iLowerLimit, final DateTimeFieldType iUpperLimit) {
        this.iLowerLimit = iLowerLimit;
        this.iUpperLimit = iUpperLimit;
    }
    
    public DateTimeFieldType getLowerLimit() {
        return this.iLowerLimit;
    }
    
    public DateTimeFieldType getUpperLimit() {
        return this.iUpperLimit;
    }
    
    public int compare(final Object o, final Object o2) {
        final InstantConverter instantConverter = ConverterManager.getInstance().getInstantConverter(o);
        final Chronology chronology = instantConverter.getChronology(o, (Chronology)null);
        long n = instantConverter.getInstantMillis(o, chronology);
        final InstantConverter instantConverter2 = ConverterManager.getInstance().getInstantConverter(o2);
        final Chronology chronology2 = instantConverter2.getChronology(o2, (Chronology)null);
        long n2 = instantConverter2.getInstantMillis(o2, chronology2);
        if (this.iLowerLimit != null) {
            n = this.iLowerLimit.getField(chronology).roundFloor(n);
            n2 = this.iLowerLimit.getField(chronology2).roundFloor(n2);
        }
        if (this.iUpperLimit != null) {
            n = this.iUpperLimit.getField(chronology).remainder(n);
            n2 = this.iUpperLimit.getField(chronology2).remainder(n2);
        }
        if (n < n2) {
            return -1;
        }
        if (n > n2) {
            return 1;
        }
        return 0;
    }
    
    private Object readResolve() {
        return getInstance(this.iLowerLimit, this.iUpperLimit);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof DateTimeComparator) {
            final DateTimeComparator dateTimeComparator = (DateTimeComparator)o;
            return (this.iLowerLimit == dateTimeComparator.getLowerLimit() || (this.iLowerLimit != null && this.iLowerLimit.equals(dateTimeComparator.getLowerLimit()))) && (this.iUpperLimit == dateTimeComparator.getUpperLimit() || (this.iUpperLimit != null && this.iUpperLimit.equals(dateTimeComparator.getUpperLimit())));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return ((this.iLowerLimit == null) ? 0 : this.iLowerLimit.hashCode()) + 123 * ((this.iUpperLimit == null) ? 0 : this.iUpperLimit.hashCode());
    }
    
    @Override
    public String toString() {
        if (this.iLowerLimit == this.iUpperLimit) {
            return "DateTimeComparator[" + ((this.iLowerLimit == null) ? "" : this.iLowerLimit.getName()) + "]";
        }
        return "DateTimeComparator[" + ((this.iLowerLimit == null) ? "" : this.iLowerLimit.getName()) + "-" + ((this.iUpperLimit == null) ? "" : this.iUpperLimit.getName()) + "]";
    }
    
    static {
        ALL_INSTANCE = new DateTimeComparator(null, null);
        DATE_INSTANCE = new DateTimeComparator(DateTimeFieldType.dayOfYear(), null);
        TIME_INSTANCE = new DateTimeComparator(null, DateTimeFieldType.dayOfYear());
    }
}
