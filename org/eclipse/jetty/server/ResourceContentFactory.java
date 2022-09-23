// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.http.ResourceHttpContent;
import java.io.IOException;
import org.eclipse.jetty.util.resource.Resource;
import java.nio.file.InvalidPathException;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.http.HttpContent;

public class ResourceContentFactory implements HttpContent.Factory
{
    private final ResourceFactory _factory;
    private final MimeTypes _mimeTypes;
    private final boolean _gzip;
    
    public ResourceContentFactory(final ResourceFactory factory, final MimeTypes mimeTypes, final boolean gzip) {
        this._factory = factory;
        this._mimeTypes = mimeTypes;
        this._gzip = gzip;
    }
    
    @Override
    public HttpContent getContent(final String pathInContext, final int maxBufferSize) throws IOException {
        try {
            final Resource resource = this._factory.getResource(pathInContext);
            final HttpContent loaded = this.load(pathInContext, resource, maxBufferSize);
            return loaded;
        }
        catch (Throwable t) {
            throw (InvalidPathException)new InvalidPathException(pathInContext, "Invalid PathInContext").initCause(t);
        }
    }
    
    private HttpContent load(final String pathInContext, final Resource resource, final int maxBufferSize) throws IOException {
        if (resource == null || !resource.exists()) {
            return null;
        }
        if (resource.isDirectory()) {
            return new ResourceHttpContent(resource, this._mimeTypes.getMimeByExtension(resource.toString()), maxBufferSize);
        }
        final String mt = this._mimeTypes.getMimeByExtension(pathInContext);
        if (this._gzip) {
            final String pathInContextGz = pathInContext + ".gz";
            final Resource resourceGz = this._factory.getResource(pathInContextGz);
            if (resourceGz.exists() && resourceGz.lastModified() >= resource.lastModified() && resourceGz.length() < resource.length()) {
                return new ResourceHttpContent(resource, mt, maxBufferSize, new ResourceHttpContent(resourceGz, this._mimeTypes.getMimeByExtension(pathInContextGz), maxBufferSize));
            }
        }
        return new ResourceHttpContent(resource, mt, maxBufferSize);
    }
    
    @Override
    public String toString() {
        return "ResourceContentFactory[" + this._factory + "]@" + this.hashCode();
    }
}
