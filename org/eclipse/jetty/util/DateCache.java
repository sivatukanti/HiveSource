// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Date;
import java.util.TimeZone;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class DateCache
{
    public static final String DEFAULT_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
    private final String _formatString;
    private final String _tzFormatString;
    private final SimpleDateFormat _tzFormat;
    private final Locale _locale;
    private volatile Tick _tick;
    
    public DateCache() {
        this("EEE MMM dd HH:mm:ss zzz yyyy");
    }
    
    public DateCache(final String format) {
        this(format, null, TimeZone.getDefault());
    }
    
    public DateCache(final String format, final Locale l) {
        this(format, l, TimeZone.getDefault());
    }
    
    public DateCache(final String format, final Locale l, final String tz) {
        this(format, l, TimeZone.getTimeZone(tz));
    }
    
    public DateCache(final String format, final Locale l, final TimeZone tz) {
        this._formatString = format;
        this._locale = l;
        final int zIndex = this._formatString.indexOf("ZZZ");
        if (zIndex >= 0) {
            final String ss1 = this._formatString.substring(0, zIndex);
            final String ss2 = this._formatString.substring(zIndex + 3);
            int tzOffset = tz.getRawOffset();
            final StringBuilder sb = new StringBuilder(this._formatString.length() + 10);
            sb.append(ss1);
            sb.append("'");
            if (tzOffset >= 0) {
                sb.append('+');
            }
            else {
                tzOffset = -tzOffset;
                sb.append('-');
            }
            final int raw = tzOffset / 60000;
            final int hr = raw / 60;
            final int min = raw % 60;
            if (hr < 10) {
                sb.append('0');
            }
            sb.append(hr);
            if (min < 10) {
                sb.append('0');
            }
            sb.append(min);
            sb.append('\'');
            sb.append(ss2);
            this._tzFormatString = sb.toString();
        }
        else {
            this._tzFormatString = this._formatString;
        }
        if (this._locale != null) {
            this._tzFormat = new SimpleDateFormat(this._tzFormatString, this._locale);
        }
        else {
            this._tzFormat = new SimpleDateFormat(this._tzFormatString);
        }
        this._tzFormat.setTimeZone(tz);
        this._tick = null;
    }
    
    public TimeZone getTimeZone() {
        return this._tzFormat.getTimeZone();
    }
    
    public String format(final Date inDate) {
        final long seconds = inDate.getTime() / 1000L;
        final Tick tick = this._tick;
        if (tick == null || seconds != tick._seconds) {
            synchronized (this) {
                return this._tzFormat.format(inDate);
            }
        }
        return tick._string;
    }
    
    public String format(final long inDate) {
        final long seconds = inDate / 1000L;
        final Tick tick = this._tick;
        if (tick == null || seconds != tick._seconds) {
            final Date d = new Date(inDate);
            synchronized (this) {
                return this._tzFormat.format(d);
            }
        }
        return tick._string;
    }
    
    public String formatNow(final long now) {
        final long seconds = now / 1000L;
        final Tick tick = this._tick;
        if (tick != null && tick._seconds == seconds) {
            return tick._string;
        }
        return this.formatTick(now)._string;
    }
    
    public String now() {
        return this.formatNow(System.currentTimeMillis());
    }
    
    public Tick tick() {
        return this.formatTick(System.currentTimeMillis());
    }
    
    protected Tick formatTick(final long now) {
        final long seconds = now / 1000L;
        synchronized (this) {
            if (this._tick == null || this._tick._seconds != seconds) {
                final String s = this._tzFormat.format(new Date(now));
                return this._tick = new Tick(seconds, s);
            }
            return this._tick;
        }
    }
    
    public String getFormatString() {
        return this._formatString;
    }
    
    public static class Tick
    {
        final long _seconds;
        final String _string;
        
        public Tick(final long seconds, final String string) {
            this._seconds = seconds;
            this._string = string;
        }
    }
}
