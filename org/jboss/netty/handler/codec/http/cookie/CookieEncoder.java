// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.cookie;

public abstract class CookieEncoder
{
    private final boolean strict;
    
    protected CookieEncoder(final boolean strict) {
        this.strict = strict;
    }
    
    protected void validateCookie(final String name, final String value) {
        if (this.strict) {
            int pos;
            if ((pos = CookieUtil.firstInvalidCookieNameOctet(name)) >= 0) {
                throw new IllegalArgumentException("Cookie name contains an invalid char: " + name.charAt(pos));
            }
            final CharSequence unwrappedValue = CookieUtil.unwrapValue(value);
            if (unwrappedValue == null) {
                throw new IllegalArgumentException("Cookie value wrapping quotes are not balanced: " + value);
            }
            if ((pos = CookieUtil.firstInvalidCookieValueOctet(unwrappedValue)) >= 0) {
                throw new IllegalArgumentException("Cookie value contains an invalid char: " + value.charAt(pos));
            }
        }
    }
}
