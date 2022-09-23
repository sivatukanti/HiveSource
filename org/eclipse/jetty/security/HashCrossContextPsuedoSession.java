// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

public class HashCrossContextPsuedoSession<T> implements CrossContextPsuedoSession<T>
{
    private final String _cookieName;
    private final String _cookiePath;
    private final Random _random;
    private final Map<String, T> _data;
    
    public HashCrossContextPsuedoSession(final String cookieName, final String cookiePath) {
        this._random = new SecureRandom();
        this._data = new HashMap<String, T>();
        this._cookieName = cookieName;
        this._cookiePath = ((cookiePath == null) ? "/" : cookiePath);
    }
    
    public T fetch(final HttpServletRequest request) {
        for (final Cookie cookie : request.getCookies()) {
            if (this._cookieName.equals(cookie.getName())) {
                final String key = cookie.getValue();
                return this._data.get(key);
            }
        }
        return null;
    }
    
    public void store(final T datum, final HttpServletResponse response) {
        String key;
        synchronized (this._data) {
            do {
                key = Long.toString(Math.abs(this._random.nextLong()), 30 + (int)(System.currentTimeMillis() % 7L));
            } while (this._data.containsKey(key));
            this._data.put(key, datum);
        }
        final Cookie cookie = new Cookie(this._cookieName, key);
        cookie.setPath(this._cookiePath);
        response.addCookie(cookie);
    }
    
    public void clear(final HttpServletRequest request) {
        for (final Cookie cookie : request.getCookies()) {
            if (this._cookieName.equals(cookie.getName())) {
                final String key = cookie.getValue();
                this._data.remove(key);
                break;
            }
        }
    }
}
