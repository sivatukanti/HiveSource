// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import java.net.MalformedURLException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.File;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.io.InputStream;
import java.net.URL;

public class DefaultFileSystem extends FileSystem
{
    @Override
    public InputStream getInputStream(final URL url) throws ConfigurationException {
        final File file = FileLocatorUtils.fileFromURL(url);
        if (file != null && file.isDirectory()) {
            throw new ConfigurationException("Cannot load a configuration from a directory");
        }
        try {
            return url.openStream();
        }
        catch (Exception e) {
            throw new ConfigurationException("Unable to load the configuration from the URL " + url, e);
        }
    }
    
    @Override
    public OutputStream getOutputStream(final URL url) throws ConfigurationException {
        final File file = FileLocatorUtils.fileFromURL(url);
        if (file != null) {
            return this.getOutputStream(file);
        }
        try {
            final URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            if (connection instanceof HttpURLConnection) {
                final HttpURLConnection conn = (HttpURLConnection)connection;
                conn.setRequestMethod("PUT");
            }
            OutputStream out = connection.getOutputStream();
            if (connection instanceof HttpURLConnection) {
                out = new HttpOutputStream(out, (HttpURLConnection)connection);
            }
            return out;
        }
        catch (IOException e) {
            throw new ConfigurationException("Could not save to URL " + url, e);
        }
    }
    
    @Override
    public OutputStream getOutputStream(final File file) throws ConfigurationException {
        try {
            this.createPath(file);
            return new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {
            throw new ConfigurationException("Unable to save to file " + file, e);
        }
    }
    
    @Override
    public String getPath(final File file, final URL url, final String basePath, final String fileName) {
        String path = null;
        if (file != null) {
            path = file.getAbsolutePath();
        }
        if (path == null) {
            if (url != null) {
                path = url.getPath();
            }
            else {
                try {
                    path = this.getURL(basePath, fileName).getPath();
                }
                catch (Exception e) {
                    if (this.getLogger().isDebugEnabled()) {
                        this.getLogger().debug(String.format("Could not determine URL for basePath = %s, fileName = %s: %s", basePath, fileName, e));
                    }
                }
            }
        }
        return path;
    }
    
    @Override
    public String getBasePath(final String path) {
        try {
            final URL url = this.getURL(null, path);
            return FileLocatorUtils.getBasePath(url);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public String getFileName(final String path) {
        try {
            final URL url = this.getURL(null, path);
            return FileLocatorUtils.getFileName(url);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public URL getURL(final String basePath, final String file) throws MalformedURLException {
        final File f = new File(file);
        if (f.isAbsolute()) {
            return FileLocatorUtils.toURL(f);
        }
        try {
            if (basePath == null) {
                return new URL(file);
            }
            final URL base = new URL(basePath);
            return new URL(base, file);
        }
        catch (MalformedURLException uex) {
            return FileLocatorUtils.toURL(FileLocatorUtils.constructFile(basePath, file));
        }
    }
    
    @Override
    public URL locateFromURL(final String basePath, final String fileName) {
        try {
            if (basePath == null) {
                return new URL(fileName);
            }
            final URL baseURL = new URL(basePath);
            final URL url = new URL(baseURL, fileName);
            InputStream in = null;
            try {
                in = url.openStream();
            }
            finally {
                if (in != null) {
                    in.close();
                }
            }
            return url;
        }
        catch (IOException e) {
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("Could not locate file " + fileName + " at " + basePath + ": " + e.getMessage());
            }
            return null;
        }
    }
    
    private void createPath(final File file) throws ConfigurationException {
        if (file != null && !file.exists()) {
            final File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new ConfigurationException("Cannot create path: " + parent);
            }
        }
    }
    
    private static class HttpOutputStream extends VerifiableOutputStream
    {
        private final OutputStream stream;
        private final HttpURLConnection connection;
        
        public HttpOutputStream(final OutputStream stream, final HttpURLConnection connection) {
            this.stream = stream;
            this.connection = connection;
        }
        
        @Override
        public void write(final byte[] bytes) throws IOException {
            this.stream.write(bytes);
        }
        
        @Override
        public void write(final byte[] bytes, final int i, final int i1) throws IOException {
            this.stream.write(bytes, i, i1);
        }
        
        @Override
        public void flush() throws IOException {
            this.stream.flush();
        }
        
        @Override
        public void close() throws IOException {
            this.stream.close();
        }
        
        @Override
        public void write(final int i) throws IOException {
            this.stream.write(i);
        }
        
        @Override
        public String toString() {
            return this.stream.toString();
        }
        
        @Override
        public void verify() throws IOException {
            if (this.connection.getResponseCode() >= 400) {
                throw new IOException("HTTP Error " + this.connection.getResponseCode() + " " + this.connection.getResponseMessage());
            }
        }
    }
}
