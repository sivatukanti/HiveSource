// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.resource;

import org.eclipse.jetty.util.log.Log;
import java.util.Arrays;
import java.util.HashSet;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.jetty.util.log.Logger;

public class ResourceCollection extends Resource
{
    private static final Logger LOG;
    private Resource[] _resources;
    
    public ResourceCollection() {
        this._resources = new Resource[0];
    }
    
    public ResourceCollection(final Resource... resources) {
        final List<Resource> list = new ArrayList<Resource>();
        for (final Resource r : resources) {
            if (r != null) {
                if (r instanceof ResourceCollection) {
                    for (final Resource r2 : ((ResourceCollection)r).getResources()) {
                        list.add(r2);
                    }
                }
                else {
                    list.add(r);
                }
            }
        }
        this._resources = list.toArray(new Resource[list.size()]);
        for (final Resource r : this._resources) {
            if (!r.exists() || !r.isDirectory()) {
                throw new IllegalArgumentException(r + " is not an existing directory.");
            }
        }
    }
    
    public ResourceCollection(final String[] resources) {
        this._resources = new Resource[resources.length];
        try {
            for (int i = 0; i < resources.length; ++i) {
                this._resources[i] = Resource.newResource(resources[i]);
                if (!this._resources[i].exists() || !this._resources[i].isDirectory()) {
                    throw new IllegalArgumentException(this._resources[i] + " is not an existing directory.");
                }
            }
        }
        catch (IllegalArgumentException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }
    
    public ResourceCollection(final String csvResources) {
        this.setResourcesAsCSV(csvResources);
    }
    
    public Resource[] getResources() {
        return this._resources;
    }
    
    public void setResources(final Resource[] resources) {
        this._resources = ((resources != null) ? resources : new Resource[0]);
    }
    
    public void setResourcesAsCSV(final String csvResources) {
        final StringTokenizer tokenizer = new StringTokenizer(csvResources, ",;");
        final int len = tokenizer.countTokens();
        if (len == 0) {
            throw new IllegalArgumentException("ResourceCollection@setResourcesAsCSV(String)  argument must be a string containing one or more comma-separated resource strings.");
        }
        final List<Resource> resources = new ArrayList<Resource>();
        try {
            while (tokenizer.hasMoreTokens()) {
                final Resource resource = Resource.newResource(tokenizer.nextToken().trim());
                if (!resource.exists() || !resource.isDirectory()) {
                    ResourceCollection.LOG.warn(" !exist " + resource, new Object[0]);
                }
                else {
                    resources.add(resource);
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        this._resources = resources.toArray(new Resource[resources.size()]);
    }
    
    @Override
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
        ArrayList<Resource> resources = null;
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
                if (resources == null) {
                    resources = new ArrayList<Resource>();
                }
                if (resource != null) {
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
            return new ResourceCollection((Resource[])resources.toArray(new Resource[resources.size()]));
        }
        return null;
    }
    
    protected Object findResource(final String path) throws IOException, MalformedURLException {
        Resource resource = null;
        ArrayList<Resource> resources = null;
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
                    resources = new ArrayList<Resource>();
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
    
    @Override
    public boolean delete() throws SecurityException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean exists() {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        return true;
    }
    
    @Override
    public File getFile() throws IOException {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        for (final Resource r : this._resources) {
            final File f = r.getFile();
            if (f != null) {
                return f;
            }
        }
        return null;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        for (final Resource r : this._resources) {
            final InputStream is = r.getInputStream();
            if (is != null) {
                return is;
            }
        }
        return null;
    }
    
    @Override
    public ReadableByteChannel getReadableByteChannel() throws IOException {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        for (final Resource r : this._resources) {
            final ReadableByteChannel channel = r.getReadableByteChannel();
            if (channel != null) {
                return channel;
            }
        }
        return null;
    }
    
    @Override
    public String getName() {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        for (final Resource r : this._resources) {
            final String name = r.getName();
            if (name != null) {
                return name;
            }
        }
        return null;
    }
    
    @Override
    public URL getURL() {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        for (final Resource r : this._resources) {
            final URL url = r.getURL();
            if (url != null) {
                return url;
            }
        }
        return null;
    }
    
    @Override
    public boolean isDirectory() {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        return true;
    }
    
    @Override
    public long lastModified() {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        for (final Resource r : this._resources) {
            final long lm = r.lastModified();
            if (lm != -1L) {
                return lm;
            }
        }
        return -1L;
    }
    
    @Override
    public long length() {
        return -1L;
    }
    
    @Override
    public String[] list() {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        final HashSet<String> set = new HashSet<String>();
        for (final Resource r : this._resources) {
            for (final String s : r.list()) {
                set.add(s);
            }
        }
        final String[] result = set.toArray(new String[set.size()]);
        Arrays.sort(result);
        return result;
    }
    
    @Override
    public void close() {
        if (this._resources == null) {
            throw new IllegalStateException("*resources* not set.");
        }
        for (final Resource r : this._resources) {
            r.close();
        }
    }
    
    @Override
    public boolean renameTo(final Resource dest) throws SecurityException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void copyTo(final File destination) throws IOException {
        int r = this._resources.length;
        while (r-- > 0) {
            this._resources[r].copyTo(destination);
        }
    }
    
    @Override
    public String toString() {
        if (this._resources == null) {
            return "[]";
        }
        return String.valueOf(Arrays.asList(this._resources));
    }
    
    @Override
    public boolean isContainedIn(final Resource r) throws MalformedURLException {
        return false;
    }
    
    static {
        LOG = Log.getLogger(ResourceCollection.class);
    }
}
