// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.resource;

import java.net.MalformedURLException;
import org.mortbay.util.URIUtil;
import java.io.OutputStream;
import java.security.Permission;
import java.io.FilePermission;
import java.io.File;
import java.io.IOException;
import org.mortbay.log.Log;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URL;

public class URLResource extends Resource
{
    protected URL _url;
    protected String _urlString;
    protected transient URLConnection _connection;
    protected transient InputStream _in;
    transient boolean _useCaches;
    
    protected URLResource(final URL url, final URLConnection connection) {
        this._in = null;
        this._useCaches = Resource.__defaultUseCaches;
        this._url = url;
        this._urlString = this._url.toString();
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
                Log.ignore(e);
            }
        }
        return this._connection != null;
    }
    
    public synchronized void release() {
        if (this._in != null) {
            try {
                this._in.close();
            }
            catch (IOException e) {
                Log.ignore(e);
            }
            this._in = null;
        }
        if (this._connection != null) {
            this._connection = null;
        }
    }
    
    public boolean exists() {
        try {
            synchronized (this) {
                if (this.checkConnection() && this._in == null) {
                    this._in = this._connection.getInputStream();
                }
            }
        }
        catch (IOException e) {
            Log.ignore(e);
        }
        return this._in != null;
    }
    
    public boolean isDirectory() {
        return this.exists() && this._url.toString().endsWith("/");
    }
    
    public long lastModified() {
        if (this.checkConnection()) {
            return this._connection.getLastModified();
        }
        return -1L;
    }
    
    public long length() {
        if (this.checkConnection()) {
            return this._connection.getContentLength();
        }
        return -1L;
    }
    
    public URL getURL() {
        return this._url;
    }
    
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
            Log.ignore(e);
            return null;
        }
    }
    
    public String getName() {
        return this._url.toExternalForm();
    }
    
    public synchronized InputStream getInputStream() throws IOException {
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
            this._connection = null;
        }
    }
    
    public OutputStream getOutputStream() throws IOException, SecurityException {
        throw new IOException("Output not supported");
    }
    
    public boolean delete() throws SecurityException {
        throw new SecurityException("Delete not supported");
    }
    
    public boolean renameTo(final Resource dest) throws SecurityException {
        throw new SecurityException("RenameTo not supported");
    }
    
    public String[] list() {
        return null;
    }
    
    public Resource addPath(String path) throws IOException, MalformedURLException {
        if (path == null) {
            return null;
        }
        path = URIUtil.canonicalPath(path);
        return Resource.newResource(URIUtil.addPaths(this._url.toExternalForm(), path));
    }
    
    public String toString() {
        return this._urlString;
    }
    
    public int hashCode() {
        return this._url.hashCode();
    }
    
    public boolean equals(final Object o) {
        return o instanceof URLResource && this._url.equals(((URLResource)o)._url);
    }
    
    public boolean getUseCaches() {
        return this._useCaches;
    }
}
