// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.util.Iterator;
import java.util.Set;
import java.util.List;
import org.apache.tools.ant.types.ResourceCollection;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import java.util.Collection;

public class Difference extends BaseResourceCollectionContainer
{
    @Override
    protected Collection<Resource> getCollection() {
        final List<ResourceCollection> rcs = this.getResourceCollections();
        final int size = rcs.size();
        if (size < 2) {
            throw new BuildException("The difference of " + size + " resource collection" + ((size == 1) ? "" : "s") + " is undefined.");
        }
        final Set<Resource> hs = new HashSet<Resource>();
        final List<Resource> al = new ArrayList<Resource>();
        for (final ResourceCollection rc : rcs) {
            for (final Resource r : rc) {
                if (hs.add(r)) {
                    al.add(r);
                }
                else {
                    al.remove(r);
                }
            }
        }
        return al;
    }
}
