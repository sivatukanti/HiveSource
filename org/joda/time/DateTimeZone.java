// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.util.Locale;
import org.joda.convert.ToString;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.FormatUtils;
import org.joda.time.chrono.BaseChronology;
import org.joda.time.tz.DefaultNameProvider;
import org.joda.time.tz.UTCProvider;
import org.joda.time.tz.ZoneInfoProvider;
import org.joda.time.tz.FixedDateTimeZone;
import java.util.HashMap;
import org.joda.time.field.FieldUtils;
import org.joda.convert.FromString;
import java.security.Permission;
import java.util.TimeZone;
import java.lang.ref.SoftReference;
import java.util.Map;
import org.joda.time.format.DateTimeFormatter;
import java.util.Set;
import org.joda.time.tz.NameProvider;
import org.joda.time.tz.Provider;
import java.io.Serializable;

public abstract class DateTimeZone implements Serializable
{
    private static final long serialVersionUID = 5546345482340108586L;
    public static final DateTimeZone UTC;
    private static final int MAX_MILLIS = 86399999;
    private static Provider cProvider;
    private static NameProvider cNameProvider;
    private static Set<String> cAvailableIDs;
    private static volatile DateTimeZone cDefault;
    private static DateTimeFormatter cOffsetFormatter;
    private static Map<String, SoftReference<DateTimeZone>> iFixedOffsetCache;
    private static Map<String, String> cZoneIdConversion;
    private final String iID;
    
    public static DateTimeZone getDefault() {
        DateTimeZone dateTimeZone = DateTimeZone.cDefault;
        if (dateTimeZone == null) {
            synchronized (DateTimeZone.class) {
                dateTimeZone = DateTimeZone.cDefault;
                if (dateTimeZone == null) {
                    DateTimeZone cDefault = null;
                    try {
                        try {
                            final String property = System.getProperty("user.timezone");
                            if (property != null) {
                                cDefault = forID(property);
                            }
                        }
                        catch (RuntimeException ex) {}
                        if (cDefault == null) {
                            cDefault = forTimeZone(TimeZone.getDefault());
                        }
                    }
                    catch (IllegalArgumentException ex2) {}
                    if (cDefault == null) {
                        cDefault = DateTimeZone.UTC;
                    }
                    dateTimeZone = (DateTimeZone.cDefault = cDefault);
                }
            }
        }
        return dateTimeZone;
    }
    
    public static void setDefault(final DateTimeZone cDefault) throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("DateTimeZone.setDefault"));
        }
        if (cDefault == null) {
            throw new IllegalArgumentException("The datetime zone must not be null");
        }
        synchronized (DateTimeZone.class) {
            DateTimeZone.cDefault = cDefault;
        }
    }
    
    @FromString
    public static DateTimeZone forID(String printOffset) {
        if (printOffset == null) {
            return getDefault();
        }
        if (printOffset.equals("UTC")) {
            return DateTimeZone.UTC;
        }
        final DateTimeZone zone = DateTimeZone.cProvider.getZone(printOffset);
        if (zone != null) {
            return zone;
        }
        if (!printOffset.startsWith("+") && !printOffset.startsWith("-")) {
            throw new IllegalArgumentException("The datetime zone id '" + printOffset + "' is not recognised");
        }
        final int offset = parseOffset(printOffset);
        if (offset == 0L) {
            return DateTimeZone.UTC;
        }
        printOffset = printOffset(offset);
        return fixedOffsetZone(printOffset, offset);
    }
    
    public static DateTimeZone forOffsetHours(final int n) throws IllegalArgumentException {
        return forOffsetHoursMinutes(n, 0);
    }
    
    public static DateTimeZone forOffsetHoursMinutes(final int i, int a) throws IllegalArgumentException {
        if (i == 0 && a == 0) {
            return DateTimeZone.UTC;
        }
        if (i < -23 || i > 23) {
            throw new IllegalArgumentException("Hours out of range: " + i);
        }
        if (a < -59 || a > 59) {
            throw new IllegalArgumentException("Minutes out of range: " + a);
        }
        if (i > 0 && a < 0) {
            throw new IllegalArgumentException("Positive hours must not have negative minutes: " + a);
        }
        int safeMultiply;
        try {
            final int n = i * 60;
            if (n < 0) {
                a = n - Math.abs(a);
            }
            else {
                a += n;
            }
            safeMultiply = FieldUtils.safeMultiply(a, 60000);
        }
        catch (ArithmeticException ex) {
            throw new IllegalArgumentException("Offset is too large");
        }
        return forOffsetMillis(safeMultiply);
    }
    
    public static DateTimeZone forOffsetMillis(final int i) {
        if (i < -86399999 || i > 86399999) {
            throw new IllegalArgumentException("Millis out of range: " + i);
        }
        return fixedOffsetZone(printOffset(i), i);
    }
    
    public static DateTimeZone forTimeZone(final TimeZone timeZone) {
        if (timeZone == null) {
            return getDefault();
        }
        final String id = timeZone.getID();
        if (id == null) {
            throw new IllegalArgumentException("The TimeZone id must not be null");
        }
        if (id.equals("UTC")) {
            return DateTimeZone.UTC;
        }
        DateTimeZone dateTimeZone = null;
        final String convertedId = getConvertedId(id);
        if (convertedId != null) {
            dateTimeZone = DateTimeZone.cProvider.getZone(convertedId);
        }
        if (dateTimeZone == null) {
            dateTimeZone = DateTimeZone.cProvider.getZone(id);
        }
        if (dateTimeZone != null) {
            return dateTimeZone;
        }
        if (convertedId == null) {
            final String s = id;
            if (s.startsWith("GMT+") || s.startsWith("GMT-")) {
                final int offset = parseOffset(s.substring(3));
                if (offset == 0L) {
                    return DateTimeZone.UTC;
                }
                return fixedOffsetZone(printOffset(offset), offset);
            }
        }
        throw new IllegalArgumentException("The datetime zone id '" + id + "' is not recognised");
    }
    
    private static synchronized DateTimeZone fixedOffsetZone(final String s, final int n) {
        if (n == 0) {
            return DateTimeZone.UTC;
        }
        if (DateTimeZone.iFixedOffsetCache == null) {
            DateTimeZone.iFixedOffsetCache = new HashMap<String, SoftReference<DateTimeZone>>();
        }
        final SoftReference<DateTimeZone> softReference = DateTimeZone.iFixedOffsetCache.get(s);
        if (softReference != null) {
            final DateTimeZone dateTimeZone = softReference.get();
            if (dateTimeZone != null) {
                return dateTimeZone;
            }
        }
        final FixedDateTimeZone referent = new FixedDateTimeZone(s, null, n, n);
        DateTimeZone.iFixedOffsetCache.put(s, new SoftReference<DateTimeZone>(referent));
        return referent;
    }
    
    public static Set<String> getAvailableIDs() {
        return DateTimeZone.cAvailableIDs;
    }
    
    public static Provider getProvider() {
        return DateTimeZone.cProvider;
    }
    
    public static void setProvider(final Provider provider0) throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("DateTimeZone.setProvider"));
        }
        setProvider0(provider0);
    }
    
    private static void setProvider0(Provider defaultProvider) {
        if (defaultProvider == null) {
            defaultProvider = getDefaultProvider();
        }
        final Set<String> availableIDs = defaultProvider.getAvailableIDs();
        if (availableIDs == null || availableIDs.size() == 0) {
            throw new IllegalArgumentException("The provider doesn't have any available ids");
        }
        if (!availableIDs.contains("UTC")) {
            throw new IllegalArgumentException("The provider doesn't support UTC");
        }
        if (!DateTimeZone.UTC.equals(defaultProvider.getZone("UTC"))) {
            throw new IllegalArgumentException("Invalid UTC zone provided");
        }
        DateTimeZone.cProvider = defaultProvider;
        DateTimeZone.cAvailableIDs = availableIDs;
    }
    
    private static Provider getDefaultProvider() {
        Provider provider = null;
        try {
            final String property = System.getProperty("org.joda.time.DateTimeZone.Provider");
            if (property != null) {
                try {
                    provider = (Provider)Class.forName(property).newInstance();
                }
                catch (Exception cause) {
                    throw new RuntimeException(cause);
                }
            }
        }
        catch (SecurityException ex2) {}
        if (provider == null) {
            try {
                provider = new ZoneInfoProvider("org/joda/time/tz/data");
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (provider == null) {
            provider = new UTCProvider();
        }
        return provider;
    }
    
    public static NameProvider getNameProvider() {
        return DateTimeZone.cNameProvider;
    }
    
    public static void setNameProvider(final NameProvider nameProvider0) throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("DateTimeZone.setNameProvider"));
        }
        setNameProvider0(nameProvider0);
    }
    
    private static void setNameProvider0(NameProvider defaultNameProvider) {
        if (defaultNameProvider == null) {
            defaultNameProvider = getDefaultNameProvider();
        }
        DateTimeZone.cNameProvider = defaultNameProvider;
    }
    
    private static NameProvider getDefaultNameProvider() {
        NameProvider nameProvider = null;
        try {
            final String property = System.getProperty("org.joda.time.DateTimeZone.NameProvider");
            if (property != null) {
                try {
                    nameProvider = (NameProvider)Class.forName(property).newInstance();
                }
                catch (Exception cause) {
                    throw new RuntimeException(cause);
                }
            }
        }
        catch (SecurityException ex) {}
        if (nameProvider == null) {
            nameProvider = new DefaultNameProvider();
        }
        return nameProvider;
    }
    
    private static synchronized String getConvertedId(final String s) {
        Map<String, String> cZoneIdConversion = DateTimeZone.cZoneIdConversion;
        if (cZoneIdConversion == null) {
            cZoneIdConversion = new HashMap<String, String>();
            cZoneIdConversion.put("GMT", "UTC");
            cZoneIdConversion.put("WET", "WET");
            cZoneIdConversion.put("CET", "CET");
            cZoneIdConversion.put("MET", "CET");
            cZoneIdConversion.put("ECT", "CET");
            cZoneIdConversion.put("EET", "EET");
            cZoneIdConversion.put("MIT", "Pacific/Apia");
            cZoneIdConversion.put("HST", "Pacific/Honolulu");
            cZoneIdConversion.put("AST", "America/Anchorage");
            cZoneIdConversion.put("PST", "America/Los_Angeles");
            cZoneIdConversion.put("MST", "America/Denver");
            cZoneIdConversion.put("PNT", "America/Phoenix");
            cZoneIdConversion.put("CST", "America/Chicago");
            cZoneIdConversion.put("EST", "America/New_York");
            cZoneIdConversion.put("IET", "America/Indiana/Indianapolis");
            cZoneIdConversion.put("PRT", "America/Puerto_Rico");
            cZoneIdConversion.put("CNT", "America/St_Johns");
            cZoneIdConversion.put("AGT", "America/Argentina/Buenos_Aires");
            cZoneIdConversion.put("BET", "America/Sao_Paulo");
            cZoneIdConversion.put("ART", "Africa/Cairo");
            cZoneIdConversion.put("CAT", "Africa/Harare");
            cZoneIdConversion.put("EAT", "Africa/Addis_Ababa");
            cZoneIdConversion.put("NET", "Asia/Yerevan");
            cZoneIdConversion.put("PLT", "Asia/Karachi");
            cZoneIdConversion.put("IST", "Asia/Kolkata");
            cZoneIdConversion.put("BST", "Asia/Dhaka");
            cZoneIdConversion.put("VST", "Asia/Ho_Chi_Minh");
            cZoneIdConversion.put("CTT", "Asia/Shanghai");
            cZoneIdConversion.put("JST", "Asia/Tokyo");
            cZoneIdConversion.put("ACT", "Australia/Darwin");
            cZoneIdConversion.put("AET", "Australia/Sydney");
            cZoneIdConversion.put("SST", "Pacific/Guadalcanal");
            cZoneIdConversion.put("NST", "Pacific/Auckland");
            DateTimeZone.cZoneIdConversion = cZoneIdConversion;
        }
        return cZoneIdConversion.get(s);
    }
    
    private static int parseOffset(final String s) {
        return -(int)offsetFormatter().withChronology(new BaseChronology() {
            private static final long serialVersionUID = -3128740902654445468L;
            
            @Override
            public DateTimeZone getZone() {
                return null;
            }
            
            @Override
            public Chronology withUTC() {
                return this;
            }
            
            @Override
            public Chronology withZone(final DateTimeZone dateTimeZone) {
                return this;
            }
            
            @Override
            public String toString() {
                return this.getClass().getName();
            }
        }).parseMillis(s);
    }
    
    private static String printOffset(int n) {
        final StringBuffer sb = new StringBuffer();
        if (n >= 0) {
            sb.append('+');
        }
        else {
            sb.append('-');
            n = -n;
        }
        final int n2 = n / 3600000;
        FormatUtils.appendPaddedInteger(sb, n2, 2);
        n -= n2 * 3600000;
        final int n3 = n / 60000;
        sb.append(':');
        FormatUtils.appendPaddedInteger(sb, n3, 2);
        n -= n3 * 60000;
        if (n == 0) {
            return sb.toString();
        }
        final int n4 = n / 1000;
        sb.append(':');
        FormatUtils.appendPaddedInteger(sb, n4, 2);
        n -= n4 * 1000;
        if (n == 0) {
            return sb.toString();
        }
        sb.append('.');
        FormatUtils.appendPaddedInteger(sb, n, 3);
        return sb.toString();
    }
    
    private static synchronized DateTimeFormatter offsetFormatter() {
        if (DateTimeZone.cOffsetFormatter == null) {
            DateTimeZone.cOffsetFormatter = new DateTimeFormatterBuilder().appendTimeZoneOffset(null, true, 2, 4).toFormatter();
        }
        return DateTimeZone.cOffsetFormatter;
    }
    
    protected DateTimeZone(final String iid) {
        if (iid == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        this.iID = iid;
    }
    
    @ToString
    public final String getID() {
        return this.iID;
    }
    
    public abstract String getNameKey(final long p0);
    
    public final String getShortName(final long n) {
        return this.getShortName(n, null);
    }
    
    public String getShortName(final long n, Locale default1) {
        if (default1 == null) {
            default1 = Locale.getDefault();
        }
        final String nameKey = this.getNameKey(n);
        if (nameKey == null) {
            return this.iID;
        }
        final String shortName = DateTimeZone.cNameProvider.getShortName(default1, this.iID, nameKey);
        if (shortName != null) {
            return shortName;
        }
        return printOffset(this.getOffset(n));
    }
    
    public final String getName(final long n) {
        return this.getName(n, null);
    }
    
    public String getName(final long n, Locale default1) {
        if (default1 == null) {
            default1 = Locale.getDefault();
        }
        final String nameKey = this.getNameKey(n);
        if (nameKey == null) {
            return this.iID;
        }
        final String name = DateTimeZone.cNameProvider.getName(default1, this.iID, nameKey);
        if (name != null) {
            return name;
        }
        return printOffset(this.getOffset(n));
    }
    
    public abstract int getOffset(final long p0);
    
    public final int getOffset(final ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return this.getOffset(DateTimeUtils.currentTimeMillis());
        }
        return this.getOffset(readableInstant.getMillis());
    }
    
    public abstract int getStandardOffset(final long p0);
    
    public boolean isStandardOffset(final long n) {
        return this.getOffset(n) == this.getStandardOffset(n);
    }
    
    public int getOffsetFromLocal(final long n) {
        final int offset = this.getOffset(n);
        final long n2 = n - offset;
        final int offset2 = this.getOffset(n2);
        if (offset != offset2) {
            if (offset - offset2 < 0) {
                long nextTransition = this.nextTransition(n2);
                if (nextTransition == n - offset) {
                    nextTransition = Long.MAX_VALUE;
                }
                long nextTransition2 = this.nextTransition(n - offset2);
                if (nextTransition2 == n - offset2) {
                    nextTransition2 = Long.MAX_VALUE;
                }
                if (nextTransition != nextTransition2) {
                    return offset;
                }
            }
        }
        else if (offset >= 0) {
            final long previousTransition = this.previousTransition(n2);
            if (previousTransition < n2) {
                final int offset3 = this.getOffset(previousTransition);
                if (n2 - previousTransition <= offset3 - offset) {
                    return offset3;
                }
            }
        }
        return offset2;
    }
    
    public long convertUTCToLocal(final long n) {
        final int offset = this.getOffset(n);
        final long n2 = n + offset;
        if ((n ^ n2) < 0L && (n ^ (long)offset) >= 0L) {
            throw new ArithmeticException("Adding time zone offset caused overflow");
        }
        return n2;
    }
    
    public long convertLocalToUTC(final long n, final boolean b, final long n2) {
        final int offset = this.getOffset(n2);
        final long n3 = n - offset;
        if (this.getOffset(n3) == offset) {
            return n3;
        }
        return this.convertLocalToUTC(n, b);
    }
    
    public long convertLocalToUTC(final long n, final boolean b) {
        final int offset = this.getOffset(n);
        int offset2 = this.getOffset(n - offset);
        if (offset != offset2 && (b || offset < 0)) {
            long nextTransition = this.nextTransition(n - offset);
            if (nextTransition == n - offset) {
                nextTransition = Long.MAX_VALUE;
            }
            long nextTransition2 = this.nextTransition(n - offset2);
            if (nextTransition2 == n - offset2) {
                nextTransition2 = Long.MAX_VALUE;
            }
            if (nextTransition != nextTransition2) {
                if (b) {
                    throw new IllegalInstantException(n, this.getID());
                }
                offset2 = offset;
            }
        }
        final long n2 = n - offset2;
        if ((n ^ n2) < 0L && (n ^ (long)offset2) < 0L) {
            throw new ArithmeticException("Subtracting time zone offset caused overflow");
        }
        return n2;
    }
    
    public long getMillisKeepLocal(DateTimeZone default1, final long n) {
        if (default1 == null) {
            default1 = getDefault();
        }
        if (default1 == this) {
            return n;
        }
        return default1.convertLocalToUTC(this.convertUTCToLocal(n), false, n);
    }
    
    public boolean isLocalDateTimeGap(final LocalDateTime localDateTime) {
        if (this.isFixed()) {
            return false;
        }
        try {
            localDateTime.toDateTime(this);
            return false;
        }
        catch (IllegalInstantException ex) {
            return true;
        }
    }
    
    public long adjustOffset(final long n, final boolean b) {
        final long n2 = n - 10800000L;
        final long n3 = n + 10800000L;
        final long n4 = this.getOffset(n2);
        final long n5 = this.getOffset(n3);
        if (n4 <= n5) {
            return n;
        }
        final long n6 = n4 - n5;
        final long nextTransition = this.nextTransition(n2);
        final long n7 = nextTransition - n6;
        final long n8 = nextTransition + n6;
        if (n < n7 || n >= n8) {
            return n;
        }
        if (n - n7 >= n6) {
            return b ? n : (n - n6);
        }
        return b ? (n + n6) : n;
    }
    
    public abstract boolean isFixed();
    
    public abstract long nextTransition(final long p0);
    
    public abstract long previousTransition(final long p0);
    
    public TimeZone toTimeZone() {
        return TimeZone.getTimeZone(this.iID);
    }
    
    @Override
    public abstract boolean equals(final Object p0);
    
    @Override
    public int hashCode() {
        return 57 + this.getID().hashCode();
    }
    
    @Override
    public String toString() {
        return this.getID();
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new Stub(this.iID);
    }
    
    static {
        UTC = UTCDateTimeZone.INSTANCE;
        setProvider0(null);
        setNameProvider0(null);
    }
    
    private static final class Stub implements Serializable
    {
        private static final long serialVersionUID = -6471952376487863581L;
        private transient String iID;
        
        Stub(final String iid) {
            this.iID = iid;
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeUTF(this.iID);
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws IOException {
            this.iID = objectInputStream.readUTF();
        }
        
        private Object readResolve() throws ObjectStreamException {
            return DateTimeZone.forID(this.iID);
        }
    }
}
