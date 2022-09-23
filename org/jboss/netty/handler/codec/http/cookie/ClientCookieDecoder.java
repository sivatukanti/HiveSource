// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.cookie;

import java.util.Date;
import java.text.ParsePosition;
import org.jboss.netty.handler.codec.http.HttpHeaderDateFormat;

public final class ClientCookieDecoder extends CookieDecoder
{
    public static final ClientCookieDecoder STRICT;
    public static final ClientCookieDecoder LAX;
    
    private ClientCookieDecoder(final boolean strict) {
        super(strict);
    }
    
    public Cookie decode(final String header) {
        if (header == null) {
            throw new NullPointerException("header");
        }
        final int headerLen = header.length();
        if (headerLen == 0) {
            return null;
        }
        CookieBuilder cookieBuilder = null;
        int i = 0;
        while (i != headerLen) {
            final char c = header.charAt(i);
            if (c == ',') {
                return cookieBuilder.cookie();
            }
            if (c == '\t' || c == '\n' || c == '\u000b' || c == '\f' || c == '\r' || c == ' ' || c == ';') {
                ++i;
            }
            else {
                final int nameBegin = i;
                int nameEnd = i;
                int valueBegin = -1;
                int valueEnd = -1;
                if (i != headerLen) {
                    while (true) {
                        final char curChar = header.charAt(i);
                        if (curChar == ';') {
                            nameEnd = i;
                            valueEnd = (valueBegin = -1);
                            break;
                        }
                        if (curChar == '=') {
                            nameEnd = i;
                            if (++i == headerLen) {
                                valueEnd = (valueBegin = 0);
                                break;
                            }
                            valueBegin = i;
                            final int semiPos = header.indexOf(59, i);
                            i = (valueEnd = ((semiPos > 0) ? semiPos : headerLen));
                            break;
                        }
                        else {
                            if (++i == headerLen) {
                                nameEnd = headerLen;
                                valueEnd = (valueBegin = -1);
                                break;
                            }
                            continue;
                        }
                    }
                }
                if (valueEnd > 0 && header.charAt(valueEnd - 1) == ',') {
                    --valueEnd;
                }
                if (cookieBuilder == null) {
                    final DefaultCookie cookie = this.initCookie(header, nameBegin, nameEnd, valueBegin, valueEnd);
                    if (cookie == null) {
                        return null;
                    }
                    cookieBuilder = new CookieBuilder(cookie);
                }
                else {
                    final String attrValue = (valueBegin == -1) ? null : header.substring(valueBegin, valueEnd);
                    cookieBuilder.appendAttribute(header, nameBegin, nameEnd, attrValue);
                }
            }
        }
        return cookieBuilder.cookie();
    }
    
    static {
        STRICT = new ClientCookieDecoder(true);
        LAX = new ClientCookieDecoder(false);
    }
    
    private static class CookieBuilder
    {
        private final DefaultCookie cookie;
        private String domain;
        private String path;
        private int maxAge;
        private String expires;
        private boolean secure;
        private boolean httpOnly;
        
        public CookieBuilder(final DefaultCookie cookie) {
            this.maxAge = Integer.MIN_VALUE;
            this.cookie = cookie;
        }
        
        private int mergeMaxAgeAndExpire(final int maxAge, final String expires) {
            if (maxAge != Integer.MIN_VALUE) {
                return maxAge;
            }
            if (expires != null) {
                final Date expiresDate = HttpHeaderDateFormat.get().parse(expires, new ParsePosition(0));
                if (expiresDate != null) {
                    final long maxAgeMillis = expiresDate.getTime() - System.currentTimeMillis();
                    return (int)(maxAgeMillis / 1000L + ((maxAgeMillis % 1000L != 0L) ? 1 : 0));
                }
            }
            return Integer.MIN_VALUE;
        }
        
        public Cookie cookie() {
            this.cookie.setDomain(this.domain);
            this.cookie.setPath(this.path);
            this.cookie.setMaxAge(this.mergeMaxAgeAndExpire(this.maxAge, this.expires));
            this.cookie.setSecure(this.secure);
            this.cookie.setHttpOnly(this.httpOnly);
            return this.cookie;
        }
        
        public void appendAttribute(final String header, final int keyStart, final int keyEnd, final String value) {
            this.setCookieAttribute(header, keyStart, keyEnd, value);
        }
        
        private void setCookieAttribute(final String header, final int keyStart, final int keyEnd, final String value) {
            final int length = keyEnd - keyStart;
            if (length == 4) {
                this.parse4(header, keyStart, value);
            }
            else if (length == 6) {
                this.parse6(header, keyStart, value);
            }
            else if (length == 7) {
                this.parse7(header, keyStart, value);
            }
            else if (length == 8) {
                this.parse8(header, keyStart, value);
            }
        }
        
        private void parse4(final String header, final int nameStart, final String value) {
            if (header.regionMatches(true, nameStart, "Path", 0, 4)) {
                this.path = value;
            }
        }
        
        private void parse6(final String header, final int nameStart, final String value) {
            if (header.regionMatches(true, nameStart, "Domain", 0, 5)) {
                this.domain = ((value.length() > 0) ? value.toString() : null);
            }
            else if (header.regionMatches(true, nameStart, "Secure", 0, 5)) {
                this.secure = true;
            }
        }
        
        private void setExpire(final String value) {
            this.expires = value;
        }
        
        private void setMaxAge(final String value) {
            try {
                this.maxAge = Math.max(Integer.valueOf(value), 0);
            }
            catch (NumberFormatException ex) {}
        }
        
        private void parse7(final String header, final int nameStart, final String value) {
            if (header.regionMatches(true, nameStart, "Expires", 0, 7)) {
                this.setExpire(value);
            }
            else if (header.regionMatches(true, nameStart, "Max-Age", 0, 7)) {
                this.setMaxAge(value);
            }
        }
        
        private void parse8(final String header, final int nameStart, final String value) {
            if (header.regionMatches(true, nameStart, "HTTPOnly", 0, 8)) {
                this.httpOnly = true;
            }
        }
    }
}
