// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import java.util.Date;
import java.text.ParseException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Calendar;

public class CalendarStringConverter implements TypeConverter<Calendar, String>
{
    private static final ThreadLocal<FormatterInfo> formatterThreadInfo;
    
    private DateFormat getFormatter() {
        final FormatterInfo formatInfo = CalendarStringConverter.formatterThreadInfo.get();
        if (formatInfo.formatter == null) {
            formatInfo.formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        }
        return formatInfo.formatter;
    }
    
    @Override
    public Calendar toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        try {
            final Date date = this.getFormatter().parse(str);
            final Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        }
        catch (ParseException pe) {
            throw new NucleusDataStoreException(CalendarStringConverter.LOCALISER.msg("016002", str, Calendar.class.getName()), pe);
        }
    }
    
    @Override
    public String toDatastoreType(final Calendar cal) {
        return (cal != null) ? this.getFormatter().format(cal.getTime()) : null;
    }
    
    static {
        formatterThreadInfo = new ThreadLocal<FormatterInfo>() {
            @Override
            protected FormatterInfo initialValue() {
                return new FormatterInfo();
            }
        };
    }
    
    static class FormatterInfo
    {
        SimpleDateFormat formatter;
    }
}
