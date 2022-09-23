// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util.resource;

import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class DefaultResourceCalculator extends ResourceCalculator
{
    @Override
    public int compare(final Resource unused, final Resource lhs, final Resource rhs) {
        return lhs.getMemory() - rhs.getMemory();
    }
    
    @Override
    public int computeAvailableContainers(final Resource available, final Resource required) {
        return available.getMemory() / required.getMemory();
    }
    
    @Override
    public float divide(final Resource unused, final Resource numerator, final Resource denominator) {
        return this.ratio(numerator, denominator);
    }
    
    @Override
    public boolean isInvalidDivisor(final Resource r) {
        return r.getMemory() == 0.0f;
    }
    
    @Override
    public float ratio(final Resource a, final Resource b) {
        return a.getMemory() / (float)b.getMemory();
    }
    
    @Override
    public Resource divideAndCeil(final Resource numerator, final int denominator) {
        return Resources.createResource(ResourceCalculator.divideAndCeil(numerator.getMemory(), denominator));
    }
    
    @Override
    public Resource normalize(final Resource r, final Resource minimumResource, final Resource maximumResource, final Resource stepFactor) {
        final int normalizedMemory = Math.min(ResourceCalculator.roundUp(Math.max(r.getMemory(), minimumResource.getMemory()), stepFactor.getMemory()), maximumResource.getMemory());
        return Resources.createResource(normalizedMemory);
    }
    
    @Override
    public Resource normalize(final Resource r, final Resource minimumResource, final Resource maximumResource) {
        return this.normalize(r, minimumResource, maximumResource, minimumResource);
    }
    
    @Override
    public Resource roundUp(final Resource r, final Resource stepFactor) {
        return Resources.createResource(ResourceCalculator.roundUp(r.getMemory(), stepFactor.getMemory()));
    }
    
    @Override
    public Resource roundDown(final Resource r, final Resource stepFactor) {
        return Resources.createResource(ResourceCalculator.roundDown(r.getMemory(), stepFactor.getMemory()));
    }
    
    @Override
    public Resource multiplyAndNormalizeUp(final Resource r, final double by, final Resource stepFactor) {
        return Resources.createResource(ResourceCalculator.roundUp((int)(r.getMemory() * by + 0.5), stepFactor.getMemory()));
    }
    
    @Override
    public Resource multiplyAndNormalizeDown(final Resource r, final double by, final Resource stepFactor) {
        return Resources.createResource(ResourceCalculator.roundDown((int)(r.getMemory() * by), stepFactor.getMemory()));
    }
}
