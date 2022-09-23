// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Collections;
import java.util.List;
import java.net.HttpCookie;
import java.net.URI;
import java.net.CookieManager;
import java.net.CookieStore;

public class HttpCookieStore implements CookieStore
{
    private final CookieStore delegate;
    
    public HttpCookieStore() {
        this.delegate = new CookieManager().getCookieStore();
    }
    
    @Override
    public void add(final URI uri, final HttpCookie cookie) {
        this.delegate.add(uri, cookie);
    }
    
    @Override
    public List<HttpCookie> get(final URI uri) {
        return this.delegate.get(uri);
    }
    
    @Override
    public List<HttpCookie> getCookies() {
        return this.delegate.getCookies();
    }
    
    @Override
    public List<URI> getURIs() {
        return this.delegate.getURIs();
    }
    
    @Override
    public boolean remove(final URI uri, final HttpCookie cookie) {
        return this.delegate.remove(uri, cookie);
    }
    
    @Override
    public boolean removeAll() {
        return this.delegate.removeAll();
    }
    
    public static class Empty implements CookieStore
    {
        @Override
        public void add(final URI uri, final HttpCookie cookie) {
        }
        
        @Override
        public List<HttpCookie> get(final URI uri) {
            return Collections.emptyList();
        }
        
        @Override
        public List<HttpCookie> getCookies() {
            return Collections.emptyList();
        }
        
        @Override
        public List<URI> getURIs() {
            return Collections.emptyList();
        }
        
        @Override
        public boolean remove(final URI uri, final HttpCookie cookie) {
            return false;
        }
        
        @Override
        public boolean removeAll() {
            return false;
        }
    }
}
