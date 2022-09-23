// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.resource;

import org.eclipse.jetty.util.log.Log;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jetty.util.B64Code;
import java.io.FileOutputStream;
import org.eclipse.jetty.util.IO;
import java.io.OutputStream;
import org.eclipse.jetty.util.StringUtil;
import java.util.Date;
import java.text.DateFormat;
import java.util.Arrays;
import org.eclipse.jetty.util.URIUtil;
import java.nio.channels.ReadableByteChannel;
import java.io.InputStream;
import org.eclipse.jetty.util.Loader;
import java.io.IOException;
import java.io.File;
import java.net.URLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URI;
import org.eclipse.jetty.util.log.Logger;
import java.io.Closeable;

public abstract class Resource implements ResourceFactory, Closeable
{
    private static final Logger LOG;
    public static boolean __defaultUseCaches;
    volatile Object _associate;
    
    public static void setDefaultUseCaches(final boolean useCaches) {
        Resource.__defaultUseCaches = useCaches;
    }
    
    public static boolean getDefaultUseCaches() {
        return Resource.__defaultUseCaches;
    }
    
    public static Resource newResource(final URI uri) throws MalformedURLException {
        return newResource(uri.toURL());
    }
    
    public static Resource newResource(final URL url) {
        return newResource(url, Resource.__defaultUseCaches);
    }
    
    static Resource newResource(final URL url, final boolean useCaches) {
        if (url == null) {
            return null;
        }
        final String url_string = url.toExternalForm();
        if (url_string.startsWith("file:")) {
            try {
                return new PathResource(url);
            }
            catch (Exception e) {
                Resource.LOG.warn(e.toString(), new Object[0]);
                Resource.LOG.debug("EXCEPTION ", e);
                return new BadResource(url, e.toString());
            }
        }
        if (url_string.startsWith("jar:file:")) {
            return new JarFileResource(url, useCaches);
        }
        if (url_string.startsWith("jar:")) {
            return new JarResource(url, useCaches);
        }
        return new URLResource(url, null, useCaches);
    }
    
    public static Resource newResource(final String resource) throws MalformedURLException {
        return newResource(resource, Resource.__defaultUseCaches);
    }
    
    public static Resource newResource(String resource, final boolean useCaches) throws MalformedURLException {
        URL url = null;
        try {
            url = new URL(resource);
        }
        catch (MalformedURLException e) {
            if (!resource.startsWith("ftp:") && !resource.startsWith("file:") && !resource.startsWith("jar:")) {
                try {
                    if (resource.startsWith("./")) {
                        resource = resource.substring(2);
                    }
                    final File file = new File(resource).getCanonicalFile();
                    return new PathResource(file);
                }
                catch (IOException e2) {
                    e.addSuppressed(e2);
                    throw e;
                }
            }
            Resource.LOG.warn("Bad Resource: " + resource, new Object[0]);
            throw e;
        }
        return newResource(url, useCaches);
    }
    
    public static Resource newResource(final File file) {
        return new PathResource(file.toPath());
    }
    
    public static Resource newSystemResource(final String resource) throws IOException {
        URL url = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            try {
                url = loader.getResource(resource);
                if (url == null && resource.startsWith("/")) {
                    url = loader.getResource(resource.substring(1));
                }
            }
            catch (IllegalArgumentException e) {
                Resource.LOG.ignore(e);
                url = null;
            }
        }
        if (url == null) {
            loader = Resource.class.getClassLoader();
            if (loader != null) {
                url = loader.getResource(resource);
                if (url == null && resource.startsWith("/")) {
                    url = loader.getResource(resource.substring(1));
                }
            }
        }
        if (url == null) {
            url = ClassLoader.getSystemResource(resource);
            if (url == null && resource.startsWith("/")) {
                url = ClassLoader.getSystemResource(resource.substring(1));
            }
        }
        if (url == null) {
            return null;
        }
        return newResource(url);
    }
    
    public static Resource newClassPathResource(final String resource) {
        return newClassPathResource(resource, true, false);
    }
    
    public static Resource newClassPathResource(final String name, final boolean useCaches, final boolean checkParents) {
        URL url = Resource.class.getResource(name);
        if (url == null) {
            url = Loader.getResource(Resource.class, name);
        }
        if (url == null) {
            return null;
        }
        return newResource(url, useCaches);
    }
    
    public static boolean isContainedIn(final Resource r, final Resource containingResource) throws MalformedURLException {
        return r.isContainedIn(containingResource);
    }
    
    @Override
    protected void finalize() {
        this.close();
    }
    
    public abstract boolean isContainedIn(final Resource p0) throws MalformedURLException;
    
    @Deprecated
    public final void release() {
        this.close();
    }
    
    @Override
    public abstract void close();
    
    public abstract boolean exists();
    
    public abstract boolean isDirectory();
    
    public abstract long lastModified();
    
    public abstract long length();
    
    @Deprecated
    public abstract URL getURL();
    
    public URI getURI() {
        try {
            return this.getURL().toURI();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public abstract File getFile() throws IOException;
    
    public abstract String getName();
    
    public abstract InputStream getInputStream() throws IOException;
    
    public abstract ReadableByteChannel getReadableByteChannel() throws IOException;
    
    public abstract boolean delete() throws SecurityException;
    
    public abstract boolean renameTo(final Resource p0) throws SecurityException;
    
    public abstract String[] list();
    
    public abstract Resource addPath(final String p0) throws IOException, MalformedURLException;
    
    @Override
    public Resource getResource(final String path) {
        try {
            return this.addPath(path);
        }
        catch (Exception e) {
            Resource.LOG.debug(e);
            return null;
        }
    }
    
    @Deprecated
    public String encode(final String uri) {
        return null;
    }
    
    public Object getAssociate() {
        return this._associate;
    }
    
    public void setAssociate(final Object o) {
        this._associate = o;
    }
    
    public boolean isAlias() {
        return this.getAlias() != null;
    }
    
    public URI getAlias() {
        return null;
    }
    
    public String getListHTML(String base, final boolean parent) throws IOException {
        base = URIUtil.canonicalPath(base);
        if (base == null || !this.isDirectory()) {
            return null;
        }
        final String[] ls = this.list();
        if (ls == null) {
            return null;
        }
        Arrays.sort(ls);
        final String decodedBase = URIUtil.decodePath(base);
        final String title = "Directory: " + deTag(decodedBase);
        final StringBuilder buf = new StringBuilder(4096);
        buf.append("<HTML><HEAD>");
        buf.append("<LINK HREF=\"").append("jetty-dir.css").append("\" REL=\"stylesheet\" TYPE=\"text/css\"/><TITLE>");
        buf.append(title);
        buf.append("</TITLE></HEAD><BODY>\n<H1>");
        buf.append(title);
        buf.append("</H1>\n<TABLE BORDER=0>\n");
        if (parent) {
            buf.append("<TR><TD><A HREF=\"");
            buf.append(URIUtil.addEncodedPaths(base, "../"));
            buf.append("\">Parent Directory</A></TD><TD></TD><TD></TD></TR>\n");
        }
        final String encodedBase = hrefEncodeURI(base);
        final DateFormat dfmt = DateFormat.getDateTimeInstance(2, 2);
        for (int i = 0; i < ls.length; ++i) {
            final Resource item = this.addPath(ls[i]);
            buf.append("\n<TR><TD><A HREF=\"");
            final String path = URIUtil.addEncodedPaths(encodedBase, URIUtil.encodePath(ls[i]));
            buf.append(path);
            if (item.isDirectory() && !path.endsWith("/")) {
                buf.append("/");
            }
            buf.append("\">");
            buf.append(deTag(ls[i]));
            buf.append("&nbsp;");
            buf.append("</A></TD><TD ALIGN=right>");
            buf.append(item.length());
            buf.append(" bytes&nbsp;</TD><TD>");
            buf.append(dfmt.format(new Date(item.lastModified())));
            buf.append("</TD></TR>");
        }
        buf.append("</TABLE>\n");
        buf.append("</BODY></HTML>\n");
        return buf.toString();
    }
    
    private static String hrefEncodeURI(final String raw) {
        StringBuffer buf = null;
        int i = 0;
    Label_0083:
        while (i < raw.length()) {
            final char c = raw.charAt(i);
            switch (c) {
                case '\"':
                case '\'':
                case '<':
                case '>': {
                    buf = new StringBuffer(raw.length() << 1);
                    break Label_0083;
                }
                default: {
                    ++i;
                    continue;
                }
            }
        }
        if (buf == null) {
            return raw;
        }
        for (i = 0; i < raw.length(); ++i) {
            final char c = raw.charAt(i);
            switch (c) {
                case '\"': {
                    buf.append("%22");
                    break;
                }
                case '\'': {
                    buf.append("%27");
                    break;
                }
                case '<': {
                    buf.append("%3C");
                    break;
                }
                case '>': {
                    buf.append("%3E");
                    break;
                }
                default: {
                    buf.append(c);
                    break;
                }
            }
        }
        return buf.toString();
    }
    
    private static String deTag(final String raw) {
        return StringUtil.sanitizeXmlString(raw);
    }
    
    public void writeTo(final OutputStream out, final long start, final long count) throws IOException {
        final InputStream in = this.getInputStream();
        Throwable x0 = null;
        try {
            in.skip(start);
            if (count < 0L) {
                IO.copy(in, out);
            }
            else {
                IO.copy(in, out, count);
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (in != null) {
                $closeResource(x0, in);
            }
        }
    }
    
    public void copyTo(final File destination) throws IOException {
        if (destination.exists()) {
            throw new IllegalArgumentException(destination + " exists");
        }
        final OutputStream out = new FileOutputStream(destination);
        Throwable x0 = null;
        try {
            this.writeTo(out, 0L, -1L);
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            $closeResource(x0, out);
        }
    }
    
    public String getWeakETag() {
        return this.getWeakETag("");
    }
    
    public String getWeakETag(final String suffix) {
        try {
            final StringBuilder b = new StringBuilder(32);
            b.append("W/\"");
            final String name = this.getName();
            final int length = name.length();
            long lhash = 0L;
            for (int i = 0; i < length; ++i) {
                lhash = 31L * lhash + name.charAt(i);
            }
            B64Code.encode(this.lastModified() ^ lhash, b);
            B64Code.encode(this.length() ^ lhash, b);
            b.append(suffix);
            b.append('\"');
            return b.toString();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Collection<Resource> getAllResources() {
        try {
            final ArrayList<Resource> deep = new ArrayList<Resource>();
            final String[] list = this.list();
            if (list != null) {
                for (final String i : list) {
                    final Resource r = this.addPath(i);
                    if (r.isDirectory()) {
                        deep.addAll(r.getAllResources());
                    }
                    else {
                        deep.add(r);
                    }
                }
            }
            return deep;
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static URL toURL(final File file) throws MalformedURLException {
        return file.toURI().toURL();
    }
    
    private static /* synthetic */ void $closeResource(final Throwable x0, final AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            }
            catch (Throwable exception) {
                x0.addSuppressed(exception);
            }
        }
        else {
            x1.close();
        }
    }
    
    static {
        LOG = Log.getLogger(Resource.class);
        Resource.__defaultUseCaches = true;
    }
}
