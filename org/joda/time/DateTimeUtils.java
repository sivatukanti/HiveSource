// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.HashMap;
import java.text.DateFormatSymbols;
import java.util.Locale;
import org.joda.time.chrono.ISOChronology;
import java.security.Permission;
import java.util.Map;

public class DateTimeUtils
{
    private static final SystemMillisProvider SYSTEM_MILLIS_PROVIDER;
    private static volatile MillisProvider cMillisProvider;
    private static volatile Map<String, DateTimeZone> cZoneNames;
    
    private static void put(final Map<String, DateTimeZone> map, final String s, final String s2) {
        try {
            map.put(s, DateTimeZone.forID(s2));
        }
        catch (RuntimeException ex) {}
    }
    
    protected DateTimeUtils() {
    }
    
    public static final long currentTimeMillis() {
        return DateTimeUtils.cMillisProvider.getMillis();
    }
    
    public static final void setCurrentMillisSystem() throws SecurityException {
        checkPermission();
        DateTimeUtils.cMillisProvider = DateTimeUtils.SYSTEM_MILLIS_PROVIDER;
    }
    
    public static final void setCurrentMillisFixed(final long n) throws SecurityException {
        checkPermission();
        DateTimeUtils.cMillisProvider = new FixedMillisProvider(n);
    }
    
    public static final void setCurrentMillisOffset(final long n) throws SecurityException {
        checkPermission();
        if (n == 0L) {
            DateTimeUtils.cMillisProvider = DateTimeUtils.SYSTEM_MILLIS_PROVIDER;
        }
        else {
            DateTimeUtils.cMillisProvider = new OffsetMillisProvider(n);
        }
    }
    
    public static final void setCurrentMillisProvider(final MillisProvider cMillisProvider) throws SecurityException {
        if (cMillisProvider == null) {
            throw new IllegalArgumentException("The MillisProvider must not be null");
        }
        checkPermission();
        DateTimeUtils.cMillisProvider = cMillisProvider;
    }
    
    private static void checkPermission() throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("CurrentTime.setProvider"));
        }
    }
    
    public static final long getInstantMillis(final ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return currentTimeMillis();
        }
        return readableInstant.getMillis();
    }
    
    public static final Chronology getInstantChronology(final ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return ISOChronology.getInstance();
        }
        final Chronology chronology = readableInstant.getChronology();
        if (chronology == null) {
            return ISOChronology.getInstance();
        }
        return chronology;
    }
    
    public static final Chronology getIntervalChronology(final ReadableInstant readableInstant, final ReadableInstant readableInstant2) {
        Chronology chronology = null;
        if (readableInstant != null) {
            chronology = readableInstant.getChronology();
        }
        else if (readableInstant2 != null) {
            chronology = readableInstant2.getChronology();
        }
        if (chronology == null) {
            chronology = ISOChronology.getInstance();
        }
        return chronology;
    }
    
    public static final Chronology getIntervalChronology(final ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return ISOChronology.getInstance();
        }
        final Chronology chronology = readableInterval.getChronology();
        if (chronology == null) {
            return ISOChronology.getInstance();
        }
        return chronology;
    }
    
    public static final ReadableInterval getReadableInterval(ReadableInterval readableInterval) {
        if (readableInterval == null) {
            final long currentTimeMillis = currentTimeMillis();
            readableInterval = new Interval(currentTimeMillis, currentTimeMillis);
        }
        return readableInterval;
    }
    
    public static final Chronology getChronology(final Chronology chronology) {
        if (chronology == null) {
            return ISOChronology.getInstance();
        }
        return chronology;
    }
    
    public static final DateTimeZone getZone(final DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            return DateTimeZone.getDefault();
        }
        return dateTimeZone;
    }
    
    public static final PeriodType getPeriodType(final PeriodType periodType) {
        if (periodType == null) {
            return PeriodType.standard();
        }
        return periodType;
    }
    
    public static final long getDurationMillis(final ReadableDuration readableDuration) {
        if (readableDuration == null) {
            return 0L;
        }
        return readableDuration.getMillis();
    }
    
    public static final boolean isContiguous(final ReadablePartial readablePartial) {
        if (readablePartial == null) {
            throw new IllegalArgumentException("Partial must not be null");
        }
        DurationFieldType type = null;
        for (int i = 0; i < readablePartial.size(); ++i) {
            final DateTimeField field = readablePartial.getField(i);
            if (i > 0 && (field.getRangeDurationField() == null || field.getRangeDurationField().getType() != type)) {
                return false;
            }
            type = field.getDurationField().getType();
        }
        return true;
    }
    
    public static final DateFormatSymbols getDateFormatSymbols(final Locale locale) {
        try {
            return (DateFormatSymbols)DateFormatSymbols.class.getMethod("getInstance", Locale.class).invoke(null, locale);
        }
        catch (Exception ex) {
            return new DateFormatSymbols(locale);
        }
    }
    
    public static final Map<String, DateTimeZone> getDefaultTimeZoneNames() {
        return DateTimeUtils.cZoneNames;
    }
    
    public static final void setDefaultTimeZoneNames(final Map<String, DateTimeZone> m) {
        DateTimeUtils.cZoneNames = Collections.unmodifiableMap((Map<? extends String, ? extends DateTimeZone>)new HashMap<String, DateTimeZone>(m));
    }
    
    public static final double toJulianDay(final long n) {
        return n / 8.64E7 + 2440587.5;
    }
    
    public static final long toJulianDayNumber(final long n) {
        return (long)Math.floor(toJulianDay(n) + 0.5);
    }
    
    public static final long fromJulianDay(final double n) {
        return (long)((n - 2440587.5) * 8.64E7);
    }
    
    static {
        SYSTEM_MILLIS_PROVIDER = new SystemMillisProvider();
        DateTimeUtils.cMillisProvider = DateTimeUtils.SYSTEM_MILLIS_PROVIDER;
        final LinkedHashMap<String, DateTimeZone> m = new LinkedHashMap<String, DateTimeZone>();
        m.put("UT", DateTimeZone.UTC);
        m.put("UTC", DateTimeZone.UTC);
        m.put("GMT", DateTimeZone.UTC);
        put(m, "EST", "America/New_York");
        put(m, "EDT", "America/New_York");
        put(m, "CST", "America/Chicago");
        put(m, "CDT", "America/Chicago");
        put(m, "MST", "America/Denver");
        put(m, "MDT", "America/Denver");
        put(m, "PST", "America/Los_Angeles");
        put(m, "PDT", "America/Los_Angeles");
        DateTimeUtils.cZoneNames = (Map<String, DateTimeZone>)Collections.unmodifiableMap((Map<?, ?>)m);
    }
    
    static class SystemMillisProvider implements MillisProvider
    {
        public long getMillis() {
            return System.currentTimeMillis();
        }
    }
    
    static class FixedMillisProvider implements MillisProvider
    {
        private final long iMillis;
        
        FixedMillisProvider(final long iMillis) {
            this.iMillis = iMillis;
        }
        
        public long getMillis() {
            return this.iMillis;
        }
    }
    
    static class OffsetMillisProvider implements MillisProvider
    {
        private final long iMillis;
        
        OffsetMillisProvider(final long iMillis) {
            this.iMillis = iMillis;
        }
        
        public long getMillis() {
            return System.currentTimeMillis() + this.iMillis;
        }
    }
    
    public interface MillisProvider
    {
        long getMillis();
    }
}
