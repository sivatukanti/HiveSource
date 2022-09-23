// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.convert;

import org.joda.time.Period;
import org.joda.time.DateTime;
import org.joda.time.ReadablePeriod;
import org.joda.time.ReadWritableInterval;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.ReadablePartial;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.Chronology;

class StringConverter extends AbstractConverter implements InstantConverter, PartialConverter, DurationConverter, PeriodConverter, IntervalConverter
{
    static final StringConverter INSTANCE;
    
    protected StringConverter() {
    }
    
    @Override
    public long getInstantMillis(final Object o, final Chronology chronology) {
        return ISODateTimeFormat.dateTimeParser().withChronology(chronology).parseMillis((String)o);
    }
    
    @Override
    public int[] getPartialValues(final ReadablePartial readablePartial, final Object o, Chronology withZone, final DateTimeFormatter dateTimeFormatter) {
        if (dateTimeFormatter.getZone() != null) {
            withZone = withZone.withZone(dateTimeFormatter.getZone());
        }
        return withZone.get(readablePartial, dateTimeFormatter.withChronology(withZone).parseMillis((String)o));
    }
    
    public long getDurationMillis(final Object o) {
        final String s2;
        final String s = s2 = (String)o;
        final int length = s2.length();
        if (length < 4 || (s2.charAt(0) != 'P' && s2.charAt(0) != 'p') || (s2.charAt(1) != 'T' && s2.charAt(1) != 't') || (s2.charAt(length - 1) != 'S' && s2.charAt(length - 1) != 's')) {
            throw new IllegalArgumentException("Invalid format: \"" + s + '\"');
        }
        final String substring = s2.substring(2, length - 1);
        int endIndex = -1;
        int n = 0;
        for (int i = 0; i < substring.length(); ++i) {
            if (substring.charAt(i) < '0' || substring.charAt(i) > '9') {
                if (i == 0 && substring.charAt(0) == '-') {
                    n = 1;
                }
                else {
                    if (i <= n || substring.charAt(i) != '.' || endIndex != -1) {
                        throw new IllegalArgumentException("Invalid format: \"" + s + '\"');
                    }
                    endIndex = i;
                }
            }
        }
        long n2 = 0L;
        final int n3 = n;
        long n4;
        if (endIndex > 0) {
            n4 = Long.parseLong(substring.substring(n3, endIndex));
            String s3 = substring.substring(endIndex + 1);
            if (s3.length() != 3) {
                s3 = (s3 + "000").substring(0, 3);
            }
            n2 = Integer.parseInt(s3);
        }
        else if (n != 0) {
            n4 = Long.parseLong(substring.substring(n3, substring.length()));
        }
        else {
            n4 = Long.parseLong(substring);
        }
        if (n != 0) {
            return FieldUtils.safeAdd(FieldUtils.safeMultiply(-n4, 1000), -n2);
        }
        return FieldUtils.safeAdd(FieldUtils.safeMultiply(n4, 1000), n2);
    }
    
    public void setInto(final ReadWritablePeriod readWritablePeriod, final Object o, final Chronology chronology) {
        final String str = (String)o;
        final PeriodFormatter standard = ISOPeriodFormat.standard();
        readWritablePeriod.clear();
        final int into = standard.parseInto(readWritablePeriod, str, 0);
        if (into < str.length()) {
            if (into < 0) {
                standard.withParseType(readWritablePeriod.getPeriodType()).parseMutablePeriod(str);
            }
            throw new IllegalArgumentException("Invalid format: \"" + str + '\"');
        }
    }
    
    public void setInto(final ReadWritableInterval readWritableInterval, final Object o, Chronology chronology) {
        final String s = (String)o;
        final int index = s.indexOf(47);
        if (index < 0) {
            throw new IllegalArgumentException("Format requires a '/' separator: " + s);
        }
        final String substring = s.substring(0, index);
        if (substring.length() <= 0) {
            throw new IllegalArgumentException("Format invalid: " + s);
        }
        final String substring2 = s.substring(index + 1);
        if (substring2.length() <= 0) {
            throw new IllegalArgumentException("Format invalid: " + s);
        }
        final DateTimeFormatter withChronology = ISODateTimeFormat.dateTimeParser().withChronology(chronology);
        final PeriodFormatter standard = ISOPeriodFormat.standard();
        long n = 0L;
        ReadablePeriod period = null;
        Chronology chronology2 = null;
        final char char1 = substring.charAt(0);
        if (char1 == 'P' || char1 == 'p') {
            period = standard.withParseType(this.getPeriodType(substring)).parsePeriod(substring);
        }
        else {
            final DateTime dateTime = withChronology.parseDateTime(substring);
            n = dateTime.getMillis();
            chronology2 = dateTime.getChronology();
        }
        final char char2 = substring2.charAt(0);
        long n2;
        if (char2 == 'P' || char2 == 'p') {
            if (period != null) {
                throw new IllegalArgumentException("Interval composed of two durations: " + s);
            }
            final Period period2 = standard.withParseType(this.getPeriodType(substring2)).parsePeriod(substring2);
            chronology = ((chronology != null) ? chronology : chronology2);
            n2 = chronology.add(period2, n, 1);
        }
        else {
            final DateTime dateTime2 = withChronology.parseDateTime(substring2);
            n2 = dateTime2.getMillis();
            final Chronology chronology3 = (chronology2 != null) ? chronology2 : dateTime2.getChronology();
            chronology = ((chronology != null) ? chronology : chronology3);
            if (period != null) {
                n = chronology.add(period, n2, -1);
            }
        }
        readWritableInterval.setInterval(n, n2);
        readWritableInterval.setChronology(chronology);
    }
    
    public Class<?> getSupportedType() {
        return String.class;
    }
    
    static {
        INSTANCE = new StringConverter();
    }
}
