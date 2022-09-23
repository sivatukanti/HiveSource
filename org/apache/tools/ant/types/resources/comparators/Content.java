// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.comparators;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.types.Resource;

public class Content extends ResourceComparator
{
    private boolean binary;
    
    public Content() {
        this.binary = true;
    }
    
    public void setBinary(final boolean b) {
        this.binary = b;
    }
    
    public boolean isBinary() {
        return this.binary;
    }
    
    @Override
    protected int resourceCompare(final Resource foo, final Resource bar) {
        try {
            return ResourceUtils.compareContent(foo, bar, !this.binary);
        }
        catch (IOException e) {
            throw new BuildException(e);
        }
    }
}
