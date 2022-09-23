// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.resource;

import org.eclipse.jetty.util.log.Log;
import java.util.jar.Manifest;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.io.FileOutputStream;
import org.eclipse.jetty.util.URIUtil;
import java.util.jar.JarInputStream;
import org.eclipse.jetty.util.IO;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URL;
import java.net.JarURLConnection;
import org.eclipse.jetty.util.log.Logger;

public class JarResource extends URLResource
{
    private static final Logger LOG;
    protected JarURLConnection _jarConnection;
    
    protected JarResource(final URL url) {
        super(url, null);
    }
    
    protected JarResource(final URL url, final boolean useCaches) {
        super(url, null, useCaches);
    }
    
    @Override
    public synchronized void close() {
        this._jarConnection = null;
        super.close();
    }
    
    @Override
    protected synchronized boolean checkConnection() {
        super.checkConnection();
        try {
            if (this._jarConnection != this._connection) {
                this.newConnection();
            }
        }
        catch (IOException e) {
            JarResource.LOG.ignore(e);
            this._jarConnection = null;
        }
        return this._jarConnection != null;
    }
    
    protected void newConnection() throws IOException {
        this._jarConnection = (JarURLConnection)this._connection;
    }
    
    @Override
    public boolean exists() {
        if (this._urlString.endsWith("!/")) {
            return this.checkConnection();
        }
        return super.exists();
    }
    
    @Override
    public File getFile() throws IOException {
        return null;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        this.checkConnection();
        if (!this._urlString.endsWith("!/")) {
            return new FilterInputStream(this.getInputStream(false)) {
                @Override
                public void close() throws IOException {
                    this.in = IO.getClosedStream();
                }
            };
        }
        final URL url = new URL(this._urlString.substring(4, this._urlString.length() - 2));
        final InputStream is = url.openStream();
        return is;
    }
    
    @Override
    public void copyTo(final File directory) throws IOException {
        if (!this.exists()) {
            return;
        }
        if (JarResource.LOG.isDebugEnabled()) {
            JarResource.LOG.debug("Extract " + this + " to " + directory, new Object[0]);
        }
        final String urlString = this.getURL().toExternalForm().trim();
        final int endOfJarUrl = urlString.indexOf("!/");
        final int startOfJarUrl = (endOfJarUrl >= 0) ? 4 : 0;
        if (endOfJarUrl < 0) {
            throw new IOException("Not a valid jar url: " + urlString);
        }
        final URL jarFileURL = new URL(urlString.substring(startOfJarUrl, endOfJarUrl));
        final String subEntryName = (endOfJarUrl + 2 < urlString.length()) ? urlString.substring(endOfJarUrl + 2) : null;
        boolean subEntryIsDir = subEntryName != null && subEntryName.endsWith("/");
        if (JarResource.LOG.isDebugEnabled()) {
            JarResource.LOG.debug("Extracting entry = " + subEntryName + " from jar " + jarFileURL, new Object[0]);
        }
        final URLConnection c = jarFileURL.openConnection();
        c.setUseCaches(false);
        final InputStream is = c.getInputStream();
        Throwable x0 = null;
        try {
            final JarInputStream jin = new JarInputStream(is);
            Throwable x2 = null;
            try {
                JarEntry entry;
                while ((entry = jin.getNextJarEntry()) != null) {
                    String entryName = entry.getName();
                    boolean shouldExtract;
                    if (subEntryName != null && entryName.startsWith(subEntryName)) {
                        if (!subEntryIsDir && subEntryName.length() + 1 == entryName.length() && entryName.endsWith("/")) {
                            subEntryIsDir = true;
                        }
                        if (subEntryIsDir) {
                            entryName = entryName.substring(subEntryName.length());
                            shouldExtract = !entryName.equals("");
                        }
                        else {
                            shouldExtract = true;
                        }
                    }
                    else {
                        shouldExtract = (subEntryName == null || entryName.startsWith(subEntryName));
                    }
                    if (!shouldExtract) {
                        if (!JarResource.LOG.isDebugEnabled()) {
                            continue;
                        }
                        JarResource.LOG.debug("Skipping entry: " + entryName, new Object[0]);
                    }
                    else {
                        String dotCheck = entryName.replace('\\', '/');
                        dotCheck = URIUtil.canonicalPath(dotCheck);
                        if (dotCheck == null) {
                            if (!JarResource.LOG.isDebugEnabled()) {
                                continue;
                            }
                            JarResource.LOG.debug("Invalid entry: " + entryName, new Object[0]);
                        }
                        else {
                            final File file = new File(directory, entryName);
                            if (entry.isDirectory()) {
                                if (file.exists()) {
                                    continue;
                                }
                                file.mkdirs();
                            }
                            else {
                                final File dir = new File(file.getParent());
                                if (!dir.exists()) {
                                    dir.mkdirs();
                                }
                                final OutputStream fout = new FileOutputStream(file);
                                Throwable x3 = null;
                                try {
                                    IO.copy(jin, fout);
                                }
                                catch (Throwable t) {
                                    x3 = t;
                                    throw t;
                                }
                                finally {
                                    $closeResource(x3, fout);
                                }
                                if (entry.getTime() < 0L) {
                                    continue;
                                }
                                file.setLastModified(entry.getTime());
                            }
                        }
                    }
                }
                if (subEntryName == null || (subEntryName != null && subEntryName.equalsIgnoreCase("META-INF/MANIFEST.MF"))) {
                    final Manifest manifest = jin.getManifest();
                    if (manifest != null) {
                        final File metaInf = new File(directory, "META-INF");
                        metaInf.mkdir();
                        final File f = new File(metaInf, "MANIFEST.MF");
                        final OutputStream fout2 = new FileOutputStream(f);
                        Throwable x4 = null;
                        try {
                            manifest.write(fout2);
                        }
                        catch (Throwable t2) {
                            x4 = t2;
                            throw t2;
                        }
                        finally {
                            $closeResource(x4, fout2);
                        }
                    }
                }
            }
            catch (Throwable t3) {
                x2 = t3;
                throw t3;
            }
            finally {
                $closeResource(x2, jin);
            }
        }
        catch (Throwable t4) {
            x0 = t4;
            throw t4;
        }
        finally {
            if (is != null) {
                $closeResource(x0, is);
            }
        }
    }
    
    public static Resource newJarResource(final Resource resource) throws IOException {
        if (resource instanceof JarResource) {
            return resource;
        }
        return Resource.newResource("jar:" + resource + "!/");
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
        LOG = Log.getLogger(JarResource.class);
    }
}
