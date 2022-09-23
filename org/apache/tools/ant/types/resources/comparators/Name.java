// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.comparators;

import org.apache.tools.ant.types.Resource;

public class Name extends ResourceComparator
{
    @Override
    protected int resourceCompare(final Resource foo, final Resource bar) {
        return foo.getName().compareTo(bar.getName());
    }
}
