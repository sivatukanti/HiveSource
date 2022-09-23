// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.BuildException;

public abstract class SizeLimitCollection extends BaseResourceCollectionWrapper
{
    private static final String BAD_COUNT = "size-limited collection count should be set to an int >= 0";
    private int count;
    
    public SizeLimitCollection() {
        this.count = 1;
    }
    
    public synchronized void setCount(final int i) {
        this.checkAttributesAllowed();
        this.count = i;
    }
    
    public synchronized int getCount() {
        return this.count;
    }
    
    @Override
    public synchronized int size() {
        final int sz = this.getResourceCollection().size();
        final int ct = this.getValidCount();
        return (sz < ct) ? sz : ct;
    }
    
    protected int getValidCount() {
        final int ct = this.getCount();
        if (ct < 0) {
            throw new BuildException("size-limited collection count should be set to an int >= 0");
        }
        return ct;
    }
}
