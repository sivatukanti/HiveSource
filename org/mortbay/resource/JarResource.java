// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.resource;

import java.util.jar.Manifest;
import java.util.jar.JarEntry;
import java.io.OutputStream;
import java.io.FileOutputStream;
import org.mortbay.util.URIUtil;
import java.util.jar.JarInputStream;
import org.mortbay.util.IO;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import org.mortbay.log.Log;
import java.net.URLConnection;
import java.net.URL;
import java.net.JarURLConnection;

public class JarResource extends URLResource
{
    protected transient JarURLConnection _jarConnection;
    
    JarResource(final URL url) {
        super(url, null);
    }
    
    JarResource(final URL url, final boolean useCaches) {
        super(url, null, useCaches);
    }
    
    public synchronized void release() {
        this._jarConnection = null;
        super.release();
    }
    
    protected boolean checkConnection() {
        super.checkConnection();
        try {
            if (this._jarConnection != this._connection) {
                this.newConnection();
            }
        }
        catch (IOException e) {
            Log.ignore(e);
            this._jarConnection = null;
        }
        return this._jarConnection != null;
    }
    
    protected void newConnection() throws IOException {
        this._jarConnection = (JarURLConnection)this._connection;
    }
    
    public boolean exists() {
        if (this._urlString.endsWith("!/")) {
            return this.checkConnection();
        }
        return super.exists();
    }
    
    public File getFile() throws IOException {
        return null;
    }
    
    public InputStream getInputStream() throws IOException {
        this.checkConnection();
        if (!this._urlString.endsWith("!/")) {
            return new FilterInputStream(super.getInputStream()) {
                public void close() throws IOException {
                    this.in = IO.getClosedStream();
                }
            };
        }
        final URL url = new URL(this._urlString.substring(4, this._urlString.length() - 2));
        final InputStream is = url.openStream();
        return is;
    }
    
    public static void extract(final Resource resource, final File directory, final boolean deleteOnExit) throws IOException {
        if (Log.isDebugEnabled()) {
            Log.debug("Extract " + resource + " to " + directory);
        }
        final String urlString = resource.getURL().toExternalForm().trim();
        final int endOfJarUrl = urlString.indexOf("!/");
        final int startOfJarUrl = (endOfJarUrl >= 0) ? 4 : 0;
        if (endOfJarUrl < 0) {
            throw new IOException("Not a valid jar url: " + urlString);
        }
        final URL jarFileURL = new URL(urlString.substring(startOfJarUrl, endOfJarUrl));
        final String subEntryName = (endOfJarUrl + 2 < urlString.length()) ? urlString.substring(endOfJarUrl + 2) : null;
        final boolean subEntryIsDir = subEntryName != null && subEntryName.endsWith("/");
        if (Log.isDebugEnabled()) {
            Log.debug("Extracting entry = " + subEntryName + " from jar " + jarFileURL);
        }
        final InputStream is = jarFileURL.openConnection().getInputStream();
        final JarInputStream jin = new JarInputStream(is);
        final String directoryCanonicalPath = directory.getCanonicalPath() + "/";
        JarEntry entry;
        while ((entry = jin.getNextJarEntry()) != null) {
            String entryName = entry.getName();
            boolean shouldExtract;
            if (subEntryName != null && entryName.startsWith(subEntryName)) {
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
                if (!Log.isDebugEnabled()) {
                    continue;
                }
                Log.debug("Skipping entry: " + entryName);
            }
            else {
                String dotCheck = entryName.replace('\\', '/');
                dotCheck = URIUtil.canonicalPath(dotCheck);
                if (dotCheck == null) {
                    if (!Log.isDebugEnabled()) {
                        continue;
                    }
                    Log.debug("Invalid entry: " + entryName);
                }
                else {
                    final File file = new File(directory, entryName);
                    if (entry.isDirectory()) {
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                    }
                    else {
                        final File dir = new File(file.getParent());
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        FileOutputStream fout = null;
                        try {
                            fout = new FileOutputStream(file);
                            IO.copy(jin, fout);
                        }
                        finally {
                            IO.close(fout);
                        }
                        if (entry.getTime() >= 0L) {
                            file.setLastModified(entry.getTime());
                        }
                    }
                    if (!deleteOnExit) {
                        continue;
                    }
                    file.deleteOnExit();
                }
            }
        }
        if (subEntryName == null || (subEntryName != null && subEntryName.equalsIgnoreCase("META-INF/MANIFEST.MF"))) {
            final Manifest manifest = jin.getManifest();
            if (manifest != null) {
                final File metaInf = new File(directory, "META-INF");
                metaInf.mkdir();
                final File f = new File(metaInf, "MANIFEST.MF");
                final FileOutputStream fout2 = new FileOutputStream(f);
                manifest.write(fout2);
                fout2.close();
            }
        }
        IO.close(jin);
    }
    
    public void extract(final File directory, final boolean deleteOnExit) throws IOException {
        extract(this, directory, deleteOnExit);
    }
}
