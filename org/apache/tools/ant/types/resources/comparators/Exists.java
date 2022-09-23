// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.comparators;

import org.apache.tools.ant.types.Resource;

public class Exists extends ResourceComparator
{
    @Override
    protected int resourceCompare(final Resource foo, final Resource bar) {
        final boolean f = foo.isExists();
        if (f == bar.isExists()) {
            return 0;
        }
        return f ? 1 : -1;
    }
}
