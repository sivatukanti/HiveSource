// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.http.impl.cookie;

import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.Cookie;
import org.apache.http.conn.util.PublicSuffixMatcher;
import java.util.Collection;
import org.apache.http.cookie.CookieAttributeHandler;

@Deprecated
public class PublicSuffixFilter implements CookieAttributeHandler
{
    private final CookieAttributeHandler wrapped;
    private Collection<String> exceptions;
    private Collection<String> suffixes;
    private PublicSuffixMatcher matcher;
    
    public PublicSuffixFilter(final CookieAttributeHandler wrapped) {
        this.wrapped = wrapped;
    }
    
    public void setPublicSuffixes(final Collection<String> suffixes) {
        this.suffixes = suffixes;
        this.matcher = null;
    }
    
    public void setExceptions(final Collection<String> exceptions) {
        this.exceptions = exceptions;
        this.matcher = null;
    }
    
    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        return !this.isForPublicSuffix(cookie) && this.wrapped.match(cookie, origin);
    }
    
    @Override
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        this.wrapped.parse(cookie, value);
    }
    
    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        this.wrapped.validate(cookie, origin);
    }
    
    private boolean isForPublicSuffix(final Cookie cookie) {
        if (this.matcher == null) {
            this.matcher = new PublicSuffixMatcher(this.suffixes, this.exceptions);
        }
        return this.matcher.matches(cookie.getDomain());
    }
}
