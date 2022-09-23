// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import java.io.IOException;
import java.net.URLConnection;
import java.lang.reflect.Method;
import java.util.Iterator;
import org.apache.commons.vfs2.FileSystemConfigBuilder;
import java.util.Map;
import java.net.MalformedURLException;
import java.net.URLStreamHandler;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.provider.UriParser;
import java.io.File;
import org.apache.commons.vfs2.FileSystemManager;
import java.io.OutputStream;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class VFSFileSystem extends DefaultFileSystem
{
    private final Log log;
    
    public VFSFileSystem() {
        this.log = LogFactory.getLog(this.getClass());
    }
    
    @Override
    public InputStream getInputStream(final URL url) throws ConfigurationException {
        try {
            final FileSystemOptions opts = this.getOptions(url.getProtocol());
            final FileObject file = (opts == null) ? VFS.getManager().resolveFile(url.toString()) : VFS.getManager().resolveFile(url.toString(), opts);
            if (file.getType() != FileType.FILE) {
                throw new ConfigurationException("Cannot load a configuration from a directory");
            }
            final FileContent content = file.getContent();
            if (content == null) {
                final String msg = "Cannot access content of " + file.getName().getFriendlyURI();
                throw new ConfigurationException(msg);
            }
            return content.getInputStream();
        }
        catch (FileSystemException fse) {
            final String msg2 = "Unable to access " + url.toString();
            throw new ConfigurationException(msg2, (Throwable)fse);
        }
    }
    
    @Override
    public OutputStream getOutputStream(final URL url) throws ConfigurationException {
        try {
            final FileSystemOptions opts = this.getOptions(url.getProtocol());
            final FileSystemManager fsManager = VFS.getManager();
            final FileObject file = (opts == null) ? fsManager.resolveFile(url.toString()) : fsManager.resolveFile(url.toString(), opts);
            if (file == null || file.getType() == FileType.FOLDER) {
                throw new ConfigurationException("Cannot save a configuration to a directory");
            }
            final FileContent content = file.getContent();
            if (content == null) {
                throw new ConfigurationException("Cannot access content of " + url);
            }
            return content.getOutputStream();
        }
        catch (FileSystemException fse) {
            throw new ConfigurationException("Unable to access " + url, (Throwable)fse);
        }
    }
    
    @Override
    public String getPath(final File file, final URL url, final String basePath, final String fileName) {
        if (file != null) {
            return super.getPath(file, url, basePath, fileName);
        }
        try {
            final FileSystemManager fsManager = VFS.getManager();
            if (url != null) {
                final FileName name = fsManager.resolveURI(url.toString());
                if (name != null) {
                    return name.toString();
                }
            }
            if (UriParser.extractScheme(fileName) != null) {
                return fileName;
            }
            if (basePath != null) {
                final FileName base = fsManager.resolveURI(basePath);
                return fsManager.resolveName(base, fileName).getURI();
            }
            final FileName name = fsManager.resolveURI(fileName);
            final FileName base2 = name.getParent();
            return fsManager.resolveName(base2, name.getBaseName()).getURI();
        }
        catch (FileSystemException fse) {
            fse.printStackTrace();
            return null;
        }
    }
    
    @Override
    public String getBasePath(final String path) {
        if (UriParser.extractScheme(path) == null) {
            return super.getBasePath(path);
        }
        try {
            final FileSystemManager fsManager = VFS.getManager();
            final FileName name = fsManager.resolveURI(path);
            return name.getParent().getURI();
        }
        catch (FileSystemException fse) {
            fse.printStackTrace();
            return null;
        }
    }
    
    @Override
    public String getFileName(final String path) {
        if (UriParser.extractScheme(path) == null) {
            return super.getFileName(path);
        }
        try {
            final FileSystemManager fsManager = VFS.getManager();
            final FileName name = fsManager.resolveURI(path);
            return name.getBaseName();
        }
        catch (FileSystemException fse) {
            fse.printStackTrace();
            return null;
        }
    }
    
    @Override
    public URL getURL(final String basePath, final String file) throws MalformedURLException {
        if ((basePath != null && UriParser.extractScheme(basePath) == null) || (basePath == null && UriParser.extractScheme(file) == null)) {
            return super.getURL(basePath, file);
        }
        try {
            final FileSystemManager fsManager = VFS.getManager();
            FileName path;
            if (basePath != null && UriParser.extractScheme(file) == null) {
                final FileName base = fsManager.resolveURI(basePath);
                path = fsManager.resolveName(base, file);
            }
            else {
                path = fsManager.resolveURI(file);
            }
            final URLStreamHandler handler = new VFSURLStreamHandler(path);
            return new URL(null, path.getURI(), handler);
        }
        catch (FileSystemException fse) {
            throw new ConfigurationRuntimeException("Could not parse basePath: " + basePath + " and fileName: " + file, (Throwable)fse);
        }
    }
    
    @Override
    public URL locateFromURL(final String basePath, final String fileName) {
        final String fileScheme = UriParser.extractScheme(fileName);
        if ((basePath == null || UriParser.extractScheme(basePath) == null) && fileScheme == null) {
            return super.locateFromURL(basePath, fileName);
        }
        try {
            final FileSystemManager fsManager = VFS.getManager();
            FileObject file;
            if (basePath != null && fileScheme == null) {
                final String scheme = UriParser.extractScheme(basePath);
                final FileSystemOptions opts = (scheme != null) ? this.getOptions(scheme) : null;
                FileObject base = (opts == null) ? fsManager.resolveFile(basePath) : fsManager.resolveFile(basePath, opts);
                if (base.getType() == FileType.FILE) {
                    base = base.getParent();
                }
                file = fsManager.resolveFile(base, fileName);
            }
            else {
                final FileSystemOptions opts2 = (fileScheme != null) ? this.getOptions(fileScheme) : null;
                file = ((opts2 == null) ? fsManager.resolveFile(fileName) : fsManager.resolveFile(fileName, opts2));
            }
            if (!file.exists()) {
                return null;
            }
            final FileName path = file.getName();
            final URLStreamHandler handler = new VFSURLStreamHandler(path);
            return new URL(null, path.getURI(), handler);
        }
        catch (FileSystemException fse) {
            return null;
        }
        catch (MalformedURLException ex) {
            return null;
        }
    }
    
    private FileSystemOptions getOptions(final String scheme) {
        final FileSystemOptions opts = new FileSystemOptions();
        FileSystemConfigBuilder builder;
        try {
            builder = VFS.getManager().getFileSystemConfigBuilder(scheme);
        }
        catch (Exception ex) {
            return null;
        }
        final FileOptionsProvider provider = this.getFileOptionsProvider();
        if (provider != null) {
            final Map<String, Object> map = provider.getOptions();
            if (map == null) {
                return null;
            }
            int count = 0;
            for (final Map.Entry<String, Object> entry : map.entrySet()) {
                try {
                    String key = entry.getKey();
                    if ("currentUser".equals(key)) {
                        key = "creatorName";
                    }
                    this.setProperty(builder, opts, key, entry.getValue());
                    ++count;
                }
                catch (Exception ex2) {}
            }
            if (count > 0) {
                return opts;
            }
        }
        return null;
    }
    
    private void setProperty(final FileSystemConfigBuilder builder, final FileSystemOptions options, final String key, final Object value) {
        final String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
        final Class<?>[] paramTypes = (Class<?>[])new Class[] { FileSystemOptions.class, value.getClass() };
        try {
            final Method method = builder.getClass().getMethod(methodName, paramTypes);
            final Object[] params = { options, value };
            method.invoke(builder, params);
        }
        catch (Exception ex) {
            this.log.warn("Cannot access property '" + key + "'! Ignoring.", ex);
        }
    }
    
    private static class VFSURLStreamHandler extends URLStreamHandler
    {
        private final String protocol;
        
        public VFSURLStreamHandler(final FileName file) {
            this.protocol = file.getScheme();
        }
        
        @Override
        protected URLConnection openConnection(final URL url) throws IOException {
            throw new IOException("VFS URLs can only be used with VFS APIs");
        }
    }
}
