// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.header;

import java.text.ParseException;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import javax.ws.rs.core.EntityTag;
import com.sun.jersey.spi.HeaderDelegateProvider;

public class EntityTagProvider implements HeaderDelegateProvider<EntityTag>
{
    @Override
    public boolean supports(final Class<?> type) {
        return type == EntityTag.class;
    }
    
    @Override
    public String toString(final EntityTag header) {
        final StringBuilder b = new StringBuilder();
        if (header.isWeak()) {
            b.append("W/");
        }
        WriterUtil.appendQuoted(b, header.getValue());
        return b.toString();
    }
    
    @Override
    public EntityTag fromString(final String header) {
        if (header == null) {
            throw new IllegalArgumentException("Entity tag is null");
        }
        try {
            final HttpHeaderReader reader = HttpHeaderReader.newInstance(header);
            final HttpHeaderReader.Event e = reader.next(false);
            if (e == HttpHeaderReader.Event.QuotedString) {
                return new EntityTag(reader.getEventValue());
            }
            if (e == HttpHeaderReader.Event.Token && reader.getEventValue().equals("W")) {
                reader.nextSeparator('/');
                return new EntityTag(reader.nextQuotedString(), true);
            }
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException("Error parsing entity tag '" + header + "'", ex);
        }
        throw new IllegalArgumentException("Error parsing entity tag '" + header + "'");
    }
}
