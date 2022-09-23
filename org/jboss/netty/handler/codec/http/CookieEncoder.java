// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import org.jboss.netty.handler.codec.http.cookie.ClientCookieEncoder;
import org.jboss.netty.handler.codec.http.cookie.ServerCookieEncoder;
import java.util.TreeSet;
import java.util.Set;

public class CookieEncoder
{
    private final Set<Cookie> cookies;
    private final boolean server;
    private final boolean strict;
    
    public CookieEncoder(final boolean server) {
        this(server, false);
    }
    
    public CookieEncoder(final boolean server, final boolean strict) {
        this.cookies = new TreeSet<Cookie>();
        this.server = server;
        this.strict = strict;
    }
    
    public void addCookie(final String name, final String value) {
        this.cookies.add(new DefaultCookie(name, value));
    }
    
    public void addCookie(final Cookie cookie) {
        this.cookies.add(cookie);
    }
    
    public String encode() {
        String answer;
        if (this.server) {
            answer = this.encodeServerSide();
        }
        else {
            answer = this.encodeClientSide();
        }
        this.cookies.clear();
        return answer;
    }
    
    private String encodeServerSide() {
        if (this.cookies.size() > 1) {
            throw new IllegalStateException("encode() can encode only one cookie on server mode: " + this.cookies.size() + " cookies added");
        }
        final Cookie cookie = this.cookies.isEmpty() ? null : this.cookies.iterator().next();
        final ServerCookieEncoder encoder = this.strict ? ServerCookieEncoder.STRICT : ServerCookieEncoder.LAX;
        return encoder.encode(cookie);
    }
    
    private String encodeClientSide() {
        final ClientCookieEncoder encoder = this.strict ? ClientCookieEncoder.STRICT : ClientCookieEncoder.LAX;
        return encoder.encode(this.cookies);
    }
}
