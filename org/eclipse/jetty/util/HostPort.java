// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

public class HostPort
{
    private final String _host;
    private final int _port;
    
    public HostPort(final String authority) throws IllegalArgumentException {
        if (authority == null) {
            throw new IllegalArgumentException("No Authority");
        }
        try {
            if (authority.isEmpty()) {
                this._host = authority;
                this._port = 0;
            }
            else if (authority.charAt(0) == '[') {
                final int close = authority.lastIndexOf(93);
                if (close < 0) {
                    throw new IllegalArgumentException("Bad IPv6 host");
                }
                this._host = authority.substring(0, close + 1);
                if (authority.length() > close + 1) {
                    if (authority.charAt(close + 1) != ':') {
                        throw new IllegalArgumentException("Bad IPv6 port");
                    }
                    this._port = StringUtil.toInt(authority, close + 2);
                }
                else {
                    this._port = 0;
                }
            }
            else {
                final int c = authority.lastIndexOf(58);
                if (c >= 0) {
                    this._host = authority.substring(0, c);
                    this._port = StringUtil.toInt(authority, c + 1);
                }
                else {
                    this._host = authority;
                    this._port = 0;
                }
            }
        }
        catch (IllegalArgumentException iae) {
            throw iae;
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Bad HostPort") {
                {
                    this.initCause(ex);
                }
            };
        }
        if (this._host == null) {
            throw new IllegalArgumentException("Bad host");
        }
        if (this._port < 0) {
            throw new IllegalArgumentException("Bad port");
        }
    }
    
    public String getHost() {
        return this._host;
    }
    
    public int getPort() {
        return this._port;
    }
    
    public int getPort(final int defaultPort) {
        return (this._port > 0) ? this._port : defaultPort;
    }
    
    public static String normalizeHost(final String host) {
        if (host.isEmpty() || host.charAt(0) == '[' || host.indexOf(58) < 0) {
            return host;
        }
        return "[" + host + "]";
    }
}
