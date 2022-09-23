// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.cookie;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jboss.netty.handler.codec.http.HttpHeaderDateFormat;
import java.util.Date;

public final class ServerCookieEncoder extends CookieEncoder
{
    public static final ServerCookieEncoder STRICT;
    public static final ServerCookieEncoder LAX;
    
    private ServerCookieEncoder(final boolean strict) {
        super(strict);
    }
    
    public String encode(final String name, final String value) {
        return this.encode(new DefaultCookie(name, value));
    }
    
    public String encode(final Cookie cookie) {
        if (cookie == null) {
            throw new NullPointerException("cookie");
        }
        final String name = cookie.name();
        final String value = (cookie.value() != null) ? cookie.value() : "";
        this.validateCookie(name, value);
        final StringBuilder buf = new StringBuilder();
        if (cookie.wrap()) {
            CookieUtil.addQuoted(buf, name, value);
        }
        else {
            CookieUtil.add(buf, name, value);
        }
        if (cookie.maxAge() != Integer.MIN_VALUE) {
            CookieUtil.add(buf, "Max-Age", cookie.maxAge());
            final Date expires = new Date(cookie.maxAge() * 1000L + System.currentTimeMillis());
            CookieUtil.add(buf, "Expires", HttpHeaderDateFormat.get().format(expires));
        }
        if (cookie.path() != null) {
            CookieUtil.add(buf, "Path", cookie.path());
        }
        if (cookie.domain() != null) {
            CookieUtil.add(buf, "Domain", cookie.domain());
        }
        if (cookie.isSecure()) {
            CookieUtil.add(buf, "Secure");
        }
        if (cookie.isHttpOnly()) {
            CookieUtil.add(buf, "HTTPOnly");
        }
        return CookieUtil.stripTrailingSeparator(buf);
    }
    
    public List<String> encode(final Cookie... cookies) {
        if (cookies == null) {
            throw new NullPointerException("cookies");
        }
        if (cookies.length == 0) {
            return Collections.emptyList();
        }
        final List<String> encoded = new ArrayList<String>(cookies.length);
        for (final Cookie c : cookies) {
            if (c == null) {
                break;
            }
            encoded.add(this.encode(c));
        }
        return encoded;
    }
    
    public List<String> encode(final Collection<? extends Cookie> cookies) {
        if (cookies == null) {
            throw new NullPointerException("cookies");
        }
        if (cookies.isEmpty()) {
            return Collections.emptyList();
        }
        final List<String> encoded = new ArrayList<String>(cookies.size());
        for (final Cookie c : cookies) {
            if (c == null) {
                break;
            }
            encoded.add(this.encode(c));
        }
        return encoded;
    }
    
    public List<String> encode(final Iterable<? extends Cookie> cookies) {
        if (cookies == null) {
            throw new NullPointerException("cookies");
        }
        if (cookies.iterator().hasNext()) {
            return Collections.emptyList();
        }
        final List<String> encoded = new ArrayList<String>();
        for (final Cookie c : cookies) {
            if (c == null) {
                break;
            }
            encoded.add(this.encode(c));
        }
        return encoded;
    }
    
    static {
        STRICT = new ServerCookieEncoder(true);
        LAX = new ServerCookieEncoder(false);
    }
}
