// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.types.EnumeratedAttribute;
import java.util.Calendar;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Enumeration;
import org.apache.tools.ant.BuildException;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import org.apache.tools.ant.Task;

public class Tstamp extends Task
{
    private Vector customFormats;
    private String prefix;
    
    public Tstamp() {
        this.customFormats = new Vector();
        this.prefix = "";
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
        if (!this.prefix.endsWith(".")) {
            this.prefix += ".";
        }
    }
    
    @Override
    public void execute() throws BuildException {
        try {
            final Date d = new Date();
            final Enumeration i = this.customFormats.elements();
            while (i.hasMoreElements()) {
                final CustomFormat cts = i.nextElement();
                cts.execute(this.getProject(), d, this.getLocation());
            }
            final SimpleDateFormat dstamp = new SimpleDateFormat("yyyyMMdd");
            this.setProperty("DSTAMP", dstamp.format(d));
            final SimpleDateFormat tstamp = new SimpleDateFormat("HHmm");
            this.setProperty("TSTAMP", tstamp.format(d));
            final SimpleDateFormat today = new SimpleDateFormat("MMMM d yyyy", Locale.US);
            this.setProperty("TODAY", today.format(d));
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }
    
    public CustomFormat createFormat() {
        final CustomFormat cts = new CustomFormat();
        this.customFormats.addElement(cts);
        return cts;
    }
    
    private void setProperty(final String name, final String value) {
        this.getProject().setNewProperty(this.prefix + name, value);
    }
    
    public class CustomFormat
    {
        private TimeZone timeZone;
        private String propertyName;
        private String pattern;
        private String language;
        private String country;
        private String variant;
        private int offset;
        private int field;
        
        public CustomFormat() {
            this.offset = 0;
            this.field = 5;
        }
        
        public void setProperty(final String propertyName) {
            this.propertyName = propertyName;
        }
        
        public void setPattern(final String pattern) {
            this.pattern = pattern;
        }
        
        public void setLocale(final String locale) {
            final StringTokenizer st = new StringTokenizer(locale, " \t\n\r\f,");
            try {
                this.language = st.nextToken();
                if (st.hasMoreElements()) {
                    this.country = st.nextToken();
                    if (st.hasMoreElements()) {
                        this.variant = st.nextToken();
                        if (st.hasMoreElements()) {
                            throw new BuildException("bad locale format", Tstamp.this.getLocation());
                        }
                    }
                }
                else {
                    this.country = "";
                }
            }
            catch (NoSuchElementException e) {
                throw new BuildException("bad locale format", e, Tstamp.this.getLocation());
            }
        }
        
        public void setTimezone(final String id) {
            this.timeZone = TimeZone.getTimeZone(id);
        }
        
        public void setOffset(final int offset) {
            this.offset = offset;
        }
        
        @Deprecated
        public void setUnit(final String unit) {
            Tstamp.this.log("DEPRECATED - The setUnit(String) method has been deprecated. Use setUnit(Tstamp.Unit) instead.");
            final Unit u = new Unit();
            u.setValue(unit);
            this.field = u.getCalendarField();
        }
        
        public void setUnit(final Unit unit) {
            this.field = unit.getCalendarField();
        }
        
        public void execute(final Project project, Date date, final Location location) {
            if (this.propertyName == null) {
                throw new BuildException("property attribute must be provided", location);
            }
            if (this.pattern == null) {
                throw new BuildException("pattern attribute must be provided", location);
            }
            SimpleDateFormat sdf;
            if (this.language == null) {
                sdf = new SimpleDateFormat(this.pattern);
            }
            else if (this.variant == null) {
                sdf = new SimpleDateFormat(this.pattern, new Locale(this.language, this.country));
            }
            else {
                sdf = new SimpleDateFormat(this.pattern, new Locale(this.language, this.country, this.variant));
            }
            if (this.offset != 0) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(this.field, this.offset);
                date = calendar.getTime();
            }
            if (this.timeZone != null) {
                sdf.setTimeZone(this.timeZone);
            }
            Tstamp.this.setProperty(this.propertyName, sdf.format(date));
        }
    }
    
    public static class Unit extends EnumeratedAttribute
    {
        private static final String MILLISECOND = "millisecond";
        private static final String SECOND = "second";
        private static final String MINUTE = "minute";
        private static final String HOUR = "hour";
        private static final String DAY = "day";
        private static final String WEEK = "week";
        private static final String MONTH = "month";
        private static final String YEAR = "year";
        private static final String[] UNITS;
        private Map calendarFields;
        
        public Unit() {
            (this.calendarFields = new HashMap()).put("millisecond", new Integer(14));
            this.calendarFields.put("second", new Integer(13));
            this.calendarFields.put("minute", new Integer(12));
            this.calendarFields.put("hour", new Integer(11));
            this.calendarFields.put("day", new Integer(5));
            this.calendarFields.put("week", new Integer(3));
            this.calendarFields.put("month", new Integer(2));
            this.calendarFields.put("year", new Integer(1));
        }
        
        public int getCalendarField() {
            final String key = this.getValue().toLowerCase(Locale.ENGLISH);
            final Integer i = this.calendarFields.get(key);
            return i;
        }
        
        @Override
        public String[] getValues() {
            return Unit.UNITS;
        }
        
        static {
            UNITS = new String[] { "millisecond", "second", "minute", "hour", "day", "week", "month", "year" };
        }
    }
}
