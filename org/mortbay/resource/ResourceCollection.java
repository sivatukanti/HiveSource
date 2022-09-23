// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.resource;

import java.util.HashSet;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.util.StringTokenizer;

public class ResourceCollection extends Resource
{
    private Resource[] _resources;
    
    public ResourceCollection() {
    }
    
    public ResourceCollection(final Resource[] resources) {
        this.setResources(resources);
    }
    
    public ResourceCollection(final String[] resources) {
        this.setResources(resources);
    }
    
    public ResourceCollection(final String csvResources) {
        this.setResources(csvResources);
    }
    
    public void setResources(final Resource[] resources) {
        if (this._resources != null) {
            throw new IllegalStateException("*resources* already set.");
        }
        if (resources == null) {
            throw new IllegalArgumentException("*resources* must not be null.");
        }
        if (resources.length == 0) {
            throw new IllegalArgumentException("arg *resources* must be one or more resources.");
        }
        this._resources = resources;
        for (int i = 0; i < this._resources.length; ++i) {
            final Resource r = this._resources[i];
            if (!r.exists() || !r.isDirectory()) {
                throw new IllegalArgumentException(r + " is not an existing directory.");
            }
        }
    }
    
    public void setResources(final String[] resources) {
        if (this._resources != null) {
            throw new IllegalStateException("*resources* already set.");
        }
        if (resources == null) {
            throw new IllegalArgumentException("*resources* must not be null.");
        }
        if (resources.length == 0) {
            throw new IllegalArgumentException("arg *resources* must be one or more resources.");
        }
        this._resources = new Resource[resources.length];
        try {
            for (int i = 0; i < resources.length; ++i) {
                this._resources[i] = Resource.newResource(resources[i]);
                if (!this._resources[i].exists() || !this._resources[i].isDirectory()) {
                    throw new IllegalArgumentException(this._resources[i] + " is not an existing directory.");
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setResources(final String csvResources) {
        if (this._resources != null) {
            throw new IllegalStateException("*resources* already set.");
        }
        if (csvResources == null) {
            throw new IllegalArgumentException("*csvResources* must not be null.");
        }
        final StringTokenizer tokenizer = new StringTokenizer(csvResources, ",;");
        final int len = tokenizer.countTokens();
        if (len == 0) {
            throw new IllegalArgumentException("arg *resources* must be one or more resources.");
        }
        this._resources = new Resource[len];
        try {
            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                this._resources[i] = Resource.newResource(tokenizer.nextToken().trim());
                if (!this._resources[i].exists() || !this._resources[i].isDirectory()) {
                    throw new IllegalArgumentException(this._resources[i] + " is not an existing directory.");
                }
                ++i;
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setResourcesAsCSV(final String csvResources) {
        this.setResources(csvResources);
    }
    
    public Resource[] getResources() {
        return this._resources;
    }
    
    public Resource addPath(final String path) throws IOException, MalformedURLException {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        if (path == null) {
            throw new MalformedURLException();
        }
        if (path.length() == 0 || "/".equals(path)) {
            return this;
        }
        Resource resource = null;
        ArrayList resources = null;
        int i = 0;
        while (i < this._resources.length) {
            resource = this._resources[i].addPath(path);
            if (resource.exists()) {
                if (resource.isDirectory()) {
                    break;
                }
                return resource;
            }
            else {
                ++i;
            }
        }
        ++i;
        while (i < this._resources.length) {
            final Resource r = this._resources[i].addPath(path);
            if (r.exists() && r.isDirectory()) {
                if (resource != null) {
                    resources = new ArrayList();
                    resources.add(resource);
                    resource = null;
                }
                resources.add(r);
            }
            ++i;
        }
        if (resource != null) {
            return resource;
        }
        if (resources != null) {
            return new ResourceCollection(resources.toArray(new Resource[resources.size()]));
        }
        return null;
    }
    
    protected Object findResource(final String path) throws IOException, MalformedURLException {
        Resource resource = null;
        ArrayList resources = null;
        int i = 0;
        while (i < this._resources.length) {
            resource = this._resources[i].addPath(path);
            if (resource.exists()) {
                if (resource.isDirectory()) {
                    break;
                }
                return resource;
            }
            else {
                ++i;
            }
        }
        ++i;
        while (i < this._resources.length) {
            final Resource r = this._resources[i].addPath(path);
            if (r.exists() && r.isDirectory()) {
                if (resource != null) {
                    resources = new ArrayList();
                    resources.add(resource);
                }
                resources.add(r);
            }
            ++i;
        }
        if (resource != null) {
            return resource;
        }
        if (resources != null) {
            return resources;
        }
        return null;
    }
    
    public boolean delete() throws SecurityException {
        throw new UnsupportedOperationException();
    }
    
    public boolean exists() {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        return true;
    }
    
    public File getFile() throws IOException {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        for (int i = 0; i < this._resources.length; ++i) {
            final File f = this._resources[i].getFile();
            if (f != null) {
                return f;
            }
        }
        return null;
    }
    
    public InputStream getInputStream() throws IOException {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        for (int i = 0; i < this._resources.length; ++i) {
            final InputStream is = this._resources[i].getInputStream();
            if (is != null) {
                return is;
            }
        }
        return null;
    }
    
    public String getName() {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        for (int i = 0; i < this._resources.length; ++i) {
            final String name = this._resources[i].getName();
            if (name != null) {
                return name;
            }
        }
        return null;
    }
    
    public OutputStream getOutputStream() throws IOException, SecurityException {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        for (int i = 0; i < this._resources.length; ++i) {
            final OutputStream os = this._resources[i].getOutputStream();
            if (os != null) {
                return os;
            }
        }
        return null;
    }
    
    public URL getURL() {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        for (int i = 0; i < this._resources.length; ++i) {
            final URL url = this._resources[i].getURL();
            if (url != null) {
                return url;
            }
        }
        return null;
    }
    
    public boolean isDirectory() {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        return true;
    }
    
    public long lastModified() {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        for (int i = 0; i < this._resources.length; ++i) {
            final long lm = this._resources[i].lastModified();
            if (lm != -1L) {
                return lm;
            }
        }
        return -1L;
    }
    
    public long length() {
        return -1L;
    }
    
    public String[] list() {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        final HashSet set = new HashSet();
        for (int i = 0; i < this._resources.length; ++i) {
            final String[] list = this._resources[i].list();
            for (int j = 0; j < list.length; ++j) {
                set.add(list[j]);
            }
        }
        return (String[])set.toArray(new String[set.size()]);
    }
    
    public void release() {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        for (int i = 0; i < this._resources.length; ++i) {
            this._resources[i].release();
        }
    }
    
    public boolean renameTo(final Resource dest) throws SecurityException {
        throw new UnsupportedOperationException();
    }
    
    public String toString() {
        if (this._resources == null) {
            return "";
        }
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < this._resources.length; ++i) {
            buffer.append(this._resources[i].toString()).append(';');
        }
        return buffer.toString();
    }
}
