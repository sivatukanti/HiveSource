// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http.pathmap;

import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("Mapped Resource")
public class MappedResource<E> implements Comparable<MappedResource<E>>
{
    private final PathSpec pathSpec;
    private final E resource;
    
    public MappedResource(final PathSpec pathSpec, final E resource) {
        this.pathSpec = pathSpec;
        this.resource = resource;
    }
    
    @Override
    public int compareTo(final MappedResource<E> other) {
        return this.pathSpec.compareTo(other.pathSpec);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final MappedResource<?> other = (MappedResource<?>)obj;
        if (this.pathSpec == null) {
            if (other.pathSpec != null) {
                return false;
            }
        }
        else if (!this.pathSpec.equals(other.pathSpec)) {
            return false;
        }
        return true;
    }
    
    @ManagedAttribute(value = "path spec", readonly = true)
    public PathSpec getPathSpec() {
        return this.pathSpec;
    }
    
    @ManagedAttribute(value = "resource", readonly = true)
    public E getResource() {
        return this.resource;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.pathSpec == null) ? 0 : this.pathSpec.hashCode());
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("MappedResource[pathSpec=%s,resource=%s]", this.pathSpec, this.resource);
    }
}
