// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.comparators;

import org.apache.tools.ant.types.Resource;

public class Size extends ResourceComparator
{
    @Override
    protected int resourceCompare(final Resource foo, final Resource bar) {
        final long diff = foo.getSize() - bar.getSize();
        return (diff > 0L) ? 1 : ((diff == 0L) ? 0 : -1);
    }
}
