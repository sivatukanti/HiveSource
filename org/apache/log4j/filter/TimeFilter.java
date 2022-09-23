// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.filter;

import org.apache.log4j.spi.LoggingEvent;
import java.text.ParseException;
import org.apache.log4j.helpers.LogLog;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.spi.Filter;

public final class TimeFilter extends Filter
{
    private boolean acceptOnMatch;
    private long start;
    private long end;
    private Calendar calendar;
    private static final long HOUR_MS = 3600000L;
    private static final long MINUTE_MS = 60000L;
    private static final long SECOND_MS = 1000L;
    
    public TimeFilter() {
        this.acceptOnMatch = true;
        this.start = 0L;
        this.end = Long.MAX_VALUE;
        this.calendar = Calendar.getInstance();
    }
    
    public void setStart(final String s) {
        final SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
        stf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            this.start = stf.parse(s).getTime();
        }
        catch (ParseException ex) {
            LogLog.warn("Error parsing start value " + s, ex);
        }
    }
    
    public void setEnd(final String s) {
        final SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
        stf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            this.end = stf.parse(s).getTime();
        }
        catch (ParseException ex) {
            LogLog.warn("Error parsing end value " + s, ex);
        }
    }
    
    public void setTimeZone(final String s) {
        if (s == null) {
            this.calendar = Calendar.getInstance();
        }
        else {
            this.calendar = Calendar.getInstance(TimeZone.getTimeZone(s));
        }
    }
    
    public synchronized void setAcceptOnMatch(final boolean acceptOnMatch) {
        this.acceptOnMatch = acceptOnMatch;
    }
    
    public synchronized boolean getAcceptOnMatch() {
        return this.acceptOnMatch;
    }
    
    public int decide(final LoggingEvent event) {
        this.calendar.setTimeInMillis(event.timeStamp);
        final long apparentOffset = this.calendar.get(11) * 3600000L + this.calendar.get(12) * 60000L + this.calendar.get(13) * 1000L + this.calendar.get(14);
        if (apparentOffset < this.start || apparentOffset >= this.end) {
            return 0;
        }
        if (this.acceptOnMatch) {
            return 1;
        }
        return -1;
    }
}
