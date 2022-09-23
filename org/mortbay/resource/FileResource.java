// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.resource;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.io.IOException;
import java.security.Permission;
import org.mortbay.util.URIUtil;
import org.mortbay.log.Log;
import java.net.URI;
import java.net.URLConnection;
import java.net.URL;
import java.io.File;

public class FileResource extends URLResource
{
    private static boolean __checkAliases;
    private File _file;
    private transient URL _alias;
    private transient boolean _aliasChecked;
    
    public static void setCheckAliases(final boolean checkAliases) {
        FileResource.__checkAliases = checkAliases;
    }
    
    public static boolean getCheckAliases() {
        return FileResource.__checkAliases;
    }
    
    public FileResource(final URL url) throws IOException, URISyntaxException {
        super(url, null);
        this._alias = null;
        this._aliasChecked = false;
        try {
            this._file = new File(new URI(url.toString()));
        }
        catch (Exception e) {
            Log.ignore(e);
            try {
                final String file_url = "file:" + URIUtil.encodePath(url.toString().substring(5));
                final URI uri = new URI(file_url);
                if (uri.getAuthority() == null) {
                    this._file = new File(uri);
                }
                else {
                    this._file = new File("//" + uri.getAuthority() + URIUtil.decodePath(url.getFile()));
                }
            }
            catch (Exception e2) {
                Log.ignore(e2);
                this.checkConnection();
                final Permission perm = this._connection.getPermission();
                this._file = new File((perm == null) ? url.getFile() : perm.getName());
            }
        }
        if (this._file.isDirectory()) {
            if (!this._urlString.endsWith("/")) {
                this._urlString += "/";
            }
        }
        else if (this._urlString.endsWith("/")) {
            this._urlString = this._urlString.substring(0, this._urlString.length() - 1);
        }
    }
    
    FileResource(final URL url, final URLConnection connection, final File file) {
        super(url, connection);
        this._alias = null;
        this._aliasChecked = false;
        this._file = file;
        if (this._file.isDirectory() && !this._urlString.endsWith("/")) {
            this._urlString += "/";
        }
    }
    
    public Resource addPath(String path) throws IOException, MalformedURLException {
        URLResource r = null;
        String url = null;
        path = URIUtil.canonicalPath(path);
        if (!this.isDirectory()) {
            r = (FileResource)super.addPath(path);
            url = r._urlString;
        }
        else {
            if (path == null) {
                throw new MalformedURLException();
            }
            String rel = path;
            if (path.startsWith("/")) {
                rel = path.substring(1);
            }
            url = URIUtil.addPaths(this._urlString, URIUtil.encodePath(rel));
            r = (URLResource)Resource.newResource(url);
        }
        final String encoded = URIUtil.encodePath(path);
        final int expected = r.toString().length() - encoded.length();
        final int index = r._urlString.lastIndexOf(encoded, expected);
        if (expected != index && (expected - 1 != index || path.endsWith("/") || !r.isDirectory()) && !(r instanceof BadResource)) {
            ((FileResource)r)._alias = new URL(url);
            ((FileResource)r)._aliasChecked = true;
        }
        return r;
    }
    
    public URL getAlias() {
        if (FileResource.__checkAliases && !this._aliasChecked) {
            try {
                final String abs = this._file.getAbsolutePath();
                final String can = this._file.getCanonicalPath();
                if (abs.length() != can.length() || !abs.equals(can)) {
                    this._alias = new File(can).toURI().toURL();
                }
                this._aliasChecked = true;
                if (this._alias != null && Log.isDebugEnabled()) {
                    Log.debug("ALIAS abs=" + abs);
                    Log.debug("ALIAS can=" + can);
                }
            }
            catch (Exception e) {
                Log.warn("EXCEPTION ", e);
                return this.getURL();
            }
        }
        return this._alias;
    }
    
    public boolean exists() {
        return this._file.exists();
    }
    
    public long lastModified() {
        return this._file.lastModified();
    }
    
    public boolean isDirectory() {
        return this._file.isDirectory();
    }
    
    public long length() {
        return this._file.length();
    }
    
    public String getName() {
        return this._file.getAbsolutePath();
    }
    
    public File getFile() {
        return this._file;
    }
    
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this._file);
    }
    
    public OutputStream getOutputStream() throws IOException, SecurityException {
        return new FileOutputStream(this._file);
    }
    
    public boolean delete() throws SecurityException {
        return this._file.delete();
    }
    
    public boolean renameTo(final Resource dest) throws SecurityException {
        return dest instanceof FileResource && this._file.renameTo(((FileResource)dest)._file);
    }
    
    public String[] list() {
        final String[] list = this._file.list();
        if (list == null) {
            return null;
        }
        int i = list.length;
        while (i-- > 0) {
            if (new File(this._file, list[i]).isDirectory() && !list[i].endsWith("/")) {
                final StringBuffer sb = new StringBuffer();
                final String[] array = list;
                final int n = i;
                array[n] = sb.append(array[n]).append("/").toString();
            }
        }
        return list;
    }
    
    public String encode(final String uri) {
        return uri;
    }
    
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
    
    public int hashCode() {
        return (null == this._file) ? super.hashCode() : this._file.hashCode();
    }
    
    static {
        FileResource.__checkAliases = "true".equalsIgnoreCase(System.getProperty("org.mortbay.util.FileResource.checkAliases", "true"));
        if (FileResource.__checkAliases) {
            Log.debug("Checking Resource aliases");
        }
        else {
            Log.warn("Resource alias checking is disabled");
        }
    }
}
