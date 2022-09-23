// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

import javax.ws.rs.core.Cookie;
import java.util.Locale;
import javax.ws.rs.core.MediaType;

public interface RequestBuilder<T extends RequestBuilder>
{
    T entity(final Object p0);
    
    T entity(final Object p0, final MediaType p1);
    
    T entity(final Object p0, final String p1);
    
    T type(final MediaType p0);
    
    T type(final String p0);
    
    T accept(final MediaType... p0);
    
    T accept(final String... p0);
    
    T acceptLanguage(final Locale... p0);
    
    T acceptLanguage(final String... p0);
    
    T cookie(final Cookie p0);
    
    T header(final String p0, final Object p1);
}
