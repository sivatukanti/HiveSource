// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.ajp;

import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.BufferCache;

public class Ajp13ResponseHeaders extends BufferCache
{
    public static final int MAGIC = 43776;
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LANGUAGE = "Content-Language";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String DATE = "Date";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String LOCATION = "Location";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String SET_COOKIE2 = "Set-Cookie2";
    public static final String SERVLET_ENGINE = "Servlet-Engine";
    public static final String STATUS = "Status";
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    public static final int CONTENT_TYPE_ORDINAL = 1;
    public static final int CONTENT_LANGUAGE_ORDINAL = 2;
    public static final int CONTENT_LENGTH_ORDINAL = 3;
    public static final int DATE_ORDINAL = 4;
    public static final int LAST_MODIFIED_ORDINAL = 5;
    public static final int LOCATION_ORDINAL = 6;
    public static final int SET_COOKIE_ORDINAL = 7;
    public static final int SET_COOKIE2_ORDINAL = 8;
    public static final int SERVLET_ENGINE_ORDINAL = 9;
    public static final int STATUS_ORDINAL = 10;
    public static final int WWW_AUTHENTICATE_ORDINAL = 11;
    public static final BufferCache CACHE;
    public static final Buffer CONTENT_TYPE_BUFFER;
    public static final Buffer CONTENT_LANGUAGE_BUFFER;
    public static final Buffer CONTENT_LENGTH_BUFFER;
    public static final Buffer DATE_BUFFER;
    public static final Buffer LAST_MODIFIED_BUFFER;
    public static final Buffer LOCATION_BUFFER;
    public static final Buffer SET_COOKIE_BUFFER;
    public static final Buffer SET_COOKIE2_BUFFER;
    public static final Buffer SERVLET_ENGINE_BUFFER;
    public static final Buffer STATUS_BUFFER;
    public static final Buffer WWW_AUTHENTICATE_BUFFER;
    
    static {
        CACHE = new BufferCache();
        CONTENT_TYPE_BUFFER = Ajp13ResponseHeaders.CACHE.add("Content-Type", 1);
        CONTENT_LANGUAGE_BUFFER = Ajp13ResponseHeaders.CACHE.add("Content-Language", 2);
        CONTENT_LENGTH_BUFFER = Ajp13ResponseHeaders.CACHE.add("Content-Length", 3);
        DATE_BUFFER = Ajp13ResponseHeaders.CACHE.add("Date", 4);
        LAST_MODIFIED_BUFFER = Ajp13ResponseHeaders.CACHE.add("Last-Modified", 5);
        LOCATION_BUFFER = Ajp13ResponseHeaders.CACHE.add("Location", 6);
        SET_COOKIE_BUFFER = Ajp13ResponseHeaders.CACHE.add("Set-Cookie", 7);
        SET_COOKIE2_BUFFER = Ajp13ResponseHeaders.CACHE.add("Set-Cookie2", 8);
        SERVLET_ENGINE_BUFFER = Ajp13ResponseHeaders.CACHE.add("Servlet-Engine", 9);
        STATUS_BUFFER = Ajp13ResponseHeaders.CACHE.add("Status", 10);
        WWW_AUTHENTICATE_BUFFER = Ajp13ResponseHeaders.CACHE.add("WWW-Authenticate", 11);
    }
}
