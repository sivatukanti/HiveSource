// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.header;

import com.sun.jersey.core.header.reader.HttpHeaderReader;
import javax.ws.rs.core.NewCookie;
import com.sun.jersey.spi.HeaderDelegateProvider;

public class NewCookieProvider implements HeaderDelegateProvider<NewCookie>
{
    @Override
    public boolean supports(final Class<?> type) {
        return type == NewCookie.class;
    }
    
    @Override
    public String toString(final NewCookie cookie) {
        final StringBuilder b = new StringBuilder();
        b.append(cookie.getName()).append('=');
        WriterUtil.appendQuotedIfWhitespace(b, cookie.getValue());
        b.append(";").append("Version=").append(cookie.getVersion());
        if (cookie.getComment() != null) {
            b.append(";Comment=");
            WriterUtil.appendQuotedIfWhitespace(b, cookie.getComment());
        }
        if (cookie.getDomain() != null) {
            b.append(";Domain=");
            WriterUtil.appendQuotedIfWhitespace(b, cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            b.append(";Path=");
            WriterUtil.appendQuotedIfWhitespace(b, cookie.getPath());
        }
        if (cookie.getMaxAge() != -1) {
            b.append(";Max-Age=");
            b.append(cookie.getMaxAge());
        }
        if (cookie.isSecure()) {
            b.append(";Secure");
        }
        return b.toString();
    }
    
    @Override
    public NewCookie fromString(final String header) {
        if (header == null) {
            throw new IllegalArgumentException("NewCookie is null");
        }
        return HttpHeaderReader.readNewCookie(header);
    }
}
