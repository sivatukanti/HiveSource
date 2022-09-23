// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.resource;

import java.nio.channels.ReadableByteChannel;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;

public class EmptyResource extends Resource
{
    public static final Resource INSTANCE;
    
    private EmptyResource() {
    }
    
    @Override
    public boolean isContainedIn(final Resource r) throws MalformedURLException {
        return false;
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public boolean exists() {
        return false;
    }
    
    @Override
    public boolean isDirectory() {
        return false;
    }
    
    @Override
    public long lastModified() {
        return 0L;
    }
    
    @Override
    public long length() {
        return 0L;
    }
    
    @Override
    public URL getURL() {
        return null;
    }
    
    @Override
    public File getFile() throws IOException {
        return null;
    }
    
    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }
    
    @Override
    public ReadableByteChannel getReadableByteChannel() throws IOException {
        return null;
    }
    
    @Override
    public boolean delete() throws SecurityException {
        return false;
    }
    
    @Override
    public boolean renameTo(final Resource dest) throws SecurityException {
        return false;
    }
    
    @Override
    public String[] list() {
        return null;
    }
    
    @Override
    public Resource addPath(final String path) throws IOException, MalformedURLException {
        return null;
    }
    
    static {
        INSTANCE = new EmptyResource();
    }
}
