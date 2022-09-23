// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.types.ResourceCollection;

public abstract class CompressedResource extends ContentTransformingResource
{
    protected CompressedResource() {
    }
    
    protected CompressedResource(final ResourceCollection other) {
        this.addConfigured(other);
    }
    
    @Override
    public String toString() {
        return this.getCompressionName() + " compressed " + super.toString();
    }
    
    protected abstract String getCompressionName();
}
