// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.header;

import com.sun.jersey.core.header.reader.HttpHeaderReader;
import javax.ws.rs.core.Cookie;
import com.sun.jersey.spi.HeaderDelegateProvider;

public class CookieProvider implements HeaderDelegateProvider<Cookie>
{
    @Override
    public boolean supports(final Class<?> type) {
        return type == Cookie.class;
    }
    
    @Override
    public String toString(final Cookie cookie) {
        final StringBuilder b = new StringBuilder();
        b.append("$Version=").append(cookie.getVersion()).append(';');
        b.append(cookie.getName()).append('=');
        WriterUtil.appendQuotedIfWhitespace(b, cookie.getValue());
        if (cookie.getDomain() != null) {
            b.append(";$Domain=");
            WriterUtil.appendQuotedIfWhitespace(b, cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            b.append(";$Path=");
            WriterUtil.appendQuotedIfWhitespace(b, cookie.getPath());
        }
        return b.toString();
    }
    
    @Override
    public Cookie fromString(final String header) {
        if (header == null) {
            throw new IllegalArgumentException();
        }
        return HttpHeaderReader.readCookie(header);
    }
}
