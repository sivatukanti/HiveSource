// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.util;

import java.text.SimpleDateFormat;
import java.text.FieldPosition;
import org.apache.htrace.shaded.fasterxml.jackson.core.io.NumberInput;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.text.DateFormat;

public class StdDateFormat extends DateFormat
{
    protected static final String DATE_FORMAT_STR_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    protected static final String DATE_FORMAT_STR_ISO8601_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    protected static final String DATE_FORMAT_STR_PLAIN = "yyyy-MM-dd";
    protected static final String DATE_FORMAT_STR_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    protected static final String[] ALL_FORMATS;
    private static final TimeZone DEFAULT_TIMEZONE;
    private static final Locale DEFAULT_LOCALE;
    protected static final DateFormat DATE_FORMAT_RFC1123;
    protected static final DateFormat DATE_FORMAT_ISO8601;
    protected static final DateFormat DATE_FORMAT_ISO8601_Z;
    protected static final DateFormat DATE_FORMAT_PLAIN;
    public static final StdDateFormat instance;
    protected transient TimeZone _timezone;
    protected final Locale _locale;
    protected transient DateFormat _formatRFC1123;
    protected transient DateFormat _formatISO8601;
    protected transient DateFormat _formatISO8601_z;
    protected transient DateFormat _formatPlain;
    
    public StdDateFormat() {
        this._locale = StdDateFormat.DEFAULT_LOCALE;
    }
    
    @Deprecated
    public StdDateFormat(final TimeZone tz) {
        this(tz, StdDateFormat.DEFAULT_LOCALE);
    }
    
    public StdDateFormat(final TimeZone tz, final Locale loc) {
        this._timezone = tz;
        this._locale = loc;
    }
    
    public static TimeZone getDefaultTimeZone() {
        return StdDateFormat.DEFAULT_TIMEZONE;
    }
    
    public StdDateFormat withTimeZone(TimeZone tz) {
        if (tz == null) {
            tz = StdDateFormat.DEFAULT_TIMEZONE;
        }
        if (tz.equals(this._timezone)) {
            return this;
        }
        return new StdDateFormat(tz, this._locale);
    }
    
    public StdDateFormat withLocale(final Locale loc) {
        if (loc.equals(this._locale)) {
            return this;
        }
        return new StdDateFormat(this._timezone, loc);
    }
    
    @Override
    public StdDateFormat clone() {
        return new StdDateFormat(this._timezone, this._locale);
    }
    
    @Deprecated
    public static DateFormat getBlueprintISO8601Format() {
        return StdDateFormat.DATE_FORMAT_ISO8601;
    }
    
    @Deprecated
    public static DateFormat getISO8601Format(final TimeZone tz) {
        return getISO8601Format(tz, StdDateFormat.DEFAULT_LOCALE);
    }
    
    public static DateFormat getISO8601Format(final TimeZone tz, final Locale loc) {
        return _cloneFormat(StdDateFormat.DATE_FORMAT_ISO8601, "yyyy-MM-dd'T'HH:mm:ss.SSSZ", tz, loc);
    }
    
    @Deprecated
    public static DateFormat getBlueprintRFC1123Format() {
        return StdDateFormat.DATE_FORMAT_RFC1123;
    }
    
    public static DateFormat getRFC1123Format(final TimeZone tz, final Locale loc) {
        return _cloneFormat(StdDateFormat.DATE_FORMAT_RFC1123, "EEE, dd MMM yyyy HH:mm:ss zzz", tz, loc);
    }
    
    @Deprecated
    public static DateFormat getRFC1123Format(final TimeZone tz) {
        return getRFC1123Format(tz, StdDateFormat.DEFAULT_LOCALE);
    }
    
    @Override
    public void setTimeZone(final TimeZone tz) {
        if (!tz.equals(this._timezone)) {
            this._formatRFC1123 = null;
            this._formatISO8601 = null;
            this._formatISO8601_z = null;
            this._formatPlain = null;
            this._timezone = tz;
        }
    }
    
    @Override
    public Date parse(String dateStr) throws ParseException {
        dateStr = dateStr.trim();
        final ParsePosition pos = new ParsePosition(0);
        final Date result = this.parse(dateStr, pos);
        if (result != null) {
            return result;
        }
        final StringBuilder sb = new StringBuilder();
        for (final String f : StdDateFormat.ALL_FORMATS) {
            if (sb.length() > 0) {
                sb.append("\", \"");
            }
            else {
                sb.append('\"');
            }
            sb.append(f);
        }
        sb.append('\"');
        throw new ParseException(String.format("Can not parse date \"%s\": not compatible with any of standard forms (%s)", dateStr, sb.toString()), pos.getErrorIndex());
    }
    
    @Override
    public Date parse(final String dateStr, final ParsePosition pos) {
        if (this.looksLikeISO8601(dateStr)) {
            return this.parseAsISO8601(dateStr, pos);
        }
        int i = dateStr.length();
        while (--i >= 0) {
            final char ch = dateStr.charAt(i);
            if (ch < '0' || ch > '9') {
                if (i > 0) {
                    break;
                }
                if (ch != '-') {
                    break;
                }
                continue;
            }
        }
        if (i < 0 && (dateStr.charAt(0) == '-' || NumberInput.inLongRange(dateStr, false))) {
            return new Date(Long.parseLong(dateStr));
        }
        return this.parseAsRFC1123(dateStr, pos);
    }
    
    @Override
    public StringBuffer format(final Date date, final StringBuffer toAppendTo, final FieldPosition fieldPosition) {
        if (this._formatISO8601 == null) {
            this._formatISO8601 = _cloneFormat(StdDateFormat.DATE_FORMAT_ISO8601, "yyyy-MM-dd'T'HH:mm:ss.SSSZ", this._timezone, this._locale);
        }
        return this._formatISO8601.format(date, toAppendTo, fieldPosition);
    }
    
    @Override
    public String toString() {
        String str = "DateFormat " + this.getClass().getName();
        final TimeZone tz = this._timezone;
        if (tz != null) {
            str = str + " (timezone: " + tz + ")";
        }
        str = str + "(locale: " + this._locale + ")";
        return str;
    }
    
    protected boolean looksLikeISO8601(final String dateStr) {
        return dateStr.length() >= 5 && Character.isDigit(dateStr.charAt(0)) && Character.isDigit(dateStr.charAt(3)) && dateStr.charAt(4) == '-';
    }
    
    protected Date parseAsISO8601(String dateStr, final ParsePosition pos) {
        int len = dateStr.length();
        char c = dateStr.charAt(len - 1);
        DateFormat df;
        if (len <= 10 && Character.isDigit(c)) {
            df = this._formatPlain;
            if (df == null) {
                final DateFormat cloneFormat = _cloneFormat(StdDateFormat.DATE_FORMAT_PLAIN, "yyyy-MM-dd", this._timezone, this._locale);
                this._formatPlain = cloneFormat;
                df = cloneFormat;
            }
        }
        else if (c == 'Z') {
            df = this._formatISO8601_z;
            if (df == null) {
                final DateFormat cloneFormat2 = _cloneFormat(StdDateFormat.DATE_FORMAT_ISO8601_Z, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", this._timezone, this._locale);
                this._formatISO8601_z = cloneFormat2;
                df = cloneFormat2;
            }
            if (dateStr.charAt(len - 4) == ':') {
                final StringBuilder sb = new StringBuilder(dateStr);
                sb.insert(len - 1, ".000");
                dateStr = sb.toString();
            }
        }
        else if (hasTimeZone(dateStr)) {
            c = dateStr.charAt(len - 3);
            if (c == ':') {
                final StringBuilder sb = new StringBuilder(dateStr);
                sb.delete(len - 3, len - 2);
                dateStr = sb.toString();
            }
            else if (c == '+' || c == '-') {
                dateStr += "00";
            }
            len = dateStr.length();
            c = dateStr.charAt(len - 9);
            if (Character.isDigit(c)) {
                final StringBuilder sb = new StringBuilder(dateStr);
                sb.insert(len - 5, ".000");
                dateStr = sb.toString();
            }
            df = this._formatISO8601;
            if (this._formatISO8601 == null) {
                final DateFormat cloneFormat3 = _cloneFormat(StdDateFormat.DATE_FORMAT_ISO8601, "yyyy-MM-dd'T'HH:mm:ss.SSSZ", this._timezone, this._locale);
                this._formatISO8601 = cloneFormat3;
                df = cloneFormat3;
            }
        }
        else {
            final StringBuilder sb = new StringBuilder(dateStr);
            final int timeLen = len - dateStr.lastIndexOf(84) - 1;
            if (timeLen <= 8) {
                sb.append(".000");
            }
            sb.append('Z');
            dateStr = sb.toString();
            df = this._formatISO8601_z;
            if (df == null) {
                final DateFormat cloneFormat4 = _cloneFormat(StdDateFormat.DATE_FORMAT_ISO8601_Z, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", this._timezone, this._locale);
                this._formatISO8601_z = cloneFormat4;
                df = cloneFormat4;
            }
        }
        return df.parse(dateStr, pos);
    }
    
    protected Date parseAsRFC1123(final String dateStr, final ParsePosition pos) {
        if (this._formatRFC1123 == null) {
            this._formatRFC1123 = _cloneFormat(StdDateFormat.DATE_FORMAT_RFC1123, "EEE, dd MMM yyyy HH:mm:ss zzz", this._timezone, this._locale);
        }
        return this._formatRFC1123.parse(dateStr, pos);
    }
    
    private static final boolean hasTimeZone(final String str) {
        final int len = str.length();
        if (len >= 6) {
            char c = str.charAt(len - 6);
            if (c == '+' || c == '-') {
                return true;
            }
            c = str.charAt(len - 5);
            if (c == '+' || c == '-') {
                return true;
            }
            c = str.charAt(len - 3);
            if (c == '+' || c == '-') {
                return true;
            }
        }
        return false;
    }
    
    private static final DateFormat _cloneFormat(DateFormat df, final String format, final TimeZone tz, final Locale loc) {
        if (!loc.equals(StdDateFormat.DEFAULT_LOCALE)) {
            df = new SimpleDateFormat(format, loc);
            df.setTimeZone((tz == null) ? StdDateFormat.DEFAULT_TIMEZONE : tz);
        }
        else {
            df = (DateFormat)df.clone();
            if (tz != null) {
                df.setTimeZone(tz);
            }
        }
        return df;
    }
    
    static {
        ALL_FORMATS = new String[] { "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "EEE, dd MMM yyyy HH:mm:ss zzz", "yyyy-MM-dd" };
        DEFAULT_TIMEZONE = TimeZone.getTimeZone("GMT");
        DEFAULT_LOCALE = Locale.US;
        (DATE_FORMAT_RFC1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", StdDateFormat.DEFAULT_LOCALE)).setTimeZone(StdDateFormat.DEFAULT_TIMEZONE);
        (DATE_FORMAT_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", StdDateFormat.DEFAULT_LOCALE)).setTimeZone(StdDateFormat.DEFAULT_TIMEZONE);
        (DATE_FORMAT_ISO8601_Z = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", StdDateFormat.DEFAULT_LOCALE)).setTimeZone(StdDateFormat.DEFAULT_TIMEZONE);
        (DATE_FORMAT_PLAIN = new SimpleDateFormat("yyyy-MM-dd", StdDateFormat.DEFAULT_LOCALE)).setTimeZone(StdDateFormat.DEFAULT_TIMEZONE);
        instance = new StdDateFormat();
    }
}
