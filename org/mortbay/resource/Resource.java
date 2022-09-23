// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.resource;

import org.mortbay.util.IO;
import org.mortbay.util.StringUtil;
import java.util.Date;
import java.text.DateFormat;
import java.util.Arrays;
import java.io.OutputStream;
import java.io.InputStream;
import org.mortbay.util.Loader;
import org.mortbay.util.URIUtil;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URLConnection;
import org.mortbay.log.Log;
import java.io.IOException;
import java.net.URL;
import java.io.Serializable;

public abstract class Resource implements Serializable
{
    public static boolean __defaultUseCaches;
    Object _associate;
    
    public static void setDefaultUseCaches(final boolean useCaches) {
        Resource.__defaultUseCaches = useCaches;
    }
    
    public static boolean getDefaultUseCaches() {
        return Resource.__defaultUseCaches;
    }
    
    public static Resource newResource(final URL url) throws IOException {
        return newResource(url, Resource.__defaultUseCaches);
    }
    
    public static Resource newResource(final URL url, final boolean useCaches) {
        if (url == null) {
            return null;
        }
        final String url_string = url.toExternalForm();
        if (url_string.startsWith("file:")) {
            try {
                final FileResource fileResource = new FileResource(url);
                return fileResource;
            }
            catch (Exception e) {
                Log.debug("EXCEPTION ", e);
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
    
    public static Resource newResource(final String resource) throws MalformedURLException, IOException {
        return newResource(resource, Resource.__defaultUseCaches);
    }
    
    public static Resource newResource(String resource, final boolean useCaches) throws MalformedURLException, IOException {
        URL url = null;
        try {
            url = new URL(resource);
        }
        catch (MalformedURLException e3) {
            if (!resource.startsWith("ftp:") && !resource.startsWith("file:") && !resource.startsWith("jar:")) {
                try {
                    if (resource.startsWith("./")) {
                        resource = resource.substring(2);
                    }
                    final File file = new File(resource).getCanonicalFile();
                    url = new URL(URIUtil.encodePath(file.toURL().toString()));
                    final URLConnection connection = url.openConnection();
                    connection.setUseCaches(useCaches);
                    final FileResource fileResource = new FileResource(url, connection, file);
                    return fileResource;
                }
                catch (Exception e2) {
                    Log.debug("EXCEPTION ", e2);
                    throw e3;
                }
            }
            Log.warn("Bad Resource: " + resource);
            throw e3;
        }
        final String nurl = url.toString();
        if (nurl.length() > 0 && nurl.charAt(nurl.length() - 1) != resource.charAt(resource.length() - 1) && (nurl.charAt(nurl.length() - 1) != '/' || nurl.charAt(nurl.length() - 2) != resource.charAt(resource.length() - 1)) && (resource.charAt(resource.length() - 1) != '/' || resource.charAt(resource.length() - 2) != nurl.charAt(nurl.length() - 1))) {
            return new BadResource(url, "Trailing special characters stripped by URL in " + resource);
        }
        return newResource(url);
    }
    
    public static Resource newSystemResource(final String resource) throws IOException {
        URL url = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            url = loader.getResource(resource);
            if (url == null && resource.startsWith("/")) {
                url = loader.getResource(resource.substring(1));
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
                url = loader.getResource(resource.substring(1));
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
            try {
                url = Loader.getResource(Resource.class, name, checkParents);
            }
            catch (ClassNotFoundException e) {
                url = ClassLoader.getSystemResource(name);
            }
        }
        if (url == null) {
            return null;
        }
        return newResource(url, useCaches);
    }
    
    protected void finalize() {
        this.release();
    }
    
    public abstract void release();
    
    public abstract boolean exists();
    
    public abstract boolean isDirectory();
    
    public abstract long lastModified();
    
    public abstract long length();
    
    public abstract URL getURL();
    
    public abstract File getFile() throws IOException;
    
    public abstract String getName();
    
    public abstract InputStream getInputStream() throws IOException;
    
    public abstract OutputStream getOutputStream() throws IOException, SecurityException;
    
    public abstract boolean delete() throws SecurityException;
    
    public abstract boolean renameTo(final Resource p0) throws SecurityException;
    
    public abstract String[] list();
    
    public abstract Resource addPath(final String p0) throws IOException, MalformedURLException;
    
    public String encode(final String uri) {
        return URIUtil.encodePath(uri);
    }
    
    public Object getAssociate() {
        return this._associate;
    }
    
    public void setAssociate(final Object o) {
        this._associate = o;
    }
    
    public URL getAlias() {
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
        final StringBuffer buf = new StringBuffer(4096);
        buf.append("<HTML><HEAD><TITLE>");
        buf.append(title);
        buf.append("</TITLE></HEAD><BODY>\n<H1>");
        buf.append(title);
        buf.append("</H1>\n<TABLE BORDER=0>\n");
        if (parent) {
            buf.append("<TR><TD><A HREF=\"");
            buf.append(URIUtil.addPaths(base, "../"));
            buf.append("\">Parent Directory</A></TD><TD></TD><TD></TD></TR>\n");
        }
        final String defangedBase = defangURI(base);
        final DateFormat dfmt = DateFormat.getDateTimeInstance(2, 2);
        for (int i = 0; i < ls.length; ++i) {
            final Resource item = this.addPath(ls[i]);
            buf.append("\n<TR><TD><A HREF=\"");
            final String path = URIUtil.addPaths(defangedBase, URIUtil.encodePath(ls[i]));
            buf.append(path);
            if (item.isDirectory() && !path.endsWith("/")) {
                buf.append("/");
            }
            buf.append("\">");
            buf.append(deTag(ls[i]));
            buf.append("&nbsp;");
            buf.append("</TD><TD ALIGN=right>");
            buf.append(item.length());
            buf.append(" bytes&nbsp;</TD><TD>");
            buf.append(dfmt.format(new Date(item.lastModified())));
            buf.append("</TD></TR>");
        }
        buf.append("</TABLE>\n");
        buf.append("</BODY></HTML>\n");
        return buf.toString();
    }
    
    private static String defangURI(final String raw) {
        StringBuffer buf = null;
        if (buf == null) {
            for (int i = 0; i < raw.length(); ++i) {
                final char c = raw.charAt(i);
                switch (c) {
                    case '\"':
                    case '\'':
                    case '<':
                    case '>': {
                        buf = new StringBuffer(raw.length() << 1);
                        break;
                    }
                }
            }
            if (buf == null) {
                return raw;
            }
        }
        for (int i = 0; i < raw.length(); ++i) {
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
        return StringUtil.replace(StringUtil.replace(raw, "<", "&lt;"), ">", "&gt;");
    }
    
    public void writeTo(final OutputStream out, final long start, final long count) throws IOException {
        final InputStream in = this.getInputStream();
        try {
            in.skip(start);
            if (count < 0L) {
                IO.copy(in, out);
            }
            else {
                IO.copy(in, out, count);
            }
        }
        finally {
            in.close();
        }
    }
    
    static {
        Resource.__defaultUseCaches = true;
    }
}
