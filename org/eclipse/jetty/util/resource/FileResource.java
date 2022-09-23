// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.resource;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.IO;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.nio.channels.ReadableByteChannel;
import java.io.FileInputStream;
import java.io.InputStream;
import org.eclipse.jetty.util.StringUtil;
import java.net.MalformedURLException;
import java.nio.file.InvalidPathException;
import java.io.IOException;
import java.security.Permission;
import java.net.URLConnection;
import org.eclipse.jetty.util.URIUtil;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import java.io.File;
import org.eclipse.jetty.util.log.Logger;

@Deprecated
public class FileResource extends Resource
{
    private static final Logger LOG;
    private final File _file;
    private final URI _uri;
    private final URI _alias;
    
    public FileResource(final URL url) throws IOException, URISyntaxException {
        File file;
        try {
            file = new File(url.toURI());
            this.assertValidPath(file.toString());
        }
        catch (URISyntaxException e) {
            throw e;
        }
        catch (Exception e2) {
            if (!url.toString().startsWith("file:")) {
                throw new IllegalArgumentException("!file:");
            }
            FileResource.LOG.ignore(e2);
            try {
                final String file_url = "file:" + URIUtil.encodePath(url.toString().substring(5));
                final URI uri = new URI(file_url);
                if (uri.getAuthority() == null) {
                    file = new File(uri);
                }
                else {
                    file = new File("//" + uri.getAuthority() + URIUtil.decodePath(url.getFile()));
                }
            }
            catch (Exception e3) {
                FileResource.LOG.ignore(e3);
                final URLConnection connection = url.openConnection();
                final Permission perm = connection.getPermission();
                file = new File((perm == null) ? url.getFile() : perm.getName());
            }
        }
        this._file = file;
        this._uri = normalizeURI(this._file, url.toURI());
        this._alias = checkFileAlias(this._uri, this._file);
    }
    
    public FileResource(final URI uri) {
        final File file = new File(uri);
        this._file = file;
        try {
            final URI file_uri = this._file.toURI();
            this._uri = normalizeURI(this._file, uri);
            this.assertValidPath(file.toString());
            if (!URIUtil.equalsIgnoreEncodings(this._uri.toASCIIString(), file_uri.toString())) {
                this._alias = this._file.toURI();
            }
            else {
                this._alias = checkFileAlias(this._uri, this._file);
            }
        }
        catch (URISyntaxException e) {
            throw new InvalidPathException(this._file.toString(), e.getMessage()) {
                {
                    this.initCause(e);
                }
            };
        }
    }
    
    public FileResource(final File file) {
        this.assertValidPath(file.toString());
        this._file = file;
        try {
            this._uri = normalizeURI(this._file, this._file.toURI());
        }
        catch (URISyntaxException e) {
            throw new InvalidPathException(this._file.toString(), e.getMessage()) {
                {
                    this.initCause(e);
                }
            };
        }
        this._alias = checkFileAlias(this._uri, this._file);
    }
    
    public FileResource(final File base, final String childPath) {
        final String encoded = URIUtil.encodePath(childPath);
        this._file = new File(base, childPath);
        URI uri;
        try {
            if (base.isDirectory()) {
                uri = new URI(URIUtil.addEncodedPaths(base.toURI().toASCIIString(), encoded));
            }
            else {
                uri = new URI(base.toURI().toASCIIString() + encoded);
            }
        }
        catch (URISyntaxException e) {
            throw new InvalidPathException(base.toString() + childPath, e.getMessage()) {
                {
                    this.initCause(e);
                }
            };
        }
        this._uri = uri;
        this._alias = checkFileAlias(this._uri, this._file);
    }
    
    private static URI normalizeURI(final File file, final URI uri) throws URISyntaxException {
        String u = uri.toASCIIString();
        if (file.isDirectory()) {
            if (!u.endsWith("/")) {
                u += "/";
            }
        }
        else if (file.exists() && u.endsWith("/")) {
            u = u.substring(0, u.length() - 1);
        }
        return new URI(u);
    }
    
    private static URI checkFileAlias(final URI uri, final File file) {
        try {
            if (!URIUtil.equalsIgnoreEncodings(uri, file.toURI())) {
                return new File(uri).getAbsoluteFile().toURI();
            }
            final String abs = file.getAbsolutePath();
            final String can = file.getCanonicalPath();
            if (!abs.equals(can)) {
                if (FileResource.LOG.isDebugEnabled()) {
                    FileResource.LOG.debug("ALIAS abs={} can={}", abs, can);
                }
                final URI alias = new File(can).toURI();
                return new URI("file://" + URIUtil.encodePath(alias.getPath()));
            }
        }
        catch (Exception e) {
            FileResource.LOG.warn("bad alias for {}: {}", file, e.toString());
            FileResource.LOG.debug(e);
            try {
                return new URI("http://eclipse.org/bad/canonical/alias");
            }
            catch (Exception e2) {
                FileResource.LOG.ignore(e2);
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    
    @Override
    public Resource addPath(String path) throws IOException, MalformedURLException {
        this.assertValidPath(path);
        path = URIUtil.canonicalPath(path);
        if (path == null) {
            throw new MalformedURLException();
        }
        if ("/".equals(path)) {
            return this;
        }
        return new FileResource(this._file, path);
    }
    
    private void assertValidPath(final String path) {
        final int idx = StringUtil.indexOfControlChars(path);
        if (idx >= 0) {
            throw new InvalidPathException(path, "Invalid Character at index " + idx);
        }
    }
    
    @Override
    public URI getAlias() {
        return this._alias;
    }
    
    @Override
    public boolean exists() {
        return this._file.exists();
    }
    
    @Override
    public long lastModified() {
        return this._file.lastModified();
    }
    
    @Override
    public boolean isDirectory() {
        return (this._file.exists() && this._file.isDirectory()) || this._uri.toASCIIString().endsWith("/");
    }
    
    @Override
    public long length() {
        return this._file.length();
    }
    
    @Override
    public String getName() {
        return this._file.getAbsolutePath();
    }
    
    @Override
    public File getFile() {
        return this._file;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this._file);
    }
    
    @Override
    public ReadableByteChannel getReadableByteChannel() throws IOException {
        return FileChannel.open(this._file.toPath(), StandardOpenOption.READ);
    }
    
    @Override
    public boolean delete() throws SecurityException {
        return this._file.delete();
    }
    
    @Override
    public boolean renameTo(final Resource dest) throws SecurityException {
        return dest instanceof FileResource && this._file.renameTo(((FileResource)dest)._file);
    }
    
    @Override
    public String[] list() {
        final String[] list = this._file.list();
        if (list == null) {
            return null;
        }
        int i = list.length;
        while (i-- > 0) {
            if (new File(this._file, list[i]).isDirectory() && !list[i].endsWith("/")) {
                final StringBuilder sb = new StringBuilder();
                final String[] array = list;
                final int n = i;
                array[n] = sb.append(array[n]).append("/").toString();
            }
        }
        return list;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (null == o || !(o instanceof FileResource)) {
            return false;
        }
        final FileResource f = (FileResource)o;
        return f._file == this._file || (null != this._file && this._file.equals(f._file));
    }
    
    @Override
    public int hashCode() {
        return (null == this._file) ? super.hashCode() : this._file.hashCode();
    }
    
    @Override
    public void copyTo(final File destination) throws IOException {
        if (this.isDirectory()) {
            IO.copyDir(this.getFile(), destination);
        }
        else {
            if (destination.exists()) {
                throw new IllegalArgumentException(destination + " exists");
            }
            IO.copy(this.getFile(), destination);
        }
    }
    
    @Override
    public boolean isContainedIn(final Resource r) throws MalformedURLException {
        return false;
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public URL getURL() {
        try {
            return this._uri.toURL();
        }
        catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public URI getURI() {
        return this._uri;
    }
    
    @Override
    public String toString() {
        return this._uri.toString();
    }
    
    static {
        LOG = Log.getLogger(FileResource.class);
    }
}
