// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.header;

import java.text.ParseException;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import com.sun.jersey.core.header.HttpDateFormat;
import java.util.Date;
import com.sun.jersey.spi.HeaderDelegateProvider;

public class DateProvider implements HeaderDelegateProvider<Date>
{
    @Override
    public boolean supports(final Class<?> type) {
        return Date.class.isAssignableFrom(type);
    }
    
    @Override
    public String toString(final Date header) {
        return HttpDateFormat.getPreferedDateFormat().format(header);
    }
    
    @Override
    public Date fromString(final String header) {
        try {
            return HttpHeaderReader.readDate(header);
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException("Error parsing date '" + header + "'", ex);
        }
    }
}
