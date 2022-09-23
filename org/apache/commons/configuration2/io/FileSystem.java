// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import java.net.MalformedURLException;
import java.io.File;
import java.io.OutputStream;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.io.InputStream;
import java.net.URL;

public abstract class FileSystem
{
    private static final ConfigurationLogger DEFAULT_LOG;
    private volatile ConfigurationLogger log;
    private volatile FileOptionsProvider optionsProvider;
    
    public ConfigurationLogger getLogger() {
        final ConfigurationLogger result = this.log;
        return (result != null) ? result : FileSystem.DEFAULT_LOG;
    }
    
    public void setLogger(final ConfigurationLogger log) {
        this.log = log;
    }
    
    public void setFileOptionsProvider(final FileOptionsProvider provider) {
        this.optionsProvider = provider;
    }
    
    public FileOptionsProvider getFileOptionsProvider() {
        return this.optionsProvider;
    }
    
    public abstract InputStream getInputStream(final URL p0) throws ConfigurationException;
    
    public abstract OutputStream getOutputStream(final URL p0) throws ConfigurationException;
    
    public abstract OutputStream getOutputStream(final File p0) throws ConfigurationException;
    
    public abstract String getPath(final File p0, final URL p1, final String p2, final String p3);
    
    public abstract String getBasePath(final String p0);
    
    public abstract String getFileName(final String p0);
    
    public abstract URL locateFromURL(final String p0, final String p1);
    
    public abstract URL getURL(final String p0, final String p1) throws MalformedURLException;
    
    static {
        DEFAULT_LOG = ConfigurationLogger.newDummyLogger();
    }
}
