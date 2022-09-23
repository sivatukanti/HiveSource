// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.util.List;
import java.util.Iterator;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.BuildException;
import java.util.ArrayList;
import org.apache.tools.ant.types.Resource;
import java.util.Collection;

public class Last extends SizeLimitCollection
{
    @Override
    protected Collection<Resource> getCollection() {
        final int count = this.getValidCount();
        final ResourceCollection rc = this.getResourceCollection();
        int i = count;
        final Iterator<Resource> iter = rc.iterator();
        int size;
        for (size = rc.size(); i < size; ++i) {
            iter.next();
        }
        final List<Resource> al = new ArrayList<Resource>(count);
        while (iter.hasNext()) {
            al.add(iter.next());
            ++i;
        }
        final int found = al.size();
        if (found == count || (size < count && found == size)) {
            return al;
        }
        final String msg = "Resource collection " + rc + " reports size " + size + " but returns " + i + " elements.";
        if (found > count) {
            this.log(msg, 1);
            return al.subList(found - count, found);
        }
        throw new BuildException(msg);
    }
}
