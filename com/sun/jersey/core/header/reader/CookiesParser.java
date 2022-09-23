// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header.reader;

import javax.ws.rs.core.NewCookie;
import java.util.LinkedHashMap;
import javax.ws.rs.core.Cookie;
import java.util.Map;

class CookiesParser
{
    public static Map<String, Cookie> parseCookies(final String header) {
        final String[] bites = header.split("[;,]");
        final Map<String, Cookie> cookies = new LinkedHashMap<String, Cookie>();
        int version = 0;
        MutableCookie cookie = null;
        for (final String bite : bites) {
            final String[] crumbs = bite.split("=", 2);
            final String name = (crumbs.length > 0) ? crumbs[0].trim() : "";
            String value = (crumbs.length > 1) ? crumbs[1].trim() : "";
            if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
                value = value.substring(1, value.length() - 1);
            }
            if (!name.startsWith("$")) {
                if (cookie != null) {
                    cookies.put(cookie.name, cookie.getImmutableCookie());
                }
                cookie = new MutableCookie(name, value);
                cookie.version = version;
            }
            else if (name.startsWith("$Version")) {
                version = Integer.parseInt(value);
            }
            else if (name.startsWith("$Path") && cookie != null) {
                cookie.path = value;
            }
            else if (name.startsWith("$Domain") && cookie != null) {
                cookie.domain = value;
            }
        }
        if (cookie != null) {
            cookies.put(cookie.name, cookie.getImmutableCookie());
        }
        return cookies;
    }
    
    public static Cookie parseCookie(final String header) {
        final Map<String, Cookie> cookies = parseCookies(header);
        return cookies.entrySet().iterator().next().getValue();
    }
    
    public static NewCookie parseNewCookie(final String header) {
        final String[] bites = header.split("[;,]");
        MutableNewCookie cookie = null;
        for (final String bite : bites) {
            final String[] crumbs = bite.split("=", 2);
            final String name = (crumbs.length > 0) ? crumbs[0].trim() : "";
            String value = (crumbs.length > 1) ? crumbs[1].trim() : "";
            if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
                value = value.substring(1, value.length() - 1);
            }
            if (cookie == null) {
                cookie = new MutableNewCookie(name, value);
            }
            else if (name.startsWith("Comment")) {
                cookie.comment = value;
            }
            else if (name.startsWith("Domain")) {
                cookie.domain = value;
            }
            else if (name.startsWith("Max-Age")) {
                cookie.maxAge = Integer.parseInt(value);
            }
            else if (name.startsWith("Path")) {
                cookie.path = value;
            }
            else if (name.startsWith("Secure")) {
                cookie.secure = true;
            }
            else if (name.startsWith("Version")) {
                cookie.version = Integer.parseInt(value);
            }
            else if (name.startsWith("Domain")) {
                cookie.domain = value;
            }
        }
        return cookie.getImmutableNewCookie();
    }
    
    private static class MutableCookie
    {
        String name;
        String value;
        int version;
        String path;
        String domain;
        
        public MutableCookie(final String name, final String value) {
            this.version = 1;
            this.path = null;
            this.domain = null;
            this.name = name;
            this.value = value;
        }
        
        public Cookie getImmutableCookie() {
            return new Cookie(this.name, this.value, this.path, this.domain, this.version);
        }
    }
    
    private static class MutableNewCookie
    {
        String name;
        String value;
        String path;
        String domain;
        int version;
        String comment;
        int maxAge;
        boolean secure;
        
        public MutableNewCookie(final String name, final String value) {
            this.name = null;
            this.value = null;
            this.path = null;
            this.domain = null;
            this.version = 1;
            this.comment = null;
            this.maxAge = -1;
            this.secure = false;
            this.name = name;
            this.value = value;
        }
        
        public NewCookie getImmutableNewCookie() {
            return new NewCookie(this.name, this.value, this.path, this.domain, this.version, this.comment, this.maxAge, this.secure);
        }
    }
}
