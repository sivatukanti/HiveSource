// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.comparators;

import org.apache.tools.ant.types.Resource;

public class Date extends ResourceComparator
{
    @Override
    protected int resourceCompare(final Resource foo, final Resource bar) {
        final long diff = foo.getLastModified() - bar.getLastModified();
        if (diff > 0L) {
            return 1;
        }
        if (diff < 0L) {
            return -1;
        }
        return 0;
    }
}
