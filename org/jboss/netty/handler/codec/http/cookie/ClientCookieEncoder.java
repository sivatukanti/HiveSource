// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.cookie;

import java.util.Iterator;

public final class ClientCookieEncoder extends CookieEncoder
{
    public static final ClientCookieEncoder STRICT;
    public static final ClientCookieEncoder LAX;
    
    private ClientCookieEncoder(final boolean strict) {
        super(strict);
    }
    
    public String encode(final String name, final String value) {
        return this.encode(new DefaultCookie(name, value));
    }
    
    public String encode(final Cookie cookie) {
        if (cookie == null) {
            throw new NullPointerException("cookie");
        }
        final StringBuilder buf = new StringBuilder();
        this.encode(buf, cookie);
        return CookieUtil.stripTrailingSeparator(buf);
    }
    
    public String encode(final Cookie... cookies) {
        if (cookies == null) {
            throw new NullPointerException("cookies");
        }
        if (cookies.length == 0) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        for (final Cookie c : cookies) {
            if (c == null) {
                break;
            }
            this.encode(buf, c);
        }
        return CookieUtil.stripTrailingSeparatorOrNull(buf);
    }
    
    public String encode(final Iterable<? extends Cookie> cookies) {
        if (cookies == null) {
            throw new NullPointerException("cookies");
        }
        final Iterator<? extends Cookie> cookiesIt = cookies.iterator();
        if (!cookiesIt.hasNext()) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        while (cookiesIt.hasNext()) {
            final Cookie c = (Cookie)cookiesIt.next();
            if (c == null) {
                break;
            }
            this.encode(buf, c);
        }
        return CookieUtil.stripTrailingSeparatorOrNull(buf);
    }
    
    private void encode(final StringBuilder buf, final Cookie c) {
        final String name = c.name();
        final String value = (c.value() != null) ? c.value() : "";
        this.validateCookie(name, value);
        if (c.wrap()) {
            CookieUtil.addQuoted(buf, name, value);
        }
        else {
            CookieUtil.add(buf, name, value);
        }
    }
    
    static {
        STRICT = new ClientCookieEncoder(true);
        LAX = new ClientCookieEncoder(false);
    }
}
