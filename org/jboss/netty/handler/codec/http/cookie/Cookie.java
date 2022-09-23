// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.cookie;

public interface Cookie extends Comparable<Cookie>
{
    String name();
    
    String value();
    
    void setValue(final String p0);
    
    boolean wrap();
    
    void setWrap(final boolean p0);
    
    String domain();
    
    void setDomain(final String p0);
    
    String path();
    
    void setPath(final String p0);
    
    int maxAge();
    
    void setMaxAge(final int p0);
    
    boolean isSecure();
    
    void setSecure(final boolean p0);
    
    boolean isHttpOnly();
    
    void setHttpOnly(final boolean p0);
}
