// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

import java.util.Iterator;
import java.text.ParseException;
import java.util.Date;
import java.util.Collections;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.List;

public class HttpDateFormat
{
    private static final String RFC1123_DATE_FORMAT_PATTERN = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final String RFC1036_DATE_FORMAT_PATTERN = "EEEE, dd-MMM-yy HH:mm:ss zzz";
    private static final String ANSI_C_ASCTIME_DATE_FORMAT_PATTERN = "EEE MMM d HH:mm:ss yyyy";
    private static ThreadLocal<List<SimpleDateFormat>> dateFormats;
    
    private HttpDateFormat() {
    }
    
    private static List<SimpleDateFormat> createDateFormats() {
        final SimpleDateFormat[] dateFormats = { new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US), new SimpleDateFormat("EEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US), new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy", Locale.US) };
        final TimeZone tz = TimeZone.getTimeZone("GMT");
        dateFormats[0].setTimeZone(tz);
        dateFormats[1].setTimeZone(tz);
        dateFormats[2].setTimeZone(tz);
        return Collections.unmodifiableList((List<? extends SimpleDateFormat>)Arrays.asList((T[])dateFormats));
    }
    
    public static List<SimpleDateFormat> getDateFormats() {
        return HttpDateFormat.dateFormats.get();
    }
    
    public static SimpleDateFormat getPreferedDateFormat() {
        return HttpDateFormat.dateFormats.get().get(0);
    }
    
    public static Date readDate(final String date) throws ParseException {
        ParseException pe = null;
        for (final SimpleDateFormat f : getDateFormats()) {
            try {
                return f.parse(date);
            }
            catch (ParseException e) {
                pe = ((pe == null) ? e : pe);
                continue;
            }
            break;
        }
        throw pe;
    }
    
    static {
        HttpDateFormat.dateFormats = new ThreadLocal<List<SimpleDateFormat>>() {
            @Override
            protected synchronized List<SimpleDateFormat> initialValue() {
                return createDateFormats();
            }
        };
    }
}
