// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.header;

import java.text.ParseException;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.util.Iterator;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.spi.HeaderDelegateProvider;

public class MediaTypeProvider implements HeaderDelegateProvider<MediaType>
{
    @Override
    public boolean supports(final Class<?> type) {
        return MediaType.class.isAssignableFrom(type);
    }
    
    @Override
    public String toString(final MediaType header) {
        final StringBuilder b = new StringBuilder();
        b.append(header.getType()).append('/').append(header.getSubtype());
        for (final Map.Entry<String, String> e : header.getParameters().entrySet()) {
            b.append("; ").append(e.getKey()).append('=');
            WriterUtil.appendQuotedIfNonToken(b, e.getValue());
        }
        return b.toString();
    }
    
    @Override
    public MediaType fromString(final String header) {
        if (header == null) {
            throw new IllegalArgumentException("Media type is null");
        }
        try {
            return valueOf(HttpHeaderReader.newInstance(header));
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException("Error parsing media type '" + header + "'", ex);
        }
    }
    
    public static MediaType valueOf(final HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        final String type = reader.nextToken();
        reader.nextSeparator('/');
        final String subType = reader.nextToken();
        Map<String, String> params = null;
        if (reader.hasNext()) {
            params = HttpHeaderReader.readParameters(reader);
        }
        return new MediaType(type, subType, params);
    }
}
