// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import java.text.ParseException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

public class DateStringConverter implements TypeConverter<Date, String>
{
    private static final ThreadLocal<FormatterInfo> formatterThreadInfo;
    
    private DateFormat getFormatter() {
        final FormatterInfo formatInfo = DateStringConverter.formatterThreadInfo.get();
        if (formatInfo.formatter == null) {
            formatInfo.formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        }
        return formatInfo.formatter;
    }
    
    @Override
    public Date toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        try {
            return this.getFormatter().parse(str);
        }
        catch (ParseException pe) {
            throw new NucleusDataStoreException(DateStringConverter.LOCALISER.msg("016002", str, Date.class.getName()), pe);
        }
    }
    
    @Override
    public String toDatastoreType(final Date date) {
        return (date != null) ? this.getFormatter().format(date) : null;
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
