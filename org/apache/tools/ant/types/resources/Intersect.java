// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.types.ResourceCollection;
import java.util.ArrayList;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import java.util.Collection;

public class Intersect extends BaseResourceCollectionContainer
{
    @Override
    protected Collection<Resource> getCollection() {
        final List<ResourceCollection> rcs = this.getResourceCollections();
        final int size = rcs.size();
        if (size < 2) {
            throw new BuildException("The intersection of " + size + " resource collection" + ((size == 1) ? "" : "s") + " is undefined.");
        }
        final List<Resource> al = new ArrayList<Resource>();
        final Iterator<ResourceCollection> rc = rcs.iterator();
        al.addAll(this.collect(rc.next()));
        while (rc.hasNext()) {
            al.retainAll(this.collect(rc.next()));
        }
        return al;
    }
    
    private List<Resource> collect(final ResourceCollection rc) {
        final List<Resource> result = new ArrayList<Resource>();
        for (final Resource r : rc) {
            result.add(r);
        }
        return result;
    }
}
