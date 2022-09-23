// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import org.eclipse.jetty.util.StringUtil;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateGenerator
{
    private static final TimeZone __GMT;
    static final String[] DAYS;
    static final String[] MONTHS;
    private static final ThreadLocal<DateGenerator> __dateGenerator;
    public static final String __01Jan1970;
    private final StringBuilder buf;
    private final GregorianCalendar gc;
    
    public DateGenerator() {
        this.buf = new StringBuilder(32);
        this.gc = new GregorianCalendar(DateGenerator.__GMT);
    }
    
    public static String formatDate(final long date) {
        return DateGenerator.__dateGenerator.get().doFormatDate(date);
    }
    
    public static void formatCookieDate(final StringBuilder buf, final long date) {
        DateGenerator.__dateGenerator.get().doFormatCookieDate(buf, date);
    }
    
    public static String formatCookieDate(final long date) {
        final StringBuilder buf = new StringBuilder(28);
        formatCookieDate(buf, date);
        return buf.toString();
    }
    
    public String doFormatDate(final long date) {
        this.buf.setLength(0);
        this.gc.setTimeInMillis(date);
        final int day_of_week = this.gc.get(7);
        final int day_of_month = this.gc.get(5);
        final int month = this.gc.get(2);
        int year = this.gc.get(1);
        final int century = year / 100;
        year %= 100;
        final int hours = this.gc.get(11);
        final int minutes = this.gc.get(12);
        final int seconds = this.gc.get(13);
        this.buf.append(DateGenerator.DAYS[day_of_week]);
        this.buf.append(',');
        this.buf.append(' ');
        StringUtil.append2digits(this.buf, day_of_month);
        this.buf.append(' ');
        this.buf.append(DateGenerator.MONTHS[month]);
        this.buf.append(' ');
        StringUtil.append2digits(this.buf, century);
        StringUtil.append2digits(this.buf, year);
        this.buf.append(' ');
        StringUtil.append2digits(this.buf, hours);
        this.buf.append(':');
        StringUtil.append2digits(this.buf, minutes);
        this.buf.append(':');
        StringUtil.append2digits(this.buf, seconds);
        this.buf.append(" GMT");
        return this.buf.toString();
    }
    
    public void doFormatCookieDate(final StringBuilder buf, final long date) {
        this.gc.setTimeInMillis(date);
        final int day_of_week = this.gc.get(7);
        final int day_of_month = this.gc.get(5);
        final int month = this.gc.get(2);
        int year = this.gc.get(1);
        year %= 10000;
        int epoch = (int)(date / 1000L % 86400L);
        final int seconds = epoch % 60;
        epoch /= 60;
        final int minutes = epoch % 60;
        final int hours = epoch / 60;
        buf.append(DateGenerator.DAYS[day_of_week]);
        buf.append(',');
        buf.append(' ');
        StringUtil.append2digits(buf, day_of_month);
        buf.append('-');
        buf.append(DateGenerator.MONTHS[month]);
        buf.append('-');
        StringUtil.append2digits(buf, year / 100);
        StringUtil.append2digits(buf, year % 100);
        buf.append(' ');
        StringUtil.append2digits(buf, hours);
        buf.append(':');
        StringUtil.append2digits(buf, minutes);
        buf.append(':');
        StringUtil.append2digits(buf, seconds);
        buf.append(" GMT");
    }
    
    static {
        (__GMT = TimeZone.getTimeZone("GMT")).setID("GMT");
        DAYS = new String[] { "Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
        MONTHS = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan" };
        __dateGenerator = new ThreadLocal<DateGenerator>() {
            @Override
            protected DateGenerator initialValue() {
                return new DateGenerator();
            }
        };
        __01Jan1970 = formatDate(0L);
    }
}
