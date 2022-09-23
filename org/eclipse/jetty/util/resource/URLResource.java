// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.resource;

import org.eclipse.jetty.util.log.Log;
import java.net.MalformedURLException;
import org.eclipse.jetty.util.URIUtil;
import java.nio.channels.ReadableByteChannel;
import java.security.Permission;
import java.io.FilePermission;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URL;
import org.eclipse.jetty.util.log.Logger;

public class URLResource extends Resource
{
    private static final Logger LOG;
    protected final URL _url;
    protected final String _urlString;
    protected URLConnection _connection;
    protected InputStream _in;
    transient boolean _useCaches;
    
    protected URLResource(final URL url, final URLConnection connection) {
        this._in = null;
        this._useCaches = Resource.__defaultUseCaches;
        this._url = url;
        this._urlString = this._url.toExternalForm();
        this._connection = connection;
    }
    
    protected URLResource(final URL url, final URLConnection connection, final boolean useCaches) {
        this(url, connection);
        this._useCaches = useCaches;
    }
    
    protected synchronized boolean checkConnection() {
        if (this._connection == null) {
            try {
                (this._connection = this._url.openConnection()).setUseCaches(this._useCaches);
            }
            catch (IOException e) {
                URLResource.LOG.ignore(e);
            }
        }
        return this._connection != null;
    }
    
    @Override
    public synchronized void close() {
        if (this._in != null) {
            try {
                this._in.close();
            }
            catch (IOException e) {
                URLResource.LOG.ignore(e);
            }
            this._in = null;
        }
        if (this._connection != null) {
            this._connection = null;
        }
    }
    
    @Override
    public boolean exists() {
        try {
            synchronized (this) {
                if (this.checkConnection() && this._in == null) {
                    this._in = this._connection.getInputStream();
                }
            }
        }
        catch (IOException e) {
            URLResource.LOG.ignore(e);
        }
        return this._in != null;
    }
    
    @Override
    public boolean isDirectory() {
        return this.exists() && this._urlString.endsWith("/");
    }
    
    @Override
    public long lastModified() {
        if (this.checkConnection()) {
            return this._connection.getLastModified();
        }
        return -1L;
    }
    
    @Override
    public long length() {
        if (this.checkConnection()) {
            return this._connection.getContentLength();
        }
        return -1L;
    }
    
    @Override
    public URL getURL() {
        return this._url;
    }
    
    @Override
    public File getFile() throws IOException {
        if (this.checkConnection()) {
            final Permission perm = this._connection.getPermission();
            if (perm instanceof FilePermission) {
                return new File(perm.getName());
            }
        }
        try {
            return new File(this._url.getFile());
        }
        catch (Exception e) {
            URLResource.LOG.ignore(e);
            return null;
        }
    }
    
    @Override
    public String getName() {
        return this._url.toExternalForm();
    }
    
    @Override
    public synchronized InputStream getInputStream() throws IOException {
        return this.getInputStream(true);
    }
    
    protected synchronized InputStream getInputStream(final boolean resetConnection) throws IOException {
        if (!this.checkConnection()) {
            throw new IOException("Invalid resource");
        }
        try {
            if (this._in != null) {
                final InputStream in = this._in;
                this._in = null;
                return in;
            }
            return this._connection.getInputStream();
        }
        finally {
            if (resetConnection) {
                this._connection = null;
                if (URLResource.LOG.isDebugEnabled()) {
                    URLResource.LOG.debug("Connection nulled", new Object[0]);
                }
            }
        }
    }
    
    @Override
    public ReadableByteChannel getReadableByteChannel() throws IOException {
        return null;
    }
    
    @Override
    public boolean delete() throws SecurityException {
        throw new SecurityException("Delete not supported");
    }
    
    @Override
    public boolean renameTo(final Resource dest) throws SecurityException {
        throw new SecurityException("RenameTo not supported");
    }
    
    @Override
    public String[] list() {
        return null;
    }
    
    @Override
    public Resource addPath(String path) throws IOException, MalformedURLException {
        if (path == null) {
            return null;
        }
        path = URIUtil.canonicalPath(path);
        return Resource.newResource(URIUtil.addEncodedPaths(this._url.toExternalForm(), URIUtil.encodePath(path)), this._useCaches);
    }
    
    @Override
    public String toString() {
        return this._urlString;
    }
    
    @Override
    public int hashCode() {
        return this._urlString.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof URLResource && this._urlString.equals(((URLResource)o)._urlString);
    }
    
    public boolean getUseCaches() {
        return this._useCaches;
    }
    
    @Override
    public boolean isContainedIn(final Resource containingResource) throws MalformedURLException {
        return false;
    }
    
    static {
        LOG = Log.getLogger(URLResource.class);
    }
}
