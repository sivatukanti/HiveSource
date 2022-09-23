// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util.resource;

import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class ResourceCalculator
{
    public abstract int compare(final Resource p0, final Resource p1, final Resource p2);
    
    public static int divideAndCeil(final int a, final int b) {
        if (b == 0) {
            return 0;
        }
        return (a + (b - 1)) / b;
    }
    
    public static int roundUp(final int a, final int b) {
        return divideAndCeil(a, b) * b;
    }
    
    public static int roundDown(final int a, final int b) {
        return a / b * b;
    }
    
    public abstract int computeAvailableContainers(final Resource p0, final Resource p1);
    
    public abstract Resource multiplyAndNormalizeUp(final Resource p0, final double p1, final Resource p2);
    
    public abstract Resource multiplyAndNormalizeDown(final Resource p0, final double p1, final Resource p2);
    
    public Resource normalize(final Resource r, final Resource minimumResource, final Resource maximumResource) {
        return this.normalize(r, minimumResource, maximumResource, minimumResource);
    }
    
    public abstract Resource normalize(final Resource p0, final Resource p1, final Resource p2, final Resource p3);
    
    public abstract Resource roundUp(final Resource p0, final Resource p1);
    
    public abstract Resource roundDown(final Resource p0, final Resource p1);
    
    public abstract float divide(final Resource p0, final Resource p1, final Resource p2);
    
    public abstract boolean isInvalidDivisor(final Resource p0);
    
    public abstract float ratio(final Resource p0, final Resource p1);
    
    public abstract Resource divideAndCeil(final Resource p0, final int p1);
}
