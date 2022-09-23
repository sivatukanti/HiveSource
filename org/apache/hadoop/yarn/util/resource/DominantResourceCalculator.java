// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util.resource;

import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class DominantResourceCalculator extends ResourceCalculator
{
    @Override
    public int compare(final Resource clusterResource, final Resource lhs, final Resource rhs) {
        if (lhs.equals(rhs)) {
            return 0;
        }
        float l = this.getResourceAsValue(clusterResource, lhs, true);
        float r = this.getResourceAsValue(clusterResource, rhs, true);
        if (l < r) {
            return -1;
        }
        if (l > r) {
            return 1;
        }
        l = this.getResourceAsValue(clusterResource, lhs, false);
        r = this.getResourceAsValue(clusterResource, rhs, false);
        if (l < r) {
            return -1;
        }
        if (l > r) {
            return 1;
        }
        return 0;
    }
    
    protected float getResourceAsValue(final Resource clusterResource, final Resource resource, final boolean dominant) {
        return dominant ? Math.max(resource.getMemory() / (float)clusterResource.getMemory(), resource.getVirtualCores() / (float)clusterResource.getVirtualCores()) : Math.min(resource.getMemory() / (float)clusterResource.getMemory(), resource.getVirtualCores() / (float)clusterResource.getVirtualCores());
    }
    
    @Override
    public int computeAvailableContainers(final Resource available, final Resource required) {
        return Math.min(available.getMemory() / required.getMemory(), available.getVirtualCores() / required.getVirtualCores());
    }
    
    @Override
    public float divide(final Resource clusterResource, final Resource numerator, final Resource denominator) {
        return this.getResourceAsValue(clusterResource, numerator, true) / this.getResourceAsValue(clusterResource, denominator, true);
    }
    
    @Override
    public boolean isInvalidDivisor(final Resource r) {
        return r.getMemory() == 0.0f || r.getVirtualCores() == 0.0f;
    }
    
    @Override
    public float ratio(final Resource a, final Resource b) {
        return Math.max(a.getMemory() / (float)b.getMemory(), a.getVirtualCores() / (float)b.getVirtualCores());
    }
    
    @Override
    public Resource divideAndCeil(final Resource numerator, final int denominator) {
        return Resources.createResource(ResourceCalculator.divideAndCeil(numerator.getMemory(), denominator), ResourceCalculator.divideAndCeil(numerator.getVirtualCores(), denominator));
    }
    
    @Override
    public Resource normalize(final Resource r, final Resource minimumResource, final Resource maximumResource, final Resource stepFactor) {
        final int normalizedMemory = Math.min(ResourceCalculator.roundUp(Math.max(r.getMemory(), minimumResource.getMemory()), stepFactor.getMemory()), maximumResource.getMemory());
        final int normalizedCores = Math.min(ResourceCalculator.roundUp(Math.max(r.getVirtualCores(), minimumResource.getVirtualCores()), stepFactor.getVirtualCores()), maximumResource.getVirtualCores());
        return Resources.createResource(normalizedMemory, normalizedCores);
    }
    
    @Override
    public Resource roundUp(final Resource r, final Resource stepFactor) {
        return Resources.createResource(ResourceCalculator.roundUp(r.getMemory(), stepFactor.getMemory()), ResourceCalculator.roundUp(r.getVirtualCores(), stepFactor.getVirtualCores()));
    }
    
    @Override
    public Resource roundDown(final Resource r, final Resource stepFactor) {
        return Resources.createResource(ResourceCalculator.roundDown(r.getMemory(), stepFactor.getMemory()), ResourceCalculator.roundDown(r.getVirtualCores(), stepFactor.getVirtualCores()));
    }
    
    @Override
    public Resource multiplyAndNormalizeUp(final Resource r, final double by, final Resource stepFactor) {
        return Resources.createResource(ResourceCalculator.roundUp((int)Math.ceil(r.getMemory() * by), stepFactor.getMemory()), ResourceCalculator.roundUp((int)Math.ceil(r.getVirtualCores() * by), stepFactor.getVirtualCores()));
    }
    
    @Override
    public Resource multiplyAndNormalizeDown(final Resource r, final double by, final Resource stepFactor) {
        return Resources.createResource(ResourceCalculator.roundDown((int)(r.getMemory() * by), stepFactor.getMemory()), ResourceCalculator.roundDown((int)(r.getVirtualCores() * by), stepFactor.getVirtualCores()));
    }
}
