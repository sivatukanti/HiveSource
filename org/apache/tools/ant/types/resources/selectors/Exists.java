// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.types.Resource;

public class Exists implements ResourceSelector
{
    public boolean isSelected(final Resource r) {
        return r.isExists();
    }
}
