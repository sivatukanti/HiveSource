// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.resource;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.File;
import java.net.URLConnection;
import java.net.URL;

class BadResource extends URLResource
{
    private String _message;
    
    BadResource(final URL url, final String message) {
        super(url, null);
        this._message = null;
        this._message = message;
    }
    
    public boolean exists() {
        return false;
    }
    
    public long lastModified() {
        return -1L;
    }
    
    public boolean isDirectory() {
        return false;
    }
    
    public long length() {
        return -1L;
    }
    
    public File getFile() {
        return null;
    }
    
    public InputStream getInputStream() throws IOException {
        throw new FileNotFoundException(this._message);
    }
    
    public OutputStream getOutputStream() throws IOException, SecurityException {
        throw new FileNotFoundException(this._message);
    }
    
    public boolean delete() throws SecurityException {
        throw new SecurityException(this._message);
    }
    
    public boolean renameTo(final Resource dest) throws SecurityException {
        throw new SecurityException(this._message);
    }
    
    public String[] list() {
        return null;
    }
    
    public String toString() {
        return super.toString() + "; BadResource=" + this._message;
    }
}
