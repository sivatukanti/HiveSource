// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http.pathmap;

import org.eclipse.jetty.util.log.Log;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.io.IOException;
import java.util.Collection;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.Dumpable;

@ManagedObject("Path Mappings")
public class PathMappings<E> implements Iterable<MappedResource<E>>, Dumpable
{
    private static final Logger LOG;
    private List<MappedResource<E>> mappings;
    private MappedResource<E> defaultResource;
    private MappedResource<E> rootResource;
    
    public PathMappings() {
        this.mappings = new ArrayList<MappedResource<E>>();
        this.defaultResource = null;
        this.rootResource = null;
    }
    
    @Override
    public String dump() {
        return ContainerLifeCycle.dump(this);
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        ContainerLifeCycle.dump(out, indent, this.mappings);
    }
    
    @ManagedAttribute(value = "mappings", readonly = true)
    public List<MappedResource<E>> getMappings() {
        return this.mappings;
    }
    
    public void reset() {
        this.mappings.clear();
    }
    
    public void removeIf(final Predicate<MappedResource<E>> predicate) {
        this.mappings.removeIf(predicate);
    }
    
    public List<MappedResource<E>> getMatches(final String path) {
        final boolean matchRoot = "/".equals(path);
        final List<MappedResource<E>> ret = new ArrayList<MappedResource<E>>();
        for (int len = this.mappings.size(), i = 0; i < len; ++i) {
            final MappedResource<E> mr = this.mappings.get(i);
            switch (mr.getPathSpec().group) {
                case ROOT: {
                    if (matchRoot) {
                        ret.add(mr);
                        break;
                    }
                    break;
                }
                case DEFAULT: {
                    if (matchRoot || mr.getPathSpec().matches(path)) {
                        ret.add(mr);
                        break;
                    }
                    break;
                }
                default: {
                    if (mr.getPathSpec().matches(path)) {
                        ret.add(mr);
                        break;
                    }
                    break;
                }
            }
        }
        return ret;
    }
    
    public MappedResource<E> getMatch(final String path) {
        if (path.equals("/") && this.rootResource != null) {
            return this.rootResource;
        }
        for (int len = this.mappings.size(), i = 0; i < len; ++i) {
            final MappedResource<E> mr = this.mappings.get(i);
            if (mr.getPathSpec().matches(path)) {
                return mr;
            }
        }
        return this.defaultResource;
    }
    
    @Override
    public Iterator<MappedResource<E>> iterator() {
        return this.mappings.iterator();
    }
    
    public void put(final PathSpec pathSpec, final E resource) {
        final MappedResource<E> entry = new MappedResource<E>(pathSpec, resource);
        switch (pathSpec.group) {
            case DEFAULT: {
                this.defaultResource = entry;
                break;
            }
            case ROOT: {
                this.rootResource = entry;
                break;
            }
        }
        this.mappings.add(entry);
        if (PathMappings.LOG.isDebugEnabled()) {
            PathMappings.LOG.debug("Added {} to {}", entry, this);
        }
        Collections.sort(this.mappings);
    }
    
    @Override
    public String toString() {
        return String.format("%s[size=%d]", this.getClass().getSimpleName(), this.mappings.size());
    }
    
    static {
        LOG = Log.getLogger(PathMappings.class);
    }
}
