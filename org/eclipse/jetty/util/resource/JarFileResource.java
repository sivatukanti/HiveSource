// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.resource;

import org.eclipse.jetty.util.log.Log;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.net.JarURLConnection;
import java.io.IOException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.io.File;
import java.util.jar.JarFile;
import org.eclipse.jetty.util.log.Logger;

class JarFileResource extends JarResource
{
    private static final Logger LOG;
    private JarFile _jarFile;
    private File _file;
    private String[] _list;
    private JarEntry _entry;
    private boolean _directory;
    private String _jarUrl;
    private String _path;
    private boolean _exists;
    
    protected JarFileResource(final URL url) {
        super(url);
    }
    
    protected JarFileResource(final URL url, final boolean useCaches) {
        super(url, useCaches);
    }
    
    @Override
    public synchronized void close() {
        this._exists = false;
        this._list = null;
        this._entry = null;
        this._file = null;
        if (!this.getUseCaches() && this._jarFile != null) {
            try {
                if (JarFileResource.LOG.isDebugEnabled()) {
                    JarFileResource.LOG.debug("Closing JarFile " + this._jarFile.getName(), new Object[0]);
                }
                this._jarFile.close();
            }
            catch (IOException ioe) {
                JarFileResource.LOG.ignore(ioe);
            }
        }
        this._jarFile = null;
        super.close();
    }
    
    @Override
    protected synchronized boolean checkConnection() {
        try {
            super.checkConnection();
        }
        finally {
            if (this._jarConnection == null) {
                this._entry = null;
                this._file = null;
                this._jarFile = null;
                this._list = null;
            }
        }
        return this._jarFile != null;
    }
    
    @Override
    protected synchronized void newConnection() throws IOException {
        super.newConnection();
        this._entry = null;
        this._file = null;
        this._jarFile = null;
        this._list = null;
        final int sep = this._urlString.lastIndexOf("!/");
        this._jarUrl = this._urlString.substring(0, sep + 2);
        this._path = this._urlString.substring(sep + 2);
        if (this._path.length() == 0) {
            this._path = null;
        }
        this._jarFile = this._jarConnection.getJarFile();
        this._file = new File(this._jarFile.getName());
    }
    
    @Override
    public boolean exists() {
        if (this._exists) {
            return true;
        }
        if (this._urlString.endsWith("!/")) {
            final String file_url = this._urlString.substring(4, this._urlString.length() - 2);
            try {
                return Resource.newResource(file_url).exists();
            }
            catch (Exception e) {
                JarFileResource.LOG.ignore(e);
                return false;
            }
        }
        final boolean check = this.checkConnection();
        if (this._jarUrl != null && this._path == null) {
            this._directory = check;
            return true;
        }
        boolean close_jar_file = false;
        JarFile jar_file = null;
        if (check) {
            jar_file = this._jarFile;
        }
        else {
            try {
                final JarURLConnection c = (JarURLConnection)new URL(this._jarUrl).openConnection();
                c.setUseCaches(this.getUseCaches());
                jar_file = c.getJarFile();
                close_jar_file = !this.getUseCaches();
            }
            catch (Exception e2) {
                JarFileResource.LOG.ignore(e2);
            }
        }
        if (jar_file != null && this._entry == null && !this._directory) {
            final JarEntry entry = jar_file.getJarEntry(this._path);
            if (entry == null) {
                this._exists = false;
            }
            else if (entry.isDirectory()) {
                this._directory = true;
                this._entry = entry;
            }
            else {
                final JarEntry directory = jar_file.getJarEntry(this._path + '/');
                if (directory != null) {
                    this._directory = true;
                    this._entry = directory;
                }
                else {
                    this._directory = false;
                    this._entry = entry;
                }
            }
        }
        if (close_jar_file && jar_file != null) {
            try {
                jar_file.close();
            }
            catch (IOException ioe) {
                JarFileResource.LOG.ignore(ioe);
            }
        }
        return this._exists = (this._directory || this._entry != null);
    }
    
    @Override
    public boolean isDirectory() {
        return this._urlString.endsWith("/") || (this.exists() && this._directory);
    }
    
    @Override
    public long lastModified() {
        if (!this.checkConnection() || this._file == null) {
            return -1L;
        }
        if (this.exists() && this._entry != null) {
            return this._entry.getTime();
        }
        return this._file.lastModified();
    }
    
    @Override
    public synchronized String[] list() {
        if (this.isDirectory() && this._list == null) {
            List<String> list = null;
            try {
                list = this.listEntries();
            }
            catch (Exception e) {
                JarFileResource.LOG.warn("Retrying list:" + e, new Object[0]);
                JarFileResource.LOG.debug(e);
                this.close();
                list = this.listEntries();
            }
            if (list != null) {
                list.toArray(this._list = new String[list.size()]);
            }
        }
        return this._list;
    }
    
    private List<String> listEntries() {
        this.checkConnection();
        final ArrayList<String> list = new ArrayList<String>(32);
        JarFile jarFile = this._jarFile;
        if (jarFile == null) {
            try {
                final JarURLConnection jc = (JarURLConnection)new URL(this._jarUrl).openConnection();
                jc.setUseCaches(this.getUseCaches());
                jarFile = jc.getJarFile();
            }
            catch (Exception e) {
                e.printStackTrace();
                JarFileResource.LOG.ignore(e);
            }
            if (jarFile == null) {
                throw new IllegalStateException();
            }
        }
        final Enumeration<JarEntry> e2 = jarFile.entries();
        final String dir = this._urlString.substring(this._urlString.lastIndexOf("!/") + 2);
        while (e2.hasMoreElements()) {
            final JarEntry entry = e2.nextElement();
            final String name = entry.getName().replace('\\', '/');
            if (name.startsWith(dir)) {
                if (name.length() == dir.length()) {
                    continue;
                }
                String listName = name.substring(dir.length());
                final int dash = listName.indexOf(47);
                if (dash >= 0) {
                    if (dash == 0 && listName.length() == 1) {
                        continue;
                    }
                    if (dash == 0) {
                        listName = listName.substring(dash + 1, listName.length());
                    }
                    else {
                        listName = listName.substring(0, dash + 1);
                    }
                    if (list.contains(listName)) {
                        continue;
                    }
                }
                list.add(listName);
            }
        }
        return list;
    }
    
    @Override
    public long length() {
        if (this.isDirectory()) {
            return -1L;
        }
        if (this._entry != null) {
            return this._entry.getSize();
        }
        return -1L;
    }
    
    public static Resource getNonCachingResource(final Resource resource) {
        if (!(resource instanceof JarFileResource)) {
            return resource;
        }
        final JarFileResource oldResource = (JarFileResource)resource;
        final JarFileResource newResource = new JarFileResource(oldResource.getURL(), false);
        return newResource;
    }
    
    @Override
    public boolean isContainedIn(final Resource resource) throws MalformedURLException {
        String string = this._urlString;
        final int index = string.lastIndexOf("!/");
        if (index > 0) {
            string = string.substring(0, index);
        }
        if (string.startsWith("jar:")) {
            string = string.substring(4);
        }
        final URL url = new URL(string);
        return url.sameFile(resource.getURI().toURL());
    }
    
    static {
        LOG = Log.getLogger(JarFileResource.class);
    }
}
