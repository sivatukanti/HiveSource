// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.tools.ant.types.Resource;
import java.util.Collection;

public class First extends SizeLimitCollection
{
    @Override
    protected Collection<Resource> getCollection() {
        final int ct = this.getValidCount();
        final Iterator<Resource> iter = this.getResourceCollection().iterator();
        final List<Resource> al = new ArrayList<Resource>(ct);
        for (int i = 0; i < ct && iter.hasNext(); ++i) {
            al.add(iter.next());
        }
        return al;
    }
}
