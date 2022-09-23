// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.resource;

import java.util.ArrayList;
import java.util.Enumeration;
import java.net.JarURLConnection;
import org.mortbay.log.Log;
import java.io.IOException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.io.File;
import java.util.jar.JarFile;

class JarFileResource extends JarResource
{
    transient JarFile _jarFile;
    transient File _file;
    transient String[] _list;
    transient JarEntry _entry;
    transient boolean _directory;
    transient String _jarUrl;
    transient String _path;
    transient boolean _exists;
    
    JarFileResource(final URL url) {
        super(url);
    }
    
    JarFileResource(final URL url, final boolean useCaches) {
        super(url, useCaches);
    }
    
    public synchronized void release() {
        this._list = null;
        this._entry = null;
        this._file = null;
        this._jarFile = null;
        super.release();
    }
    
    protected boolean checkConnection() {
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
    
    protected void newConnection() throws IOException {
        super.newConnection();
        this._entry = null;
        this._file = null;
        this._jarFile = null;
        this._list = null;
        final int sep = this._urlString.indexOf("!/");
        this._jarUrl = this._urlString.substring(0, sep + 2);
        this._path = this._urlString.substring(sep + 2);
        if (this._path.length() == 0) {
            this._path = null;
        }
        this._jarFile = this._jarConnection.getJarFile();
        this._file = new File(this._jarFile.getName());
    }
    
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
                Log.ignore(e);
                return false;
            }
        }
        final boolean check = this.checkConnection();
        if (this._jarUrl != null && this._path == null) {
            this._directory = check;
            return true;
        }
        JarFile jarFile = null;
        if (check) {
            jarFile = this._jarFile;
        }
        else {
            try {
                final JarURLConnection c = (JarURLConnection)new URL(this._jarUrl).openConnection();
                c.setUseCaches(this.getUseCaches());
                jarFile = c.getJarFile();
            }
            catch (Exception e2) {
                Log.ignore(e2);
            }
        }
        if (jarFile != null && this._entry == null && !this._directory) {
            final Enumeration e3 = jarFile.entries();
            while (e3.hasMoreElements()) {
                final JarEntry entry = e3.nextElement();
                final String name = entry.getName().replace('\\', '/');
                if (name.equals(this._path)) {
                    this._entry = entry;
                    this._directory = this._path.endsWith("/");
                    break;
                }
                if (this._path.endsWith("/")) {
                    if (name.startsWith(this._path)) {
                        this._directory = true;
                        break;
                    }
                    continue;
                }
                else {
                    if (name.startsWith(this._path) && name.length() > this._path.length() && name.charAt(this._path.length()) == '/') {
                        this._directory = true;
                        break;
                    }
                    continue;
                }
            }
        }
        return this._exists = (this._directory || this._entry != null);
    }
    
    public boolean isDirectory() {
        return this._urlString.endsWith("/") || (this.exists() && this._directory);
    }
    
    public long lastModified() {
        if (this.checkConnection() && this._file != null) {
            return this._file.lastModified();
        }
        return -1L;
    }
    
    public synchronized String[] list() {
        if (this.isDirectory() && this._list == null) {
            final ArrayList list = new ArrayList(32);
            this.checkConnection();
            JarFile jarFile = this._jarFile;
            if (jarFile == null) {
                try {
                    final JarURLConnection jc = (JarURLConnection)new URL(this._jarUrl).openConnection();
                    jc.setUseCaches(this.getUseCaches());
                    jarFile = jc.getJarFile();
                }
                catch (Exception e) {
                    Log.ignore(e);
                }
            }
            final Enumeration e2 = jarFile.entries();
            final String dir = this._urlString.substring(this._urlString.indexOf("!/") + 2);
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
            list.toArray(this._list = new String[list.size()]);
        }
        return this._list;
    }
    
    public long length() {
        if (this.isDirectory()) {
            return -1L;
        }
        if (this._entry != null) {
            return this._entry.getSize();
        }
        return -1L;
    }
    
    public String encode(final String uri) {
        return uri;
    }
    
    public static Resource getNonCachingResource(final Resource resource) {
        if (!(resource instanceof JarFileResource)) {
            return resource;
        }
        final JarFileResource oldResource = (JarFileResource)resource;
        final JarFileResource newResource = new JarFileResource(oldResource.getURL(), false);
        return newResource;
    }
}
