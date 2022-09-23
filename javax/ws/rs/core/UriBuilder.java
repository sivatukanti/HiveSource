// 
// Decompiled by Procyon v0.5.36
// 

package javax.ws.rs.core;

import java.util.Map;
import java.lang.reflect.Method;
import java.net.URI;
import javax.ws.rs.ext.RuntimeDelegate;

public abstract class UriBuilder
{
    protected UriBuilder() {
    }
    
    protected static UriBuilder newInstance() {
        final UriBuilder b = RuntimeDelegate.getInstance().createUriBuilder();
        return b;
    }
    
    public static UriBuilder fromUri(final URI uri) throws IllegalArgumentException {
        final UriBuilder b = newInstance();
        b.uri(uri);
        return b;
    }
    
    public static UriBuilder fromUri(final String uri) throws IllegalArgumentException {
        URI u;
        try {
            u = URI.create(uri);
        }
        catch (NullPointerException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
        return fromUri(u);
    }
    
    public static UriBuilder fromPath(final String path) throws IllegalArgumentException {
        final UriBuilder b = newInstance();
        b.path(path);
        return b;
    }
    
    public static UriBuilder fromResource(final Class<?> resource) throws IllegalArgumentException {
        final UriBuilder b = newInstance();
        b.path(resource);
        return b;
    }
    
    public abstract UriBuilder clone();
    
    public abstract UriBuilder uri(final URI p0) throws IllegalArgumentException;
    
    public abstract UriBuilder scheme(final String p0) throws IllegalArgumentException;
    
    public abstract UriBuilder schemeSpecificPart(final String p0) throws IllegalArgumentException;
    
    public abstract UriBuilder userInfo(final String p0);
    
    public abstract UriBuilder host(final String p0) throws IllegalArgumentException;
    
    public abstract UriBuilder port(final int p0) throws IllegalArgumentException;
    
    public abstract UriBuilder replacePath(final String p0);
    
    public abstract UriBuilder path(final String p0) throws IllegalArgumentException;
    
    public abstract UriBuilder path(final Class p0) throws IllegalArgumentException;
    
    public abstract UriBuilder path(final Class p0, final String p1) throws IllegalArgumentException;
    
    public abstract UriBuilder path(final Method p0) throws IllegalArgumentException;
    
    public abstract UriBuilder segment(final String... p0) throws IllegalArgumentException;
    
    public abstract UriBuilder replaceMatrix(final String p0) throws IllegalArgumentException;
    
    public abstract UriBuilder matrixParam(final String p0, final Object... p1) throws IllegalArgumentException;
    
    public abstract UriBuilder replaceMatrixParam(final String p0, final Object... p1) throws IllegalArgumentException;
    
    public abstract UriBuilder replaceQuery(final String p0) throws IllegalArgumentException;
    
    public abstract UriBuilder queryParam(final String p0, final Object... p1) throws IllegalArgumentException;
    
    public abstract UriBuilder replaceQueryParam(final String p0, final Object... p1) throws IllegalArgumentException;
    
    public abstract UriBuilder fragment(final String p0);
    
    public abstract URI buildFromMap(final Map<String, ?> p0) throws IllegalArgumentException, UriBuilderException;
    
    public abstract URI buildFromEncodedMap(final Map<String, ?> p0) throws IllegalArgumentException, UriBuilderException;
    
    public abstract URI build(final Object... p0) throws IllegalArgumentException, UriBuilderException;
    
    public abstract URI buildFromEncoded(final Object... p0) throws IllegalArgumentException, UriBuilderException;
}
