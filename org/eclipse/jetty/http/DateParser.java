// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class DateParser
{
    private static final TimeZone __GMT;
    static final String[] __dateReceiveFmt;
    private static final ThreadLocal<DateParser> __dateParser;
    final SimpleDateFormat[] _dateReceive;
    
    public DateParser() {
        this._dateReceive = new SimpleDateFormat[DateParser.__dateReceiveFmt.length];
    }
    
    public static long parseDate(final String date) {
        return DateParser.__dateParser.get().parse(date);
    }
    
    private long parse(final String dateVal) {
        int i = 0;
        while (i < this._dateReceive.length) {
            if (this._dateReceive[i] == null) {
                (this._dateReceive[i] = new SimpleDateFormat(DateParser.__dateReceiveFmt[i], Locale.US)).setTimeZone(DateParser.__GMT);
            }
            try {
                final Date date = (Date)this._dateReceive[i].parseObject(dateVal);
                return date.getTime();
            }
            catch (Exception ex) {
                ++i;
                continue;
            }
            break;
        }
        if (dateVal.endsWith(" GMT")) {
            final String val = dateVal.substring(0, dateVal.length() - 4);
            final SimpleDateFormat[] dateReceive = this._dateReceive;
            final int length = dateReceive.length;
            int j = 0;
            while (j < length) {
                final SimpleDateFormat element = dateReceive[j];
                try {
                    final Date date2 = (Date)element.parseObject(val);
                    return date2.getTime();
                }
                catch (Exception ex2) {
                    ++j;
                    continue;
                }
                break;
            }
        }
        return -1L;
    }
    
    static {
        (__GMT = TimeZone.getTimeZone("GMT")).setID("GMT");
        __dateReceiveFmt = new String[] { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss", "EEE MMM dd HH:mm:ss yyyy", "EEE, dd MMM yyyy HH:mm:ss", "EEE dd MMM yyyy HH:mm:ss zzz", "EEE dd MMM yyyy HH:mm:ss", "EEE MMM dd yyyy HH:mm:ss zzz", "EEE MMM dd yyyy HH:mm:ss", "EEE MMM-dd-yyyy HH:mm:ss zzz", "EEE MMM-dd-yyyy HH:mm:ss", "dd MMM yyyy HH:mm:ss zzz", "dd MMM yyyy HH:mm:ss", "dd-MMM-yy HH:mm:ss zzz", "dd-MMM-yy HH:mm:ss", "MMM dd HH:mm:ss yyyy zzz", "MMM dd HH:mm:ss yyyy", "EEE MMM dd HH:mm:ss yyyy zzz", "EEE, MMM dd HH:mm:ss yyyy zzz", "EEE, MMM dd HH:mm:ss yyyy", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE dd-MMM-yy HH:mm:ss zzz", "EEE dd-MMM-yy HH:mm:ss" };
        __dateParser = new ThreadLocal<DateParser>() {
            @Override
            protected DateParser initialValue() {
                return new DateParser();
            }
        };
    }
}
